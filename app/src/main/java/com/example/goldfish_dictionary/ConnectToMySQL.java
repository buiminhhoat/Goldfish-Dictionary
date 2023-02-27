package com.example.goldfish_dictionary;

import static com.example.goldfish_dictionary.Constants.*;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;

public final class ConnectToMySQL {
    private static Connection connection = null;
    public ConnectToMySQL() {

    }

    public static synchronized Connection getConnection() {
        if (connection == null) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, PASSWORD);
            }
            catch (Exception e) {
                String error = e.toString();
            }
        }
        return connection;
    }
}
