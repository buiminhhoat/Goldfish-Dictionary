package com.goldfish_dictionary;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import java.util.Date;

public class Word extends Activity {
    private final static String DEFAULT_NOTIFICATION_CHANNEL_ID = "Goldfish Dictionary";
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
        String ipa = dataBaseHelper.getVocabulary(word).getIpa();
        tv_ipa.setText(ipa);

        TextView tv_meaning = findViewById(R.id.txt_meaning);
        String meaning = dataBaseHelper.getVocabulary(word).getMeaning();
        tv_meaning.setText(meaning);

        createNotificationChannel();
//        createNotification("Goldfish Dictionary", word);

        scheduleNotification(getNotification(word + " [" + ipa + "]",
                meaning), 5);
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
            int importance = NotificationManager.IMPORTANCE_HIGH;
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

    private void scheduleNotification(Notification notification, int secondsDelay) {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + secondsDelay * 1000L;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String contentTitle, String contentText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);
        builder.setSmallIcon(R.drawable.icon);
        builder.setPriority(NotificationManager.IMPORTANCE_MAX);
        builder.setAutoCancel(true);
        builder.setChannelId("Goldfish Dictionary");
        return builder.build();
    }
}
