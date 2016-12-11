package nl.desertspring.traffic;

import static nl.desertspring.traffic.Util.openFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import nl.desertspring.traffic.MeasurementCharacteristics.MeasurementType;

public class Datex2MstReader {
	private static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";

	private Datex2MstRepository repository;

	public Datex2MstReader(Datex2MstRepository repository) {
		this.repository = repository;
	}
	
	public void parse(File file) throws XMLStreamException, ParseException, IOException {
		parse(openFile(file));
	}
	
	public void parse(InputStream inputStream) throws XMLStreamException, ParseException, IOException {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();

		XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);

		while (reader.hasNext()) {
			int type = reader.next();

			if (type != XMLStreamReader.START_ELEMENT) {
				continue;
			}

			String elemName = reader.getLocalName();

			if (elemName.equals("payloadPublication")) {
				readPayloadPublication(reader);
			}
		}
		
		reader.close();
	}

	private void readPayloadPublication(XMLStreamReader reader) throws XMLStreamException, ParseException {
		String publicationType = reader.getAttributeValue(XSI_NS, "type");

		if (!publicationType.equals("MeasurementSiteTablePublication")) {
			return;
		}
				
		while (reader.hasNext()) {
			int type = reader.next();

			switch (type) {
			case XMLStreamReader.START_ELEMENT:
				if (reader.getLocalName().equals("measurementSiteTable")) {
					readMeasurementSiteTable(reader);
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				if (reader.getLocalName().equals("payloadPublication")) {
					return;
				}
			}
		}
	}

	private void readMeasurementSiteTable(XMLStreamReader reader) throws XMLStreamException, ParseException {
		String id = reader.getAttributeValue(null, "id");
		String version = reader.getAttributeValue(null, "version");
		
		while (reader.hasNext()) {
			int type = reader.next();
			
			switch(type) {
			case XMLStreamReader.START_ELEMENT:
				if (reader.getLocalName().equals("measurementSiteRecord")) {
					readMeasurementSiteRecord(reader, id, version);
				}
				
				break;
			case XMLStreamReader.END_ELEMENT:
				return;
			}
		}
	}

	private void readMeasurementSiteRecord(XMLStreamReader reader, String id, String version) throws XMLStreamException, ParseException {
		String recordId = reader.getAttributeValue(null, "id");
		String recordVersion = reader.getAttributeValue(null, "version");
		
		Map<String, List<MeasurementCharacteristics>> results = new HashMap<>();
		while (reader.hasNext()) {
			int type = reader.next();
			
			switch(type) {
			case XMLStreamReader.START_ELEMENT:
				if (reader.getLocalName().equals("measurementSpecificCharacteristics")) {
					results.putAll(readMeasurementSpecificCharacteristics(id, version, recordId, recordVersion, reader));
				} else if (reader.getLocalName().equals("measurementSiteLocation")) {
					readMeasurementSiteLocation(id, version, recordId, recordVersion, results, reader);
				}
				
				break;
			case XMLStreamReader.END_ELEMENT:
				if (reader.getLocalName().equals("measurementSiteRecord")) {
					return;
				}
			}
		}
	}

	private void readMeasurementSiteLocation(String id, String version, String recordId, String recordVersion,
			Map<String, List<MeasurementCharacteristics>> results, XMLStreamReader reader) throws NumberFormatException, XMLStreamException, ParseException {
		
		double lat = 0.0;
		double lng = 0.0;
		while (reader.hasNext()) {
			int type = reader.next();
			
			switch(type) {
			case XMLStreamReader.START_ELEMENT:
				if (reader.getLocalName().equals("latitude")) {
					lat = Double.parseDouble(readCharacters(reader));
				} else if (reader.getLocalName().equals("longitude")) {
					lng = Double.parseDouble((readCharacters(reader)));
				}
			case XMLStreamReader.END_ELEMENT:
				if (reader.getLocalName().equals("measurementSiteLocation")) {
					saveCompleteMeasurementPoint(id, version, recordId, recordVersion, lat, lng, results);
					return;
				}				
			}
		}		
	}

	private void saveCompleteMeasurementPoint(String id, String version, String recordId, String recordVersion,
			double lat, double lng, Map<String, List<MeasurementCharacteristics>> results) {
		for (Entry<String, List<MeasurementCharacteristics>> result : results.entrySet()) {
			String index = result.getKey();
			
			MeasurementCharacteristics value = result.getValue().get(0);			
			value.withLatLng(lat, lng);
			
			repository.save(id, Integer.parseInt(version), recordId, 
		    		Integer.parseInt(recordVersion), 
		    		Integer.parseInt(index), value);
		}		
	}

	private Map<String, List<MeasurementCharacteristics>> readMeasurementSpecificCharacteristics(String id, String version, String recordId,
			String recordVersion, XMLStreamReader reader) throws XMLStreamException, ParseException {
		String index = reader.getAttributeValue(null, "index");
		
		Map<String, List<MeasurementCharacteristics>> results = new HashMap<>(); 
		
		outer:
		while (reader.hasNext()) {
			int type = reader.next();
			
			switch(type) {
			case XMLStreamReader.START_ELEMENT:
				if (reader.getLocalName().equals("measurementSpecificCharacteristics")) {
					List<MeasurementCharacteristics> characteristics = readMeasurementSpecificCharacteristics(id, version, recordId, recordVersion, index, reader);
					results.put(index, characteristics);
				}
				
				break;
			case XMLStreamReader.END_ELEMENT:
				break outer;
			}
		}
		
		return results;
	}

	private List<MeasurementCharacteristics> readMeasurementSpecificCharacteristics(String id, String version, String recordId,
			String recordVersion, String index, XMLStreamReader reader) throws XMLStreamException, ParseException {
		
		String period = null;
		String specificLane = null;
		String specificMeasurementValueType = null;
		boolean isAnyVehicleType = false;
		
		List<MeasurementCharacteristics> results = new ArrayList<>();
		
		outer:
		while (reader.hasNext()) {
			int type = reader.next();
			
			switch(type) {
			case XMLStreamReader.START_ELEMENT:
				String elem = reader.getLocalName();
				switch(elem) {
				case "period":
					period = readCharacters(reader);
					break;
				case "specificLane":
					specificLane = readCharacters(reader);
					break;
				case "specificMeasurementValueType":
					specificMeasurementValueType = readCharacters(reader);
					break;
				case "specificVehicleCharacteristics":
					isAnyVehicleType = readSpecificVehicleCharacteristics(reader);
					
					MeasurementCharacteristics characteristics = new MeasurementCharacteristics()
							.withAnyVehicleType(isAnyVehicleType)
							.withId(recordId)							
							.withPeriod(Double.parseDouble(period))
							.withType(parseMeasurementType(specificMeasurementValueType))
							.withLane(parseLane(specificLane));
					results.add(characteristics);				    
					
					break;
				}
				
				break;
			case XMLStreamReader.END_ELEMENT:
				if (reader.getLocalName().equals("measurementSpecificCharacteristics")) {
					break outer;
				}
			}
			
		}
		
		return results;
	}
		
	private int parseLane(String specificLane) {
		if (specificLane != null && specificLane.startsWith("lane")) {
			return Integer.parseInt(specificLane.replace("lane", ""));
		}
		
		return 0;
	}

	private MeasurementType parseMeasurementType(String specificMeasurementValueType) {
		switch(specificMeasurementValueType) {
		case "trafficSpeed":
			return MeasurementType.TRAFFIC_SPEED;
		default:
			return MeasurementType.UNKNOWN;
		}
	}

	private boolean readSpecificVehicleCharacteristics(XMLStreamReader reader) throws XMLStreamException, ParseException {
		while (reader.hasNext()) {
			int type = reader.next();
			
			switch(type) {
			case XMLStreamReader.START_ELEMENT:
				if (reader.getLocalName().equals("vehicleType")) {
					String vehicleType = readCharacters(reader);
					
					return vehicleType.equals("anyVehicle");
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				if (reader.getLocalName().equals("specificVehicleCharacteristics")) {
					return false;
				}
			}
		}
		
		return false;
	}

	private String readCharacters(XMLStreamReader reader) throws XMLStreamException, ParseException {
		StringBuilder result = new StringBuilder();
		while (reader.hasNext()) {
			int eventType = reader.next();
			switch (eventType) {
			case XMLStreamReader.CHARACTERS:
			case XMLStreamReader.CDATA:
				result.append(reader.getText());
				break;
			case XMLStreamReader.END_ELEMENT:
				return result.toString().trim();
			}
		}

		return null;
	}	
}