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
		MeasurementCharacteristics measurementCharacteristics = new MeasurementCharacteristics()
				.withId("PUT01_N413.03")
				.withLane(1);
		
		AverageVehicleSpeedMeasurement speedMeasurement1 = new AverageVehicleSpeedMeasurement()
				.withMeasurementCharacteristics(measurementCharacteristics)
				.withAverageVehicleSpeed(44)				
				.withMeasurementTime(dateFromIso("2016-09-09T09:50:00Z"));
		
		AverageVehicleSpeedMeasurement speedMeasurement2 = new AverageVehicleSpeedMeasurement()
				.withMeasurementCharacteristics(measurementCharacteristics)						
				.withAverageVehicleSpeed(64)				
				.withMeasurementTime(dateFromIso("2016-09-09T09:51:00Z"));
		
		AverageVehicleSpeedMeasurement speedMeasurement3 = new AverageVehicleSpeedMeasurement()
				.withMeasurementCharacteristics(measurementCharacteristics)
				.withAverageVehicleSpeed(84)				
				.withMeasurementTime(dateFromIso("2016-09-09T09:52:00Z"));
		
		influxWriter.measurementRead(speedMeasurement1);
		influxWriter.measurementRead(speedMeasurement2);
		influxWriter.measurementRead(speedMeasurement3);
		influxWriter.flush();
		
		verifyInfluxDatabaseHasValueOnTime();
	}

	private void verifyInfluxDatabaseHasValueOnTime() {
		
	}
}
