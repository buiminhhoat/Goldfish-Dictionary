package com.goldfish_dictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Profile extends Activity {
    private DatabaseHelper databaseHelper;
    private TextView username_profile;
    private EditText last_name_profile;
    private EditText first_name_profile;
    private EditText email_profile;
    private EditText password_profile;
    private EditText confirm_password_profile;
    private Button button_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializationDatabase();

        List<User> users = databaseHelper.getAllProfile("user");

        if (users.isEmpty()) {
            Intent intent = new Intent(Profile.this, SignIn.class);
            startActivity(intent);
            return;
        }

        if (users.size() > 1) {
            throw new RuntimeException("More than one user!");
        }

        User info = users.get(0);

        username_profile = findViewById(R.id.username_profile);
        last_name_profile = findViewById(R.id.last_name_profile);
        first_name_profile = findViewById(R.id.first_name_profile);
        email_profile = findViewById(R.id.email_profile);

        username_profile.setText(info.username);
        last_name_profile.setHint(info.lastName);
        first_name_profile.setHint(info.firstName);
        email_profile.setHint(info.email);

    }

    private void initializationDatabase() {
        databaseHelper = new DatabaseHelper(Profile.this, "goldfish_dictionary_client.db");
        databaseHelper.createDatabase();

    }

}
