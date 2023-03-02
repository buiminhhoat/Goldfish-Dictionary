package com.goldfish_dictionary;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.goldfish_dictionary.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializationDatabase();
    }

    private void initializationDatabase() {
        DatabaseHelper dataBaseHelper;
        dataBaseHelper = new DatabaseHelper(MainActivity.this, "en_vi.db");
        dataBaseHelper.createDatabase();
        ArrayList<Vocabulary> vocabularyArrayList = dataBaseHelper.getFilterVocabulary("cat", 5);

/*
        System.out.println(vocabularyArrayList.size());
        for (int i = 0; i < vocabularyArrayList.size(); ++i) {
            System.out.println(vocabularyArrayList.get(i).word + " " + vocabularyArrayList.get(i).ipa);
        }
        */

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
