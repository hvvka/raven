const bodyParser = require('body-parser');
const express = require('express');
const timeseries = require("timeseries-analysis");
const fs = require('fs');
const http = require('http');
const _ = require('lodash');
const MongoClient = require('mongodb').MongoClient;

const app = express();
const PORT = 3000;
const HOST = '0.0.0.0';

app.use(bodyParser.urlencoded({extended: true}));
app.use(bodyParser.json());
app.use(express.static('public'));

app.get('/', async function (req, res) {
    let stats = await fetchStatsFind({});
    res.sendFile("public/index.html", {root: __dirname});
});

app.get('/data', async function (req, res) {
    let stats = await fetchStatsFind({});
    res.send(stats);
});


app.get('/details', async function (req, res) {
    let stats = await fetchStatsFind({});
    const t = new timeseries.main(stats);
    const details = {};
    details["Mean"] = t.mean();
    details["Standard Deviation"] = t.stdev();
    details["Min"] = t.min();
    details["Max"] = t.max();
    res.send(details);
});

app.post('/home', async function (req, res) {
    let stats = await fetchStatsFind({});
    res.send(stats);
});

app.post('/ma', async function (req, res) {
    let stats = await fetchStatsFind({});
    const t = new timeseries.main(stats);
    const movingAverage = t.ma().chart({main: true});
    res.send(movingAverage);
});

app.post('/lwma', async function (req, res) {
    let stats = await fetchStatsFind({});
    const t = new timeseries.main(stats);
    const linearWeightedMA = t.lwma().chart({main: true});
    res.send(linearWeightedMA);
});

app.post('/trend', async function (req, res) {
    let stats = await fetchStatsFind({});
    const t = new timeseries.main(stats);
    const trend = t.dsp_itrend({
        alpha: 0.5
    }).chart({main: true});
    res.send(trend);
});

app.post('/smoothing', async function (req, res) {
    let stats = await fetchStatsFind({});
    const t = new timeseries.main(stats);
    const smooth = t.smoother({
        period: 5
    }).chart({main: true});

    res.send(smooth);
});

app.post('/noise', function (req, res) {
    const t = new timeseries.main(stats);
    const noise = t.smoother({period: 10}).noiseData().smoother({period: 5}).chart();
    res.send(noise);
});

app.post('/forecast', async function (req, res) {
    const dateFrom = req.body.from;
    const dateTo = req.body.to;
    let stats = await fetchStats(dateFrom, dateTo);
    console.log(stats);
    const t = new timeseries.main(stats);
    t.smoother({period: 1}).save('smoothed');
    const bestSettings = t.regression_forecast_optimize();
    const forecast = t.sliding_regression_forecast({
        sample: bestSettings.sample,
        degree: bestSettings.degree,
        method: bestSettings.method
    });
    const forecastChart = forecast.chart({main: false}) + "&chm=s,0000ff,0," + bestSettings.sample + ",8.0";
    res.send(forecastChart);
});

app.post('/forecast/chart', async (req, res) => {
    console.log(req.body);
    const dateFrom = req.body.from;
    const dateTo = req.body.to;
    let stats = await fetchStats(dateFrom, dateTo);
    const t = new timeseries.main(stats);
    t.smoother({period: 1}).save('smoothed');
    const bestSettings = t.regression_forecast_optimize();
    const forecast = t.sliding_regression_forecast({
        sample: bestSettings.sample,
        degree: bestSettings.degree,
        method: bestSettings.method
    });
    res.send(forecast.data);
});

app.set('port', PORT);
let server = http.createServer(app);
app.listen(PORT);

console.log(`Running on http://${HOST}:${PORT}`);

eval(fs.readFileSync('consumer.js') + '');

/* MongoDb */

const fetchStatsFind = async (condition) => {
    const url = 'mongodb://localhost:27017/test';
    const db = await MongoClient.connect(url);
    const dbo = db.db("test");
    const result = await dbo.collection('Flights')
        .find(condition)
        .sort({departure: 1})
        .toArray();
    return _.map(result, f =>
        [`${f.departure.getFullYear()}-${("0" + f.departure.getMonth() + 1).slice(-2)}-${("0" + f.departure.getDate()).slice(-2)} ${("0" + f.departure.getHours()).slice(-2)}:${("0" + f.departure.getMinutes()).slice(-2)}:${("0" + f.departure.getSeconds()).slice(-2)}`,
            f.relativeDelay]);
};

const fetchStats = async (dateFrom, dateTo) => {
    return fetchStatsFind({
        departure: {
            $gte: new Date(dateFrom),
            $lt: new Date(dateTo)
        }
    });
};

module.exports = app;