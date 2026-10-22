package javiergs.openai;

import javiergs.HttpApi;
import org.json.JSONArray;
import org.json.JSONObject;
import java.beans.PropertyChangeSupport;
import java.net.http.HttpRequest;
import java.util.Properties;

/** Sends a prompt to OpenAI's Responses API on a worker thread. */
public class ChatbotBrain extends PropertyChangeSupport implements Runnable {
    private final String input;

    public ChatbotBrain(String input) {
        super(new Object());
        this.input = input;
    }

    @Override
    public void run() {
        String output;
        try {
            output = getResponse();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            output = "Request interrupted.";
        } catch (Exception e) {
            output = "Request failed: " + e.getMessage();
        }
        firePropertyChange("output", null, output);
    }

    private String getResponse() throws Exception {
        Properties config = HttpApi.configuration();
        String endpoint = config.getProperty("OPENAI_SERVER", "https://api.openai.com/v1/responses");
        String model = config.getProperty("OPENAI_MODEL", "gpt-4.1-mini");
        JSONObject payload = new JSONObject().put("model", model).put("input", input);
        HttpRequest request = HttpApi.request(endpoint)
                .header("Authorization", "Bearer " + HttpApi.setting(config, "OPENAI_API_KEY"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString())).build();
        JSONObject response = new JSONObject(HttpApi.send(request).body());
        StringBuilder text = new StringBuilder();
        JSONArray outputs = response.optJSONArray("output");
        if (outputs != null) for (int i = 0; i < outputs.length(); i++) {
            JSONArray parts = outputs.getJSONObject(i).optJSONArray("content");
            if (parts != null) for (int j = 0; j < parts.length(); j++) {
                JSONObject part = parts.getJSONObject(j);
                if ("output_text".equals(part.optString("type"))) text.append(part.optString("text"));
            }
        }
        if (text.isEmpty()) throw new IllegalStateException("No text in API response");
        return text.toString().trim();
    }
}
