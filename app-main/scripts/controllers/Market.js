'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RequestsCtrl
 * @description
 * # RequestsCtrl
 */
angular.module('sbAdminApp')
  .controller('MarketCtrl', ['$scope', '$interval', 'txService', function($scope, $interval, txService) {
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
      'location' : 'Bedok Mall',
      'product': 'Milk',
      'qty': 100,
      'expiryDate': 1492103145123,
      'price': 150,
      'transportCost': 60
    },
    {
      'storename' : 'Giant',
      'location' : 'Katong V',
      'product': 'Parmesan Cheese',
      'qty': 120,
      'expiryDate': 1492103145123,
      'price': 600,
      'transportCost': 77
    },
    {
      'storename' : 'Cold Storage',
      'location' : 'Tampines Mall',
      'product': 'Yoghurt',
      'qty': 200,
      'expiryDate': 1492103145123,
      'price': 350,
      'transportCost': 69
    }
    ],

    $scope.vegetable = [{
      'storename' : 'NTUC',
      'location' : 'Bedok Mall',
      'product': 'Carrots',
      'qty': 180,
      'expiryDate': 1492103145123,
      'price': 120,
      'transportCost': 60
    },
    {
      'storename' : 'Giant',
      'location' : 'Katong V',
      'product': 'Broccoli',
      'qty': 220,
      'expiryDate': 1492103145123,
      'price': 250,
      'transportCost': 77
    },
    {
      'storename' : 'NTUC',
      'location' : 'Bedok Mall',
      'product': 'Carrots',
      'qty': 200,
      'expiryDate': 1492103145123,
      'price': 100,
      'transportCost': 69
    }
    ],

    $scope.fruit = [{
      'storename' : 'Cold Storage',
      'location' : 'Tampines Mall',
      'product': 'Apples',
      'qty': 350,
      'expiryDate': 1492103145123,
      'price': 120,
      'transportCost': 60
    },
    {
      'storename' : 'Giant',
      'location' : 'Katong V',
      'product': 'Oranges',
      'qty': 250,
      'expiryDate': 1492103145123,
      'price': 125,
      'transportCost': 77
    },
    {
      'storename' : 'NTUC',
      'location' : 'Bedok Mall',
      'product': 'Dragon Fruit',
      'qty': 130,
      'expiryDate': 1492103145123,
      'price': 90,
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
