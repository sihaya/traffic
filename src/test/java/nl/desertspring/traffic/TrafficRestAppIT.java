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
		givenAnMstWithSomeMeasurementPoints();
		givenMultipleDataPublicationsFromTheLastHour();
		
		whenRequestingDataForTheMeasurementLocation();
		
		thenItReturnsTheDataFromTheLastHour();
	}

	private void givenACleanDatabase() {
		Datex2MdpRepository.DB_NAME = "traffic_it";
		
		InfluxDB influxDb = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
				
		influxDb.deleteDatabase(Datex2MdpRepository.DB_NAME);
	}

	private void thenItReturnsTheDataFromTheLastHour() {
		response
		.log().all().and()
		.contentType(ContentType.JSON)
		.body("lanes.size()", is(1))
		.body("lanes[0].measurements[0].data[0].timestamp", is("2016-09-09T13:57:00Z"));
	}

	private void whenRequestingDataForTheMeasurementLocation() {
		response = given()
				.param("start_time", "2016-09-09T13:57:00Z")
				.param("period", "60")				
				.param("type", "traffic_speed")
			.when()
				.get("/measurements/{0}", "PZH01_MST_0004_00")
			.then();
	}

	private void givenMultipleDataPublicationsFromTheLastHour() throws Exception {
		TrafficImportApp.main(new String[] {
				"src/test/resources/mst_for_radius.xml",
				"BEGIN_MDP",
				"src/test/resources/traffic_speed_sample.xml"
		});
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
