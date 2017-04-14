angular.module("sbAdminApp").factory('predictionService', ['$http', '$timeout', function($http, $timeout) {

    var baseUrl = 'http://localhost:8080/'

    var getQuantity = function(successCallback, errorCallback) {
        $http({
            method: 'GET',
            url: baseUrl + 'prediction_qty',
        }).then(function(response) {
            if (successCallback) {
                successCallback(response.data);
            }
        },function(response) {
            console.log("server error, code = " + response.status);
            errorCallback();
        })
    }

    var getPredictions = function(successCallback, errorCallback) {
        $http({
            method: 'GET',
            url: baseUrl + 'predictions',
        }).then(function(response) {
            if (successCallback) {
                successCallback(response.data);
            }
        },function(response) {
            console.log("server error, code = " + response.status);
            errorCallback();
        })
    }

    var shortageViewOffers = function(remStockVM, callback, errorCallback) {
        $http({
            method: 'POST',
            url: baseUrl + "matchingTxForShortage",
            headers: {
                'Content-Type': 'application/json'
            },
            data: remStockVM
        }).then(function(response) {
            if (callback) {
                callback(response.data)
            }
        },function(response)  {
            //console.log("server error, code = " + status)
            if (errorCallback) {
                errorCallback();
            }
        })
    }

    var getChart = function(product, callback, errorCallback) {
        $http({
            method: 'POST',
            url: baseUrl + "product_table",
            headers: {
                'Content-Type': 'application/json'
            },
            data: product
        }).then(function(response) {
            if (callback) {
                console.log(response.data);
                callback(collateChart(response.data), response.data.demand)
            }
        },function(response)  {
            //console.log("server error, code = " + status)
            if (errorCallback) {
                errorCallback();
            }
        })
    }

    var collateChart = function(productSeries) {
        var chartInfo = [];
        for (var i = 0; i < productSeries.demand.length; i++) {
            var oneDay = {
                disposal: null,
                stock: null,
                startingInventory: null,
                demand: null,
                endingInventory: null
            };
            oneDay.disposal = productSeries.disposal[i];
            oneDay.stock = productSeries.stock[i];
            oneDay.startingInventory = productSeries.startingInventory[i];
            oneDay.demand = productSeries.demand[i];
            oneDay.endingInventory = productSeries.endingInventory[i];
            chartInfo.push(oneDay);
        }
        return chartInfo;
    }

    var excessViewOffers = function(exceShipVM, callback, errorCallback) {
        $http({
            method: 'POST',
            url: baseUrl + "matchingTxForExcess",
            headers: {
                'Content-Type': 'application/json'
            },
            data: exceShipVM
        }).then(function(response) {
            if (callback) {
                callback(response.data)
            }
        },function(response)  {
            //console.log("server error, code = " + status)
            if (errorCallback) {
                errorCallback();
            }
        })
    }

    return {
        getChart: getChart,
        getQuantity: getQuantity,
        getPredictions: getPredictions,
        excessViewOffers: excessViewOffers,
        shortageViewOffers: shortageViewOffers
    }
}]);
