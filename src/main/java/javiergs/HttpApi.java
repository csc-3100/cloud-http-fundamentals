package javiergs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

/** Small shared HTTP client for the teaching examples. */
public final class HttpApi {
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();

    private HttpApi() { }

    public static Properties configuration() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = HttpApi.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) properties.load(input);
        }
        return properties;
    }

    public static String setting(Properties properties, String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) value = properties.getProperty(name);
        if (value == null || value.isBlank() || value.equals("your_api_key_here"))
            throw new IllegalArgumentException("Missing configuration: " + name);
        return value.trim();
    }

    public static HttpClient client() { return CLIENT; }

    public static HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300)
            throw new IOException("HTTP " + response.statusCode() + ": " +
                    response.body().substring(0, Math.min(300, response.body().length())));
        return response;
    }

    public static HttpRequest.Builder request(String url) {
        return HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(20));
    }
}
