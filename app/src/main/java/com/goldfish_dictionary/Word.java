package com.goldfish_dictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

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
}
