'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:SoldCtrl
 * @description
 * # SoldCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
  .controller('SoldCtrl', ['$scope', '$interval', 'txService' ,function($scope, $interval, txService) {
    console.log("loaded");
    $scope.balance = 0;

    var reloadData = function() {
        txService.getEther(function(balance) {
            $scope.balance = balance;
        })
        txService.getSold(function(txs) {
            $scope.sold = txs;
        }, function() {
            $scope.sold = [];
        })
    }

    $interval(function() {
        reloadData();
    }, 5000)

    $scope.sold = [{
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
