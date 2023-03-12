package com.goldfish_dictionary;

import static com.goldfish_dictionary.Constants.*;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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

    public static void update(String table, String key_id, int id, String [] key, String [] value) throws SQLException {
        if (key.length != value.length) {
            throw new RuntimeException("Error update MySQL");
        }
        Statement statement = connection.createStatement();
        String queryUpdate = "UPDATE " + table + " SET ";

        for (int i = 0; i < key.length; ++i) {
            queryUpdate += key[i].toString() + " = \"" + value[i].toString() + "\"";
            if (i + 1 < key.length) {
                queryUpdate += ",";
            }
            queryUpdate += " ";
        }
        queryUpdate += "WHERE " + key_id + " = " + id;
        System.out.println("queryUpdate: " + queryUpdate);
        statement.executeUpdate(queryUpdate);
    }

    public static void insert(String table, String [] key, String [] value) throws SQLException {
//        statement.executeUpdate("INSERT INTO user(username, email, passwordHash) "
//                + "VALUES (\"" + username + "\", \"" + email + "\", \"" + password + "\")");
        Statement statement = connection.createStatement();
        String queryInsert = "INSERT INTO " + table;
        String keyString = "(";
        for (int i = 0; i < key.length; ++i) {
            if (i + 1 < key.length) {
                keyString += key[i].toString() + ", ";
            }
            else {
                keyString += key[i].toString() + ") ";
            }
        }
        queryInsert += keyString;
        String valueString = "VALUES (";
        for (int i = 0; i < value.length; ++i) {
            if (i + 1 < value.length) {
                valueString += "\"" + value[i] + "\",";
            }
            else {
                valueString += "\"" + value[i] + "\")";
            }
        }
        queryInsert += valueString;
        statement.executeUpdate(queryInsert);
    }

    public static void delete(String table, String [] key, String [] value) throws SQLException {
        String queryDelete = "DELETE FROM " + table + " WHERE ";
        for (int i = 0; i < key.length; ++i) {
            queryDelete += key[i] + " = " + value[i];
        }
        queryDelete += ";";
        Statement statement = connection.createStatement();
        statement.execute(queryDelete);
    }
}
