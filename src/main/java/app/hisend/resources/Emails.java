package app.hisend.resources;

import app.hisend.HisendClient;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;

public class Emails {
    private final HisendClient client;

    public Emails(HisendClient client) {
        this.client = client;
    }

    public List<Map<String, Object>> list() {
        return client.request("GET", "emails", null, new TypeToken<List<Map<String, Object>>>(){}.getType());
    }

    public Map<String, Object> get(int id) {
        return client.request("GET", "emails/" + id, null, new TypeToken<Map<String, Object>>(){}.getType());
    }

    public Map<String, Object> send(Map<String, Object> data) {
        return client.request("POST", "emails", data, new TypeToken<Map<String, Object>>(){}.getType());
    }

    public Map<String, Object> sendBatch(List<Map<String, Object>> data) {
        return client.requestListBody("POST", "emails/batch", data, new TypeToken<Map<String, Object>>(){}.getType());
    }
}
