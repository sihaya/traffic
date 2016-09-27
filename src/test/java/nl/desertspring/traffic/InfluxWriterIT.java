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
		AverageVehicleSpeedMeasurement speedMeasurement1 = new AverageVehicleSpeedMeasurement()
				.withMeasurementPoint("PUT01_N413.03")
				.withAverageVehicleSpeed(44)
				.withLane(1)
				.withMeasurementTime(dateFromIso("2016-09-09T09:50:00Z"));
		
		AverageVehicleSpeedMeasurement speedMeasurement2 = new AverageVehicleSpeedMeasurement()
				.withMeasurementPoint("PUT01_N413.03")
				.withAverageVehicleSpeed(64)
				.withLane(1)
				.withMeasurementTime(dateFromIso("2016-09-09T09:51:00Z"));
		
		AverageVehicleSpeedMeasurement speedMeasurement3 = new AverageVehicleSpeedMeasurement()
				.withMeasurementPoint("PUT01_N413.03")
				.withAverageVehicleSpeed(84)
				.withLane(1)
				.withMeasurementTime(dateFromIso("2016-09-09T09:52:00Z"));
		
		influxWriter.addMeasurement(speedMeasurement1);
		influxWriter.addMeasurement(speedMeasurement2);
		influxWriter.addMeasurement(speedMeasurement3);
		influxWriter.flush();
		
		verifyInfluxDatabaseHasValueOnTime();
	}

	private void verifyInfluxDatabaseHasValueOnTime() {
		
	}
}
