package com.goldfish_dictionary.activity;

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
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import com.goldfish_dictionary.connection.DatabaseHelper;
import com.goldfish_dictionary.notification.NotificationReceiver;
import com.goldfish_dictionary.utilities.Pronounce;
import com.goldfish_dictionary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Word extends Activity {
    private final static String DEFAULT_NOTIFICATION_CHANNEL_ID = "Goldfish Dictionary";
    private String word;
    private String name_database;
    private DatabaseHelper dataBaseHelper;
    private DatabaseHelper clientDataBaseHelper;
    private TextView list_synonym;
    private TextView list_antonym;
    private ImageView speaker;
    private ImageView exit_word;
    private ImageView imageview_savedvocabulary;
    private Pronounce pronounce;

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
        this.name_database = intent.getStringExtra("NAME_DATABASE");

        initializationDatabase();
        client = new OkHttpClient();

        TextView tv_word = findViewById(R.id.txt_word);
        TextView tv_ipa = findViewById(R.id.txt_ipa);
        TextView tv_meaning = findViewById(R.id.txt_meaning);
        list_synonym = findViewById(R.id.list_synonym);
        list_antonym = findViewById(R.id.list_antonym);

        tv_word.setText(word);
        String id = dataBaseHelper.getVocabulary(word).getWord_id();
        String ipa = dataBaseHelper.getVocabulary(word).getIpa();
        tv_ipa.setText(ipa);

        String meaning = dataBaseHelper.getVocabulary(word).getMeaning();
        tv_meaning.setText(meaning);

        list_synonym.setText("Loading...");
        list_antonym.setText("Loading...");
        setSynonymsAndAntonyms();

        updateHistory();

        createNotificationChannel();

        if (Objects.equals(ipa, "")) {
            scheduleNotification(getNotification(word,
                    meaning), 15);
        }
        else {
            scheduleNotification(getNotification(word + " [" + ipa + "]",
                    meaning), 15);
        }

        clickBtnSpeaker();
        clickBtnExitWord();
        clickImageViewSavedVocabulary();
    }

    private void clickImageViewSavedVocabulary() {
        imageview_savedvocabulary = findViewById(R.id.imageview_savedvocabulary);
        imageview_savedvocabulary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word_id = dataBaseHelper.getVocabulary(word).getWord_id();
                String ipa = dataBaseHelper.getVocabulary(word).getIpa();
                String meaning = dataBaseHelper.getVocabulary(word).getMeaning();
                boolean is_synced = false;
                boolean is_deleted = false;
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                clientDataBaseHelper.deleteQuery("saved_vocabulary", new String[]{"word"},
                        new String[]{word});
                clientDataBaseHelper.addQuery("saved_vocabulary",
                        new String[]{"word_id", "word", "ipa", "meaning", "name_database", "is_synced", "is_deleted", "date_saved"},
                        new String[]{word_id, word, ipa, meaning, name_database, String.valueOf(is_synced),
                                String.valueOf(is_deleted), String.valueOf(dateTimeFormatter.format(now))});
            }
        });

    }

    private void clickBtnExitWord() {
        exit_word = findViewById(R.id.exit_word);
        exit_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Word.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void clickBtnSpeaker() {
        pronounce = null;
        speaker = findViewById(R.id.speaker);
        speaker.setImageResource(R.drawable.speaker_loading);
        Handler handler = new Handler();
        Thread thread = new Thread(){
            public void run() {
                try {
                    pronounce = new Pronounce(word);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    public void run() {
                        speaker.setImageResource(R.drawable.speaker);
                    }
                });
            }
        };
        thread.start();
        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pronounce != null) {
                    pronounce.pronounce();
                }
            }
        });
    }

    private void updateHistory() {
        String word_id = dataBaseHelper.getVocabulary(word).getWord_id();
        String ipa = dataBaseHelper.getVocabulary(word).getIpa();
        String meaning = dataBaseHelper.getVocabulary(word).getMeaning();
        boolean is_synced = false;
        boolean is_deleted = false;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        clientDataBaseHelper.deleteQuery("search_history", new String[]{"word"},
                new String[]{word});
        clientDataBaseHelper.addQuery("search_history",
                new String[]{"word_id", "word", "ipa", "meaning", "name_database", "is_synced", "is_deleted", "date_search"},
                new String[]{word_id, word, ipa, meaning, name_database, String.valueOf(is_synced),
                        String.valueOf(is_deleted), String.valueOf(dateTimeFormatter.format(now))});
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
                    return "Unable to connect to database!";
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
                    return "Unable to connect to database!";
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
        dataBaseHelper = new DatabaseHelper(Word.this, name_database);
        dataBaseHelper.createDatabase();

        clientDataBaseHelper = new DatabaseHelper(this, "goldfish_dictionary_client.db");
        clientDataBaseHelper.createDatabase();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Word.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
