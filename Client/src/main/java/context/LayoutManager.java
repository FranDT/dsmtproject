package context;

import controllers.UIController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LayoutManager {

    private Stage primary;
    public final ApplicationContext context = new ApplicationContext();

    LayoutManager() {}

    public void startApp(Stage primaryStage) {
        primary = primaryStage;
        primaryStage.setTitle("DSMTProject");
        showAuthenticationPage(Path.LOGIN);
        primaryStage.show();
    }

    public void showAuthenticationPage(String path){
        FXMLLoader authLoader = getLoader(path);

        try {
            primary.setScene(new Scene(authLoader.load(), 862, 628));
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public FXMLLoader getLoader(String path){
        return new FXMLLoader(getClass().getClassLoader().getResource(path));
    }

    public void setContent(String resource) {
        FXMLLoader loader = getLoader(resource);
        UIController controller = loader.getController();
        controller.init();
        primary.show();
    }
}
