package javiergs.virtualearth;

import javiergs.HttpApi;
import java.awt.GridLayout;
import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.*;

/** Displays a map image downloaded over HTTP (legacy Bing Maps example). */
public class MapsPanel extends JPanel {
    public MapsPanel() {
        setLayout(new GridLayout(1, 1));
        try {
            add(new JLabel(getMap()));
        } catch (Exception e) {
            add(new JLabel("Map unavailable: " + e.getMessage()));
        }
    }

    private ImageIcon getMap() throws Exception {
        Properties config = HttpApi.configuration();
        String base = config.getProperty("MAPS_SERVER", "https://dev.virtualearth.net/REST/v1/Imagery/Map/Road");
        String url = base.replaceAll("/+$", "") + "/35.270378,-120.680656" +
                "?zoomLevel=10&mapSize=400,400&key=" +
                URLEncoder.encode(HttpApi.setting(config, "MAPS_API_KEY"), StandardCharsets.UTF_8);
        HttpRequest request = HttpApi.request(url).GET().build();
        HttpResponse<byte[]> response = HttpApi.client().send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() < 200 || response.statusCode() >= 300)
            throw new java.io.IOException("HTTP " + response.statusCode());
        var image = ImageIO.read(new ByteArrayInputStream(response.body()));
        if (image == null) throw new java.io.IOException("Response was not an image");
        return new ImageIcon(image);
    }
}
