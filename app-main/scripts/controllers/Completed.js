'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:CompletedCtrl
 * @description
 * # CompletedCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
  .controller('CompletedCtrl', ['$scope', '$interval', 'txService' ,function($scope, $interval, txService) {
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
                if (txs.pendingAgrees[i].sell) {
                    $scope.tempSold.push(txs.pendingAgrees[i]);
                }
                else {
                    $scope.tempBought.push(txs.pendingAgrees[i]);
                }
            }
        });
        txService.getBought(function(txs) {
            $scope.bought = $scope.tempBought.concat(txs);
            $scope.bought = $scope.bought.filter($scope.filterOutDonations);
        }, function() {
            $scope.bought = [
                {
                    accepter:{name:'NTUC'},
                    item: 'Apples',
                    quantity: 300,
                    price: 2,
                    txDate: 1492003145123,
                    expiry: 1492503145123
                },{
                    accepter:{name:'Giant'},
                    item: 'Oranges',
                    quantity: 300,
                    price: 2,
                    txDate: 1491903145123,
                    expiry: 1492503145123
                }
            ];
        })
        txService.getSold(function(txs) {
            $scope.sold = $scope.tempSold.concat(txs);
            $scope.sold = $scope.sold.filter($scope.filterOutDonations);
        }, function() {
            $scope.sold = [
                {
                    accepter:{name:'NTUC'},
                    item: 'Apples',
                    quantity: 300,
                    price: 2,
                    txDate: 1491503145123,
                    expiry: 1492303145123
                },{
                    accepter:{name:'Cold Storage'},
                    item: 'Oranges',
                    quantity: 300,
                    price: 2,
                    txDate: 1491503145123,
                    expiry: 1492303145123
                }
            ];
        })
    }

    $scope.filterOutDonations = function(item){
        if (item.price == 0){
            return false;
        }
        return true;
    };

    $interval(function() {
        reloadData();
    }, 5000)
    reloadData();

  }]);
