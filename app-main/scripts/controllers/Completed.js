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
        })
        txService.getBought(function(txs) {
            $scope.bought = txs;
        }, function() {
            $scope.bought = [
                {
                    accepter:{name:'NTUC'},
                    item: 'Apples',
                    quantity: 300,
                    price: 2,
                    txDate: 12315234522,
                    expiry: 143624624245
                },{
                    accepter:{name:'GIANT'},
                    item: 'Oranges',
                    quantity: 300,
                    price: 2,
                    txDate: 12315234522,
                    expiry: 143624624245
                }
            ];
        })
        txService.getSold(function(txs) {
            $scope.sold = txs;
        }, function() {
            $scope.sold = [
                {
                    accepter:{name:'NTUC'},
                    item: 'Apples',
                    quantity: 300,
                    price: 2,
                    txDate: 12315234522,
                    expiry: 143624624245
                },{
                    accepter:{name:'GIANT'},
                    item: 'Oranges',
                    quantity: 300,
                    price: 2,
                    txDate: 12315234522,
                    expiry: 143624624245
                }
            ];
        })
    }

    $interval(function() {
        reloadData();
    }, 5000)
    reloadData();

  }]);
