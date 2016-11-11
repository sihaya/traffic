package nl.desertspring.traffic;

import static spark.Spark.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.xml.stream.XMLStreamException;

import com.google.gson.Gson;

public class TrafficRestApp {
	public static void main(String[] args) throws XMLStreamException, ParseException, IOException {
		if (args.length != 1) {
			System.err.println("usage: TrafficRestApp <mst-file>");
			System.exit(1);			
		}
		
		String mstFile = args[0];
		
		Datex2MstRepository repository = initializeRepository(new File(mstFile));
		
		Gson gson = new Gson();
        get("/measurementpoints", (req, res) -> {
        	double northEastLat = Double.parseDouble(req.queryParams("north_east_lat"));
        	double northEastLng = Double.parseDouble(req.queryParams("north_east_lng"));
        	double southWestLat = Double.parseDouble(req.queryParams("south_west_lat"));
        	double southWestLng = Double.parseDouble(req.queryParams("south_west_lng"));
        	
        	res.header("Content-Type", "application/json");
        	res.header("Access-Control-Allow-Origin", "*");
        	
        	return repository.findByBounds(northEastLat, northEastLng, southWestLat, southWestLng);        	
        }, gson::toJson);
        
        // SELECT average_speed FROM average_vehicle_speed_measurement " +
		//"WHERE average_speed != -1 and measurement_point = '" + model.id + "' and lane = '1'";
    }

	private static Datex2MstRepository initializeRepository(File file) throws XMLStreamException, ParseException, IOException {
		Datex2MstRepository repository = new Datex2MstRepository();
		
		Datex2MstReader reader = new Datex2MstReader(repository);
		reader.parse(file);
		
		return repository;		
	}
}
