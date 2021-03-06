package nl.desertspring.traffic;

import static nl.desertspring.traffic.IsoDateUtil.dateFromIso;
import static nl.desertspring.traffic.IsoDateUtil.dateToIso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.desertspring.traffic.MeasurementCharacteristics.MeasurementType;
import nl.desertspring.traffic.api.MdpMeasurement;
import nl.desertspring.traffic.api.MdpMeasurementData;
import nl.desertspring.traffic.api.MdpMeasurementLane;

/**
 * select mean(average_speed), spread(average_speed) 
 * from average_vehicle_speed_measurement where average_speed != -1 and 
 * measurement_point = 'RWS01_MONIBAS_0201hrr0215ra' and time > '2016-12-12' and time < '2016-12-13' group by lane, time(15m)
 * @author sihaya
 *
 */
public class Datex2MdpRepository {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Datex2MdpRepository.class);
	
	private InfluxDB influxDb;
	public static String DB_NAME = "trafficTest";
	private BatchPoints batch;
	
	

	public Datex2MdpRepository() {
		String url = System.getProperty("traffic.db.url", "http://localhost:8086");
		String username = System.getProperty("traffic.db.username", "root");
		String password = System.getProperty("traffic.db.password", "root");	
		
		influxDb = InfluxDBFactory.connect(url, username, password);
	}
	
	public void measurementRead(AverageVehicleSpeedMeasurement speedMeasurement) {
		if (batch == null) {
			batch = BatchPoints
					.database(DB_NAME)					
					.consistency(ConsistencyLevel.ALL)
					.build();
		}
		
		Point point = Point.measurement("average_vehicle_speed_measurement")
                .time(speedMeasurement.getMeasurementTime().getTimeInMillis(), TimeUnit.MILLISECONDS)                                
                .addField("average_speed", speedMeasurement.getAverageVehicleSpeed())
                .tag("measurement_point", speedMeasurement.getMeasurementCharacteristics().getId())
                .tag("lane", Integer.toString(speedMeasurement.getMeasurementCharacteristics().getLane()))
                .tag("period", String.format("%.2f", speedMeasurement.getMeasurementCharacteristics().getPeriod()))
                .build();
		
		batch.point(point);		
	}

	public void flush() {
		if (batch == null) {
			return;
		}
		
		LOGGER.info("writing a batch of {} points", batch.getPoints().size());
		
		influxDb.write(batch);
		
		batch = null;
	}
		
	public void resetDb() {			
		influxDb.createDatabase(DB_NAME);
	}

	public MdpMeasurement findByPeriodAndType(String id, MeasurementType type, Calendar startTime, long period, int aggregationPeriodS) {
		Calendar endTime = (Calendar) startTime.clone();
		endTime.add(Calendar.SECOND, (int)period);
		
		
		String query = String.format("SELECT mean(average_speed) FROM average_vehicle_speed_measurement " 
				+ "WHERE average_speed != -1 and measurement_point = '%s' "
				+ " AND time >= '%s' AND time < '%s'"
				+ " group by lane, time(%ds)", 
				 	id, dateToIso(startTime), dateToIso(endTime), aggregationPeriodS);
		
		LOGGER.info("Querying: {}", query);
		
		QueryResult queryResult = influxDb.query(new Query(query, DB_NAME));			
		
		if (queryResult.hasError()) {
			LOGGER.error("error while querying: {}. Error was: {}", query, queryResult.getError());
			
			return null;
		}
		
		List<MdpMeasurementLane> lanes = processResults(queryResult);
		
		return new MdpMeasurement()
				.withId(id)
				.withLanes(new ArrayList<>(lanes));
	}

	private List<MdpMeasurementLane> processResults(QueryResult queryResult) {				
		List<MdpMeasurementLane> result = new ArrayList<>();		
		
		if (queryResult.getResults().get(0).getSeries() == null) {
			return result;
		}
		
		for (Series series : queryResult.getResults().get(0).getSeries()) {
			MdpMeasurementLane lane = new MdpMeasurementLane()
						.withName("lane_" + series.getTags().get("lane"));
							
			lane.getMeasurements().add(
					new MdpMeasurementData()
					.withType(MeasurementType.TRAFFIC_SPEED));
			
			for(List<Object> row : series.getValues()) {
				MdpMeasurementData.ValueTuple tuple = new MdpMeasurementData.ValueTuple()
						.withTimestamp(dateFromIso(row.get(0).toString()))
						.withValue(row.get(1) != null ? ((Double)row.get(1)).longValue() : 0);
				
				lane.getMeasurements().get(0).getData().add(tuple);
			}
			
			result.add(lane);
		}
		return result;
	}

	public void resetDbForTesting() {
		if (DB_NAME.equals("trafficTest")) {
			throw new IllegalStateException("Please set the testing database");
		}
		
		influxDb.deleteDatabase(DB_NAME);
		influxDb.createDatabase(DB_NAME);
	}

}
