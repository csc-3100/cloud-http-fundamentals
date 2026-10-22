![Static Badge](https://img.shields.io/badge/author-javiergs-orange)

# HTTP Fundamentals

Java desktop examples of **consuming third-party HTTP APIs**, rather than implementing a REST server.

## Examples

- **Weather:** HTTP GET, JSON parsing (AccuWeather).
- **Maps:** HTTP GET, binary image response (legacy Bing Maps example).
- **Chatbot:** HTTP POST, JSON request/response, bearer authentication (OpenAI Responses API).
- **GitHub:** HTTP GET to list/download repository files and HTTP PUT to upload sample data.

The examples use Java's built-in `java.net.http.HttpClient`, status-code validation, timeouts, and `org.json`.

## Requirements

JDK 20 or later and Maven. API credentials are needed for each service used.

## Configuration

Copy `src/main/resources/config.sample.properties` to `src/main/resources/config.properties` and replace placeholders. This file is Git-ignored. Alternatively, supply matching environment variables; they take precedence for required credentials. Never commit API keys.

| Example | Required values | Optional values |
| --- | --- | --- |
| OpenAI | `OPENAI_API_KEY` | `OPENAI_SERVER`, `OPENAI_MODEL` |
| Weather | `WEATHER_API_KEY` | `WEATHER_SERVER` |
| Maps | `MAPS_API_KEY` | `MAPS_SERVER` |
| GitHub upload | `GITHUB_TOKEN`, `GITHUB_OWNER`, `GITHUB_REPO` | `GITHUB_API_SERVER`, `GITHUB_BRANCH`, `GITHUB_FILE_PATH` |

GitHub upload needs a fine-grained token with **Contents: Read and write** permission for the destination repository. The upload example creates a new file; it does not overwrite an existing one. External API services may charge for usage.

## Run

```bash
mvn compile
mvn exec:java -Dexec.mainClass=javiergs.Main
```

To run only the chatbot, use `-Dexec.mainClass=javiergs.openai.ChatbotMain`. The GitHub download/upload classes also have standalone `main` methods.

The combined Swing app calls weather and map APIs at startup, so missing credentials or retired third-party services may display errors. Bing Maps is a **legacy example** and may require replacement by a currently supported maps provider.
