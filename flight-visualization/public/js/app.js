var app = angular.module('app', ['ui.router', 'ui.bootstrap']);

app.run(function ($rootScope) {
    $rootScope.adaptChart = function (data, legend) {
        let src = "";
        if (legend !== "Predykcja")
            src = data + "&chf=c,s,2b3e50|bg,s,2b3e50&chco=ff0000,0ce3ac&chdl=" + legend + "|Dane&chdls=0ce3ac,20&chxs=0N*,3498db";
        else
            src = data + "&chf=c,s,2b3e50|bg,s,2b3e50&chco=ff0000,0ce3ac,0000ff&chdl=" + legend + "|Dane|Start prognozy&chdls=0ce3ac,20&chxs=0N*,3498db";

        if (legend === "Noise")
            src = src.replace("|Dane", "");

        return src.replace("800x200", "1000x300");
    }
});

app.controller('homeController', function ($scope, $http) {
    let stats = [];
    const xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            stats = JSON.parse(this.responseText);
            google.charts.load('current', {'packages': ['corechart']});
            google.charts.setOnLoadCallback(drawChart);
        }
    };
    xhttp.open("GET", "status.json", false);
    xhttp.send();

    function drawChart() {
        const data = google.visualization.arrayToDataTable(stats);

        const options = {
            title: 'Sumaryczny czas opóźnień na dany dzień',
            curveType: 'function',
            titleTextStyle: {color: '#FFF'},
            legend: {position: 'bottom', textStyle: {color: '#f39c12'}},
            colors: ['#0ce3ac'],
            backgroundColor: "#2b3e50",
            hAxis: {
                textStyle: {color: '#3498db'}
            },
            vAxis: {
                textStyle: {color: '#0ce3ac'}
            }
        };

        const chart = new google.visualization.LineChart(document.getElementById('lineChart'));
        chart.draw(data, options);
    }

});

app.controller('statsController', function ($scope, $rootScope, $http, $stateParams, $sce) {

    $scope.getData = function () {
        $http({
            method: 'GET',
            url: 'partials/' + $stateParams.stats + '.json'
        }).then(response => {
            const data = response.data;
            $scope.title = data.title;
            $scope.description = $sce.trustAsHtml(data.description);
            $scope.legend = data.legend;

            $http({
                method: 'POST',
                url: "/" + $stateParams.stats,
                headers: {"Content-Type": "application/json"},
                data: {from: $scope.flights.from, to: $scope.flights.to}
            }).then(response => {
                $scope.statsSrc = $rootScope.adaptChart(response.data, $scope.legend);
            });

            $http({
                method: 'POST',
                url: "/forecast/chart",
                headers: {"Content-Type": "application/json"},
                data: {from: $scope.flights.from, to: $scope.flights.to}
            }).then(response => {
                if (response) {
                    $scope.items = [["Data wylotu", "Względne opóźnienie", "Prognozowane względne opóźnienie"], ...response.data];
                }
            });

        });
    };
    $scope.getData();
    $scope.statsSrc = "";

    $http({
        method: 'GET',
        url: '/details'
    }).then(response => {
        $scope.details = "<br><br>";
        const data = response.data;
        for (let i in data)
            $scope.details += '<h3><span class="text-warning">' + i + ':</span> <span class="text-success">' + data[i] + '</h3>';
        $scope.details = $sce.trustAsHtml($scope.details);
    });

    $scope.flights = {
        from: new Date(2016, 0, 4),
        to: new Date(2016, 0, 14),
    };
});
