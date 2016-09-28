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
		InfluxWriter influxWriter = mock(InfluxWriter.class);
		
		Datex2MstRepository datex2MstRepository = mock(Datex2MstRepository.class);
		Datex2Reader reader = new Datex2Reader(influxWriter, datex2MstRepository);
		
		MeasurementCharacteristics characteristics = new MeasurementCharacteristics()
				.withLane(2)
				.withType(MeasurementType.TRAFFIC_SPEED)
				.withAnyVehicleType(true);
				
		
		when(datex2MstRepository.findByIdAndIndex("PUT01_PUVIS_N416.03_1_2", 2)).thenReturn(characteristics);
		
		
		reader.parse(new File("src/test/resources/traffic_speed_sample.xml"));
//		reader.parse(new File("../traffic-sample-data/big_traffic_speed_sample.xml"));
		
		
		ArgumentCaptor<AverageVehicleSpeedMeasurement> values = ArgumentCaptor.forClass(AverageVehicleSpeedMeasurement.class);		
		verify(influxWriter, times(1)).measurementRead(values.capture());
		
		assertThat(values.getAllValues().get(0).getAverageVehicleSpeed(), equalTo(80.0));
		assertThat(values.getAllValues().get(0).getMeasurementCharacteristics(), equalTo(characteristics));		
	}
}
