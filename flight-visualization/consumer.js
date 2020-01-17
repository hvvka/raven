'use strict';

let kafka = require('kafka-node');
let _ = require('lodash');

let Consumer = kafka.Consumer;
let Offset = kafka.Offset;
let Client = kafka.Client;
let argv = require('optimist').argv;
let topic = 'processed-data';
let consumerGroupName = 'visualization-group';

let client = new Client('zookeeper:2181', consumerGroupName);
let topics = [{topic: topic, partition: 0}];
let options = {autoCommit: true, fetchMaxWaitMs: 1000, fetchMaxBytes: 1024 * 1024};

let consumer = new Consumer(client, topics, options);
let offset = new Offset(client);


client.on('ready', function () {
    console.log('Client\'s ready!')
});


let io = require('socket.io').listen(server);
consumer.on('message', function (message) {
    const messageString = message.value;
    // console.log(messageString);
    io.sockets.emit('news', messageString);
});


consumer.on('error', function (err) {
    console.error(`${err.stack || err.message}`, err);
});

/*
* If consumer get `offsetOutOfRange` event, fetch data from the smallest(oldest) offset
*/
consumer.on('offsetOutOfRange', function (topic) {
    topic.maxNum = 2;
    offset.fetch([topic], function (err, offsets) {
        if (err) {
            return console.error(err);
        }
        let min = Math.min.apply(null, offsets[topic.topic][topic.partition]);
        consumer.setOffset(topic.topic, topic.partition, min);
    });
});

const MongoClient = require('mongodb').MongoClient;
const url = 'mongodb://localhost:27017/test';

MongoClient.connect(url, async function (err, db) {
    let flights = await db.collection('Flights')
        .find()
        .sort({departure: 1})
        .toArray();
    flights = flights.slice(Math.max(flights.length - 30, 1)); // get latest 30 departures
    stats = _.map(flights, f =>
        [`${f.departure.getFullYear()}-${("0" + f.departure.getMonth() + 1).slice(-2)}-${("0" + f.departure.getDate()).slice(-2)} ${("0" + f.departure.getHours()).slice(-2)}:${("0" + f.departure.getMinutes()).slice(-2)}:${("0" + f.departure.getSeconds()).slice(-2)}`,
            f.relativeDelay]);

    // TODO: Save to status.json or home should return it
});