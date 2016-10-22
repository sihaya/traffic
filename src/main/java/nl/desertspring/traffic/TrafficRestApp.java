package nl.desertspring.traffic;

import static spark.Spark.*;
import com.google.gson.Gson;

public class TrafficRestApp {
	public static void main(String[] args) {
		Gson gson = new Gson();
        get("/hello", (req, res) -> {
        	return "";
        }, gson::toJson);
    }
}
