package nl.desertspring.traffic;

import static nl.desertspring.traffic.Util.openFile;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import nl.desertspring.traffic.MeasurementCharacteristics.MeasurementType;

public class Datex2MdpReader {
	private static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
	private Datex2MstRepository datex2MstRepository;
	private Datex2MdpRepository influxWriter;
	private String tableId;
	private int tableVersion;

	public Datex2MdpReader(Datex2MdpRepository influxWriter, Datex2MstRepository datex2MstRepository) {
		this.influxWriter = influxWriter;
		this.datex2MstRepository = datex2MstRepository;
	}

	public void parse(File file) throws Exception {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();

		XMLStreamReader reader = inputFactory.createXMLStreamReader(openFile(file));

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
	}

	private void readPayloadPublication(XMLStreamReader reader) throws XMLStreamException, ParseException {
		String publicationType = reader.getAttributeValue(XSI_NS, "type");

		if (!publicationType.equals("MeasuredDataPublication")) {
			return;
		}

		while (reader.hasNext()) {
			int type = reader.next();

			switch (type) {
			case XMLStreamReader.START_ELEMENT:
				String elemName = reader.getLocalName();
				switch (elemName) {
				case "measurementSiteTableReference":
					readMeasurementSiteTableReference(reader);
					break;
				case "siteMeasurements":
					readSiteMeasurements(reader);
					break;
				}
			}
		}
	}

	private void readSiteMeasurements(XMLStreamReader reader) throws XMLStreamException, ParseException {
		String id = null;
		Calendar timeDefault = null;
		int version = 0;
		
		while (reader.hasNext()) {
			int type = reader.next();

			String elemName;
			switch (type) {
			case XMLStreamReader.START_ELEMENT:
				elemName = reader.getLocalName();

				switch (elemName) {
				case "measurementSiteReference":
					id = reader.getAttributeValue(null, "id");
					version = Integer.parseInt(reader.getAttributeValue(null, "version"));
					break;
				case "measurementTimeDefault":
					timeDefault = readCharactersIsoTime(reader);
					break;
				case "measuredValue":
					readMeasuredValue(id, version, timeDefault, reader);
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				elemName = reader.getLocalName();

				if (elemName.equals("siteMeasurements")) {
					return;
				}
			}

		}
	}

	private Calendar readCharactersIsoTime(XMLStreamReader reader) throws XMLStreamException, ParseException {
		return javax.xml.bind.DatatypeConverter.parseDateTime(readCharacters(reader));
	}

	private void readMeasuredValue(String id, int version, Calendar timeDefault, XMLStreamReader reader) throws XMLStreamException, ParseException {
		int index = Integer.parseInt(reader.getAttributeValue(null, "index"));

		MeasurementCharacteristics characteristics = datex2MstRepository.findByIdAndIndex(tableId, tableVersion, id, version, index);

		boolean firstSeen = false;
		while (reader.hasNext()) {
			int type = reader.next();

			String elemName;

			switch (type) {
			case XMLStreamReader.START_ELEMENT:
				elemName = reader.getLocalName();
				if (characteristics != null && characteristics.getType() == MeasurementType.TRAFFIC_SPEED
						&& elemName.equals("averageVehicleSpeed")) {
					readAverageVehicleSpeed(id, timeDefault, characteristics, reader);
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				elemName = reader.getLocalName();

				if (elemName.equals("measuredValue")) {
					if (firstSeen) {
						return;
					}

					firstSeen = true;
				}
				break;
			}

		}
	}

	private void readAverageVehicleSpeed(String id, Calendar timeDefault, MeasurementCharacteristics characteristics,
			XMLStreamReader reader) throws XMLStreamException, ParseException {
		while (reader.hasNext()) {
			int type = reader.next();

			switch (type) {
			case XMLStreamReader.START_ELEMENT:
				if (reader.getLocalName().equals("speed")) {
					double speed = Double.parseDouble(readCharacters(reader));
					
					influxWriter.measurementRead(new AverageVehicleSpeedMeasurement()
							.withAverageVehicleSpeed(speed)						
							.withMeasurementTime(timeDefault)
							.withMeasurementCharacteristics(characteristics));
				}
				
				break;
			case XMLStreamReader.END_ELEMENT:
				return;
			}

		}
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

	private void readMeasurementSiteTableReference(XMLStreamReader reader) throws XMLStreamException {
		tableId = reader.getAttributeValue(null, "id");
		tableVersion = Integer.parseInt(reader.getAttributeValue(null, "version"));
		
		while (reader.hasNext()) {
			int type = reader.next();

			if (type == XMLStreamReader.END_ELEMENT) {
				return;
			}
		}
	}

}
