package com.goldfish_dictionary;

import static com.goldfish_dictionary.Constants.languages;
import static java.net.URLEncoder.encode;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Translate extends Activity {
    static OkHttpClient client = null;
    private ImageView select_language_inp;
    private ImageView select_language_out;
    private TextView language_inp;
    private TextView language_out;
    private PopupMenu popupLanguageInp;
    private PopupMenu popupLanguageOut;
    private ConstraintLayout btn_reverse;

    EditText editText_input;
    TextView textView_output;
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

        editText_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = editText_input.getText().toString();
                if (input.equals("")) return;
                if (input.charAt(input.length() - 1) != '.') {
                    return;
                }
                String output = "\n";
                try {
                    String source = "en";
                    String target = "vi";
                    output = translate(source, target, List.of(input.split("\n")));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                textView_output.setText(output);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        setPopupMenu();
        clickSelectLanguageInp();
        clickSelectLanguageOut();
        clickBtnReverse();
    }

    private void clickBtnReverse() {
        btn_reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmp = (String) language_inp.getText();
                language_inp.setText(language_out.getText());
                language_out.setText(tmp);
                editText_input.setText(textView_output.getText());
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
        language_inp = findViewById(R.id.language_inp);
        language_out = findViewById(R.id.language_out);
        btn_reverse = findViewById(R.id.btn_reverse);
    }

    public static String translate(String source, String target, String input) throws IOException, JSONException {
        String url = String.format
                ("https://translate.googleapis.com/translate_a/single?client=gtx&sl=%s&tl=%s&dt=t&q=%s",
                        source, target, encode(input, "UTF-8"));

        String result = sendGET(url);

        JSONTokener tokener = new JSONTokener(result);
        JSONArray finalResult = new JSONArray(tokener);
//        for (int i = 0; i < finalResult.length(); ++i) {
//            System.out.println(finalResult.get(i));
//        }
        result = finalResult.get(0).toString();
        tokener = new JSONTokener(result);
        finalResult = new JSONArray(tokener);

        result = finalResult.get(0).toString();
        tokener = new JSONTokener(result);
        finalResult = new JSONArray(tokener);

        String translate = finalResult.get(0).toString();
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
}
