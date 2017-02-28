'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:OffersCtrl
 * @description
 * # OffersCtrl
 */
angular.module('sbAdminApp')
  .controller('OffersCtrl', ['$scope', '$interval', 'txService', function($scope, $interval, txService) {
    console.log("loaded");
    $scope.balance = 0;
    var reloadData = function() {
        txService.getEther(function(balance) {
            $scope.balance = balance;
        })
    }

    $interval(function() {
        reloadData();
    }, 5000)

    $scope.dairy = [{
      'storename' : 'NTUC',
      'product': 'Milk',
      'qty': 12,
      'expiryDate': 123145123,
      'price': 120,
      'transportCost': 60
    },
    {
      'storename' : 'Giant',
      'product': 'Goat Cheese',
      'qty': 22,
      'expiryDate': 993145123,
      'price': 1200.08,
      'transportCost': 77
    },
    {
      'storename' : 'NTUC',
      'product': 'Yoghurt',
      'qty': 44,
      'expiryDate': 1003145123,
      'price': 28.99,
      'transportCost': 69
    }
    ],

    $scope.vegetable = [{
      'storename' : 'NTUC',
      'product': 'Cabbage',
      'qty': 12,
      'expiryDate': 123145123,
      'price': 120,
      'transportCost': 60
    },
    {
      'storename' : 'Giant',
      'product': 'Broccoli',
      'qty': 22,
      'expiryDate': 993145123,
      'price': 1200.08,
      'transportCost': 77
    },
    {
      'storename' : 'NTUC',
      'product': 'Carrots',
      'qty': 44,
      'expiryDate': 1003145123,
      'price': 28.99,
      'transportCost': 69
    }
    ],

    $scope.fruit = [{
      'storename' : 'NTUC',
      'product': 'Apples',
      'qty': 12,
      'expiryDate': 123145123,
      'price': 120,
      'transportCost': 60
    },
    {
      'storename' : 'Giant',
      'product': 'Oranges',
      'qty': 22,
      'expiryDate': 993145123,
      'price': 1200.08,
      'transportCost': 77
    },
    {
      'storename' : 'NTUC',
      'product': 'Dragon Fruit',
      'qty': 44,
      'expiryDate': 1003145123,
      'price': 28.99,
      'transportCost': 69
    }
    ];

  $scope.sortColumn = "name";
  $scope.reverseSort = false;

  $scope.sortData = function (column) {
    $scope.reverseSort = ($scope.sortColumn == column) ? !$scope.reverseSort : false;
    $scope.sortColumn = column;
  };

  $scope.getSortClass = function (column) {
    if ($scope.sortColumn == column) {
      return $scope.reverseSort ? 'arrow-down' : 'arrow-up'
    }
  };
  }]);
