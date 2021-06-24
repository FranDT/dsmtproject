package common;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.inject.Singleton;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class Configuration {
    private final JSONObject configJson;
    private final JSONObject defaultConfigJson;

    public Configuration() {
        JSONObject configJson;
        try {
            JSONParser parser = new JSONParser();
            configJson = (JSONObject) parser.parse(new FileReader("AccessNode/settings.json"));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.out.println("Using default configuration");
            configJson = new JSONObject();
        }
        this.configJson = configJson;
        this.defaultConfigJson = defaultConfig();
    }

    private JSONObject defaultConfig() {
        JSONObject configJson = new JSONObject();
        configJson.put("AuthenticationNode", "localhost:8090");
        configJson.put("Cookie", "");
        configJson.put("ControlNodeServerName", "control_node@localhost");
        configJson.put("ControlNodeServerRegisteredName", "control_server");
        configJson.put("AccessNodeName", "access_node_"+ ThreadLocalRandom.current().nextLong() +"@localhost");
        return configJson;
    }


    public String getUrlAuthNode() {
        return (String) configJson.getOrDefault(
                "AuthenticationNode",
                defaultConfigJson.get("AuthenticationNode")
        );
    }

    public String getCookie() {
        return (String) configJson.getOrDefault(
                "Cookie",
                defaultConfigJson.get("Cookie")
        );
    }

    public String getControlNodeServerName() {
        return (String) configJson.getOrDefault(
                "ControlNodeServerName",
                defaultConfigJson.get("ControlNodeServerName")
        );
    }

    public String getControlNodeServerRegisteredName() {
        return (String) configJson.getOrDefault(
                "ControlNodeServerRegisteredName",
                defaultConfigJson.get("ControlNodeServerRegisteredName")
        );
    }

    public String getAccessNodeName() {
        return (String) configJson.getOrDefault(
                "AccessNodeName",
                defaultConfigJson.get("AccessNodeName")
        );
    }
}
