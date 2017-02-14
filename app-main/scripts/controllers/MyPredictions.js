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
      SKU : "asdasdas",
      Product: "afdad",
	 date: 123145123
    }]
  }]);
