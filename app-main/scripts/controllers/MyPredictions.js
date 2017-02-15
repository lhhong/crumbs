'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
  .controller('PredictCtrl', ['$scope', function($scope) {

    console.log("loaded");
    $scope.predictions = [{
      'SKU' : '00123123',
      product: 'Apples',
      qty: 12,
      date: 123145123
    },
    {
      'SKU' : '00123144',
      product: 'Oranges',
      qty: 21,
      date: 893545365
    }

    ]
    $scope.viewChart = function(productName) {
        console.log(productName);
    }
  }]);
