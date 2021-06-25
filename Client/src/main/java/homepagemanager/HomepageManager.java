package homepagemanager;

import restclient.RestClient;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class HomepageManager {

    public HomepageManager() {}

    public Map<String, String> getFileList() {
        Map<String, String> map = new HashMap<>();

        String list = RestClient.getList();
        if(list.equals("Cannot connect to the server"))
            return null;

        String[] temp = list.split(";");
        for(String s : temp){
            String[] app = s.split(":");
            map.put(app[0],app[1]);
        }

        return map;
    }

    public int getByKey(String username, String fileName){
        String result = RestClient.getByKey(username + ":" + fileName);
        if(result.equals("Cannot connect to the server"))
            return 1;
        else {
            try{
                byte[] bytes = Base64.getDecoder().decode(result.getBytes());
                Files.write(Paths.get(System.getProperty("user.dir") + "/downloadedFiles"), bytes);
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
            return 0;
        }
    }

    public int removeSelected(String username, String fileName){
        return RestClient.delete(username + ":" + fileName);
    }

    public int uploadFile(String username, String fileName){
        String fileValue = getFileContent();
        if(fileValue.equals("File not uploaded"))
            return 3;
        else if(fileValue.equals("An error occurred while reading the file, please try again"))
            return 2;
        return RestClient.post(username + ":" + fileName, fileValue);
    }

    public int updateFile(String username, String fileName){
        String fileValue = getFileContent();
        if(fileValue.equals("File not uploaded"))
            return 3;
        else if(fileValue.equals("An error occurred while reading the file, please try again"))
            return 2;
        return RestClient.put(username + ":" + fileName, fileValue);
    }

    private String getFileContent(){
        JFileChooser chooser = new JFileChooser();
        byte[] bytes = null;
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal != JFileChooser.APPROVE_OPTION)
            return "File not uploaded";
        try {
            String path = chooser.getSelectedFile().getAbsolutePath();
            bytes = Files.readAllBytes(Paths.get(path));
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        if(bytes == null)
            return "An error occurred while reading the file, please try again";
        return new String(Base64.getEncoder().encode(bytes));
    }

    public int logout(String username){
        return RestClient.deleteConnection(username);
    }
}
