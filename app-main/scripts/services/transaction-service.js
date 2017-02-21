angular.module("sbAdminApp").factory('txService', ['$http', '$timeout', function($http, $timeout) {

    var baseUrl = 'http://localhost:8080/'

    var sendOffer = function(offer, callback) {
        $http({
            method: 'POST',
            url: baseUrl + 'offer',
            headers: {
                'Content-Type': 'application/json'
            },
            data: offer
        }).success(function(response) {
            if (callback) {
                if (response) console.log("offer included in blockchain");
                else console.log("check again later")
                callback(response)
            }
        })
    }

    var register = function(name, x, y) {
        $http({
            method: 'POST',
            url: baseUrl + 'register',
            headers: {
                'Content-Type': 'application/json'
            },
            data: {
                name: name,
                x: x,
                y: y
            }
        }).success(function(response) {
            console.log("registration request sent")
        }).error(function(response) {
            console.log("server error, code = " + response.status)
        })
    }

    var getEther = function(callback) {
        $http({
            method: 'GET',
            url: baseUrl + 'getBalance',
        }).success(function(response) {
            if (callback) {
                callback(response)
            }
        }).error(function(response) {
            console.log("server error, code = " + response.status)
        })
    }

    return {
        sendOffer: sendOffer,
        getEther: getEther
    }
}]);
