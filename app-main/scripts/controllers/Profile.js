angular.module('sbAdminApp')
  .controller('ProfileCtrl', ['$scope', '$http', function($scope, $http) {
        $http({
            method: 'GET',
            url: baseUrl + 'register',
        }).then(function(response) {
           $scope.storeName = response.data.name;
           $scope.location = response.data.location;
        },function(response) {
            console.log("server error, code = " + response.status);
        });
  }]);
