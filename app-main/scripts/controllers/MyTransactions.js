'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
  .controller('TransactionCtrl', ['$scope', 'txService' ,function($scope, txService) {
    console.log("loaded");
    $scope.bought = [{
      'storename' : 'NTUC',
      'product': 'Apples',
      'qty': 12,
      'price': 120
    },
    {
      'storename' : 'Giant',
      'product': 'Oranges',
      'qty': 21,
      'price': 120.09
    }
    ]
  }]);
