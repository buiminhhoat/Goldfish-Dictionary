package com.goldfish_dictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private Button button_profile;
    private Button btn_log_out;

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

        email_profile.setHint(info.email);

        clickBtnLogOut();
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
