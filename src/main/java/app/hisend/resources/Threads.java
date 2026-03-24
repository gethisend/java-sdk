package app.hisend.resources;

import app.hisend.HisendClient;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;

public class Threads {
    private final HisendClient client;

    public Threads(HisendClient client) {
        this.client = client;
    }

    public List<Map<String, Object>> list() {
        return client.request("GET", "threads", null, new TypeToken<List<Map<String, Object>>>(){}.getType());
    }

    public List<Map<String, Object>> getEmails(int id) {
        return client.request("GET", "threads/" + id + "/emails", null, new TypeToken<List<Map<String, Object>>>(){}.getType());
    }
}
