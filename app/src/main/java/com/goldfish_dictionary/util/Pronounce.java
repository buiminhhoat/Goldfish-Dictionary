package com.goldfish_dictionary;

import android.media.AudioAttributes;
import android.media.MediaPlayer;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Pronounce {
    private String pronounceUrl;
    private MediaPlayer mediaPlayer;

    public Pronounce(String word) throws JSONException, IOException, InterruptedException {
        String id = createTTSJob(word);
//        System.out.println("id = " + id);
        Thread.sleep(3000);
        pronounceUrl = getJobStatus(id);
//        System.out.println("url = " + pronounceUrl);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.setDataSource(pronounceUrl);
        mediaPlayer.prepare();
    }

    public String createTTSJob(String word) throws JSONException, IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        String value = "{\r\"text\": \"" + word + "\"\r}";
        RequestBody body = RequestBody.create(mediaType, value);
        Request request = new Request.Builder()
                .url("https://large-text-to-speech.p.rapidapi.com/tts")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("X-RapidAPI-Key", "3456b2c895msh3e1a281afb1cd7ap1b9c2djsn204d1936446b")
                .addHeader("X-RapidAPI-Host", "large-text-to-speech.p.rapidapi.com")
                .build();

        Response response = client.newCall(request).execute();
        JSONTokener tokener = new JSONTokener(response.body().string());
        JSONObject jsonObject = new JSONObject(tokener);
        String id = String.valueOf(jsonObject.get("id"));
        return id;
    }

    public String getJobStatus(String id) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://large-text-to-speech.p.rapidapi.com/tts?id=" + id)
                .get()
                .addHeader("X-RapidAPI-Key", "3456b2c895msh3e1a281afb1cd7ap1b9c2djsn204d1936446b")
                .addHeader("X-RapidAPI-Host", "large-text-to-speech.p.rapidapi.com")
                .build();

        Response response = client.newCall(request).execute();

        String getBody = response.body().string();

        System.out.println(getBody);
        JSONTokener tokener = new JSONTokener(getBody);
        JSONObject jsonObject = new JSONObject(tokener);
        System.out.println(String.valueOf(jsonObject.get("status")));
        String url = "";
        if (jsonObject.has("url")) {
            url = String.valueOf(jsonObject.get("url"));
        }
        return url;
    }

    public void pronounce(){
        mediaPlayer.start();
    }
}
