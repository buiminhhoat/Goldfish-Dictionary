package com.goldfish_dictionary.activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.goldfish_dictionary.connection.ConnectToMySQL;
import com.goldfish_dictionary.connection.DatabaseHelper;
import com.goldfish_dictionary.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class SignIn extends AppCompatActivity {
    private static final int RC_SIGN_IN = 200;
    private DatabaseHelper databaseHelper;
    private Connection connection = null;
    private EditText txt_username = null;
    private EditText txt_password = null;
    private ImageButton btn_login_facebook;

    private ImageButton btn_login_google;
    private CallbackManager callbackManager;
    private TextView tv_createNewOne;

    private ImageView btn_back;
    private Button btn_sign_in;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        map();

        databaseHelper = new DatabaseHelper(SignIn.this, "goldfish_dictionary_client.db");

        connection = ConnectToMySQL.getConnection(this);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("onSuccess");
                AccessToken accessToken = loginResult.getAccessToken();
                getUserProfileFacebook(accessToken);
            }

            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                System.out.println("onCancel");
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        clickBtnLoginFacebook();
        clickBtnLoginGoogle();
        clickTvCreateNewOne();
        clickBtnSignIn();
        clickBtnBack();
    }

    private void clickBtnBack() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void clickBtnLoginFacebook() {
        btn_login_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(SignIn.this, Arrays.asList("public_profile", "email"));
            }
        });
    }

    private void clickBtnLoginGoogle() {
        btn_login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void clickTvCreateNewOne() {
        tv_createNewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void clickBtnSignIn() {
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean sucessLogin = loginAccount();
                    if (sucessLogin) {
                        Toast.makeText(getApplicationContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignIn.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void map() {
        btn_back = findViewById(R.id.btn_back);
        txt_username = findViewById(R.id.txt_username);
        txt_password = findViewById(R.id.txt_password);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        tv_createNewOne = findViewById(R.id.tv_createNewOne);
        btn_login_google = findViewById(R.id.btn_login_google);
        btn_login_facebook = findViewById(R.id.btn_login_facebook);
    }

    private void registerAccountWithSocial(String username, String name, String email) throws Exception {
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
                return;
            }
        }

        resultSet = connection.createStatement().executeQuery("SELECT email " + "FROM user");
        while (resultSet.next()) {
            if (resultSet.getString("email").equals(email)) {
                return;
            }
        }
        String query = "INSERT INTO user(username, first_name, email) "
                + "VALUES (\"" + username + "\", \"" + name + "\"" + ", \"" + email + "\")";
        statement.executeUpdate(query);
    }
    private boolean loginAccountWithSocial(String username, String name, String email) throws Exception {
        if (connection == null) {
            throw new Exception("Unable to connect to server\nPlease check your internet connection");
        }
        // create a Statement from the connection
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new Exception("Unable to connect to database!");
        }

        registerAccountWithSocial(username, name, email);

        String query = "SELECT * " + "FROM user WHERE username = " + "\"" + username + "\";";
        ResultSet resultSet = statement.executeQuery(query);
        boolean res = resultSet.next();
        if (!res) {
            return false;
        }
        Blob blob = resultSet.getBlob("avatar_bitmap");
        byte[] avatar = null;
        if (blob != null) {
            avatar = blob.getBytes(1, (int) blob.length());
        }
        if (databaseHelper.isEmpty("user")) {
            databaseHelper.insertTableUser(resultSet.getInt("user_id"),
                    resultSet.getString("username"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email"),
                    resultSet.getString("password_hash"),
                    avatar
            );
        }
        return true;
    }

    private boolean loginAccount() throws Exception {
        String username = txt_username.getText().toString().trim();
        String password = txt_password.getText().toString().trim();

        if (connection == null) {
            throw new Exception("Unable to connect to server\nPlease check your internet connection");
        }
        if (TextUtils.isEmpty(username)) {
            throw new Exception("Enter username address!");
        }

        if (TextUtils.isEmpty(password)) {
            throw new Exception("Enter password!");
        }

        if (password.length() < 6) {
            throw new Exception("Password too short, enter minimum 6 characters!");
        }

        // create a Statement from the connection
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new Exception("Unable to connect to database!");
        }

        String query = "SELECT * " + "FROM user WHERE username = " + "\"" + username + "\""
                + " AND password_hash = " + "\"" + password + "\";";
        ResultSet resultSet = statement.executeQuery(query);
        boolean res = resultSet.next();
        if (!res) {
            return false;
        }

        Blob blob = resultSet.getBlob("avatar_bitmap");
        byte[] avatar = null;
        if (blob != null) {
            avatar = blob.getBytes(1, (int) blob.length());
        }
        if (databaseHelper.isEmpty("user")) {
            databaseHelper.insertTableUser(resultSet.getInt("user_id"),
                                        resultSet.getString("username"),
                                        resultSet.getString("first_name"),
                                        resultSet.getString("last_name"),
                                        resultSet.getString("email"),
                                        resultSet.getString("password_hash"),
                                        avatar
                                        );
        }
        return true;
    }

    private void getUserProfileFacebook(AccessToken accessToken) {
        /**
         Creating the GraphRequest to fetch user details
         1st Param - AccessToken
         2nd Param - Callback (which will be invoked once the request is successful)
         **/
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String id = object.getString("id");
                    String name = object.getString("name");
                    String email = object.getString("email");
                    String image = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    System.out.println(id + " " + name + " " + email + " " + image);
                    try {
                        boolean sucessLogin = loginAccountWithSocial(email.split("@")[0], name, email);
                        if (sucessLogin) {
                            Toast.makeText(getApplicationContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String email = account.getEmail();
            String name = account.getDisplayName();
            Uri picture = account.getPhotoUrl();
            // Signed in successfully, show authenticated UI.
            try {
                boolean sucessLogin = loginAccountWithSocial(email.split("@")[0], name, email);
                if (sucessLogin) {
                    Toast.makeText(getApplicationContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignIn.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            System.out.println("Success");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignIn.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}