angular.module("sbAdminApp").factory('predictionService', ['$http', '$timeout', function($http, $timeout) {

    var baseUrl = 'http://localhost:8080/'

    var getPredictions = function(successCallback, errorCallback) {
        $http({
            method: 'GET',
            url: baseUrl + 'predictions',
        }).success(function(response) {
            if (successCallback) {
                successCallback(response);
            }
        }).error(function(response) {
            console.log("server error, code = " + response.status);
            errorCallback();
        })
    }

    return {
        getPredictions: getPredictions
    }
}]);
