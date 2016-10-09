package nl.desertspring.traffic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import nl.desertspring.traffic.MeasurementCharacteristics.MeasurementType;

public class Datex2MstRepositoryTest {
	@Test
	public void findsMeasurementSiteRecords() {		
		Datex2MstRepository datex2MstRepository = new Datex2MstRepository();
		
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
}
