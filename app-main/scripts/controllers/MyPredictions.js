'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
  .controller('PredictCtrl', ['$interval', '$timeout', '$scope', 'txService', 'predictionService', function($interval, $timeout, $scope, txService, predictionService) {

    console.log("loaded");
    $scope.balance = 0;
    predictionService.getPredictions(
        function(predictions) {
            $scope.predictions = predictions;
        },
        function() {
            $scope.predictions = {
                stockShortages:  [{
                  name : 'Apples',
                  quantity: -300,
                  percentExtra: -12,
                  requestDate : 1492503145123,
                  price : 4,
                  urgencyLevel: "red"
                },
                {
                  name : 'Oranges',
                  quantity: -200,
                  percentExtra: -12,
                  requestDate : 1492403145123,
                  price : 5,
                  urgencyLevel: "orange"
                }
                ],
                excessShipments: [{
                  name : 'Oranges',
                  quantity: 450,
                  percentExtra: 12,
                  expiry : 1492603145123,
                  price : 5,
                  urgencyLevel: "orange"
                }]
            };
        }
    );

    $scope.excessViewOffer = function(index) {
        $scope.forExcess = true;
        $scope.offering = $scope.predictions.excessShipments[index];
        predictionService.excessViewOffers($scope.predictions.excessShipments[index],
            function(response) {
                $scope.offers = response;
            }, function(response) {
                console.log("server error");
                $scope.offers = [{price: 567}, {price: 22}];
            });
    }

    $scope.shortageViewOffer = function(index) {
        $scope.forExcess = false;
        $scope.offering = $scope.predictions.stockShortages[index];
        predictionService.shortageViewOffers($scope.predictions.stockShortages[index],
            function(response) {
                $scope.offers = response;
            }, function(response) {
                console.log("server error");
                $scope.offers = [{price: 567}, {price: 600}];
            });
    }

    $scope.excessViewPrediction = function(index) {
        $scope.predictedItem = $scope.predictions.excessShipments[index];
    }
    $scope.shortageViewPrediction = function(index) {
        $scope.predictedItem = $scope.predictions.stockShortages[index];
    }

    $scope.acceptOffer = function(index) {
        txService.accept($scope.offers[index], function(response) {});
    };

    $scope.shortageOffer = function(stockShortage) {
        txService.shortageOffer(stockShortage, function(response) {})
    };

    $scope.excessOffer = function(excessShipment) {
        console.log("printing offer");
        console.log(excessShipment);
        txService.excessOffer(excessShipment, function(response) {})
    };

    $scope.getColour = function(shortOrExce, index) {
        var code = $scope.predictions[shortOrExce][index].urgencyLevel;
        var colour;
        if (code == "red") {
            colour = '#ff8888'
        }
        if (code == "orange") {
            colour = '#ffb888'
        }
        if (code == "yellow") {
            colour = '#ffff88'
        }

        return {
            background: colour
        }
    };

    var reloadData = function() {
        txService.getEther(function(balance) {
            $scope.balance = balance;
        })
    };

    $interval(function() {
        reloadData();
    }, 5000);
    reloadData();

    $scope.viewChart = function(productName) {
        console.log(productName);
        txService.getEther(function (data) {
            console.log(data)
        })
    };
    $scope.salesData = {
	    labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
	    series: ['Series A'],
	    data: [
	      [65, 59, 80, 81, 56, 55, 40]
	    ],
	    onClick: function (points, evt) {
	      console.log(points, evt);
	    }
    };

    $('#PredictionModal').on('shown.bs.modal', function (event) {

        var button = $(event.relatedTarget);
        var modal = $(this);
        var canvas = modal.find('.modal-body canvas');

        // Chart initialiser
        var ctx = canvas[0].getContext("2d");
        var chart = new Chart(ctx).Line({
            labels: ["January", "February", "March", "April", "May"],
            datasets: [
                {
                    fillColor: "rgba(190,144,212,0.2)",
                    strokeColor: "rgba(190,144,212,1)",
                    pointColor: "rgba(190,144,212,1)",
                    pointStrokeColor: "#fff",
                    pointHighlightFill: "#fff",
                    pointHighlightStroke: "rgba(220,220,220,1)",
                    data: [65, 59, 80, 81, 56]
                },
            ]
        }, {});
    });

  }]);
