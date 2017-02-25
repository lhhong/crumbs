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

            $scope.predictions = {stockShortages:  [{
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
                //TODO add default mock data
        }
    )

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

    $scope.viewChart = function(productName) {
        console.log(productName);
        txService.getEther(function (data) {
            console.log(data)
        })
        var offer = {
            price: 20,
            item: "dasdsf",
            quantity: 100,
            expiry: 1234523452,
            isSell: true,
            txDate: 274523454543
        }
        /*txService.sendOffer(offer, function(data) {
            console.log(data)
            if (data) {
                //codes when transaction is included
            }
        })*/
    }
  }]);
