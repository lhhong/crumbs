'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RequestsCtrl
 * @description
 * # RequestsCtrl
 */
angular.module('sbAdminApp')
  .controller('DonationsCtrl', ['$scope', '$interval', 'txService', '$stateParams', function($scope, $interval, txService, $stateParams) {
    console.log("loaded");
    $scope.balance = 0;

    var reloadData = function() {
        txService.getEther(function(balance) {
            $scope.balance = balance;
        });
        $scope.tempDonationsCompleted = [];
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
            $scope.agrees = txs.offersAccepted;

            for (var i = 0; i<txs.offersAccepted.length; i++) {
                txs.offersAccepted[i].agreeing = true;
            }
            // For completed donations
            for (var i = 0; i<txs.pendingAgrees.length; i++) {
                txs.pendingAgrees[i].agreeing = true;
                txs.pendingAgrees[i].pending = true;
                if (txs.pendingAgrees[i].sell && txs.pendingAgrees[i].price == 0) {
                    $scope.tempDonationsCompleted.push(txs.pendingAgrees[i]);
                }
            }

            $scope.offers = $scope.offers.concat($scope.agrees);
            // Get donations
            $scope.donations = $scope.offers.filter($scope.getDonations);
            $scope.reloaded = true;

        });

        txService.getSold(function(txs) {
            $scope.donationsCompleted = $scope.tempDonationsCompleted.concat(txs);
            $scope.donationsCompleted = $scope.donationsCompleted.filter($scope.getDonations);
        });

        $scope.reloaded = true;
    };

    $scope.getDonations = function(item){
        if (item.price == 0){
            return true;
        }
        return false;
    };

    $interval(function() {
        reloadData();
    }, 3000)
    reloadData();

    $scope.getBackground = function(pending) {
        if (pending) {
            return {
                background: '#ffb888'
            }
        }
    };

    $scope.txAlert = $stateParams.txSent;
    $scope.txDetails = $stateParams.txDetails;

    $scope.closeTxAlert = function(index) {
        $scope.txAlert = false;
    };

    $scope.completeAlert = false;

    $scope.closeCompleteAlert = function(index) {
        $scope.completeAlert = false;
    };
    $scope.txName = "null";
    console.log($scope.txName);
    $scope.agree = function(uuid, item) {
        txService.agree(uuid, function(response) {
            console.log("Agree sent");
            $scope.reloaded = false;
            $scope.completeAlert = true;
        })
        $scope.txName = item;
        console.log(item);
        reloadData();
    };

  }]);
