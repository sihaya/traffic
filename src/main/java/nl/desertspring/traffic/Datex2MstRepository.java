package nl.desertspring.traffic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.desertspring.traffic.MeasurementCharacteristics.MeasurementType;

public class Datex2MstRepository {
	private static class MstKey {
		private String tableName;
		private int tableVersion;
		private String measurementSiteId;
		private int version;
		private int index;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + index;
			result = prime * result + ((measurementSiteId == null) ? 0 : measurementSiteId.hashCode());
			result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
			result = prime * result + tableVersion;
			result = prime * result + version;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MstKey other = (MstKey) obj;
			if (index != other.index)
				return false;
			if (measurementSiteId == null) {
				if (other.measurementSiteId != null)
					return false;
			} else if (!measurementSiteId.equals(other.measurementSiteId))
				return false;
			if (tableName == null) {
				if (other.tableName != null)
					return false;
			} else if (!tableName.equals(other.tableName))
				return false;
			if (tableVersion != other.tableVersion)
				return false;
			if (version != other.version)
				return false;
			return true;
		}
		
		public MstKey(String tableName, int tableVersion, String measurementSiteId, int version, int index) {
			this.tableName = tableName;
			this.tableVersion = tableVersion;
			this.measurementSiteId = measurementSiteId;
			this.version = version;
			this.index = index;
		}		
	}
	
	private Map<MstKey, MeasurementCharacteristics> data = new HashMap<>();

	public MeasurementCharacteristics findByIdAndIndex(String tableName, int tableVersion, String measurementSiteId, int version, int index) {
		return data.get(new MstKey(tableName, tableVersion, measurementSiteId, version, index));
	}

	public void save(String tableName, int tableVersion, String measurementSiteId, int version, int index, MeasurementCharacteristics characteristics) {
		data.put(new MstKey(tableName, tableVersion, measurementSiteId, version, index), characteristics);
	}

	public List<MeasurementCharacteristics> findByBounds(MeasurementType measurementType, double northEastLat, double northEastLng, double southWestLat, double southWestLng) {
		Map<String, MeasurementCharacteristics> results = new HashMap<>();
		
		for(MeasurementCharacteristics characteristics : data.values()) {
			if (characteristics.getLat() < northEastLat && characteristics.getLng() < northEastLng && 
					characteristics.getLat() > southWestLat && characteristics.getLng() > southWestLng &&
					characteristics.getType() == measurementType) {
				results.put(characteristics.getId(), characteristics);
			}
		}
		
		return new ArrayList<>(results.values());
	}

}
