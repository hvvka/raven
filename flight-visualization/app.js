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
    let stats = mapFlightsToForecastStats(await findFlights({}));
    res.send(stats);
});


app.get('/details', async function (req, res) {
    let stats = mapFlightsToForecastStats(await findFlights({}));
    const t = new timeseries.main(stats);
    const details = {};
    details["Mean"] = t.mean();
    details["Standard Deviation"] = t.stdev();
    details["Min"] = t.min();
    details["Max"] = t.max();
    res.send(details);
});

// TODO: Delete Home
app.post('/home', async function (req, res) {
    const dateFrom = req.body.from;
    const dateTo = req.body.to;
    let stats = mapFlightsToForecastStats(await findFlightBetweenDates(dateFrom, dateTo));
    res.send(stats);
});

app.post('/airlines', async function (req, res) {
    const dateFrom = req.body.from;
    const dateTo = req.body.to;
    let flights = await findFlightBetweenDates(dateFrom, dateTo);
    res.send(_.uniq(flights.map(f => f.airline)));
});

app.post('/ma', async function (req, res) {
    const dateFrom = req.body.from;
    const dateTo = req.body.to;
    const airline = req.body.airline;
    const flights = airline ? await findFlightBetweenDatesAndForAirline(dateFrom, dateTo, airline) : await findFlightBetweenDates(dateFrom, dateTo);
    const stats = mapFlightsToForecastStats(flights);
    const t = new timeseries.main(stats);
    const result = {chart: t.ma().chart({main: true})};
    res.send(result);
});

app.post('/lwma', async function (req, res) {
    const dateFrom = req.body.from;
    const dateTo = req.body.to;
    const airline = req.body.airline;
    const flights = airline ? await findFlightBetweenDatesAndForAirline(dateFrom, dateTo, airline) : await findFlightBetweenDates(dateFrom, dateTo);
    const stats = mapFlightsToForecastStats(flights);
    const t = new timeseries.main(stats);
    const result = {chart: t.lwma().chart({main: true})};
    res.send(result);
});

app.post('/trend', async function (req, res) {
    const dateFrom = req.body.from;
    const dateTo = req.body.to;
    const airline = req.body.airline;
    const flights = airline ? await findFlightBetweenDatesAndForAirline(dateFrom, dateTo, airline) : await findFlightBetweenDates(dateFrom, dateTo);
    const stats = mapFlightsToForecastStats(flights);
    const t = new timeseries.main(stats);
    const result = {
        chart: t.dsp_itrend({
            alpha: 0.5
        }).chart({main: true})
    };
    res.send(result);
});

app.post('/smoothing', async function (req, res) {
    const dateFrom = req.body.from;
    const dateTo = req.body.to;
    const airline = req.body.airline;
    const flights = airline ? await findFlightBetweenDatesAndForAirline(dateFrom, dateTo, airline) : await findFlightBetweenDates(dateFrom, dateTo);
    const stats = mapFlightsToForecastStats(flights);
    const t = new timeseries.main(stats);
    const result = {
        chart: t.smoother({
            period: 5
        }).chart({main: true})
    };
    res.send(result);
});

app.post('/noise', async function (req, res) {
    const dateFrom = req.body.from;
    const dateTo = req.body.to;
    const airline = req.body.airline;
    const flights = airline ? await findFlightBetweenDatesAndForAirline(dateFrom, dateTo, airline) : await findFlightBetweenDates(dateFrom, dateTo);
    const stats = mapFlightsToForecastStats(flights);
    const t = new timeseries.main(stats);
    const result = {chart: t.smoother({period: 10}).noiseData().smoother({period: 5}).chart()};
    res.send(result);
});

app.post('/traffic', async function (req, res) {
    const dateFrom = req.body.from;
    const dateTo = req.body.to;
    const arrivalDepartureFlights = await getArrivalDepartureSumForDay(dateFrom, dateTo);
    const delayedCancelledFlights = await getDelayedCancelledSumForDay(dateFrom, dateTo);
    const result = {
        table: [["Data wylotu", "Łączna liczba lotów", "Suma lotów opóźnionych i anulowanych"],
            ...arrivalDepartureFlights.map((f, i) => [...f, delayedCancelledFlights[i][1]])]
    };
    res.send(result);
});

app.post('/forecast', async function (req, res) {
    const dateFrom = req.body.from;
    const dateTo = req.body.to;
    const airline = req.body.airline;
    const flights = airline ? await findFlightBetweenDatesAndForAirline(dateFrom, dateTo, airline) : await findFlightBetweenDates(dateFrom, dateTo);
    const stats = mapFlightsToForecastStats(flights);
    const t = new timeseries.main(stats);
    t.smoother({period: 1}).save('smoothed');
    const forecast = t.sliding_regression_forecast({
        sample: 3,
        degree: 1,
        method: req.body.method
    });
    const result = {
        table: [["Data wylotu", "Sumaryczne opóźnienie", "Prognozowane sumaryczne opóźnienie"],
            ...stats.map((row, i) => [...row, forecast.data[i][1].toString()])],
        chart: forecast.chart({main: false}) + "&chm=s,0000ff,0," + forecast.sample + ",8.0"
    };
    res.send(result);
});

app.set('port', PORT);
let server = http.createServer(app);
app.listen(PORT);

console.log(`Running on http://${HOST}:${PORT}`);

eval(fs.readFileSync('consumer.js') + '');

/* MongoDb */

const findFlightBetweenDates = async (dateFrom, dateTo) => {
    return findFlights({
        departure: {
            $gte: new Date(dateFrom),
            $lt: new Date(dateTo)
        }
    });
};

const findFlightBetweenDatesAndForAirline = async (dateFrom, dateTo, airline) => {
    return findFlights({
        departure: {
            $gte: new Date(dateFrom),
            $lt: new Date(dateTo)
        },
        airline: {$eq: airline}
    });
};

const findFlights = async (condition) => {
    const url = 'mongodb://localhost:27017/test';
    const db = await MongoClient.connect(url);
    const dbo = db.db("test");
    return await dbo.collection('Flights')
        .find(condition)
        .sort({departure: 1})
        .toArray();
};

const getArrivalDepartureSumForDay = async (dateFrom, dateTo) => {
    let flights = await findFlightBetweenDates(dateFrom, dateTo);
    flights.forEach(f => {
        f.departure = getDate(f.departure);
        f.arrival = getDate(f.arrival)
    });
    let flightsByDay = getDatesBetween(new Date(dateFrom), new Date(dateTo));

    flights.forEach(f => {
        flightsByDay.find(row => row[0] === f.departure)[1]++;
        flightsByDay.find(row => row[0] === f.arrival)[1]++;
    });
    return flightsByDay;
};

const getDelayedCancelledSumForDay = async (dateFrom, dateTo) => {
    let flights = await findFlights({
        departure: {
            $gte: new Date(dateFrom),
            $lt: new Date(dateTo)
        },
        $or: [
            {relativeDelay: {$gt: 0}},
            {flightStatus: {$eq: "Cancelled"}}
        ]
    });
    flights.forEach(f => {
        f.departure = getDate(f.departure);
        f.arrival = getDate(f.arrival)
    });
    let flightsByDay = getDatesBetween(new Date(dateFrom), new Date(dateTo));

    flights.forEach(f => {
        flightsByDay.find(row => row[0] === f.departure)[1]++;
        flightsByDay.find(row => row[0] === f.arrival)[1]++;
    });
    return flightsByDay;
};

const getDatesBetween = (startDate, endDate) => {
    let dates = [],
        date = startDate,
        addDays = function (days) {
            date = new Date(this.valueOf());
            date.setDate(date.getDate() + days);
            return date;
        };
    while (date <= endDate) {
        const stringDate = `${date.getFullYear()}-${("0" + date.getMonth() + 1).slice(-2)}-${("0" + date.getDate()).slice(-2)}`;
        dates.push([stringDate, 0]);
        date = addDays.call(date, 1);
    }
    return dates;
};

const getDate = (date) => {
    return `${date.getFullYear()}-${("0" + date.getMonth() + 1).slice(-2)}-${("0" + date.getDate()).slice(-2)}`;
};

const mapFlightsToForecastStats = result => _.map(result, f =>
    [`${f.departure.getFullYear()}-${("0" + f.departure.getMonth() + 1).slice(-2)}-${("0" + f.departure.getDate()).slice(-2)} ${("0" + f.departure.getHours()).slice(-2)}:${("0" + f.departure.getMinutes()).slice(-2)}:${("0" + f.departure.getSeconds()).slice(-2)}`,
        f.departureDelay + f.arrivalDelay]);

module.exports = app;