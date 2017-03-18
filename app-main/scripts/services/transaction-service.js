angular.module("sbAdminApp").factory('txService', ['$http', '$timeout', function($http, $timeout) {

    var baseUrl = 'http://localhost:8080/'

    var accept = function(tx, callback, errorCallback) {
        $http({
            method: 'POST',
            url: baseUrl + 'accept',
            headers: {
                'Content-Type': 'application/json'
            },
            data: tx
        }).then(function(response) {
            if (callback) {
                callback(response.data)
            }
        },function(response) {
            if (response.status == 422) {
                if (errorCallback) errorCallback();
            }
            else {
                if (callback) {
                    callback(response.data)
                }
            }
        })
    }

    var excessOffer = function(offer, callback, errorCallback) {
        $http({
            method: 'POST',
            url: baseUrl + 'offer_excess',
            headers: {
                'Content-Type': 'application/json'
            },
            data: offer
        }).then(function(response) {
            if (callback) {
                if (response.data) console.log("offer included in blockchain");
                else console.log("check again later")
                callback(response.data)
            }
        },function(response) {
            if (response.status == 422) {
                if (errorCallback) errorCallback();
            }
            else {
                if (callback) {
                    callback(response.data)
                }
            }
        })
    }

    var shortageOffer = function(offer, callback, errorCallback) {
        $http({
            method: 'POST',
            url: baseUrl + 'offer_shortage',
            headers: {
                'Content-Type': 'application/json'
            },
            data: offer
        }).then(function(response) {
            if (callback) {
                if (response.data) console.log("offer included in blockchain");
                else console.log("check again later")
                callback(response.data)
            }
        },function(response) {
            if (response.status == 422) {
                if (errorCallback) errorCallback();
            }
            else {
                if (callback) {
                    callback(response.data)
                }
            }
        })
    }

    var register = function(name, x, y, errorCallback) {
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
        }).then(function(response) {
            console.log("registration request sent")
        },function(response) {
            if (response.status == 422) {
                if (errorCallback) errorCallback();
            }
        })
    }

    var getEther = function(callback) {
        $http({
            method: 'GET',
            url: baseUrl + 'getBalance',
        }).then(function(response) {
            if (callback) {
                callback(response.data)
            }
        },function(response) {
            console.log("server error, code = " + response.status)
        })
    }

    var getSold = function(callback, failureCallback) {
        $http({
            method: 'GET',
            url: baseUrl + "sold_tx"
        }).then(function(response) {
            if (callback) {
                callback(response.data);
            }
        },function(response)  {
            console.log("server error, code = " + response.status)
            if (failureCallback) {
                failureCallback();
            }
        })
    }

    var getBought = function(callback, failureCallback) {
        $http({
            method: 'GET',
            url: baseUrl + "bought_tx"
        }).then(function(response) {
            if (callback) {
                callback(response.data);
            }
        },function(response)  {
            console.log("server error, code = " + response.status)
            if (failureCallback) {
                failureCallback();
            }
        })
    }

    var getTransactions = function(callback, failureCallback) {
        $http({
            method: 'GET',
            url: baseUrl + "all_tx"
        }).then(function(response) {
            if (callback) {
                callback(response.data);
            }
        },function(response)  {
            //console.log("server error, code = " + status)
            if (failureCallback) {
                failureCallback();
            }
        })
    }

    var agree = function(uuid, callback, errorCallback) {
        $http({
            method: 'POST',
            url: baseUrl + "agree",
            headers: {
                'Content-Type': 'application/json'
            },
            data: uuid
        }).then(function(response) {
            if (callback) {
                callback(response.data)
            }
        },function(response) {
            if (response.status == 422) {
                if (errorCallback) errorCallback();
            }
            else {
                if (callback) {
                    callback(response.data)
                }
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
