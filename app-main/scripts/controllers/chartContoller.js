'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
  .controller('ChartCtrl', ['$scope', '$timeout', function ($scope, $timeout) {
	  console.log("in chart controller");
    $scope.line = {
	    labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
	    series: ['Series A', 'Series B'],
	    data: [
	      [65, 59, 80, 81, 56, 55, 40],
	      [28, 48, 40, 19, 86, 27, 90]
	    ],
	    onClick: function (points, evt) {
	      console.log(points, evt);
	    }
    };
    $scope.lineMonth = {
      labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
		  series: ['Sold', 'Redistributed', 'Wasted'],
      data: [
		   [65, 59, 80, 81, 56, 55],
		   [28, 48, 40, 19, 86, 27],
       [30, 32, 21, 33, 42, 55]
     ],
     onClick: function (points, evt) {
       console.log(points, evt);
     }
    };

    $scope.bar = {
	    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
		  series: ['Sold', 'Redistributed', 'Wasted'],
      data: [
		   [65, 59, 80, 81, 56, 55],
		   [28, 48, 40, 19, 86, 27],
       [30, 32, 21, 33, 42, 55]
		]
    };

    $scope.donut = {
    	labels: ["Percentage Sold", "Percentage Redistributed", "Percentage Wasted"],
    	data: [50, 5, 45]
    };

    $scope.radar = {
    	labels:["Grains", "Dairy", "Fruit", "Vegetables", "Meat/Poultry", "Sweets", "Oils"],
      series: ["Redistributed", "Wasted"],
    	data:[
    	    [65, 59, 90, 81, 56, 55, 40],
    	    [28, 48, 40, 19, 96, 27, 100]
    	]
    };

    $scope.pie = {
    	labels : ["Download Sales", "In-Store Sales", "Mail-Order Sales"],
    	data : [300, 500, 100]
    };

    $scope.polar = {
    	labels : ["Download Sales", "In-Store Sales", "Mail-Order Sales", "Tele Sales", "Corporate Sales"],
    	data : [300, 500, 100, 40, 120]
    };

    $scope.dynamic = {
    	labels : ["Download Sales", "In-Store Sales", "Mail-Order Sales", "Tele Sales", "Corporate Sales"],
    	data : [300, 500, 100, 40, 120],
    	type : 'PolarArea',

    	toggle : function ()
    	{
    		this.type = this.type === 'PolarArea' ?
    	    'Pie' : 'PolarArea';
		}
    };
    $scope.donutcolours = ["#70DBDB", "#66CD00", "#DB2929"];
    $scope.barcolours = ["#66CCCC", "#66CD00", "#EE0000"];
    $scope.radarcolours = ["#66CD00", "#EE0000"];
    $scope.lineMonthColours = ["#66CCCC", "#66CD00", "#EE0000"];

    /*["rgba(224, 108, 112, 1)",
            "rgba(224, 108, 112, 1)",
            "rgba(224, 108, 112, 1)"]*/
}]);
