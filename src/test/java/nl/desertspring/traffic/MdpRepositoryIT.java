package nl.desertspring.traffic;

import static nl.desertspring.traffic.IsoDateUtil.dateFromIso;
import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import nl.desertspring.traffic.MeasurementCharacteristics.MeasurementType;
import nl.desertspring.traffic.api.MdpMeasurement;
import nl.desertspring.traffic.api.MdpMeasurementData;
import nl.desertspring.traffic.api.MdpMeasurementLane;

public class MdpRepositoryIT {
	Datex2MdpRepository mdpRepository;
	
	@Before
	public void setup() {
		mdpRepository = new Datex2MdpRepository();
		Datex2MdpRepository.DB_NAME = "traffic_it";
		mdpRepository.resetDbForTesting();
	}
	
	@Test
	public void selectsDataAggregatedByPeriod() {
		MeasurementCharacteristics measurementCharacteristics1 = new MeasurementCharacteristics()
				.withId("PUT01_N413.03")
				.withPeriod(60.0)
				.withLane(1);
		
		MeasurementCharacteristics measurementCharacteristics2 = new MeasurementCharacteristics()
				.withId("PUT01_N413.05")
				.withPeriod(60.0)
				.withLane(1);
		
		measurement(new AverageVehicleSpeedMeasurement()
				.withMeasurementCharacteristics(measurementCharacteristics1)
				.withAverageVehicleSpeed(44)				
				.withMeasurementTime(dateFromIso("2016-09-09T09:50:00Z")));
		
		measurement(new AverageVehicleSpeedMeasurement()
				.withMeasurementCharacteristics(measurementCharacteristics1)
				.withAverageVehicleSpeed(46)				
				.withMeasurementTime(dateFromIso("2016-09-09T09:50:01Z")));
						
		measurement(new AverageVehicleSpeedMeasurement()
				.withMeasurementCharacteristics(measurementCharacteristics1)						
				.withAverageVehicleSpeed(64)				
				.withMeasurementTime(dateFromIso("2016-09-09T09:51:00Z")));
		
		measurement(new AverageVehicleSpeedMeasurement()
				.withMeasurementCharacteristics(measurementCharacteristics1)
				.withAverageVehicleSpeed(84)				
				.withMeasurementTime(dateFromIso("2016-09-09T09:52:00Z")));
		
		measurement(new AverageVehicleSpeedMeasurement()
				.withMeasurementCharacteristics(measurementCharacteristics2)
				.withAverageVehicleSpeed(80)				
				.withMeasurementTime(dateFromIso("2016-09-09T09:52:00Z")));
				
		mdpRepository.flush();
		
		verifyInfluxDatabaseHasValueOnTime();
	}

	private void measurement(AverageVehicleSpeedMeasurement speedMeasurement) {		
		mdpRepository.measurementRead(speedMeasurement);
	}

	private void verifyInfluxDatabaseHasValueOnTime() {
		Calendar startTime = dateFromIso("2016-09-09T09:50:00Z");
		
		MdpMeasurement measurement = mdpRepository.findByPeriodAndType("PUT01_N413.03", MeasurementType.TRAFFIC_SPEED, startTime, 60, 30);
		
		assertNotNull(measurement);
		
		assertEquals("PUT01_N413.03", measurement.getId());
		assertEquals(1, measurement.getLanes().size());
		
		MdpMeasurementLane lane = measurement.getLanes().get(0);
		assertEquals("lane_1", lane.getName());		
		assertEquals(1, lane.getMeasurements().size());
		
		MdpMeasurementData data = lane.getMeasurements().get(0);
		assertEquals(1, data.getData().size());
		
		assertEquals(45, data.getData().get(0).getValue());
	}
}
