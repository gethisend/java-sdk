package app.hisend.resources;

import app.hisend.HisendClient;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;

public class Routing {
    private final HisendClient client;

    public Routing(HisendClient client) {
        this.client = client;
    }

    public List<Map<String, Object>> list(int domainId) {
        return client.request("GET", "domains/" + domainId + "/routing", null, new TypeToken<List<Map<String, Object>>>(){}.getType());
    }

    public Map<String, Object> create(int domainId, Map<String, Object> data) {
        return client.request("POST", "domains/" + domainId + "/routing", data, new TypeToken<Map<String, Object>>(){}.getType());
    }

    public Map<String, Object> update(int domainId, int id, Map<String, Object> data) {
        return client.request("PUT", "domains/" + domainId + "/routing/" + id, data, new TypeToken<Map<String, Object>>(){}.getType());
    }

    public Map<String, Object> get(int domainId, int id) {
        return client.request("GET", "domains/" + domainId + "/routing/" + id, null, new TypeToken<Map<String, Object>>(){}.getType());
    }

    public void delete(int domainId, int id) {
        client.request("DELETE", "domains/" + domainId + "/routing/" + id, null, Void.class);
    }
}
