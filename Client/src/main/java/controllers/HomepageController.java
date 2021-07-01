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
import java.util.Set;

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

    private Map<String, Set<String>> fileList;
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
        allFilesBox.getChildren().clear();
        yourFilesBox.getChildren().clear();
        fileList = manager.getFileList();
        System.out.println(fileList.toString());
        if(fileList == null){
            System.out.println("Cannot download list of files");
            return;
        }
        Iterator iter = fileList.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry pair = (Map.Entry) iter.next();
            String username = (String)pair.getKey();
            // all files
            Iterator iterFileUser = ((Set<String>)pair.getValue()).iterator();
            while (iterFileUser.hasNext()) {
                String filename = (String) iterFileUser.next();
                FilePane newEntryAll = new FilePane(username, filename);
                newEntryAll.setOnMouseClicked( e -> highlight(username, filename));
                allFilesBox.getChildren().add(newEntryAll);
                // single user files
                if(layoutManager.context.getAuthenticatedUser().equals(username)) {
                    FilePane newEntryUser = new FilePane(username, filename);
                    newEntryUser.setOnMouseClicked( e -> {
                        highlight(username, filename);
                    });
                    yourFilesBox.getChildren().add(newEntryUser);
                }
            }

        }
    }

    private void highlight(String username, String filename){
        highlightedFilename = filename;
        highlightedUsername = username;
    }

    private void removeHighlight(){
        highlightedFilename = "";
        highlightedUsername = "";
    }

    public void downloadSelected(){
        if(manager.getByKey(layoutManager.context.getAuthenticatedUser() + "-" + highlightedUsername, highlightedFilename) == 1)
            System.out.println("Error: cannot connect to the server");
        else
            System.out.println("File downloaded correctly!");
        removeHighlight();
    }

    public void uploadFile(){
        int res = manager.uploadFile(layoutManager.context.getAuthenticatedUser());
        removeHighlight();
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
        removeHighlight();
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
        removeHighlight();
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
