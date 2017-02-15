'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
  .controller('MarketCtrl', ['$scope', function($scope) {
    console.log("loaded");
    $scope.selling = [{
      'storename' : 'NTUC',
      product: 'Apples',
      qty: 12,
      price: 120
    },
    {
      'storename' : 'Giant',
      product: 'Oranges',
      qty: 21,
      price: 120.09
    }
    ],
    $scope.request = [{
      
    }]
  }]);
