package homepagemanager;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class FilePane extends AnchorPane {

    private Label username;
    private Label fileName;

    public FilePane(String username, String fileName){
        this.username.setText(username);
        this.fileName.setText(fileName);

    }
}
