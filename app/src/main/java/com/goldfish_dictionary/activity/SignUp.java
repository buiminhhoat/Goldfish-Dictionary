package com.goldfish_dictionary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.goldfish_dictionary.connection.ConnectToMySQL;
import com.goldfish_dictionary.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignUp extends AppCompatActivity {
    private Connection connection = null;
    private EditText txt_username = null;
    private EditText txt_email = null;
    private EditText txt_password = null;
    private EditText txt_confirm_password = null;

    private Button btn_sign_up = null;

    private TextView tv_signup = null;

    private ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        map();

        connection = ConnectToMySQL.getConnection(this);

        clickBtnSignUp();
        clickTvSignUp();
        clickBtnBack();
    }

    private void clickBtnSignUp() {
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createAccount();
                    Toast.makeText(getApplicationContext(), "Account successfully created", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUp.this, SignIn.class);
                    startActivity(intent);
                    finish();
                } catch (Exception exception) {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clickTvSignUp() {
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void map() {
        btn_back = findViewById(R.id.btn_back);
        txt_username = findViewById(R.id.txt_username);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        txt_confirm_password = findViewById(R.id.txt_confirm_password);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        tv_signup = findViewById(R.id.tv_signup);
    }

    private void clickBtnBack() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void createAccount() throws Exception {
        String username = txt_username.getText().toString().trim();
        String email = txt_email.getText().toString().trim();
        String password = txt_password.getText().toString().trim();
        String confirm_password = txt_confirm_password.getText().toString().trim();

        if (connection == null) {
            throw new Exception("Unable to connect to server\nPlease check your internet connection");
        }

        if (TextUtils.isEmpty(username)) {
            throw new Exception("Enter username address!");
        }

        if (TextUtils.isEmpty(email)) {
            throw new Exception("Enter email address!");
        }

        if (TextUtils.isEmpty(password)) {
            throw new Exception("Enter password!");
        }

        if (TextUtils.isEmpty(confirm_password)) {
            throw new Exception("Enter confirm_password!!");
        }

        if (password.length() < 6) {
            throw new Exception("Password too short, enter minimum 6 characters!");
        }

        if (!password.equals(confirm_password)) {
            throw new Exception("Your password and confirmation password must match!");
        }
        // create a Statement from the connection
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new Exception("Unable to connect to database!");
        }

        ResultSet resultSet = connection.createStatement().executeQuery("SELECT username " + "FROM user");
        while (resultSet.next()) {
            if (resultSet.getString("username").equals(email)) {
                throw new Exception("Username already exists");
            }
        }

        resultSet = connection.createStatement().executeQuery("SELECT email " + "FROM user");
        while (resultSet.next()) {
            if (resultSet.getString("email").equals(email)) {
                throw new Exception("Email already exists");
            }
        }
        statement.executeUpdate("INSERT INTO user(username, email, password_hash) "
                + "VALUES (\"" + username + "\", \"" + email + "\", \"" + password + "\")");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUp.this, SignIn.class);
        startActivity(intent);
        finish();
    }
}