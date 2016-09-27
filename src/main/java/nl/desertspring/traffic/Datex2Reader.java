package nl.desertspring.traffic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class Datex2Reader {
	private static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
	private static final String DATEX_NS = "http://www.w3.org/2001/XMLSchema-instance";
	
	public Datex2Reader(Datex2MstRepository datex2MstRepository) {

	}

	public void parse(File file) throws Exception {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		
		XMLStreamReader reader = inputFactory.createXMLStreamReader(new FileInputStream(file));
				
		while(reader.hasNext()) {
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

	private void readPayloadPublication(XMLStreamReader reader) throws XMLStreamException {
		String publicationType = reader.getAttributeValue(XSI_NS, "type");
		
		if (!publicationType.equals("MeasuredDataPublication")) {
			return;
		}
				
		while(reader.hasNext()) {
			int type = reader.next();
			
			switch(type) {
			case XMLStreamReader.START_ELEMENT:
				String elemName = reader.getLocalName();
				switch(elemName) {
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

	private void readSiteMeasurements(XMLStreamReader reader) throws XMLStreamException {
		while(reader.hasNext()) {
			int type = reader.next();
			
			String id;
			
			String elemName;
			Calendar timeDefault;
			
			switch(type) {
			case XMLStreamReader.START_ELEMENT:
				elemName = reader.getLocalName();
				
				switch(elemName) {
				case "measurementSiteReference":
					id = reader.getAttributeValue(DATEX_NS, "id");
					break;
				case "measurementTimeDefault":
					timeDefault = readCharactersIsoTime(reader);
				}
				
			case XMLStreamReader.END_ELEMENT:
				elemName = reader.getLocalName();
				
				if (elemName.equals("siteMeasurements")) {
					return;
				}
			}
			
			
		}
	}

	private Calendar readCharactersIsoTime(XMLStreamReader reader) {
		// TODO Auto-generated method stub
		return null;
	}

	private void readMeasurementSiteTableReference(XMLStreamReader reader) throws XMLStreamException {
		while(reader.hasNext()) {
			int type = reader.next();
			
			if (type == XMLStreamReader.END_ELEMENT) {
				return;
			}
		}
	}

}
