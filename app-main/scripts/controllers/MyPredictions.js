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
    $scope.txCollapsed=true;
    $scope.toggleTxCollapsed = function() {
        if (!$scope.donationsCollapsed){ $scope.toggleDonationsCollapsed() }
        $scope.txCollapsed = !$scope.txCollapsed;
    }
    $scope.donationsCollapsed=true;
    $scope.toggleDonationsCollapsed = function() {
        if (!$scope.txCollapsed){ $scope.toggleTxCollapsed() }
        $scope.donationsCollapsed = !$scope.donationsCollapsed;
    }

    console.log("loaded");
    $scope.balance = 0;
    predictionService.getPredictions(
        function(predictions) {
            $scope.predictions = predictions;
            console.log(predictions);
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
        $scope.txCollapsed = true;
        $scope.donationsCollapsed=true;
        $scope.forExcess = true;
        var index = $scope.predictions.excessShipments.indexOf(x);
        $scope.offering = $scope.predictions.excessShipments[index];
        $scope.inputQuantity = $scope.offering.offerQuantity;
        $scope.inputQuantityDonate = $scope.offering.offerQuantity;
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
        $scope.txCollapsed = true;
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

    $scope.excessDonate = function(excessShipment) {
        excessShipment.price = 0; // Default price is set to 0 for donations
        excessShipment.offerQuantity = $scope.inputQuantityDonate;
        console.log("printing offer");
        console.log(excessShipment);
        txService.excessOffer(excessShipment, function(response) {
            $('.modal-backdrop').remove();
            $state.go('dashboard.Donations', {txSent: true, txDetails: excessShipment.name });
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

    $scope.getDisposalCellColour = function(colNumber) {
        var colour;
        var indexCol = $scope.colNumbers.indexOf(colNumber);
        if (indexCol != -1){
            colour = $scope.colColours[indexCol].background;
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

    $scope.formatColumn = function(colNumber) {
        var indexCol = $scope.colNumbers.indexOf(colNumber);
        if (indexCol != -1){
            return $scope.colColours[indexCol]
        }
     };

    // Hide any offer quantities below the cutoff
    $scope.isWithinCutOff = function(qty,isShortage){
        var cutoffMatrix = [ 100, 50 ]; // [ Excess, Shortage ]
        var cutoff = cutoffMatrix[isShortage];
        var withinCutoff = true;
        if (qty < cutoff){
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

    $scope.viewChart = function(shortOrExce, predictedItem) {
        $scope.predictedItem = predictedItem;
        $scope.getColumns(shortOrExce, predictedItem);
        console.log(predictedItem.name);
        var productName = predictedItem.name;
        predictionService.getChart(productName, function(chart, demand) {
            $scope.chart = chart;
            $scope.demand = demand;
        })
    };

    $scope.getColumns = function(shortOrExce, predictedItem){
        var colNumbers = [];
        var colColours = [];
        if (shortOrExce == 'excessShipments'){
            $scope.predictForExcess = true;
            for (var i = 0; i < $scope.predictions.excessShipments.length; i++) {
                var excessShipment = $scope.predictions.excessShipments[i];
                if (excessShipment.name == predictedItem.name && ($scope.isWithinCutOff(excessShipment.offerQuantity,0)).withinCutoff){
                    colNumbers.push($scope.convertToColNumber(excessShipment.expiry));
                    colColours.push($scope.getColour(shortOrExce, excessShipment));
                }
            }
        }
        else {
            $scope.predictForExcess = false;
            for (var i = 0; i < $scope.predictions.stockShortages.length; i++) {
                var stockShortage = $scope.predictions.stockShortages[i];
                if (stockShortage.name == predictedItem.name && $scope.isWithinCutOff(stockShortage.offerQuantity,1)){
                    colNumbers.push($scope.convertToColNumber(stockShortage.requestDate));
                    colColours.push($scope.getColour(shortOrExce, stockShortage));
                }
            }
        }
        $scope.colNumbers = colNumbers;
        $scope.colColours = colColours;
    };

    $scope.convertToColNumber = function(predictedDate){
        var day = new Date(predictedDate);
        return (day.getDate() - 7);
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
