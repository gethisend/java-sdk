package app.hisend;

import app.hisend.exceptions.HisendException;
import app.hisend.resources.Domains;
import app.hisend.resources.Emails;
import app.hisend.resources.Routing;
import app.hisend.resources.Threads;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class HisendClient {
    private final String apiKey;
    private final String baseUrl;
    private final HttpClient httpClient;
    private final Gson gson;

    private final Emails emails;
    private final Domains domains;
    private final Routing routing;
    private final Threads threads;

    public HisendClient(String apiKey) {
        this(apiKey, "https://api.hisend.app/v1", HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build());
    }

    public HisendClient(String apiKey, String baseUrl, HttpClient httpClient) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.httpClient = httpClient;
        this.gson = new Gson();

        this.emails = new Emails(this);
        this.domains = new Domains(this);
        this.routing = new Routing(this);
        this.threads = new Threads(this);
    }

    public Emails emails() { return emails; }
    public Domains domains() { return domains; }
    public Routing routing() { return routing; }
    public Threads threads() { return threads; }

    public <T> T request(String method, String endpoint, Map<String, Object> payload, Type responseType) {
        String urlString = this.baseUrl + (endpoint.startsWith("/") ? endpoint : "/" + endpoint);
        
        // Map 'from_' to 'from' to keep Java aesthetics if used
        if (payload != null && payload.containsKey("from_")) {
            payload.put("from", payload.remove("from_"));
        }

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .header("Authorization", "Bearer " + this.apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");

        if (payload != null) {
            String jsonPayload = gson.toJson(payload);
            requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(jsonPayload));
        } else {
            requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        HttpRequest request = requestBuilder.build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String responseBody = response.body();

            if (statusCode < 200 || statusCode >= 300) {
                String errorMessage = responseBody;
                try {
                    Map<String, Object> errorMap = gson.fromJson(responseBody, new TypeToken<Map<String, Object>>(){}.getType());
                    if (errorMap.containsKey("message")) {
                        errorMessage = String.valueOf(errorMap.get("message"));
                    } else if (errorMap.containsKey("error")) {
                        errorMessage = String.valueOf(errorMap.get("error"));
                    }
                } catch (JsonSyntaxException ignored) {
                }
                throw new HisendException("API request failed: " + errorMessage, statusCode);
            }

            if (responseBody == null || responseBody.trim().isEmpty()) {
                return null;
            }

            return gson.fromJson(responseBody, responseType);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new HisendException("HTTP Request failed: " + e.getMessage(), e);
        }
    }
    
    // Helper to send list payloads (like batch emails)
    public <T> T requestListBody(String method, String endpoint, List<Map<String, Object>> payload, Type responseType) {
        String urlString = this.baseUrl + (endpoint.startsWith("/") ? endpoint : "/" + endpoint);

        if (payload != null) {
            for (Map<String, Object> item : payload) {
                if (item.containsKey("from_")) {
                    item.put("from", item.remove("from_"));
                }
            }
        }

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .header("Authorization", "Bearer " + this.apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");

        if (payload != null) {
            String jsonPayload = gson.toJson(payload);
            requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(jsonPayload));
        } else {
            requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        HttpRequest request = requestBuilder.build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String responseBody = response.body();

            if (statusCode < 200 || statusCode >= 300) {
                // handle error similar to above
                throw new HisendException("API request failed: " + responseBody, statusCode);
            }

            return gson.fromJson(responseBody, responseType);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new HisendException("HTTP Request failed: " + e.getMessage(), e);
        }
    }
}
