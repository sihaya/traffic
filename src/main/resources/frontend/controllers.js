angular.module('traffic', [ 'chart.js', 'ui.bootstrap' ]);

angular.module('traffic').controller('TrafficController', [ '$scope', '$http', '$window', function($scope, $http, $window) {
	var map = new google.maps.Map(document.getElementById('map-container'), {
	    center: {
	    	lat: 51.91691, lng: 4.36735
	    },
	    zoom: 14
	});

	var loadMeasurementPoints = function() {
		$http.get("http://localhost:4567/measurementpoints", {
			params: {
				"north_east_lat": map.getBounds().getNorthEast().lat(),
				"north_east_lng": map.getBounds().getNorthEast().lng(),
				"south_west_lat": map.getBounds().getSouthWest().lat(),
				"south_west_lng": map.getBounds().getSouthWest().lng()				
			}
		}).then(function(data) {
			var markers = [], i;
			
			for (i in $scope.markers) {
				$scope.markers[i].setMap(null);
			}
			
			for (i in data.data) {
		        var marker = new google.maps.Marker({
		          position: {
		        	  lat: data.data[i].lat,
		        	  lng: data.data[i].lng,
		        	  title: data.data[i].id
		          },
		          map: map,
		          title: 'Hello World!'
		        });
			}
			
			$scope.markers = markers;
		});		
	}
	
	map.addListener("idle", loadMeasurementPoints);
		
	$scope.markers = [];
		
	$scope.markerClicked = function(instance, event, model) {						
		$http.get('/mockdata.json', {
			params: {
				"period": "24",
				"type": "average_speed"
			}
		}).then(function(response) {
			$scope.series = ["average speed"]
								
			var graphDatas = [];
			
			for (l in response.data.lanes) {
				var lane = response.data.lanes[l], labels = [], data = [];
				
				var graphData = {
					"name": lane.name
				}
				
				for(i in lane.measurements[0].data) {
					labels.push(lane.measurements[0].data[i].timestamp)
					data.push(lane.measurements[0].data[i].value)
				}
				
				graphData.data = [data];
				graphData.labels = labels;
				
				graphDatas.push(graphData)
			}
			
			$scope.datasetOverride = [{ yAxisID: 'y-axis-1' }]
			$scope.lanes = graphDatas;
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
	
	$scope.markerClicked();
} ]);