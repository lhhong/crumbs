'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RequestsCtrl
 * @description
 * # RequestsCtrl
 */
angular.module('sbAdminApp')
  .controller('InProgressCtrl', ['$scope', '$interval', 'txService', function($scope, $interval, txService) {
    console.log("loaded");
    $scope.balance = 0;

    var reloadData = function() {
        txService.getEther(function(balance) {
            $scope.balance = balance;
        })
        txService.getTransactions(function(txs) {
            $scope.txs = txs;
            for (var i = 0; i<txs.pendingOffers.length; i++) {
                txs.pendingOffers[i].pending = true;
            }
            $scope.offers = txs.pendingOffers.concat(txs.successfulOffers);

            for (var i = 0; i<txs.pendingAccepts.length; i++) {
                txs.pendingAccepts[i].pending = true;
            }
            $scope.accepts = txs.pendingAccepts.concat(txs.successfulAccepts);

            for (var i = 0; i<txs.pendingAgrees.length; i++) {
                txs.pendingAgrees[i].agreeing = true;
            }
            $scope.agrees = txs.pendingAgrees.concat(txs.offersAccepted);
            $scope.reloaded = true;
        }, function() {
            //add mock data here when server not running
            $scope.offers = []; //Offers you made
            $scope.accepts = []; //Offers that you accepted and waiting for other party to agree
            $scope.agrees = []; //Offers accepted waiting for you to agree, or agreed but not included in block chain
            $scope.reloaded = true;
        })
    }

    $interval(function() {
        reloadData();
    }, 5000)
    reloadData();

    $scope.getBackground = function(pending) {
        if (pending) {
            return {
                background: '#ffb888'
            }
        }
    }

    $scope.agree = function(uuid) {
        txService.agree(uuid, function(response) {
            console.log("Agree sent");
        })
        $scope.reloaded = false;
    }
  }]);
