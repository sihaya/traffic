package nl.desertspring.traffic;

import static nl.desertspring.traffic.IsoDateUtil.dateFromIso;

import org.junit.Before;
import org.junit.Test;

public class InfluxWriterIT {
	InfluxWriter influxWriter;
	
	@Before
	public void setup() {
		influxWriter = new InfluxWriter();
		influxWriter.resetDb();
	}
	
	@Test
	public void writesSpeedValuesToInfluxAfterFlushing() {
		MeasurementCharacteristics measurementCharacteristics1 = new MeasurementCharacteristics()
				.withId("PUT01_N413.03")
				.withPeriod(60.0)
				.withLane(1);
		
		MeasurementCharacteristics measurementCharacteristics2 = new MeasurementCharacteristics()
				.withId("PUT01_N413.05")
				.withPeriod(60.0)
				.withLane(1);
		
		AverageVehicleSpeedMeasurement speedMeasurement1 = new AverageVehicleSpeedMeasurement()
				.withMeasurementCharacteristics(measurementCharacteristics1)
				.withAverageVehicleSpeed(44)				
				.withMeasurementTime(dateFromIso("2016-09-09T09:50:00Z"));
		
		AverageVehicleSpeedMeasurement speedMeasurement2 = new AverageVehicleSpeedMeasurement()
				.withMeasurementCharacteristics(measurementCharacteristics1)						
				.withAverageVehicleSpeed(64)				
				.withMeasurementTime(dateFromIso("2016-09-09T09:51:00Z"));
		
		AverageVehicleSpeedMeasurement speedMeasurement3 = new AverageVehicleSpeedMeasurement()
				.withMeasurementCharacteristics(measurementCharacteristics1)
				.withAverageVehicleSpeed(84)				
				.withMeasurementTime(dateFromIso("2016-09-09T09:52:00Z"));
		
		AverageVehicleSpeedMeasurement speedMeasurement4 = new AverageVehicleSpeedMeasurement()
				.withMeasurementCharacteristics(measurementCharacteristics2)
				.withAverageVehicleSpeed(80)				
				.withMeasurementTime(dateFromIso("2016-09-09T09:52:00Z"));
		
		influxWriter.measurementRead(speedMeasurement1);
		influxWriter.measurementRead(speedMeasurement2);
		influxWriter.measurementRead(speedMeasurement3);
		influxWriter.measurementRead(speedMeasurement4);
		influxWriter.flush();
		
		verifyInfluxDatabaseHasValueOnTime();
	}

	private void verifyInfluxDatabaseHasValueOnTime() {
		// SELECT * FROM average_vehicle_speed_measurement
	}
}
