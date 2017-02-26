'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:BoughtCtrl
 * @description
 * # BoughtCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
  .controller('BoughtCtrl', ['$scope', '$interval', 'txService' ,function($scope, $interval, txService) {
    console.log("loaded");
    $scope.balance = 0;

    var reloadData = function() {
        txService.getEther(function(balance) {
            $scope.balance = balance;
        })
        txService.getBought(function(txs) {
            $scope.bought = txs;
        }, function() {
            $scope.bought = [];
        })
    }

    $interval(function() {
        reloadData();
    }, 5000)

    $scope.bought = [{
      'storename' : 'NTUC',
      'product': 'Apples',
      'qty': 12,
      'price': 120,
      'tdate': 123145123,
      'ddate': 123145123
    },
    {
      'storename' : 'Giant',
      'product': 'Oranges',
      'qty': 21,
      'price': 120.09,
      'tdate': 123145123,
      'ddate': 123145123
    }
    ]
  }]);
