package nl.desertspring.traffic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import nl.desertspring.traffic.MeasurementCharacteristics.MeasurementType;

public class Datex2MstReaderTest {
	@Test
	public void findsMeasurementCharacteristicsById() throws Exception {
		Datex2MstRepository repository = mock(Datex2MstRepository.class);
		Datex2MstReader reader = new Datex2MstReader(repository);
		
		reader.parse(new File("src/test/resources/mst.xml"));
		
		ArgumentCaptor<MeasurementCharacteristics> characteristics = ArgumentCaptor.forClass(MeasurementCharacteristics.class);
		
		verify(repository).save(eq("NDW01_MT"), eq(868), eq("PZH01_MST_0004_00"), eq(12), eq(8), characteristics.capture());
		
		assertTrue(characteristics.getValue().isAnyVehicleType());
		assertEquals(MeasurementType.TRAFFIC_SPEED, characteristics.getValue().getType());
		assertEquals(1, characteristics.getValue().getLane());
		assertEquals(52.1223, characteristics.getValue().getLat(), 0.0001);
		assertEquals(4.49208, characteristics.getValue().getLng(), 0.0001);
	}
}
