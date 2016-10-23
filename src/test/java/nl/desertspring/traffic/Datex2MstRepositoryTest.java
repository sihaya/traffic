package nl.desertspring.traffic;

import static org.junit.Assert.assertEquals;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import nl.desertspring.traffic.MeasurementCharacteristics.MeasurementType;

public class Datex2MstRepositoryTest {
	Datex2MstRepository datex2MstRepository = new Datex2MstRepository();
	
	@Test
	public void findsMeasurementSiteRecords() {						
		MeasurementCharacteristics expected = new MeasurementCharacteristics()
				.withAnyVehicleType(true)
				.withId("ZH_0001")
				.withLane(5)
				.withPeriod(60)
				.withType(MeasurementType.TRAFFIC_SPEED);
		datex2MstRepository.save("NDW_1", 123, "ZH_0001", 1, 2, expected);
		
		MeasurementCharacteristics actual = datex2MstRepository.findByIdAndIndex("NDW_1", 123, "ZH_0001", 1, 2);
		
		assertNotNull(actual);
		assertEquals(expected.getId(), actual.getId());		
		assertEquals(expected.getLane(), actual.getLane());
		assertEquals(expected.getPeriod(), actual.getPeriod(), 0.00001);
		assertEquals(expected.getType(), actual.getType());		
	}
	
	@Test
	public void findMeasurementSiteRecordsWithinCoordinates() {
		MeasurementCharacteristics expected = new MeasurementCharacteristics()
				.withAnyVehicleType(true)
				.withId("ZH_0001")
				.withLane(5)
				.withPeriod(60)
				.withLatLng(53.00000, 35.0000)
				.withType(MeasurementType.TRAFFIC_SPEED);
		
		MeasurementCharacteristics notMatching = new MeasurementCharacteristics()
				.withAnyVehicleType(true)
				.withId("ZH_0002")
				.withLane(5)
				.withPeriod(60)
				.withLatLng(2.00000, 3.0000)
				.withType(MeasurementType.TRAFFIC_SPEED);
		
		datex2MstRepository.save("NDW_1", 123, "ZH_0001", 1, 2, expected);
		datex2MstRepository.save("NDW_1", 123, "ZH_0002", 1, 2, notMatching);
		
		List<MeasurementCharacteristics> results = datex2MstRepository.findByBounds(54.0000, 54.0000, 3.0000, 3.0000);
		
		assertThat(results, contains(expected));
		
	}
}
