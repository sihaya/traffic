package nl.desertspring.traffic;

public class MeasurementCharacteristics {
	public static enum MeasurementType {
		TRAFFIC_SPEED, UNKNOWN
	}

	private String id;
	private int lane;
	private MeasurementType type;
	private boolean anyVehicleType;
	private double period;
	private double lat;
	private double lng;
	
	public MeasurementCharacteristics withId(String id) {
		this.id = id;
		
		return this;
	}

	public MeasurementCharacteristics withLane(int lane) {
		this.lane = lane;

		return this;
	}

	public MeasurementCharacteristics withType(MeasurementType type) {
		this.type = type;

		return this;
	}

	public MeasurementCharacteristics withAnyVehicleType(boolean anyVehicleType) {
		this.anyVehicleType = anyVehicleType;

		return this;
	}
	
	public MeasurementCharacteristics withPeriod(double period) {
		this.period = period;
		
		return this;
	}
	
	public MeasurementCharacteristics withLatLng(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
		return this;
	}	

	public int getLane() {
		return lane;
	}

	public MeasurementType getType() {
		return type;
	}

	public boolean isAnyVehicleType() {
		return anyVehicleType;
	}
	
	public String getId() {
		return id;
	}
	
	public double getPeriod() {
		return period;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLng() {
		return lng;
	}
}
