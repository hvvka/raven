const bodyParser = require('body-parser');
const express = require('express');
const timeseries = require("timeseries-analysis");
const fs = require('fs');
const http = require('http');

// var stats = [];

// var url = "http://188.213.170.165/helias/azth/status.json";
// request({
//   url: url,
//   json: true
// }, function (error, response, body) {

//   if (!error && response.statusCode === 200) {
//     body.shift();
//     stats = body;
//   }
// })

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
    var t = new timeseries.main(stats);
    var details = {};
    details["Mean"] = t.mean();
    details["Standard Deviation"] = t.stdev();
    details["Min"] = t.min();
    details["Max"] = t.max();
    res.send(details);
});

app.post('/ma', function (req, res) {
    var t = new timeseries.main(stats);
    var movingAverage = t.ma().chart({main: true});
    res.send(movingAverage);
});

app.post('/lwma', function (req, res) {
    console.log("lwma");
    var t = new timeseries.main(stats);
    var linearWeightedMA = t.lwma().chart({main: true});
    res.send(linearWeightedMA);
});

app.post('/trend', function (req, res) {
    var t = new timeseries.main(stats);
    var trend = t.dsp_itrend({
        alpha: 0.5
    }).chart({main: true});
    res.send(trend);
});

app.post('/smoothing', function (req, res) {
    var t = new timeseries.main(stats);
    var smooth = t.smoother({
        period: 5
    }).chart({main: true});

    res.send(smooth);
});

app.post('/noise', function (req, res) {
    var t = new timeseries.main(stats);
    var noise = t.smoother({period: 10}).noiseData().smoother({period: 5}).chart();
    res.send(noise);
});

app.post('/forecast', function (req, res) {
    var t = new timeseries.main(stats);
    t.smoother({period: 1}).save('smoothed');
    var bestSettings = t.regression_forecast_optimize();
    var forecast = t.sliding_regression_forecast({
        sample: bestSettings.sample,
        degree: bestSettings.degree,
        method: bestSettings.method
    });
    var forecastChart = forecast.chart({main: false}) + "&chm=s,0000ff,0," + bestSettings.sample + ",8.0";
    res.send(forecastChart);
});

app.post('/forecast/chart', function (req, res) {
    var t = new timeseries.main(stats);
    t.smoother({period: 1}).save('smoothed');
    var bestSettings = t.regression_forecast_optimize();
    var forecast = t.sliding_regression_forecast({
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

eval(fs.readFileSync('src/consumer.js') + '');


var stats = [
    [
        "2016-01-01",
        178
    ],
    [
        "2016-01-02",
        139
    ],
    [
        "2016-01-03",
        0
    ],
    [
        "2016-01-04",
        0
    ],
    [
        "2016-01-05",
        75
    ],
    [
        "2016-01-06",
        584
    ],
    [
        "2016-01-07",
        53
    ],
    [
        "2016-01-08",
        60
    ],
    [
        "2016-01-09",
        167
    ],
    [
        "2016-01-10",
        178
    ],
    [
        "2016-01-11",
        0
    ],
    [
        "2016-01-12",
        0
    ],
    [
        "2016-01-13",
        0
    ],
    [
        "2016-01-14",
        360
    ],
    [
        "2016-01-15",
        344
    ],
    [
        "2016-01-16",
        424
    ],
    [
        "2016-01-17",
        723
    ],
    [
        "2016-01-18",
        319
    ],
    [
        "2016-01-19",
        2939
    ],
    [
        "2016-01-20",
        119
    ],
    [
        "2016-01-21",
        0
    ],
    [
        "2016-01-22",
        2953
    ],
    [
        "2016-01-23",
        0
    ],
    [
        "2016-01-24",
        0
    ],
    [
        "2016-01-25",
        22
    ],
    [
        "2016-01-26",
        43
    ],
    [
        "2016-01-27",
        95
    ],
    [
        "2016-01-28",
        0
    ],
    [
        "2016-01-29",
        0
    ],
    [
        "2016-01-30",
        127
    ],
    [
        "2016-01-31",
        0
    ]
];

module.exports = app;