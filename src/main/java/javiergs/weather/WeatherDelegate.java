package javiergs.weather;

import javiergs.HttpApi;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/** Fetches current temperature through an HTTP GET request. */
public class WeatherDelegate {
    public double getTemperature() throws Exception {
        Properties config = HttpApi.configuration();
        String base = config.getProperty("WEATHER_SERVER", "https://dataservice.accuweather.com/currentconditions/v1");
        String url = base.replaceAll("/+$", "") + "/331999?apikey=" +
                URLEncoder.encode(HttpApi.setting(config, "WEATHER_API_KEY"), StandardCharsets.UTF_8);
        var request = HttpApi.request(url).GET().build();
        String body = HttpApi.send(request).body();
        JSONArray readings = new JSONArray(body);
        if (readings.isEmpty()) throw new IllegalStateException("Weather response is empty");
        JSONObject metric = readings.getJSONObject(0).getJSONObject("Temperature").getJSONObject("Metric");
        return metric.getDouble("Value");
    }
}
