angular.module('traffic', [ 'uiGmapgoogle-maps', 'chart.js' ]).config([ 'uiGmapGoogleMapApiProvider', function(uiGmapGoogleMapApiProvider) {
	uiGmapGoogleMapApiProvider.configure({
		key : '',
		v : '3.20', // defaults to latest 3.X anyhow
		libraries : 'weather,geometry,visualization'
	});
} ]);

angular.module('traffic').controller('TrafficController', [ '$scope', '$http', function($scope, $http) {
	$scope.map = {
		center : {
			latitude : 51.91691,
			longitude : 4.36735
		},
		zoom : 14
	};
	
	$scope.markers = [{
		id: 'RWS01_MONICA_00D0040B1829D0050309',
	    latitude: 51.91691,
	    longitude: 4.36735,
	    title: 'RWS01_MONICA_00D0040B1829D0050309'
	}];
	
	$scope.markerClicked = function(instance, event, model) {
		alert("jow");
		
		var query = "SELECT average_speed FROM average_vehicle_speed_measurement " +
		"WHERE average_speed != -1 and measurement_point = '" + model.id + "' and lane = '2'";
		
		$http.get('http://localhost:8086/query', {
			params: {
				db: 'trafficTest',
				q: query
			}
		}).then(function(response) {
			$scope.series = ["average speed"]
			var data = [], labels = [], i, values = response.data.results[0].series[0].values;
			
			for(i in values) {
				labels.push(values[i][0])
				data.push(values[i][1])			
			}
			
			$scope.data = [data]
			$scope.labels = labels
			$scope.datasetOverride = [{ yAxisID: 'y-axis-1' }]
			
			$scope.options = {
				scales: {
					yAxes: [{
						id: 'y-axis-1',
				        type: 'linear',
				        display: true,
				        position: 'left'
					}			    
					]
				}
			}		
		});
	}	
} ]);