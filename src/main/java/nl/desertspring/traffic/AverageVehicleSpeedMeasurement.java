package nl.desertspring.traffic;

import java.util.Calendar;

public class AverageVehicleSpeedMeasurement {
	private MeasurementCharacteristics measurementCharacteristics;
	private int lane;
	private double averageVehicleSpeed;
	private Calendar measurementTime;
	
	public AverageVehicleSpeedMeasurement withMeasurementCharacteristics(MeasurementCharacteristics measurementCharacteristics) {
		this.measurementCharacteristics = measurementCharacteristics;
		
		return this;
	}
	
	public AverageVehicleSpeedMeasurement withAverageVehicleSpeed(double averageVehicleSpeed) {
		this.averageVehicleSpeed = averageVehicleSpeed;
		
		return this;		
	}

	public AverageVehicleSpeedMeasurement withMeasurementTime(Calendar measurementTime) {
		this.measurementTime = measurementTime;
		
		return this;
	}
	
	public AverageVehicleSpeedMeasurement withLane(int lane) {
		this.lane = lane;
		
		return this;
	}

	public MeasurementCharacteristics getMeasurementPoint() {
		return measurementCharacteristics;
	}

	public int getLane() {
		return lane;
	}

	public double getAverageVehicleSpeed() {
		return averageVehicleSpeed;
	}

	public Calendar getMeasurementTime() {
		return measurementTime;
	}
}
