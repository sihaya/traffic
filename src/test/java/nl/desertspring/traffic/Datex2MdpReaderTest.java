package nl.desertspring.traffic;

import java.io.File;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import nl.desertspring.traffic.MeasurementCharacteristics.MeasurementType;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class Datex2MdpReaderTest {
	@Test
	public void extractsAverageVehicleSpeedFromDatex2() throws Exception {
		Datex2MdpRepository influxWriter = mock(Datex2MdpRepository.class);
		
		Datex2MstRepository datex2MstRepository = mock(Datex2MstRepository.class);
		Datex2MdpReader reader = new Datex2MdpReader(influxWriter, datex2MstRepository);
		
		MeasurementCharacteristics characteristics = new MeasurementCharacteristics()
				.withLane(2)
				.withType(MeasurementType.TRAFFIC_SPEED)
				.withAnyVehicleType(true);
				
		
		when(datex2MstRepository.findByIdAndIndex("NDW01_MT", 868, "PZH01_MST_0004_00", 12, 8)).thenReturn(characteristics);
		
		
		reader.parse(new File("src/test/resources/traffic_speed_sample.xml.gz"));
//		reader.parse(new File("../traffic-sample-data/big_traffic_speed_sample.xml"));
		
		
		ArgumentCaptor<AverageVehicleSpeedMeasurement> values = ArgumentCaptor.forClass(AverageVehicleSpeedMeasurement.class);		
		verify(influxWriter, times(1)).measurementRead(values.capture());
		
		assertThat(values.getAllValues().get(0).getAverageVehicleSpeed(), equalTo(80.0));
		assertThat(values.getAllValues().get(0).getMeasurementCharacteristics(), equalTo(characteristics));		
	}
}
