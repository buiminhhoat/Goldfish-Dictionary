package com.goldfish_dictionary.activity;

import static com.goldfish_dictionary.utilities.Constants.languages;
import static com.goldfish_dictionary.utilities.Constants.languages_ISO_639;
import static java.net.URLEncoder.encode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.goldfish_dictionary.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Translate extends Activity {
    static OkHttpClient client = null;
    private ImageView select_language_inp;
    private ImageView select_language_out;
    private ImageView ic_translate_sound_in;
    private ImageView ic_translate_sound_out;
    private TextView language_inp;
    private TextView language_out;
    private PopupMenu popupLanguageInp;
    private PopupMenu popupLanguageOut;
    private ConstraintLayout btn_reverse;
    private ImageView btn_exit_translate;

    private EditText editText_input;
    private TextView textView_output;
    private TextToSpeech textToSpeechInp;
    private TextToSpeech textToSpeechOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        map();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        client = new OkHttpClient();

        setPopupMenu();
        focusChangeEditTextInput();
        clickSelectLanguageInp();
        clickSelectLanguageOut();
        clickIcTranslateSoundIn();
        clickIcTranslateSoundOut();
        clickBtnReverse();
        clickBtnExitTranslate();

        textToSpeechInp = new android.speech.tts.TextToSpeech(getApplicationContext(),
                new android.speech.tts.TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status == TextToSpeech.SUCCESS){
                            textToSpeechInp.setLanguage(Locale.ENGLISH);
                        }
                    }
                });
        textToSpeechOut = new android.speech.tts.TextToSpeech(getApplicationContext(),
                new android.speech.tts.TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status == TextToSpeech.SUCCESS){
                            textToSpeechOut.setLanguage(new Locale("vi"));
                        }
                    }
                });

    }

    private void clickIcTranslateSoundIn() {
        ic_translate_sound_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText_input.getText().toString();
                int speech = textToSpeechInp.speak(text, textToSpeechInp.QUEUE_FLUSH,null);
            }
        });
    }

    private void clickIcTranslateSoundOut() {
        ic_translate_sound_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = textView_output.getText().toString();
                int speech = textToSpeechOut.speak(text, textToSpeechOut.QUEUE_FLUSH,null);
            }
        });
    }

    private void focusChangeEditTextInput() {
        editText_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }

        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void clickBtnExitTranslate() {
        btn_exit_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Translate.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void clickBtnReverse() {
        btn_reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String input = editText_input.getText().toString();
                if (input.equals("")) return;
//                System.out.println(input);
                String output = "\n";
                try {
                    String source = languages_ISO_639.get(languages.indexOf((String) language_inp.getText()));
                    String target = languages_ISO_639.get(languages.indexOf((String) language_out.getText()));
                    output = translate(source, target, List.of(input.split("\n")));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                textView_output.setText(output);

//                String tmp = (String) language_inp.getText();
//                language_inp.setText(language_out.getText());
//                language_out.setText(tmp);
//                editText_input.setText(textView_output.getText());
            }
        });
    }

    private void setPopupMenu() {
        popupLanguageInp = new PopupMenu(this, select_language_inp);
        for (int i = 0; i < languages.size(); i++) {
            popupLanguageInp.getMenu().add(languages.get(i));
        }

        popupLanguageInp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String selectedLanguage = item.getTitle().toString();
                language_inp.setText(selectedLanguage);

                textToSpeechInp = new android.speech.tts.TextToSpeech(getApplicationContext(),
                        new android.speech.tts.TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if(status == TextToSpeech.SUCCESS){
                                    String iso = languages_ISO_639.get(languages.indexOf(selectedLanguage));
                                    textToSpeechInp.setLanguage(new Locale(iso));
                                }
                            }
                        });
                return true;
            }
        });

        popupLanguageOut = new PopupMenu(this, select_language_out);
        for (int i = 0; i < languages.size(); i++) {
            popupLanguageOut.getMenu().add(languages.get(i));
        }

        popupLanguageOut.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String selectedLanguage = item.getTitle().toString();
                language_out.setText(selectedLanguage);

                textToSpeechOut = new android.speech.tts.TextToSpeech(getApplicationContext(),
                        new android.speech.tts.TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if(status == TextToSpeech.SUCCESS){
                                    String iso = languages_ISO_639.get(languages.indexOf(selectedLanguage));
                                    textToSpeechOut.setLanguage(new Locale(iso));
                                }
                            }
                        });
                return true;
            }
        });
    }

    private void clickSelectLanguageOut() {
        select_language_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupLanguageOut.show();
            }
        });
    }

    private void clickSelectLanguageInp() {
        select_language_inp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupLanguageInp.show();
            }
        });
    }

    private void map() {
        editText_input = findViewById(R.id.editText_input);
        textView_output = findViewById(R.id.textView_output);
        select_language_inp = findViewById(R.id.select_language_inp);
        select_language_out = findViewById(R.id.select_language_out);
        ic_translate_sound_in = findViewById(R.id.ic_translate_sound_in);
        ic_translate_sound_out = findViewById(R.id.ic_translate_sound_out);
        language_inp = findViewById(R.id.language_inp);
        language_out = findViewById(R.id.language_out);
        btn_reverse = findViewById(R.id.btn_reverse);
        btn_exit_translate = findViewById(R.id.btn_exit_translate);
    }

    public static String translate(String source, String target, String input) throws IOException, JSONException {
        String url = String.format
                ("https://translate.googleapis.com/translate_a/single?client=gtx&sl=%s&tl=%s&dt=t&q=%s",
                        source, target, encode(input, "UTF-8"));

        String result = sendGET(url);

        // Parse chuỗi JSON thành một đối tượng JSONArray
        JSONArray jsonArray = new JSONArray(result);

        // Truy cập vào phần tử đầu tiên của đối tượng JSONArray
        JSONArray subArray = jsonArray.getJSONArray(0);

        String translate = "";
        // Lặp qua mỗi mảng con và truy cập vào phần tử đầu tiên của mỗi mảng con
        for (int i = 0; i < subArray.length(); i++) {
            JSONArray subSubArray = subArray.getJSONArray(i);
            String firstString = subSubArray.getString(0);
            translate += firstString;
        }
        return translate;
    }

    public static String translate(String source, String target, List<String> list) throws JSONException, IOException {
        String result = "";
        for (String s: list) {
            if (Objects.equals(s, "")) continue;
            result += translate(source, target, s) + "\n";
        }
        return result;
    }

    public static String sendGET(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Translate.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
