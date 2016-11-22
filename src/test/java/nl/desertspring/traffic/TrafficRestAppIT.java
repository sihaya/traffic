package nl.desertspring.traffic;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import spark.Spark;

public class TrafficRestAppIT {
	private ValidatableResponse response;
	
	@BeforeClass
	public static void start() throws Exception {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = 4567;
		
		TrafficRestApp.main(new String[] { "src/test/resources/mst_for_radius.xml" });
		
		Spark.awaitInitialization();
	}
	
	@AfterClass
	public static void stop() {
		Spark.stop();
	}

	@Test
	public void returnsMeasurementPointsWithinAWgs84Radius() {
		givenAnMstWithSomeMeasurementPoints();
		
		whenRequestingMeasurementPointsWithinRadius();
		
		thenVerifyItReturnsTheMeasurementPointsWithinTheRadius();
	}
	
	@Test
	public void returnsMeasurementDataFromTheLastHourForAMeasurementLocation() throws Exception {
		givenACleanDatabase();
		givenTheClockIsSetToSomeFixedTime();
		givenAnMstWithSomeMeasurementPoints();
		givenMultipleDataPublicationsFromTheLastHour();
		
		whenRequestingDataForTheMeasurementLocation();
		
		thenItReturnsTheDataFromTheLastHour();
	}

	private void givenACleanDatabase() {
		InfluxWriter.DB_NAME = "traffic_it";
		
		InfluxDB influxDb = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
				
		influxDb.deleteDatabase(InfluxWriter.DB_NAME);
	}

	private void thenItReturnsTheDataFromTheLastHour() {
		response
		.contentType(ContentType.JSON)
		.body("lanes.size()", is(1));
	}

	private void whenRequestingDataForTheMeasurementLocation() {
		response = given()
				.param("period", "1")
				.param("time_unit", "hour")
				.param("type", "average_speed")
			.when()
				.get("measurements/{0}", "PZH01_MST_0004_00")
			.then();
	}

	private void givenMultipleDataPublicationsFromTheLastHour() throws Exception {
		TrafficImportApp.main(new String[] {
				"src/test/resources/mst_for_radius.xml",
				"src/test/resources/traffic_speed_sample.xml"
		});
	}

	private void givenTheClockIsSetToSomeFixedTime() {
		TrafficRestApp.setClock(4234234);
	}

	private void thenVerifyItReturnsTheMeasurementPointsWithinTheRadius() {
		response
			.contentType(ContentType.JSON)
			.body("size()", is(3));
	}

	private void whenRequestingMeasurementPointsWithinRadius() {
		response = given()
			.param("north_east_lat", "53.22222")
			.param("north_east_lng", "53.22222")
			.param("south_west_lat", "4.2222")
			.param("south_west_lng", "4.2222")			
		.when()
			.get("measurementpoints")
		.then();
	}

	private void givenAnMstWithSomeMeasurementPoints() {
		// DONE IN BEFORE
	}	
}
