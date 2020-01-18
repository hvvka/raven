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
    res.sendFile("public/index.html", {root: __dirname});
});

app.get('/data', async function (req, res) {
    let stats = mapFlightToForecastStats(await findFlight({}));
    res.send(stats);
});


app.get('/details', async function (req, res) {
    let stats = mapFlightToForecastStats(await findFlight({}));
    const t = new timeseries.main(stats);
    const details = {};
    details["Mean"] = t.mean();
    details["Standard Deviation"] = t.stdev();
    details["Min"] = t.min();
    details["Max"] = t.max();
    res.send(details);
});

app.post('/home', async function (req, res) {
    let stats = mapFlightToForecastStats(await findFlight({}));
    res.send(stats);
});

app.post('/ma', async function (req, res) {
    let stats = mapFlightToForecastStats(await findFlight({}));
    const t = new timeseries.main(stats);
    const movingAverage = t.ma().chart({main: true});
    res.send(movingAverage);
});

app.post('/lwma', async function (req, res) {
    let stats = mapFlightToForecastStats(await findFlight({}));
    const t = new timeseries.main(stats);
    const linearWeightedMA = t.lwma().chart({main: true});
    res.send(linearWeightedMA);
});

app.post('/trend', async function (req, res) {
    let stats = mapFlightToForecastStats(await findFlight({}));
    const t = new timeseries.main(stats);
    const trend = t.dsp_itrend({
        alpha: 0.5
    }).chart({main: true});
    res.send(trend);
});

app.post('/smoothing', async function (req, res) {
    let stats = mapFlightToForecastStats(await findFlight({}));
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
    console.log(req.body);
    const dateFrom = req.body.from;
    const dateTo = req.body.to;
    let stats = await fetchForecastStats(dateFrom, dateTo);
    console.log(stats);
    const t = new timeseries.main(stats);
    t.smoother({period: 1}).save('smoothed');
    const forecast = t.sliding_regression_forecast({
        sample: 3,
        degree: 1,
        method: 'ARLeastSquare'
    });
    const forecastChart = forecast.chart({main: false}) + "&chm=s,0000ff,0," + forecast.sample + ",8.0";
    res.send(forecastChart);
});

app.post('/forecast/chart', async (req, res) => {
    const dateFrom = req.body.from;
    const dateTo = req.body.to;
    let stats = await fetchForecastStats(dateFrom, dateTo);
    const t = new timeseries.main(stats);
    t.smoother({period: 1}).save('smoothed');
    const forecast = t.sliding_regression_forecast({
        sample: 3,
        degree: 1,
        method: 'ARLeastSquare'
    });
    const result = stats.map((row, i) => [...row, forecast.data[i][1].toString()]);
    res.send(result);
});

app.set('port', PORT);
let server = http.createServer(app);
app.listen(PORT);

console.log(`Running on http://${HOST}:${PORT}`);

eval(fs.readFileSync('consumer.js') + '');

/* MongoDb */

const fetchForecastStats = async (dateFrom, dateTo) => {
    return mapFlightToForecastStats(
        await findFlight({
            departure: {
                $gte: new Date(dateFrom),
                $lt: new Date(dateTo)
            }
        })
    );
};

const findFlight = async (condition) => {
    const url = 'mongodb://localhost:27017/test';
    const db = await MongoClient.connect(url);
    const dbo = db.db("test");
    return await dbo.collection('Flights')
        .find(condition)
        .sort({departure: 1})
        .toArray();
};

function mapFlightToForecastStats(result) {
    return _.map(result, f =>
        [`${f.departure.getFullYear()}-${("0" + f.departure.getMonth() + 1).slice(-2)}-${("0" + f.departure.getDate()).slice(-2)} ${("0" + f.departure.getHours()).slice(-2)}:${("0" + f.departure.getMinutes()).slice(-2)}:${("0" + f.departure.getSeconds()).slice(-2)}`,
            f.relativeDelay]);
}

module.exports = app;