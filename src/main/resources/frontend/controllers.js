angular.module('traffic', [ 'chart.js', 'ui.bootstrap' ]);

angular.module('traffic').controller('TrafficController', [ '$scope', '$http', '$window', function($scope, $http, $window) {
	$window.initMap = new google.maps.Map(document.getElementById('map-container'), {
	    center: {
	    	lat: 51.91691, lng: 4.36735
	    },
	    zoom: 14
	});	
			
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
		var query = "SELECT average_speed FROM average_vehicle_speed_measurement " +
		"WHERE average_speed != -1 and measurement_point = '" + model.id + "' and lane = '1'";
		
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