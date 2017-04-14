'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:CompletedCtrl
 * @description
 * # CompletedCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
  .controller('CompletedCtrl', ['$scope', '$interval', 'txService', '$stateParams' ,function($scope, $interval, txService, $stateParams) {
    console.log("loaded");
    $scope.balance = 0;

    var reloadData = function() {
        txService.getEther(function(balance) {
            $scope.balance = balance;
        });
        $scope.tempBought = [];
        $scope.tempSold = [];
        txService.getTransactions(function(txs) {
            for (var i = 0; i<txs.pendingAgrees.length; i++) {
                txs.pendingAgrees[i].agreeing = true;
                txs.pendingAgrees[i].pending = true;
                if (txs.pendingAgrees[i].sell && txs.pendingAgrees[i].price != 0) {
                    $scope.tempSold.push(txs.pendingAgrees[i]);
                }
                else if (!txs.pendingAgrees[i].sell && txs.pendingAgrees[i].price != 0) {
                    $scope.tempBought.push(txs.pendingAgrees[i]);
                }
            }
        });
        txService.getBought(function(txs) {
            $scope.tempBought = $scope.tempBought.concat(txs);
            $scope.bought = $scope.tempBought.filter($scope.filterOutDonations);
        });
        txService.getSold(function(txs) {
            $scope.tempSold = $scope.tempSold.concat(txs);
            $scope.sold = $scope.tempSold.filter($scope.filterOutDonations);
        });
    }

    $scope.filterOutDonations = function(item){
        if (item.price == 0){
            return false;
        }
        return true;
    };

    $interval(function() {
        reloadData();
        console.log($scope.sold);
    }, 3000)
    reloadData();

    $scope.txAlert = $stateParams.txSent;
    $scope.txDetails = $stateParams.txDetails;

    $scope.closeTxAlert = function(index) {
        $scope.txAlert = false;
    };

  }]);
