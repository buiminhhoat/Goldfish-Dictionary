package com.goldfish_dictionary.activity;

import static com.goldfish_dictionary.utilities.Util.imageViewToByte;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.goldfish_dictionary.connection.ConnectToMySQL;
import com.goldfish_dictionary.connection.DatabaseHelper;
import com.goldfish_dictionary.R;
import com.goldfish_dictionary.object.User;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends Activity {
    private TextView username_profile;
    private EditText last_name_profile;
    private EditText first_name_profile;
    private EditText email_profile;
    private EditText password_profile;
    private EditText confirm_password_profile;
    private ImageView btn_search;
    private TextView btn_save_modified;
    private TextView btn_log_out;
    private CircleImageView avatar_profile;

    private User info;
    private DatabaseHelper databaseHelper;
    private Connection connection = null;

    private final int CAMERA_REQUEST = 8888;
    private final int GALLERY_REQUEST = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        map();

        initializationDatabase();

        Context context = this;
        Thread thread = new Thread(){
            public void run() {
                connection = ConnectToMySQL.getConnection(context);
            }
        };

        thread.start();
        loadInfo();
        clickBtnSearch();
        clickBtnLogOut();
        clickBtnSaveModified();
        clickAvatarProfile();
    }

    private void clickAvatarProfile() {
        avatar_profile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                boolean pick = true;
                if (pick) {
                    if(!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        PickImage();
                    }
                } else {
                    if(!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        PickImage();
                    }
                }
            }
        });
    }

    private void PickImage() {
        CropImage.activity().start(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private boolean checkStoragePermission() {
        boolean res2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res2;
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res1 && res2;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(resultUri);
                    Bitmap photo = BitmapFactory.decodeStream(inputStream);
                    avatar_profile.setImageBitmap(Bitmap.createScaledBitmap(photo, 400, 400, false));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void map() {
        username_profile = findViewById(R.id.username_profile);
        last_name_profile = findViewById(R.id.last_name_profile);
        first_name_profile = findViewById(R.id.first_name_profile);
        email_profile = findViewById(R.id.email_profile);
        password_profile = findViewById(R.id.password_profile);
        confirm_password_profile = findViewById(R.id.confirm_password_profile);
        btn_search = findViewById(R.id.btn_search);
        btn_save_modified = findViewById(R.id.btn_save_modified);
        btn_log_out = findViewById(R.id.btn_log_out);
        avatar_profile = findViewById(R.id.avatar_profile);
    }

    private void clickBtnSearch() {
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadInfo() {
        List<User> users = databaseHelper.getAllProfile("user");

        if (users.isEmpty()) {
            Intent intent = new Intent(Profile.this, SignIn.class);
            startActivity(intent);
            finish();
            return;
        }

        if (users.size() > 1) {
            throw new RuntimeException("More than one user!");
        }

        info = users.get(0);

        username_profile.setText(info.getUsername());
        email_profile.setHint(info.getEmail());
        if (info.getFirst_name().equals("null")) {
            first_name_profile.setHint("First name");
        } else {
            first_name_profile.setHint(info.getFirst_name());
        }

        if (info.getLast_name().equals("null")) {
            last_name_profile.setHint("Last name");
        } else {
            last_name_profile.setHint(info.getLast_name());
        }

        if (info.getAvatar_bitmap() != null) {
            byte[] avatar_byte = info.getAvatar_bitmap();
            Bitmap bitmap = BitmapFactory.decodeByteArray(avatar_byte, 0, avatar_byte.length);
            avatar_profile.setImageBitmap(bitmap);
        }
    }

    private void clickBtnSaveModified() {
        btn_save_modified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connection == null) {
                    Toast.makeText(getApplicationContext(), "Unable to connect to server!",
                            Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Please check your internet connection!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                String username = username_profile.getText().toString().trim();
                String last_name = last_name_profile.getText().toString().trim();
                String first_name = first_name_profile.getText().toString().trim();
                String email = email_profile.getText().toString().trim();
                String password_hash = password_profile.getText().toString().trim();
                String confirm_password = confirm_password_profile.getText().toString().trim();
                byte[] avatar = imageViewToByte(avatar_profile);

                if (!password_hash.equals(confirm_password)) {
                    Toast.makeText(getApplicationContext(), "Your password and confirmation password must match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (username.equals("")) username = info.getUsername();
                if (last_name.equals("")) last_name = info.getLast_name();
                if (first_name.equals("")) first_name = info.getFirst_name();
                if (email.equals("")) email = info.getEmail();
                if (password_hash.equals("")) password_hash = info.getPassword_hash();

                if (username.equals(info.getUsername()) &&
                        last_name.equals(info.getLast_name()) &&
                        first_name.equals(info.getFirst_name()) &&
                        email.equals(info.getEmail()) &&
                        password_hash.equals(info.getPassword_hash()) &&
                        Arrays.equals(avatar, info.getAvatar_bitmap())
                    ) {
                    Toast.makeText(getApplicationContext(), "Nothing changes", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    ConnectToMySQL.update("user", "user_id", info.getUser_id(), imageViewToByte(avatar_profile),
                            new String[] {"username", "first_name", "last_name", "email", "password_hash"},
                            new String[] {username, first_name, last_name, email, password_hash});
                    databaseHelper.clearTable("user");
                    databaseHelper.insertTableUser(info.getUser_id(),
                                                    username,
                                                    first_name,
                                                    last_name,
                                                    email,
                                                    password_hash,
                                                    imageViewToByte(avatar_profile));
                    loadInfo();
                    Toast.makeText(getApplicationContext(), "Profile change successful", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void clickBtnLogOut() {
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
                finish();
            }
        });
    }

    private void initializationDatabase() {
        databaseHelper = new DatabaseHelper(Profile.this, "goldfish_dictionary_client.db");
        databaseHelper.createDatabase();
    }
}
