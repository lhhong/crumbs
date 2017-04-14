angular.module('sbAdminApp')
  .controller('ProfileCtrl', ['$scope', '$http', function($scope, $http) {
         var baseUrl = 'http://localhost:8080/'
        console.log('profile controller loaded');
        $http({
            method: 'GET',
            url: baseUrl + 'register',
        }).then(function(response) {
           $scope.storeName = response.data.name;
           $scope.location = response.data.location;
        },function(response) {
            console.log("server error, code = " + response.status);
           $scope.storeName = 'storeName';
           $scope.location = 'location';
        });
  }]);
