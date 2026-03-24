package app.hisend.resources;

import app.hisend.HisendClient;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;

public class Domains {
    private final HisendClient client;

    public Domains(HisendClient client) {
        this.client = client;
    }

    public List<Map<String, Object>> list() {
        return client.request("GET", "domains", null, new TypeToken<List<Map<String, Object>>>(){}.getType());
    }

    public Map<String, Object> get(int id) {
        return client.request("GET", "domains/" + id, null, new TypeToken<Map<String, Object>>(){}.getType());
    }

    public Map<String, Object> verify(int id) {
        return client.request("GET", "domains/" + id + "/verify", null, new TypeToken<Map<String, Object>>(){}.getType());
    }

    public Map<String, Object> add(Map<String, Object> data) {
        return client.request("POST", "domains", data, new TypeToken<Map<String, Object>>(){}.getType());
    }

    public void delete(int id) {
        client.request("DELETE", "domains/" + id, null, Void.class);
    }
}
