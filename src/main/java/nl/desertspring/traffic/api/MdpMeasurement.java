package nl.desertspring.traffic.api;

import java.util.List;

public class MdpMeasurement {
	private String id;
	
	private List<MdpMeasurementLane> lanes;

	public String getId() {
		return id;
	}

	public List<MdpMeasurementLane> getLanes() {
		return lanes;
	}

	public MdpMeasurement withId(String id) {
		this.id = id;
		
		return this;
	}

	public MdpMeasurement withLanes(List<MdpMeasurementLane> lanes) {
		this.lanes = lanes;
		
		return this;
	}
	
	
}
