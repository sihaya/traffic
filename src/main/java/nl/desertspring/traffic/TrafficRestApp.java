package nl.desertspring.traffic;

import static spark.Spark.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import nl.desertspring.traffic.MeasurementCharacteristics.MeasurementType;
import spark.Response;

import static nl.desertspring.traffic.IsoDateUtil.*;

public class TrafficRestApp {
	private static final Logger LOGGER = LoggerFactory.getLogger(TrafficRestApp.class);
	
	public static void main(String[] args) throws XMLStreamException, ParseException, IOException {
		if (args.length != 1) {
			System.err.println("usage: TrafficRestApp <mst-file>");
			System.exit(1);			
		}
		
		String mstFile = args[0];
		
		Datex2MstRepository repository = initializeRepository(new File(mstFile));
		Datex2MdpRepository mdpRepository = initializeMdpRepository();
		
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(GregorianCalendar.class, calendarTypeAdapter())
				.create();
		
        get("/measurementpoints", (req, res) -> {
        	double northEastLat = Double.parseDouble(req.queryParams("north_east_lat"));
        	double northEastLng = Double.parseDouble(req.queryParams("north_east_lng"));
        	double southWestLat = Double.parseDouble(req.queryParams("south_west_lat"));
        	double southWestLng = Double.parseDouble(req.queryParams("south_west_lng"));
        	
        	responseHeaders(res);
        	
        	return repository.findByBounds(northEastLat, northEastLng, southWestLat, southWestLng);        	
        }, gson::toJson);
        
        get("/measurements/:id", (req, res) -> {
        	long period = Long.parseLong(req.queryParams("period"));        	
        	MeasurementType type = MeasurementType.valueOf(req.queryParams("type").toUpperCase());
        	Calendar startTime = dateFromIso(req.queryParams("start_time"));
        	String id = req.params("id");
        	
        	responseHeaders(res);
        	
        	return mdpRepository.findByPeriodAndType(id, type, startTime, period);
        }, gson::toJson);
        

        exception(Exception.class, (exception, request, response) -> {
        	LOGGER.error("Caught exception", exception);
        	
        	response.status(500);
        });


    }

	private static Object calendarTypeAdapter() {
		return new JsonSerializer<Calendar>() {

			@Override
			public JsonElement serialize(Calendar src, Type typeOfSrc, JsonSerializationContext context) {
				return new JsonPrimitive(IsoDateUtil.dateToIso(src));
			}
			
		};
	}

	private static void responseHeaders(Response res) {
		res.header("Content-Type", "application/json");
		res.header("Access-Control-Allow-Origin", "*");
	}

	private static Datex2MdpRepository initializeMdpRepository() {
		return new Datex2MdpRepository();
	}

	private static Datex2MstRepository initializeRepository(File file)
			throws XMLStreamException, ParseException, IOException {
		Datex2MstRepository repository = new Datex2MstRepository();

		Datex2MstReader reader = new Datex2MstReader(repository);
		reader.parse(file);

		return repository;
	}
}
