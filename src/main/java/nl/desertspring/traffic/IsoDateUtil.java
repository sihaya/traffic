package nl.desertspring.traffic;

import java.util.Calendar;

public class IsoDateUtil {
	public static Calendar dateFromIso(String isoString) {
		return javax.xml.bind.DatatypeConverter.parseDate(isoString);
	}
}
