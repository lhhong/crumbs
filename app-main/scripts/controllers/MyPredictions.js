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
                  quantity: -3,
                  percentExtra: -12,
                  requestDate : 123145123,
                  price : 123145123,
                  urgencyLevel: "red"
                },
                {
                  name : 'Orange',
                  quantity: -3,
                  percentExtra: -12,
                  requestDate : 123145123,
                  price : 12323,
                  urgencyLevel: "orange"
                }
                ],
                excessShipments: [{
                  name : 'Orange',
                  quantity: 354,
                  percentExtra: 12,
                  expiry : 23153145123,
                  price : 1232,
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
                $scope.offers = [{price: 567}, {price: 23452}];
            });
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
    $scope.line = {
	    labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
	    series: ['Series A', 'Series B'],
	    data: [
	      [65, 59, 80, 81, 56, 55, 40],
	      [28, 48, 40, 19, 86, 27, 90]
	    ],
	    onClick: function (points, evt) {
	      console.log(points, evt);
	    }
    };
  }]);
