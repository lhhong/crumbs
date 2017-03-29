'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RequestsCtrl
 * @description
 * # RequestsCtrl
 */
angular.module('sbAdminApp')
  .controller('MarketCtrl', ['$state', '$interval', '$timeout', '$scope', 'txService', 'predictionService', function($state, $interval, $timeout, $scope, txService, predictionService) {
    console.log("loaded");
    $scope.balance = 0;

    var reloadData = function() {
        txService.getEther(function(balance) {
            $scope.balance = balance;
        });
        txService.getAvailTransactions(function(txs) {
            var salesTx = [];
            var purchasesTx = [];
            for (var i = 0; i<txs.length; i++) {
                if ((txs[i]).sell){
                    salesTx.push(txs[i]);
                }
                else{
                    purchasesTx.push(txs[i]);
                }
            }
            $scope.salesTx = salesTx;
            $scope.purchasesTx = purchasesTx;

            $scope.salesTx = $scope.salesTx.filter($scope.filterOutDonations);
            $scope.purchasesTx = $scope.purchasesTx.filter($scope.filterOutDonations);
        });
    }

    $interval(function() {
        reloadData();
    }, 3000)
    reloadData();

    $scope.viewOffer = function(x, isSell) {
        $scope.txViewed = x;
        $scope.isSell = isSell;
        if (isSell){
            $scope.date = new Date($scope.txViewed.txDate);
        }
        else{
            $scope.date = new Date($scope.txViewed.expiry);
        }
        txService.getTransportPrice(x,function(transportPrice) {
            $scope.transportPrice = transportPrice;
        })
    };

    $scope.acceptOffer = function(txViewed) {
        txViewed.transportPrice = 200;
        if ($scope.isSell){
            txViewed.expiry = $scope.date.getTime();
        }
        else{
            txViewed.txDate = $scope.date.getTime();
        }
        console.log(txViewed);
        txService.accept(txViewed, function(response) {
            $('.modal-backdrop').remove();
            if ($scope.isSell) {
                $state.go('dashboard.InProgressSelling');
            }
            else {
                $state.go('dashboard.InProgressBuying');
            }
        }, function () {
            $scope.alert = true;
        })
        $('.modal-backdrop').remove();
    };

    $scope.isSomeoneElseSelling = function(x){
        var isSell = false;
        if ( x.sender!= null && x.accepter == null && x.sell ){
            isSell = true;
        }
        return { isSell }
    };

    $scope.isSomeoneElseBuying = function(x){
        var isBuy = false;
        if ( x.sender!= null && x.accepter == null && !x.sell ){
            isBuy = true;
        }
        return { isBuy }
    };

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

  $scope.filterOutDonations = function(item){
    if (item.price == 0){
      return false;
    }
    return true;
  };

  $scope.alert = false;

  $scope.closeAlert = function(index) {
    $scope.alert = false;
  };

  $('#txOnMarketModal').on('show.bs.modal', function (event) {
      $(this).find('.modal-dialog').css({width:'60%',
                                 height:'40%',
                                'max-height':'80%'});
  });

  }]);
