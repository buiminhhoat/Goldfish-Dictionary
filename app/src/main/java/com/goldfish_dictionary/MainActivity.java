package com.goldfish_dictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private VocabularyAdapter vocabularyAdapter;
    private RecyclerView recyclerWords;
    private EditText searchBar;
    private DatabaseHelper dataBaseHelper;
    private ConstraintLayout btn_vi_en;
    private ConstraintLayout btn_fr_vi;
    private ConstraintLayout btn_vi_fr;
    private ConstraintLayout btn_translate;
    private ImageView btn_profile;
    private Button btn_search_history;
    private Button btn_saved_vocabulary;
    private String name_database = "en_vi.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map();

        initializationDatabase();
        eventRecyclerWord();
        eventSearchBar();
        clickBtnTranslate();
        clickBtnProfile();
        clickBtnDictionary();
        clickBtnSearchHistory();
        clickBtnSavedVocabulary();
    }

    private void map() {
        btn_fr_vi = findViewById(R.id.btn_fr_vi);
        searchBar = findViewById(R.id.action_search);
        recyclerWords = findViewById(R.id.recycler_voca);
        btn_saved_vocabulary = findViewById(R.id.btn_saved_vocabulary);
        btn_search_history = findViewById(R.id.btn_search_history);
        btn_vi_en = findViewById(R.id.btn_vi_en);
        btn_profile = findViewById(R.id.btn_profile);
        btn_translate = findViewById(R.id.btn_translate);
    }

    private void clickBtnTranslate() {
        btn_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Translate.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void eventRecyclerWord() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerWords.setLayoutManager(linearLayoutManager);

        vocabularyAdapter = new VocabularyAdapter(dataBaseHelper, this, name_database, "vocabulary", false);
        recyclerWords.setAdapter(vocabularyAdapter);
    }

    private void eventSearchBar() {
        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                    recyclerWords.setAdapter(null);
                } else {
                    recyclerWords.setAdapter(vocabularyAdapter);
                }
            }

        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                vocabularyAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void clickBtnProfile() {
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void clickBtnDictionary() {
        btn_vi_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Dictionary.class);
                intent.putExtra("NAME_DATABASE", "vi_en.db");
                startActivity(intent);
            }
        });

        btn_fr_vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Dictionary.class);
                intent.putExtra("NAME_DATABASE", "fr_vi.db");
                startActivity(intent);
            }
        });

        btn_vi_fr = findViewById(R.id.btn_vi_fr);
        btn_vi_fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Dictionary.class);
                intent.putExtra("NAME_DATABASE", "vi_fr.db");
                startActivity(intent);
            }
        });
    }

    private void clickBtnSearchHistory() {

        btn_search_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, History.class);
                startActivity(intent);
            }
        });
    }

    private void clickBtnSavedVocabulary() {
        btn_saved_vocabulary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SavedVocabulary.class);
                startActivity(intent);
            }
        });
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void initializationDatabase() {
        dataBaseHelper = new DatabaseHelper(MainActivity.this, "vi_en.db");
        dataBaseHelper.createDatabase();
        dataBaseHelper = new DatabaseHelper(MainActivity.this, "fr_vi.db");
        dataBaseHelper.createDatabase();
        dataBaseHelper = new DatabaseHelper(MainActivity.this, "vi_fr.db");
        dataBaseHelper.createDatabase();

        dataBaseHelper = new DatabaseHelper(MainActivity.this, "en_vi.db");
        dataBaseHelper.createDatabase();
    }
}
