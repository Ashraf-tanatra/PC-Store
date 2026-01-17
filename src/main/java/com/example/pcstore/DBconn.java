package com.example.pcstore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

//Connection Code With DataBase...
public class DBconn {
    private final String URL;
    private final String port;
    private final String dbName;
    private final String dbUsername;
    private final String dbPassword;

    public DBconn(String URL, String port, String dbName, String dbUsername, String dbPassword) {
        this.URL = URL;
        this.port = port;
        this.dbName = dbName;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    public Connection connectDB() throws ClassNotFoundException, SQLException {

        String dbURL = "jdbc:mysql://" + URL + ":" + port + "/" + dbName
                + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

        Properties p = new Properties();
        p.setProperty("user", dbUsername);
        p.setProperty("password", dbPassword);

        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(dbURL, p);
    }

}

