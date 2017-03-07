angular.module('traffic', [ 'chart.js', 'ui.bootstrap' ]);

angular.module('traffic').controller('TrafficController', [ '$scope', '$http', '$window', function($scope, $http, $window) {
	var map = new google.maps.Map(document.getElementById('map-container'), {
	    center: {
	    	lat: 51.91691, lng: 4.36735
	    },
	    zoom: 14
	});

	var loadMeasurementPoints = function() {
		$http.get("/measurementpoints", {
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
			
			data.data.forEach(function(value) {
				var marker = new google.maps.Marker({
		          position: {
		        	  lat: value.lat,
		        	  lng: value.lng		        	  
		          },
		          map: map,
		          title: value.id
		        });

				marker.addListener('click', function() {
					markerClicked(value.id);
				});
			});			
			
			$scope.markers = markers;
		});		
	}
	
	map.addListener("idle", loadMeasurementPoints);
		
	$scope.markers = [];
		
	var markerClicked = function(id) {
		$scope.measurementPoint = id;

		$http.get('/measurements/' + id, {
			params: {
				"period": 3600 * 24,
				"start_time": $scope.selectedDate.toISOString(),
				"type": "traffic_speed"
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
					data.push(lane.measurements[0].data[i].value ? lane.measurements[0].data[i].value : null)
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
	
  	$scope.openDate = function() {
    	$scope.dateOpen = true;
  	};

	$scope.selectedDate = new Date(2016, 5, 9);
} ]);