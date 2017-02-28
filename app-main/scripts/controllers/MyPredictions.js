'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
  .controller('PredictCtrl', ['$interval', '$scope', 'txService', 'predictionService', function($interval, $scope, txService, predictionService) {

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
    )

    $scope.shortageOffer = function(index) {
        txService.shortageOffer($scope.predictions.stockShortages[index], function(response) {})
    }

    $scope.excessOffer = function(index) {
        txService.excessOffer($scope.predictions.excessShipments[index], function(response) {})
    }

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
    }

    var reloadData = function() {
        txService.getEther(function(balance) {
            $scope.balance = balance;
        })
    }

    $interval(function() {
        reloadData();
    }, 5000)
    reloadData();

    $scope.viewChart = function(productName) {
        console.log(productName);
        txService.getEther(function (data) {
            console.log(data)
        })
    }
  }]);
