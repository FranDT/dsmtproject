package controllers;

import authentication.AuthResult;
import authentication.AuthenticationFactory;
import authentication.AuthenticationManager;
import context.LayoutManager;
import context.LayoutManagerFactory;
import context.Path;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AuthenticationController implements UIController {

    @FXML
    private Label resultField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    private LayoutManager manager;
    private AuthenticationManager authManager;

    @Override
    public void init(){
        authManager = AuthenticationFactory.CreateAuthenticationManager();
        this.manager = LayoutManagerFactory.getManager();
    }

    @FXML
    public void login(ActionEvent event) {
        if(authManager != null && manager != null){
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (username.isEmpty()) {
                resultField.setText("Username not set");
                return;
            }
            if (password.isEmpty()) {
                resultField.setText("Password not set");
                return;
            }
            if (username.contains("-") || password.contains("-")) {
                resultField.setText("'-' is not permitted");
                return;
            }
            AuthResult result = authManager.login(username, password);
            if(result.success){
                manager.context.setAuthenticatedUser(result.authenticated);
                resultField.setText("Login");
                manager.setContent(Path.MAIN_PAGE);
            }else{
                resultField.setText(result.errorMsg);
            }
        }
    }

    @FXML
    public void register(ActionEvent event){
        if(authManager != null && manager != null){
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (username.isEmpty()) {
                resultField.setText("Username not set");
                return;
            }
            if (password.isEmpty()) {
                resultField.setText("Password not set");
                return;
            }
            if (username.contains("-") || password.contains("-")) {
                resultField.setText("'-' is not permitted");
                return;
            }
            AuthResult result = authManager.register(username, password);
            if(result.success){
                manager.context.setAuthenticatedUser(result.authenticated);
                resultField.setText("Login");
                manager.setContent(Path.MAIN_PAGE);
            }else{
                resultField.setText(result.errorMsg);
            }
        }
    }
}
