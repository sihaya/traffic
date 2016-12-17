package nl.desertspring.traffic;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TrafficImportAppIT {
	@Test
	public void importsAListOfMdps() throws Exception {
		List<String> arguments = Arrays.asList("src/test/resources/mst_a20_test.xml", "BEGIN_MDP", "src/test/resources/mdp_a20_test_1.xml");
		
		TrafficImportApp app = new TrafficImportApp();
		
		app.importMdps(arguments);
	}
}
