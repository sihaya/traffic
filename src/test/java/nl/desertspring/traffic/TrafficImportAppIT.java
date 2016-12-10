package nl.desertspring.traffic;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TrafficImportAppIT {
	@Test
	public void importsAListOfMdps() throws Exception {
		List<String> arguments = Arrays.asList("src/test/resources/mst.xml", "BEGIN_MDP", "src/test/resources/traffic_speed_sample.xml");
		
		TrafficImportApp app = new TrafficImportApp();
		
		app.importMdps(arguments);
	}
}
