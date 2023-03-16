package com.goldfish_dictionary;

import static com.goldfish_dictionary.Util.imageViewToByte;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

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
        connection = ConnectToMySQL.getConnection(this);

        loadInfo();
        clickBtnSearch();
        clickBtnLogOut();
        clickBtnSaveModified();
        clickAvatarProfile();
    }

//    private void camera() {
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, CAMERA_REQUEST);
//    }
//
//    private void gallery() {
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
//                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//        galleryIntent.setType("image/*");
//        galleryIntent.putExtra("crop", "true");
//        galleryIntent.putExtra("scale", true);
//        galleryIntent.putExtra("outputX", 256);
//        galleryIntent.putExtra("outputY", 256);
//        galleryIntent.putExtra("aspectX", 1);
//        galleryIntent.putExtra("aspectY", 1);
//        galleryIntent.putExtra("return-data", true);
//
//        startActivityForResult(galleryIntent, GALLERY_REQUEST);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            avatar_profile.setImageBitmap(Bitmap.createScaledBitmap(photo, 200, 200, false));
//        }
//    }

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
                    avatar_profile.setImageBitmap(Bitmap.createScaledBitmap(photo, 200, 200, false));
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
            return;
        }

        if (users.size() > 1) {
            throw new RuntimeException("More than one user!");
        }

        info = users.get(0);

        username_profile.setText(info.username);
        email_profile.setHint(info.email);
        if (info.first_name.equals("null")) {
            first_name_profile.setHint("First name");
        } else {
            first_name_profile.setHint(info.first_name);
        }

        if (info.last_name.equals("null")) {
            last_name_profile.setHint("Last name");
        } else {
            last_name_profile.setHint(info.last_name);
        }

        if (info.avatar_bitmap != null) {
            byte[] avatar_byte = info.avatar_bitmap;
            Bitmap bitmap = BitmapFactory.decodeByteArray(avatar_byte, 0, avatar_byte.length);
            avatar_profile.setImageBitmap(bitmap);
        }
    }

    private void clickBtnSaveModified() {
        btn_save_modified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                if (username.equals("")) username = info.username;
                if (last_name.equals("")) last_name = info.last_name;
                if (first_name.equals("")) first_name = info.first_name;
                if (email.equals("")) email = info.email;
                if (password_hash.equals("")) password_hash = info.password_hash;

                if (username.equals(info.username) &&
                        last_name.equals(info.last_name) &&
                        first_name.equals(info.first_name) &&
                        email.equals(info.email) &&
                        password_hash.equals(info.password_hash) &&
                        Arrays.equals(avatar, info.avatar_bitmap)
                    ) {
                    Toast.makeText(getApplicationContext(), "Nothing changes", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    ConnectToMySQL.update("user", "user_id", info.user_id, imageViewToByte(avatar_profile),
                            new String[] {"username", "first_name", "last_name", "email", "password_hash"},
                            new String[] {username, first_name, last_name, email, password_hash});
                    databaseHelper.clearTable("user");
                    databaseHelper.insertTableUser(info.user_id,
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
            }
        });
    }

    private void initializationDatabase() {
        databaseHelper = new DatabaseHelper(Profile.this, "goldfish_dictionary_client.db");
        databaseHelper.createDatabase();
    }
}
