package com.goldfish_dictionary;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import java.util.Date;

public class Word extends Activity {
    private String word;
    private Intent intent;
    private DatabaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        initializationDatabase();

        Intent intent = getIntent();
        this.word = intent.getStringExtra("WORD");

        TextView tv_word = findViewById(R.id.txt_word);
        tv_word.setText(word);

        TextView tv_ipa = findViewById(R.id.txt_ipa);
        tv_ipa.setText(dataBaseHelper.getVocabulary(word).getIpa());

        TextView tv_meaning = findViewById(R.id.txt_meaning);
        tv_meaning.setText(dataBaseHelper.getVocabulary(word).getMeaning());

        createNotificationChannel();
        createNotification("Goldfish Dictionary", word);

    }

    private void initializationDatabase() {
        dataBaseHelper = new DatabaseHelper(Word.this, "en_vi.db");
        dataBaseHelper.createDatabase();
        /*
        dataBaseHelper = new DatabaseHelper(Welcome.this, "vi_en.db");
        dataBaseHelper.createDatabase();
        dataBaseHelper = new DatabaseHelper(Welcome.this, "fr_vi.db");
        dataBaseHelper.createDatabase();
        dataBaseHelper = new DatabaseHelper(Welcome.this, "vi_fr.db");
        dataBaseHelper.createDatabase();
        */
    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Goldfish Dictionary";
            String description = "Goldfish Dictionary";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Goldfish Dictionary", name, importance);
            channel.setDescription(description);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createNotification(String contentTitle, String contentText) {
        android.app.Notification notification = new NotificationCompat.Builder(this, "Goldfish Dictionary")
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.icon)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) new Date().getTime(), notification);
    }
}
