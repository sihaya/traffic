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
	
	var loadMeasurementPoints = function(maps) {
		$http.get("http://localhost:4567/measurementpoints", {
			params: {
				"north_east_lat": maps.getBounds().getNorthEast().lat(),
				"north_east_lng": maps.getBounds().getNorthEast().lng(),
				"south_west_lat": maps.getBounds().getSouthWest().lat(),
				"south_west_lng": maps.getBounds().getSouthWest().lng()				
			}
		}).then(function(data) {
			var markers = [], i;
			
			for (i in data.data) {
				markers.push({
					id: data.data[i].id,
				    latitude: data.data[i].lat,
				    longitude: data.data[i].lng,
				    title: data.data[i].id
				});				
			}
			
			$scope.markers = markers;
		});		
	}
	
	$scope.events = {
			"idle": loadMeasurementPoints
	}
	
		
	$scope.markers = [];
	
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