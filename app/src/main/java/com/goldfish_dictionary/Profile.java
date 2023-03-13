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
import java.util.List;

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
        if (info.first_name.equals("null")) {
            first_name_profile.setHint("");
        } else {
            first_name_profile.setHint(info.first_name);
        }

        if (info.last_name.equals("null")) {
            last_name_profile.setHint("");
        } else {
            last_name_profile.setHint(info.last_name);
        }
    }

    private void clickBtnSaveModified() {
        btn_save_modified = findViewById(R.id.btn_save_modified);
        btn_save_modified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = username_profile.getText().toString().trim();
                String last_name = last_name_profile.getText().toString().trim();
                String first_name = first_name_profile.getText().toString().trim();
                String email = email_profile.getText().toString().trim();
                String password_hash = password_profile.getText().toString().trim();

                if (username.equals("")) username = info.username;
                if (last_name.equals("")) last_name = info.last_name;
                if (first_name.equals("")) first_name = info.first_name;
                if (email.equals("")) email = info.email;
                if (password_hash.equals("")) password_hash = info.password_hash;

                if (username.equals(info.username) &&
                    last_name.equals(info.last_name) &&
                    first_name.equals(info.first_name) &&
                    email.equals(info.email) && password_hash.equals(info.password_hash)
                    ) {
                    Toast.makeText(getApplicationContext(), "Nothing changes", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    ConnectToMySQL.update("user", "user_id", info.user_id,
                            new String[] {"username", "first_name", "last_name", "email", "password_hash"},
                            new String[] {username, first_name, last_name, email, password_hash});
                    databaseHelper.clearTable("user");
                    databaseHelper.insertTableUser(info.user_id, username, first_name, last_name, email, password_hash);
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
