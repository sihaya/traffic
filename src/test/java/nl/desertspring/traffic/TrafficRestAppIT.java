package nl.desertspring.traffic;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

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
