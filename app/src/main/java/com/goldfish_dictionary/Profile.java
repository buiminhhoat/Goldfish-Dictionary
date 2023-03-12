package com.goldfish_dictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Profile extends Activity {
    private DatabaseHelper databaseHelper;
    private TextView username_profile;
    private EditText last_name_profile;
    private EditText first_name_profile;
    private EditText email_profile;
    private EditText password_profile;
    private EditText confirm_password_profile;
    private Button btn_save_modified;
    private Button btn_log_out;
    private User info;
    private Connection connection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializationDatabase();

        connection = ConnectToMySQL.getConnection();
        username_profile = findViewById(R.id.username_profile);
        last_name_profile = findViewById(R.id.last_name_profile);
        first_name_profile = findViewById(R.id.first_name_profile);
        email_profile = findViewById(R.id.email_profile);
        password_profile = findViewById(R.id.password_profile);

        loadInfo();
        clickBtnLogOut();
        clickBtnSaveModified();
    }

    private void loadInfo() {
        List<User> users = databaseHelper.getAllProfile("user");

        if (users.isEmpty()) {
            Intent intent = new Intent(Profile.this, SignIn.class);
            startActivity(intent);
            return;
        }

        if (users.size() > 1) {
            throw new RuntimeException("More than one user!");
        }

        info = users.get(0);

        username_profile.setText(info.username);
        email_profile.setHint(info.email);
        if (info.firstName.equals("null")) {
            first_name_profile.setHint("");
        } else {
            first_name_profile.setHint(info.firstName);
        }

        if (info.lastName.equals("null")) {
            last_name_profile.setHint("");
        } else {
            last_name_profile.setHint(info.lastName);
        }
    }

    private void clickBtnSaveModified() {
        btn_save_modified = findViewById(R.id.btn_save_modified);
        btn_save_modified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = username_profile.getText().toString().trim();
                String lastName = last_name_profile.getText().toString().trim();
                String firstName = first_name_profile.getText().toString().trim();
                String email = email_profile.getText().toString().trim();
                String password = password_profile.getText().toString().trim();

                if (username.equals("")) username = info.username;
                if (lastName.equals("")) lastName = info.lastName;
                if (firstName.equals("")) firstName = info.firstName;
                if (email.equals("")) email = info.email;
                if (password.equals("")) password = info.passwordHash;

                if (username.equals(info.username) &&
                    lastName.equals(info.lastName) &&
                    firstName.equals(info.firstName) &&
                    email.equals(info.email) &&
                    password.equals(info.passwordHash)
                    ) {
                    Toast.makeText(getApplicationContext(), "Nothing changes", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    ConnectToMySQL.update("user", "user_id", info.user_id,
                            new String[] {"username", "firstName", "lastName", "email", "passwordHash"},
                            new String[] {username, firstName, lastName, email, password});
                    databaseHelper.clearTable("user");
                    databaseHelper.insertTableUser(info.user_id, username, firstName, lastName, email, password);
                    loadInfo();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), "Profile change successful", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void clickBtnLogOut() {
        btn_log_out = findViewById(R.id.btn_log_out);
        btn_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<User> users1 = databaseHelper.getAllProfile("user");
                System.out.println(users1.size());

                databaseHelper.clearTable("user");
                List<User> users = databaseHelper.getAllProfile("user");
                System.out.println(users.size());
                Intent intent = new Intent(Profile.this, SignIn.class);
                startActivity(intent);
            }
        });
    }

    private void initializationDatabase() {
        databaseHelper = new DatabaseHelper(Profile.this, "goldfish_dictionary_client.db");
        databaseHelper.createDatabase();

    }

}
