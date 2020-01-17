const bodyParser = require('body-parser');
const express = require('express');
const timeseries = require("timeseries-analysis");
const fs = require('fs');
const http = require('http');

const app = express();
const PORT = 3000;
const HOST = '0.0.0.0';

app.use(bodyParser.urlencoded({extended: true}));
app.use(bodyParser.json());
app.use(express.static('public'));

app.get('/', function (req, res) {
    res.sendFile("public/index.html", {root: __dirname});
});

app.get('/data', function (req, res) {
    res.send(stats);
});


app.get('/details', function (req, res) {
    const t = new timeseries.main(stats);
    const details = {};
    details["Mean"] = t.mean();
    details["Standard Deviation"] = t.stdev();
    details["Min"] = t.min();
    details["Max"] = t.max();
    res.send(details);
});

app.post('/home', function (req, res) {
    res.send(stats);
});

app.post('/ma', function (req, res) {
    const t = new timeseries.main(stats);
    const movingAverage = t.ma().chart({main: true});
    res.send(movingAverage);
});

app.post('/lwma', function (req, res) {
    const t = new timeseries.main(stats);
    const linearWeightedMA = t.lwma().chart({main: true});
    res.send(linearWeightedMA);
});

app.post('/trend', function (req, res) {
    const t = new timeseries.main(stats);
    const trend = t.dsp_itrend({
        alpha: 0.5
    }).chart({main: true});
    res.send(trend);
});

app.post('/smoothing', function (req, res) {
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

app.post('/forecast', function (req, res) {
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

app.post('/forecast/chart', function (req, res) {
    const t = new timeseries.main(stats);
    t.smoother({period: 1}).save('smoothed');
    const bestSettings = t.regression_forecast_optimize();
    const forecast = t.sliding_regression_forecast({
        sample: bestSettings.sample,
        degree: bestSettings.degree,
        method: bestSettings.method
    });
    // console.log(forecast);
    // console.log(forecast.saved[0].data);
    res.send(forecast.data);
});

app.set('port', PORT);
let server = http.createServer(app);
app.listen(PORT);

console.log(`Running on http://${HOST}:${PORT}`);

eval(fs.readFileSync('consumer.js') + '');

stats = [];

module.exports = app;