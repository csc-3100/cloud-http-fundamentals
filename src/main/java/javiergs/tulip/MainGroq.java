package javiergs.tulip;


import javiergs.tulip.groq.GroqClient;
import javiergs.tulip.groq.GroqConfig;

import java.io.InputStream;
import java.util.Properties;

public class MainGroq {
  public static void main(String[] args) throws Exception {

    Properties p = javiergs.HttpApi.configuration();

    String apiKey = javiergs.HttpApi.setting(p, "GROQ_API_KEY");
    String baseUrl = p.getProperty("GROQ_BASE_URL", "https://api.groq.com/openai/v1");
    String model = p.getProperty("GROQ_MODEL", "llama-3.1-8b-instant");

    GroqConfig cfg = new GroqConfig(apiKey, baseUrl, model);
    GroqClient groq = new GroqClient(cfg);

    String story = "As a user, I want to be able to write javadoc comments for my code ";
    String answer = groq.chat("Is the following a correct user story: " + story);
    System.out.println(answer);

    answer = groq.chat("Recommend a list of tasks for the story:  " + story);
    System.out.println(answer);
  }
}

