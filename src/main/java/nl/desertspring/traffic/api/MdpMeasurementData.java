package nl.desertspring.traffic.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import nl.desertspring.traffic.MeasurementCharacteristics.MeasurementType;

public class MdpMeasurementData {
	public static class ValueTuple {
		private long value;
		private Calendar timestamp;

		public long getValue() {
			return value;
		}

		public Calendar getTimestamp() {
			return timestamp;
		}
		
		public ValueTuple withTimestamp(Calendar timestamp) {			
			this.timestamp = timestamp;
			return this;
		}
		
		public ValueTuple withValue(long value) {
			this.value = value;
			return this;
		}
	}

	private MeasurementType type;
	private List<ValueTuple> measurements = new ArrayList<>();

	public MeasurementType getType() {
		return type;
	}

	public List<ValueTuple> getMeasurements() {
		return measurements;
	}
	
	public MdpMeasurementData withMeasurements(List<ValueTuple> measurements) {
		this.measurements = measurements;
		
		return this;
	}
	
	public MdpMeasurementData withType(MeasurementType type) {
		this.type = type;
		
		return this;
	}

}
