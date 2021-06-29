package common;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

public class Configuration {
    private final JSONObject configJson;
    private final JSONObject defaultConfigJson;
    private static volatile Configuration configuration;

    private static Configuration getConfiguration() {
        if (configuration == null) {
            synchronized (Configuration.class) {
                if (configuration == null) {
                    configuration = new Configuration();
                }
            }
        }
        return configuration;
    }

    private Configuration() {
        System.out.println("Creazione conf");
        JSONObject configJson;
        try {
            JSONParser parser = new JSONParser();
            configJson = (JSONObject) parser.parse(
                    new FileReader(
                            new File(
                                    getClass()
                                    .getClassLoader()
                                    .getResource
                                    (
                                "settings.json"
                                    )
                                    .toURI()
                            )
                    )
            );
        } catch (NullPointerException | IOException | ParseException | URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Using default configuration");
            configJson = new JSONObject();
        }
        this.configJson = configJson;
        this.defaultConfigJson = defaultConfig();
    }

    private JSONObject defaultConfig() {
        JSONObject configJson = new JSONObject();
        configJson.put("AuthenticationNode", "http://localhost:8090");
        configJson.put("Cookie", "");
        configJson.put("ControlNodeServerName", "control_node@localhost");
        configJson.put("ControlNodeServerRegisteredName", "control_server");
        // TODO: Probabilmente bisognera' cambiare il nome del nodo access, cosi' da avere univocita'
        configJson.put("AccessNodeName", "access_node@localhost");
        return configJson;
    }

    private static Object get(String key) {
        Configuration conf = getConfiguration();
        return conf.configJson.getOrDefault(
                key,
                conf.defaultConfigJson.get(key)
        );
    }

    public static String getUrlAuthNode() {
        return (String) get("AuthenticationNode");
    }

    public static String getCookie() {
        return (String) get("Cookie");
    }

    public static String getControlNodeServerName() {
        return (String) get("ControlNodeServerName");
    }

    public static String getControlNodeServerRegisteredName() {
        return (String) get("ControlNodeServerRegisteredName");
    }

    public static String getAccessNodeName() {
        return (String) get("AccessNodeName");
    }
}
