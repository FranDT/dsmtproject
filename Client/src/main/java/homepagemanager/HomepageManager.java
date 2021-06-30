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
        if (!list.isEmpty()) {
            String[] temp = list.split(";");
            for (String s : temp) {
                String[] app = s.split("-");
                map.put(app[0], app[1]);
            }
        }
        return map;
    }

    public int getByKey(String username, String fileName){
        String result = RestClient.getByKey(username + "-" + fileName);
        if(result.equals("Cannot connect to the server"))
            return 1;
        else if(result.equals("Not found")){
            return 2;
        }
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
        return RestClient.delete(username + "-" + fileName);
    }

    public int uploadFile(String username){
        Map<String, String> fileMap = getFileContent();
        String fileName = fileMap.keySet().stream().findFirst().get();
        if(fileName.equals("File not uploaded"))
            return 3;
        else if(fileName.equals("An error occurred while reading the file, please try again"))
            return 2;
        return RestClient.post(username + "-" + fileName, fileMap.get(fileName));
    }

    public int updateFile(String username, String filename){
        Map<String, String> fileMap = getFileContent();
        String possibleFileName = fileMap.keySet().stream().findFirst().get();
        if(possibleFileName.equals("File not uploaded"))
            return 3;
        else if(possibleFileName.equals("An error occurred while reading the file, please try again"))
            return 2;

        if (filename.equals(possibleFileName)) {
            return RestClient.put(username + "-" + filename, fileMap.get(filename));
        }
        return 5;
    }

    private Map<String, String> getFileContent(){
        Map<String, String> result = new HashMap<>();
        JFileChooser chooser = new JFileChooser();
        byte[] bytes = null;
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            result.put("File not uploaded", null);
        }
        else {
            String path = "";
            try {
                path = chooser.getSelectedFile().getAbsolutePath();
                bytes = Files.readAllBytes(Paths.get(path));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            if (bytes == null) {
                result.put("An error occurred while reading the file, please try again", null);
            }
            else {
                result.put(Paths.get(path).getFileName().toString(), new String(Base64.getEncoder().encode(bytes)));
            }
        }
        return result;
    }

    public int logout(String username){
        return RestClient.deleteConnection(username);
    }
}
