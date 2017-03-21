'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
  .controller('PredictCtrl', ['$state', '$interval', '$timeout', '$scope', 'txService', 'predictionService', function($state, $interval, $timeout, $scope, txService, predictionService) {

    $scope.testError = function() {
        console.log("testing Error");
        errorService.displayError();
    }
    $scope.collapsed=true;
    $scope.toggleCollapsed = function() {
        $scope.collapsed = !$scope.collapsed;
    }
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
                  offerQuantity: 300,
                  percentExtra: -12,
                  requestDate : 1492503145123,
                  price : 4,
                  urgencyLevel: "red"
                },
                {
                  name : 'Oranges',
                  offerQuantity: 200,
                  percentExtra: -12,
                  requestDate : 1492403145123,
                  price : 5,
                  urgencyLevel: "orange"
                }
                ],
                excessShipments: [{
                  name : 'Marigold Milk',
                  quantity: 100,
                  percentExtra: 8,
                  expiry : 1492603145123,
                  price : 10,
                  urgencyLevel: "orange"
                },
                {
                  name : 'Parmesan Cheese',
                  quantity: 200,
                  percentExtra: 10,
                  expiry : 1492703145123,
                  price : 5,
                  urgencyLevel: "yellow"
                },
                {
                  name : 'Corn',
                  quantity: 200,
                  percentExtra: 10,
                  expiry : 1492703145123,
                  price : 5,
                  urgencyLevel: "yellow"
                }
                ]
            };
        }
    );

    $scope.excessViewOffer = function(x) {
        $scope.collapsed = true;
        $scope.forExcess = true;
        var index = $scope.predictions.excessShipments.indexOf(x);
        $scope.offering = $scope.predictions.excessShipments[index];
        $scope.inputQuantity = $scope.offering.offerQuantity;
        $scope.inputPrice = $scope.offering.price * $scope.offering.offerQuantity;
        predictionService.excessViewOffers($scope.predictions.excessShipments[index],
            function(response) {
                $scope.offers = response;
            }, function(response) {
                console.log("server error");
                $scope.offers = [{price: 567}, {price: 22}];
            });
    }

    $scope.shortageViewOffer = function(x) {
        $scope.collapsed = true;
        $scope.forExcess = false;
        var index = $scope.predictions.stockShortages.indexOf(x);
        $scope.offering = $scope.predictions.stockShortages[index];
        $scope.inputQuantity = $scope.offering.offerQuantity;
        $scope.inputPrice = $scope.offering.price * $scope.offering.offerQuantity;
        predictionService.shortageViewOffers($scope.predictions.stockShortages[index],
            function(response) {
                $scope.offers = response;
            }, function(response) {
                console.log("server error");
                $scope.offers = [{price: 567}, {price: 600}];
            });
    };

    $scope.acceptOffer = function(index, selling) {
        txService.accept($scope.offers[index], function(response) {
            $('.modal-backdrop').remove();
            if (selling) {
                $state.go('dashboard.InProgressSelling');
            }
            else {
                $state.go('dashboard.InProgressBuying');
            }
        }, function () {
            $scope.alert = true;
        })
        $('.modal-backdrop').remove();
    };

    $scope.alert = false;

    $scope.closeAlert = function(index) {
        $scope.alert = false;
    }

    $scope.shortageOffer = function(stockShortage) {
        stockShortage.price = $scope.inputPrice;
        stockShortage.offerQuantity = $scope.inputQuantity;
        console.log("printing offer");
        console.log(stockShortage);
        txService.shortageOffer(stockShortage, function(response) {
            $('.modal-backdrop').remove();
            $state.go('dashboard.InProgressBuying', {txSent: true, txDetails: stockShortage.name });
        }, function () {
            $scope.alert = true;
        })
    };

    $scope.excessOffer = function(excessShipment) {
        excessShipment.price = $scope.inputPrice;
        excessShipment.offerQuantity = $scope.inputQuantity;
        console.log("printing offer");
        console.log(excessShipment);
        txService.excessOffer(excessShipment, function(response) {
            $('.modal-backdrop').remove();
            $state.go('dashboard.InProgressSelling', {txSent: true, txDetails: excessShipment.name });
        }, function () {
            $scope.alert = true;
        })
    };

    $scope.getColour = function(shortOrExce, x) {
        var index = $scope.predictions[shortOrExce].indexOf(x);
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

    $scope.getDisposalCellColour = function(colNumber,columnNumberToHighlight) {
        var colour;
        if (colNumber == columnNumberToHighlight) {
            colour = '#FF9191'
        }
        else {
            colour = '#ffffff'
        }
        return {
            background: colour
        }
    };

    $scope.getMonthCellColour = function(n){
        var colour = '#ECECEE';
        return {
            background: colour
        }
    };

    $scope.replaceWithDashes = function(number) {
        var newString;
        newString = number;
        if (number == 0) { newString = '-' }
        return { newString }
     };

    $scope.formatDisposal = function(number) {
        var newString;
        if (number == 0) {
            newString = '-';
        }
        else {
            newString = '(' + number + ')';
        }
        return { newString }
     };

    $scope.formatColumn = function(colNumber,colNumberToHighlight) {
        var colour;
        if (colNumber == colNumberToHighlight) {
            colour = '#FF9191'
        }
        return {
            background: colour
        }
     };

    $scope.isWithinCutOff = function(qty){
        var withinCutoff = true;
        if (qty < 60){
            withinCutoff = false;
        }
        return {withinCutoff}
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

    // To control the size of the modals
    $('#ViewMarketModal').on('show.bs.modal', function (event) {
            $(this).find('.modal-dialog').css({width:'60%',
                                       height:'40%',
                                      'max-height':'80%'});
    });

    $('#PredictionModal').on('show.bs.modal', function (event) {
            $(this).find('.modal-dialog').css({width:'80%',
                                       height:'50%',
                                      'max-height':'80%'});
    });

    // Plotting functions for predictions chart
    $scope.chart = [];

    $scope.viewChart = function(predictedItem,isSell) {
        $scope.predictedItem = predictedItem;
        if (isSell){ var day = new Date(predictedItem.expiry); }
        else { var day = new Date(predictedItem.requestDate); }
        $scope.columnNumberToHighlight = day.getDate()-7;
        console.log(predictedItem.name);
        var productName = predictedItem.name;
        predictionService.getChart(productName, function(chart, demand) {
            $scope.chart = chart;
            $scope.demand = demand;
        })
    };

    $('#PredictionModal').on('shown.bs.modal', function (event) {
        var modal = $(this);
        var canvas = modal.find('.modal-body canvas');

        // Prediction Chart
        var ctx = canvas[0].getContext("2d");
        ctx.canvas.width = 300;
        ctx.canvas.height = 80;
        var predictions = $scope.demand;
        var paddingLeft = [null,null,null,null,null,null,null];
        var predictionChart = new Chart(ctx).Line({
            labels: ["Apr 7", "", "Apr 9", "", "Apr 11", "", "Apr 13", "", "Apr 15", "", "Apr 17", "", "Apr 19", "", "Apr 21", "", "Apr 23", "", "Apr 25", "", "Apr 27"],
            datasets: [
                {
                // plotting past demand
                    label: "Past sales",
                    fillColor: "rgba(190,144,212,0.2)",
                    strokeColor: "rgba(190,144,212,1)",
                    pointColor: "rgba(190,144,212,1)",
                    pointStrokeColor: "#fff",
                    pointHighlightFill: "#fff",
                    pointHighlightStroke: "rgba(220,220,220,1)",
                    data: (predictions.slice(0, 8))
                },
                {
                // plotting future demand
                    label: "Predicted sales",
                    fillColor: "rgba(10,144,212,0.2)",
                    strokeColor: "rgba(10,144,212,1)",
                    pointColor: "rgba(10,144,212,1)",
                    pointStrokeColor: "#fff",
                    pointHighlightFill: "#fff",
                    pointHighlightStroke: "rgba(220,220,220,1)",
                    data: paddingLeft.concat(predictions.slice(7))
                }
            ]
        });

    });

    // Sorting functionality
    // For Excess Table
    $scope.sortByUrgencyEx = function () {
      $scope.sortColumnEx = null;
      $scope.reverseSortEx = false;
    };

    $scope.sortColumnEx = null;
    $scope.reverseSortEx = false;

    $scope.sortDataEx = function (column) {
    $scope.reverseSortEx = ($scope.sortColumnEx == column) ? !$scope.reverseSortEx : false;
    $scope.sortColumnEx = column;
    };

    $scope.getSortClassEx = function (column) {
    if ($scope.sortColumnEx == column) {
      return $scope.reverseSortEx ? 'arrow-down' : 'arrow-up'
    }
    };

    // For Shortage Table
    $scope.sortByUrgencySh = function () {
        $scope.sortColumnSh = null;
        $scope.reverseSortSh = false;
    };

    $scope.sortColumnSh = null;
    $scope.reverseSortSh = false;

    $scope.sortDataSh = function (column) {
      $scope.reverseSortSh = ($scope.sortColumnSh == column) ? !$scope.reverseSortSh : false;
      $scope.sortColumnSh = column;
    };

    $scope.getSortClassSh = function (column) {
      if ($scope.sortColumnSh == column) {
        return $scope.reverseSortSh ? 'arrow-down' : 'arrow-up'
      }
    };

  }]);
