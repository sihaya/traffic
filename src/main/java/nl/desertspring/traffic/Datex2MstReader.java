package nl.desertspring.traffic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;

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

	public void parse(File file) throws FileNotFoundException, XMLStreamException, ParseException {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();

		XMLStreamReader reader = inputFactory.createXMLStreamReader(new FileInputStream(file));

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
		
		while (reader.hasNext()) {
			int type = reader.next();
			
			switch(type) {
			case XMLStreamReader.START_ELEMENT:
				if (reader.getLocalName().equals("measurementSpecificCharacteristics")) {
					readMeasurementSpecificCharacteristics(id, version, recordId, recordVersion, reader);
				}
				
				break;
			case XMLStreamReader.END_ELEMENT:
				if (reader.getLocalName().equals("measurementSiteRecord")) {
					return;
				}
			}
			
	}

}

	private void readMeasurementSpecificCharacteristics(String id, String version, String recordId,
			String recordVersion, XMLStreamReader reader) throws XMLStreamException, ParseException {
		String index = reader.getAttributeValue(null, "index");
		while (reader.hasNext()) {
			int type = reader.next();
			
			switch(type) {
			case XMLStreamReader.START_ELEMENT:
				if (reader.getLocalName().equals("measurementSpecificCharacteristics")) {
					readMeasurementSpecificCharacteristics(id, version, recordId, recordVersion, index, reader);
				}
				
				break;
			case XMLStreamReader.END_ELEMENT:
				return;
			}
		}
	}

	private void readMeasurementSpecificCharacteristics(String id, String version, String recordId,
			String recordVersion, String index, XMLStreamReader reader) throws XMLStreamException, ParseException {
		
		String period = null;
		String specificLane = null;
		String specificMeasurementValueType = null;
		boolean isAnyVehicleType = false;
		
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
							.withPeriod(Integer.parseInt(period))
							.withType(parseMeasurementType(specificMeasurementValueType))
							.withLane(parseLane(specificLane));
				    repository.save(id, Integer.parseInt(version), recordId, 
				    		Integer.parseInt(recordVersion), 
				    		Integer.parseInt(index), characteristics);
					
					break;
				}
				
				break;
			case XMLStreamReader.END_ELEMENT:
				if (reader.getLocalName().equals("measurementSpecificCharacteristics")) {
					return;
				}
			}
			
		}
	}
		
	private int parseLane(String specificLane) {
		return Integer.parseInt(specificLane.replace("lane", ""));
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
				return result.toString();
			}
		}

		return null;
	}		
}