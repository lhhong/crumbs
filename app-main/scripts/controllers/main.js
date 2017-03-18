'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
  .controller('MainCtrl', ['$scope', '$interval', 'txService', 'predictionService', function($scope, $interval, txService, predictionService) {
    console.log("main controller loaded");
    $scope.balance = 0;
    $scope.donut = {
    	labels: ["Download Sales", "In-Store Sales", "Mail-Order Sales"],
    	data: [300, 500, 100]
    };

    var reloadData = function() {
        txService.getEther(function(balance) {
            $scope.balance = balance;
        });
        txService.getTransactions(function(txs) {
            $scope.txs = txs;
            //count selling and buying
            var count = 0;
            for (var i = 0; i<txs.pendingAgrees.length; i++) {
                if (txs.pendingAgrees[i].sell){
                    count++;
                }
            }
            $scope.numOfSalesToApprove = count;
            $scope.numOfPurchasesToApprove = txs.pendingAgrees.length-count;
        });
        predictionService.getQuantity(function(predictQty) {
            if (predictQty.valid) {
                $scope.excess = predictQty.excess;
                $scope.shortage = predictQty.shortage;
                $scope.valid = true;
            }
            else {
                $scope.valid = false;
            }
        }, function() {
            $scope.numOfSalesToApprove = "0";
            $scope.numOfPurchasesToApprove = "0";
            $scope.excess = "4";
            $scope.shortage = "1";
        })
    }

    $interval(function() {
        reloadData();
    }, 5000);

    reloadData();
  }]);
