package com.goldfish_dictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SavedVocabulary extends AppCompatActivity {
    private DatabaseHelper clientDataBaseHelper;

    private VocabularyAdapter vocabularyAdapter;
    private RecyclerView recyclerWords;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_vocabulary);

        initializationDatabase();
        eventRecyclerWord();
        eventSearchBar();
    }

    private void eventRecyclerWord() {
        recyclerWords = findViewById(R.id.recycler_saved_vocabulary);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerWords.setLayoutManager(linearLayoutManager);

        vocabularyAdapter = new VocabularyAdapter(clientDataBaseHelper, this, "", "saved_vocabulary", true);
        recyclerWords.setAdapter(vocabularyAdapter);
    }

    private void eventSearchBar() {
        searchBar = findViewById(R.id.search_saved_vocabulary);

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

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void initializationDatabase() {
        clientDataBaseHelper = new DatabaseHelper(this, "goldfish_dictionary_client.db");
        clientDataBaseHelper.createDatabase();
    }
}
