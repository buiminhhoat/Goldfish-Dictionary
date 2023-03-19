package com.goldfish_dictionary;

import static com.goldfish_dictionary.Constants.*;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public final class ConnectToMySQL {
    private static Connection connection = null;
    public ConnectToMySQL() {

    }

    private static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
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

    public static synchronized Connection getConnection(Context context) {
        if (isNetworkConnected(context) == false) return null;
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

    public static void update(String table, String key_id, int id, byte[] avatar_bitmap, String [] key, String [] value) throws SQLException {
        if (key.length != value.length) {
            throw new RuntimeException("Error update MySQL");
        }

        String queryUpdate = "UPDATE " + table + " SET ";

        for (int i = 0; i < key.length; ++i) {
            queryUpdate += key[i].toString() + " = \"" + value[i].toString() + "\"";
            if (i + 1 < key.length) {
                queryUpdate += ",";
            }
            queryUpdate += " ";
        }

        if (key.length > 0) {
            queryUpdate += ", ";
        }
        queryUpdate += "avatar_bitmap = ? ";
        queryUpdate += "WHERE " + key_id + " = " + id;
        System.out.println("queryUpdate: " + queryUpdate);

        PreparedStatement statement = connection.prepareStatement(queryUpdate);
        statement.setBytes(1, avatar_bitmap);
        statement.executeUpdate();
    }

    public static void insert(String table, String [] key, String [] value) throws SQLException {
//        statement.executeUpdate("INSERT INTO user(username, email, password_hash) "
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
        try {
            statement.executeUpdate(queryInsert);
        }
        catch (Exception exception) {
            return;
        }
    }

    public static void delete(String table, String [] key, String [] value) throws SQLException {
        String queryDelete = "DELETE FROM " + table + " WHERE ";
        for (int i = 0; i < key.length; ++i) {
            if (i + 1 < key.length) {
                queryDelete += key[i] + " = " + "\"" + value[i] + "\" AND ";
            }
            else {
                queryDelete += key[i] + " = " + "\"" + value[i] + "\"";
            }
        }
        queryDelete += ";";
        System.out.println(queryDelete);
        Statement statement = connection.createStatement();
        statement.execute(queryDelete);
    }
}
