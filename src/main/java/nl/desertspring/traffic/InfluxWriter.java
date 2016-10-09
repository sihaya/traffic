package nl.desertspring.traffic;

import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

public class InfluxWriter {
	
	private InfluxDB influxDb;
	private static final String DB_NAME = "trafficTest";
	private BatchPoints batch;
	
	

	public InfluxWriter() {
		influxDb = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
	}
	
	public void measurementRead(AverageVehicleSpeedMeasurement speedMeasurement) {
		if (batch == null) {
			batch = BatchPoints
					.database(DB_NAME)                
					.retentionPolicy("default")
					.consistency(ConsistencyLevel.ALL)
					.build();
		}
		
		Point point = Point.measurement("average_vehicle_speed_measurement")
                .time(speedMeasurement.getMeasurementTime().getTimeInMillis(), TimeUnit.MILLISECONDS)                                
                .addField("average_speed", speedMeasurement.getAverageVehicleSpeed())
                .tag("measurement_point", speedMeasurement.getMeasurementCharacteristics().getId())
                .tag("lane", Integer.toString(speedMeasurement.getMeasurementCharacteristics().getLane()))
                .tag("period", String.format("%.2f", speedMeasurement.getMeasurementCharacteristics().getPeriod()))
                .build();
		
		batch.point(point);		
	}

	public void flush() {
		if (batch == null) {
			return;
		}
		
		influxDb.write(batch);
		
		batch = null;
	}
		
	public void resetDb() {				
		influxDb.createDatabase(DB_NAME);
	}

}