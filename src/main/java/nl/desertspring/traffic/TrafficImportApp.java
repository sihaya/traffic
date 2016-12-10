package nl.desertspring.traffic;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrafficImportApp {
	private static final Logger LOGGER = LoggerFactory.getLogger(TrafficImportApp.class);
	private static final String MDP_MARKER = "BEGIN_MDP";

	public static void main(String[] args) throws Exception {
		List<String> arguments = Arrays.asList(args);
		
		if (args.length < 3 || !arguments.contains(MDP_MARKER)) {
			System.out.println("usage: mst... BEGIN_MDP mdp..");
			System.exit(1);
			return;
		}
		
		new TrafficImportApp().importMdps(arguments);
	}

	public void importMdps(List<String> arguments) throws Exception {		
		int mdpMarker = arguments.indexOf(MDP_MARKER);
		Datex2MstRepository repository = readMst(arguments.subList(0, mdpMarker));
		
		Datex2MdpRepository influxWriter = new Datex2MdpRepository();
		influxWriter.resetDb();
		
		Datex2MdpReader mdpReader = new Datex2MdpReader(influxWriter, repository);
		
		for(String mdpFile : arguments.subList(mdpMarker + 1, arguments.size())) {
			LOGGER.info("Parsing {}", mdpFile);
			
			mdpReader.parse(new File(mdpFile));
			influxWriter.flush();
			
			LOGGER.info("Done parsing {}", mdpFile);
		}
	}

	private Datex2MstRepository readMst(List<String> arguments)
			throws XMLStreamException, ParseException, IOException {
		Datex2MstRepository repository = new Datex2MstRepository();
		Datex2MstReader mstReader = new Datex2MstReader(repository);
		
		for(String mstFile : arguments) {
			LOGGER.info("Parsing {}", mstFile);
			
			mstReader.parse(new File(mstFile));
			
			LOGGER.info("Done parsing {}", mstFile);
		}
		
		return repository;
	}
}
