package controllers;

import context.LayoutManager;
import context.LayoutManagerFactory;
import context.Path;
import homepagemanager.FilePane;
import homepagemanager.HomepageManager;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.Iterator;
import java.util.Map;

public class HomepageController implements UIController {

    @FXML
    private ScrollPane yourFilesPane;
    @FXML
    private Label yourFilesText;
    @FXML
    private ScrollPane allFilesPane;
    @FXML
    private Label allFilesText;
    @FXML
    private VBox allFilesBox;
    @FXML
    private VBox yourFilesBox;

    private Map<String, String> fileList;
    private HomepageManager manager;
    private String highlightedUsername;
    private String highlightedFilename;
    private LayoutManager layoutManager;

    public void init(){
        layoutManager = LayoutManagerFactory.getManager();
        manager = new HomepageManager();
        highlightedUsername = null;
        highlightedFilename = null;
        createList();
    }

    private void createList(){
        fileList = manager.getFileList();
        if(fileList == null){
            System.out.println("Cannot download list of files");
            return;
        }
        Iterator iter = fileList.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry pair = (Map.Entry) iter.next();
            FilePane newEntry = new FilePane((String)pair.getKey(), (String)pair.getValue());
            newEntry.setOnMouseClicked( e -> {
                highlight((String)pair.getKey(), (String)pair.getValue());
            });
            allFilesBox.getChildren().add(newEntry);
            if(layoutManager.context.getAuthenticatedUser() == pair.getKey())
                yourFilesBox.getChildren().add(newEntry);
        }
    }

    private void highlight(String username, String filename){
        highlightedFilename = filename;
        highlightedUsername = username;
    }

    public void downloadSelected(){
        if(manager.getByKey(highlightedUsername, highlightedFilename) == 1)
            System.out.println("Error: cannot connect to the server");
        else
            System.out.println("File downloaded correctly!");
    }

    public void uploadFile(){
        int res = manager.uploadFile(layoutManager.context.getAuthenticatedUser());
        if(res == 1)
            System.out.println("Error: cannot connect to the server");
        else if(res == 2)
            System.out.println("A problem occured while trying to upload the file");
        else if(res == 3)
            return;
        else if(res == 4)
            System.out.println("Error: it was not possible to insert the file inside the data node");
        else {
            System.out.println("File uploaded correctly!");
            refreshList();
        }
    }

    public void updateSelected(){
        if(!highlightedUsername.equals(layoutManager.context.getAuthenticatedUser())){
            System.out.println("You can't update a file it's not yours! Please select a file from the Your Files tab");
            return;
        }
        int result = manager.updateFile(highlightedUsername, highlightedFilename);
        if(result == 1)
            System.out.println("Error: cannot connect to the server");
        else if(result == 2)
            System.out.println("A problem occured while trying to upload the file");
        else if(result == 3)
            return;
        else if(result == 4)
            System.out.println("Error: it was not possible to insert the file inside the data node");
        else if(result == 5)
            System.out.println("Error: you're trying to upload a different file! Please select the correct one");
        else {
            System.out.println("File updated correctly!");
            refreshList();
        }
    }

    public void removeSelected(){
        if(!highlightedUsername.equals(layoutManager.context.getAuthenticatedUser())){
            System.out.println("You can't remove a file it's not yours! Please select a file from the Your Files tab");
            return;
        }
        int result = manager.removeSelected(highlightedUsername, highlightedFilename);
        if(result == 1)
            System.out.println("Error: cannot connect to the server");
        else if(result == 2)
            System.out.println("Error: the file was not found inside the ring");
        else {
            System.out.println("File removed correctly!");
            refreshList();
        }
    }

    public void refreshList(){
        createList();
    }

    public void logoutHandler(){
        if(manager.logout(layoutManager.context.getAuthenticatedUser()) == 2)
            System.out.println("Error during disconnection from the server, please contact support");
        else {
            System.out.println("Logout successful, see you soon!");
            layoutManager.setContent(Path.LOGIN);
        }
    }

}
