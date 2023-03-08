package com.goldfish_dictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.goldfish_dictionary.R;

import org.w3c.dom.Text;

public class Dictionary extends Activity {
    private String typeTranslate;
    private DatabaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        Intent intent = getIntent();
        typeTranslate = intent.getStringExtra("TYPE");
        System.out.println(typeTranslate);

        initializationDatabase();
        changeTitle();
    }

    private void changeTitle() {
        TextView txt_dictionary = findViewById(R.id.txt_dictionary);
        switch (typeTranslate) {
            case "VI_EN":
                txt_dictionary.setText("Từ điển Việt - Anh");
                break;
            case "FR_VI":
                txt_dictionary.setText("Từ điển Pháp - Việt");
                break;
            case "VI_FR":
                txt_dictionary.setText("Từ điển Việt - Pháp");
                break;
        }
    }

    private void initializationDatabase() {
//        switch (typeTranslate) {
//            case "VI_EN":
//                dataBaseHelper = new DatabaseHelper(Dictionary.this, "vi_en.db");
//                break;
//            case "FR_VI":
//                dataBaseHelper = new DatabaseHelper(Dictionary.this, "fr_vi.db");
//                break;
//            case "VI_FR":
//                dataBaseHelper = new DatabaseHelper(Dictionary.this, "vi_fr.db");
//                break;
//        }
        dataBaseHelper = new DatabaseHelper(Dictionary.this, "vi_en.db");
        dataBaseHelper.createDatabase();
    }
}
