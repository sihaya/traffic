package nl.desertspring.traffic;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class TrafficImportApp {
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("usage: mst mdp..");
			System.exit(1);
			return;
		}
		
		new TrafficImportApp().importMdps(Arrays.asList(args));
	}

	public void importMdps(List<String> arguments) throws Exception {
		String mstFile = arguments.get(0);
						
		Datex2MstRepository repository = new Datex2MstRepository();
		Datex2MstReader mstReader = new Datex2MstReader(repository);
		
		System.out.println("Reading mst");
		mstReader.parse(new File(mstFile));
		
		Datex2MdpRepository influxWriter = new Datex2MdpRepository();
		influxWriter.resetDb();
		
		Datex2MdpReader mdpReader = new Datex2MdpReader(influxWriter, repository);
		
		for(String mdpFile : arguments.subList(1, arguments.size())) {
			mdpReader.parse(new File(mdpFile));
			influxWriter.flush();
		}
	}
}
