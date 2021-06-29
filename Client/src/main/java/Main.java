import context.LayoutManager;
import context.LayoutManagerFactory;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import restclient.RestClient;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.ThreadLocalRandom;

public class Main extends Application{

    @Override
    public void start(Stage primaryStage) {

        int randomNum = ThreadLocalRandom.current().nextInt(0, 3);
        String url;

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(new FileReader("Client/settings.json"));
            url = (String) jsonObj.get("AccessNode" + randomNum);
        }
        catch(FileNotFoundException e){
            url = "";
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
            return;
        }

        RestClient.launch(url);
        LayoutManager manager = LayoutManagerFactory.getManager();
        manager.startApp(primaryStage);
    }

    @Override
    public void stop() throws Exception {
        RestClient.close();
        super.stop();
    }

    public static void main(String args[]) {launch(args);}
}
