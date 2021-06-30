package homepagemanager;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class FilePane extends AnchorPane {

    private Label username;
    private Label fileName;

    public FilePane(String username, String fileName){
        this.username = new Label(username);
        this.fileName = new Label(fileName);
        this.getChildren().addAll(this.username, this.fileName);
        this.fileName.setLayoutX(50.0);
    }

}
