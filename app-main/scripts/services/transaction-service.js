angular.module("sbAdminApp").factory('txService', ['$http', '$timeout', function($http, $timeout) {

    var baseUrl = 'http://localhost:8080/'

    var accept = function(tx, callback) {
        $http({
            method: 'POST',
            url: baseUrl + 'accept',
            headers: {
                'Content-Type': 'application/json'
            },
            data: tx
        }).success(function(response) {
            if (callback) {
                callback(response)
            }
        })
    }

    var excessOffer = function(offer, callback) {
        $http({
            method: 'POST',
            url: baseUrl + 'offer_excess',
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

    var shortageOffer = function(offer, callback) {
        $http({
            method: 'POST',
            url: baseUrl + 'offer_shortage',
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

    var getSold = function(callback, failureCallback) {
        $http({
            method: 'GET',
            url: baseUrl + "sold_tx"
        }).success(function(response) {
            if (callback) {
                callback(response);
            }
        }).error(function(response, status)  {
            console.log("server error, code = " + status)
            if (failureCallback) {
                failureCallback();
            }
        })
    }

    var getBought = function(callback, failureCallback) {
        $http({
            method: 'GET',
            url: baseUrl + "bought_tx"
        }).success(function(response) {
            if (callback) {
                callback(response);
            }
        }).error(function(response, status)  {
            console.log("server error, code = " + status)
            if (failureCallback) {
                failureCallback();
            }
        })
    }

    var getTransactions = function(callback, failureCallback) {
        $http({
            method: 'GET',
            url: baseUrl + "all_tx"
        }).success(function(response) {
            if (callback) {
                callback(response);
            }
        }).error(function(response, status)  {
            console.log("server error, code = " + status)
            if (failureCallback) {
                failureCallback();
            }
        })
    }

    var agree = function(uuid, callback) {
        $http({
            method: 'GET',
            url: baseUrl + "agree",
            headers: {
                'Content-Type': 'application/json'
            },
            data: uuid
        }).success(function(response) {
            if (callback) {
                callback(response)
            }
        })
    }

    return {
        accept: accept,
        register: register,
        shortageOffer: shortageOffer,
        excessOffer: excessOffer,
        getEther: getEther,
        getTransactions: getTransactions,
        getBought: getBought,
        getSold: getSold,
        agree: agree
    }
}]);
