'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
  .controller('MainCtrl', ['$scope', '$position', '$interval', 'txService', function($scope, $position, $interval, txService) {
    console.log("main controller loaded");
    $scope.balance = 0;
    $scope.donut = {
    	labels: ["Download Sales", "In-Store Sales", "Mail-Order Sales"],
    	data: [300, 500, 100]
    };

    var reloadData = function() {
        txService.getEther(function(balance) {
            $scope.balance = balance;
        })
    }

    $interval(function() {
        reloadData();
    }, 5000)
  }]);
