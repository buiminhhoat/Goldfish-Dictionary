package com.goldfish_dictionary;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Word extends Activity {
    private final static String DEFAULT_NOTIFICATION_CHANNEL_ID = "Goldfish Dictionary";
    private String word;
    private String typeTranslate;
    private DatabaseHelper dataBaseHelper;
    private TextView list_synonym;
    private TextView list_antonym;
    private Button btn_speak;

    static OkHttpClient client = null;

    public Word() throws IOException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        Intent intent = getIntent();
        this.word = intent.getStringExtra("WORD");
        this.typeTranslate = intent.getStringExtra("TYPE");

        client = new OkHttpClient();
        initializationDatabase();

        TextView tv_word = findViewById(R.id.txt_word);
        tv_word.setText(word);

        TextView tv_ipa = findViewById(R.id.txt_ipa);
        String ipa = dataBaseHelper.getVocabulary(word).getIpa();
        tv_ipa.setText(ipa);

        TextView tv_meaning = findViewById(R.id.txt_meaning);
        String meaning = dataBaseHelper.getVocabulary(word).getMeaning();
        tv_meaning.setText(meaning);


        list_synonym = findViewById(R.id.list_synonym);
        list_antonym = findViewById(R.id.list_antonym);
        list_synonym.setText("Loading...");
        list_antonym.setText("Loading...");
        setSynonymsAndAntonyms();

        createNotificationChannel();
//        createNotification("Goldfish Dictionary", word);

        if (Objects.equals(ipa, "")) {
            scheduleNotification(getNotification(word,
                    meaning), 15);
        }
        else {
            scheduleNotification(getNotification(word + " [" + ipa + "]",
                    meaning), 15);
        }

//        Pronounce pronounce = new Pronounce();
//        try {
//            pronounce.pronounce(word);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    private void setSynonymsAndAntonyms (){
        Handler handler = new Handler();
        Thread thread = new Thread(){
            private String getSynonyms() {
                try {
                    String jsonSynonym = sendGET("https://api.datamuse.com/words?ml=" + word);
                    JSONTokener tokener = new JSONTokener(jsonSynonym);
                    JSONArray finalResult = new JSONArray(tokener);

                    String synonyms = "- ";
                    for (int i = 0; i < finalResult.length(); ++i) {
                        JSONObject jsonObject = (JSONObject) finalResult.get(i);
                        synonyms += (String) jsonObject.get("word");
                        if (i != finalResult.length() - 1) {
                            synonyms += ", ";
                        }
                    }
                    if (synonyms.equals("- ")) {
                        return "No synonyms found!";
                    }
                    return synonyms;
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            private String getAntonyms() {
                try {
                    String jsonAntonym = sendGET("https://api.datamuse.com/words?rel_ant=" + word);
                    JSONTokener tokener = new JSONTokener(jsonAntonym);
                    JSONArray finalResult = new JSONArray(tokener);

                    String antonyms = "- ";
                    for (int i = 0; i < finalResult.length(); ++i) {
                        JSONObject jsonObject = (JSONObject) finalResult.get(i);
                        antonyms += (String) jsonObject.get("word");
                        if (i != finalResult.length() - 1) {
                            antonyms += ", ";
                        }
                    }
                    if (antonyms.equals("- ")) {
                        return "No antonyms found!";
                    }
                    return antonyms;
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            public void run() {
                String synonyms = getSynonyms();
                String antonyms = getAntonyms();
                handler.post(new Runnable() {
                    public void run() {
//                        if (synonyms.equals("No synonyms found!")) {
//                            list_synonym.setTextColor(Color.rgb(254, 169, 31));
//                        }
//                        if (antonyms.equals("No antonyms found!")) {
//                            list_antonym.setTextColor(Color.rgb(254, 169, 31));
//                        }
                        list_synonym.setText(synonyms);
                        list_antonym.setText(antonyms);
                    }
                });
            }
        };
        thread.start();
    }

    private void initializationDatabase() {
        dataBaseHelper = new DatabaseHelper(Word.this, typeTranslate + ".db");
        dataBaseHelper.createDatabase();
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

    public static String sendGET(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        String res = "";
        try (Response response = client.newCall(request).execute()) {
            res = response.body().string();
        }
        catch (Exception exception) {
            System.out.println(exception);
        }
        return res;
    }
}
