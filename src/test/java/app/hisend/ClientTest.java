package app.hisend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClientTest {

    private HttpServer server;
    private String baseUrl;
    private String capturedAuthorization;
    private String capturedContentType;
    private String capturedMethod;
    private String capturedPath;

    @BeforeEach
    public void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                capturedMethod = exchange.getRequestMethod();
                capturedPath = exchange.getRequestURI().getPath();
                capturedAuthorization = exchange.getRequestHeaders().getFirst("Authorization");
                capturedContentType = exchange.getRequestHeaders().getFirst("Content-Type");

                String response = "[{\"id\": 1, \"name\": \"example.com\"}]";
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }
        });
        
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.start();
        baseUrl = "http://localhost:" + server.getAddress().getPort();
    }

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void testGetDomainsHasCorrectHeadersAndDecodesJson() {
        HisendClient client = new HisendClient("test_api_key_123", baseUrl, java.net.http.HttpClient.newBuilder().build());

        List<Map<String, Object>> domains = client.domains().list();

        assertNotNull(domains);
        assertEquals(1, domains.size());
        assertEquals(1.0, ((Number) domains.get(0).get("id")).doubleValue());
        assertEquals("example.com", domains.get(0).get("name"));

        assertEquals("GET", capturedMethod);
        assertEquals("/domains", capturedPath);
        assertEquals("Bearer test_api_key_123", capturedAuthorization);
        assertEquals("application/json", capturedContentType);
    }
}
