'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
  .controller('PredictCtrl', function($scope,$position) {

    console.log("loaded");
    $scope.predictions = [{
      'SKU' : "asdasdas",
      Product: "afdad"
    }]
  });
