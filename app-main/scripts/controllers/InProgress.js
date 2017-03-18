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
            for (var i = 0; i<txs.offersAccepted.length; i++) {
                txs.offersAccepted[i].agreeing = true;
            }
            $scope.agrees = txs.pendingAgrees.concat(txs.offersAccepted);
            $scope.offers = $scope.offers.concat($scope.agrees);
            $scope.reloaded = true;
        }, function() {
            //add mock data here when server not running
            $scope.offers = [{
                sell: true,
                item: "Orange",
                quantity: 100,
                price: 2,
                accepter: {
                    name: "ShengSiong"
                },
                agreeing: true,
                transportCost: 123
            },{
                sell: true,
                item: "Mango",
                quantity: 120,
                price: 2,
            },{
                sell: true,
                item: "Banana",
                quantity: 200,
                price: 2,
            },{
                sell: false,
                item: "Mango",
                quantity: 110,
                price: 3,
            },{
                sell: false,
                item: "Apple",
                quantity: 100,
                price: 2,
                accepter: {
                    name: "ShengSiong"
                },
                agreeing: true,
                transportCost: 123
            }]; //Offers you made

            $scope.accepts = [{
                sell: true,
                item: "Mango",
                quantity: 120,
                price: 2,
            }
            ]; //Offers that you accepted and waiting for other party to agree
            $scope.agrees = []; //Offers accepted waiting for you to agree, or agreed but not included in block chain

        })
        $scope.reloaded = true;
    };

    $scope.txView = {};

    $scope.viewTx = function(tx) {
        $scope.txView = tx;
    };

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
    };

    $scope.alert = false;

    $scope.closeAlert = function(index) {
        $scope.alert = false;
    }

    $scope.agree = function(uuid) {
        txService.agree(uuid, function(response) {
            console.log("Agree sent");
        }, function() {
            $scope.alert = true;
        })
        $scope.reloaded = false;
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
