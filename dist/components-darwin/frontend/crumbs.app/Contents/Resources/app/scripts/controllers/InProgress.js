'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RequestsCtrl
 * @description
 * # RequestsCtrl
 */
angular.module('sbAdminApp')
  .controller('InProgressCtrl', ['$state', '$scope', '$interval', 'txService', '$stateParams', function($state, $scope, $interval, txService, $stateParams) {
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
            $scope.agrees = txs.offersAccepted;

            for (var i = 0; i<txs.offersAccepted.length; i++) {
                txs.offersAccepted[i].agreeing = true;
            }

            $scope.offers = $scope.offers.concat($scope.agrees);

            // Filter out donations
            $scope.accepts = $scope.accepts.filter($scope.filterOutDonations);
            $scope.agrees = $scope.agrees.filter($scope.filterOutDonations);
            $scope.offers = $scope.offers.filter($scope.filterOutDonations);
            $scope.reloaded = true;

        })
        $scope.reloaded = true;
    };

    $scope.filterOutDonations = function(item){
        if (item.price == 0){
            return false;
        }
        return true;
    };

    $scope.txView = {};

    $scope.viewTx = function(tx) {
        $scope.txView = tx;
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

    $scope.alert = false;

    $scope.closeAlert = function(index) {
        $scope.alert = false;
    };

    $scope.txAlert = $stateParams.txSent;
    $scope.txDetails = $stateParams.txDetails;

    $scope.closeTxAlert = function(index) {
        $scope.txAlert = false;
    };

    $scope.agree = function(uuid) {
        txService.agree(uuid, function(response) {
            console.log("Agree sent");
            $scope.reloaded = false;
            $('.modal-backdrop').remove();
            $state.go('dashboard.Completed', {txSent: true, txDetails: $scope.txView.item });
        }, function() {
            $scope.alert = true;
        })
    };
    $('#ViewRequestModal').on('show.bs.modal', function (event) {
            $(this).find('.modal-dialog').css({width:'60%',
                                       height:'40%',
                                      'max-height':'80%'});
    });
    $('#ViewOfferModal').on('show.bs.modal', function (event) {
            $(this).find('.modal-dialog').css({width:'60%',
                                       height:'40%',
                                      'max-height':'80%'});
    });
    $('#ViewDetailsModal1').on('show.bs.modal', function (event) {
            $(this).find('.modal-dialog').css({width:'60%',
                                       height:'40%',
                                      'max-height':'80%'});
    });
    $('#ViewDetailsModal2').on('show.bs.modal', function (event) {
            $(this).find('.modal-dialog').css({width:'60%',
                                       height:'40%',
                                      'max-height':'80%'});
    });
  }]);
