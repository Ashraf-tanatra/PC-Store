package com.example.pcstore;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class Main extends Application {

    private static final String URL = "localhost";
    private static final String PORT = "3306";
    private static final String DB_NAME = "ia_computer_db";
    private static final String USER = "root";
    private static final String PASS = "12345";
    DBconn conn;
    BorderPane root;
    @Override
    public void start(Stage stage) throws IOException {
        root = new BorderPane();

        conn=new DBconn(URL, PORT, DB_NAME, USER, PASS);
        SignInScene sign = new SignInScene(this );
        sign.handle(null);
        root.setCenter(sign.getRoot());

        Scene scene = new Scene(root, 320, 240);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setMaximized(true);
    }
}
