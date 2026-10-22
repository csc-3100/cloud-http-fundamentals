package javiergs.githubapi;

import javiergs.HttpApi;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

/** Downloads top-level files from a public GitHub repository. */
public class GitHubDownload {
    public static void main(String[] args) {
        try {
            new GitHubDownload().downloadFiles("csc-3100", "Game-Pong", "main");
        } catch (Exception e) {
            System.err.println("Download failed: " + e.getMessage());
        }
    }

    public void downloadFiles(String owner, String repo, String branch) throws Exception {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/contents?ref=" + branch;
        HttpRequest request = HttpApi.request(url).header("Accept", "application/vnd.github+json").GET().build();
        JSONArray files = new JSONArray(HttpApi.send(request).body());
        Path destination = Path.of("downloads").toAbsolutePath().normalize();
        Files.createDirectories(destination);
        for (int i = 0; i < files.length(); i++) {
            JSONObject file = files.getJSONObject(i);
            if (!"file".equals(file.optString("type"))) continue;
            String downloadUrl = file.optString("download_url");
            if (downloadUrl.isBlank()) continue;
            Path target = destination.resolve(file.getString("name")).normalize();
            if (!target.startsWith(destination)) throw new IOException("Unsafe filename");
            HttpResponse<byte[]> response = HttpApi.client().send(
                    HttpApi.request(downloadUrl).GET().build(), HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() < 200 || response.statusCode() >= 300)
                throw new IOException("Download HTTP " + response.statusCode());
            Files.write(target, response.body());
            System.out.println("Downloaded " + target);
        }
    }
}
