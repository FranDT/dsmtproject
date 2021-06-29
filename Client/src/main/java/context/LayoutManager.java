package context;

import controllers.UIController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

    public void setContent(String resource){
        FXMLLoader loader = getLoader(resource);
        // ref: "https://stackoverflow.com/questions/37164835/how-can-i-fix-a-nullpointerexception-when-interacting-with-fxml-elements-in-the#:~:text=The%20FXMLLoader%20creates%20a%20instance%20of%20the%20controller,and%20use%20getController%20%28%29%20after%20loading%20the%20fxml."
        try {
            Parent root = loader.load();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        UIController controller = loader.getController();
        controller.init();
        primary.show();
    }
}
