package nl.desertspring.traffic;

public class MeasurementCharacteristics {
	public static enum MeasurementType {
		TRAFFIC_SPEED
	}

	private String id;
	private int lane;
	private MeasurementType type;
	private boolean anyVehicleType;
	
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
}
