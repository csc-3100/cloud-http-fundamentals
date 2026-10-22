package javiergs.githubapi;

import javiergs.HttpApi;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

/** Uploads a bundled sample file using GitHub's Contents API (HTTP PUT). */
public class GithubUpload {
    public static void main(String[] args) {
        try {
            new GithubUpload().upload();
        } catch (Exception e) {
            System.err.println("Upload failed: " + e.getMessage());
        }
    }

    public void upload() throws Exception {
        Properties config = HttpApi.configuration();
        String owner = HttpApi.setting(config, "GITHUB_OWNER");
        String repo = HttpApi.setting(config, "GITHUB_REPO");
        String token = HttpApi.setting(config, "GITHUB_TOKEN");
        String branch = config.getProperty("GITHUB_BRANCH", "main");
        String filename = config.getProperty("GITHUB_FILE_PATH", "sample_data_file.csv");
        if (filename.contains("/") || filename.contains("\\") || filename.equals(".."))
            throw new IllegalArgumentException("Use a simple file name");
        byte[] bytes;
        try (var stream = getClass().getClassLoader().getResourceAsStream("sample_data_file.csv")) {
            if (stream == null) throw new IllegalStateException("Missing sample_data_file.csv");
            bytes = stream.readAllBytes();
        }
        String server = config.getProperty("GITHUB_API_SERVER", "https://api.github.com");
        String url = server.replaceAll("/+$", "") + "/repos/" + owner + "/" + repo +
                "/contents/" + URLEncoder.encode(filename, StandardCharsets.UTF_8);
        JSONObject payload = new JSONObject().put("message", "Upload sample data")
                .put("content", Base64.getEncoder().encodeToString(bytes)).put("branch", branch);
        HttpRequest request = HttpApi.request(url)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(payload.toString())).build();
        System.out.println("GitHub response: " + HttpApi.send(request).statusCode());
    }
}
