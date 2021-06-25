package context;

import controllers.UIController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        System.out.println(path);

        try {
            primary.setScene(new Scene(authLoader.load(), 862, 628));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        UIController controller = authLoader.getController();
        controller.init();
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
