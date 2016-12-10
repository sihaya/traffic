package nl.desertspring.traffic.api;

import java.util.ArrayList;
import java.util.List;

public class MdpMeasurementLane {
	private String name;
	
	private List<MdpMeasurementData> measurements = new ArrayList<>();
	
	public String getName() {
		return name;
	}
	
	public List<MdpMeasurementData> getMeasurements() {
		return measurements;
	}
	
	public MdpMeasurementLane withName(String name) {
		this.name = name;		
		return this;
	}
}
