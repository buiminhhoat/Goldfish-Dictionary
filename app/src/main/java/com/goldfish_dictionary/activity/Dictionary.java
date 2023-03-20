package com.goldfish_dictionary.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.goldfish_dictionary.connection.DatabaseHelper;
import com.goldfish_dictionary.R;
import com.goldfish_dictionary.adapter.VocabularyAdapter;

public class Dictionary extends AppCompatActivity {
    private String name_database;
    private DatabaseHelper dataBaseHelper;

    private VocabularyAdapter vocabularyAdapter;
    private RecyclerView recyclerWords;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        Intent intent = getIntent();
        name_database = intent.getStringExtra("NAME_DATABASE");
        System.out.println(name_database);

        initializationDatabase();
        changeTitle();

        recyclerWords = findViewById(R.id.recycler_voca);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerWords.setLayoutManager(linearLayoutManager);

        vocabularyAdapter = new VocabularyAdapter(dataBaseHelper, this, name_database, "vocabulary", false);
        recyclerWords.setAdapter(vocabularyAdapter);

//        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        recyclerWords.addItemDecoration(itemDecoration);

        searchBar = findViewById(R.id.action_search);

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

    private void changeTitle() {
        TextView txt_dictionary = findViewById(R.id.txt_dictionary);
        switch (name_database) {
            case "vi_en.db":
                txt_dictionary.setText("Từ điển Việt - Anh");
                break;
            case "fr_vi.db":
                txt_dictionary.setText("Từ điển Pháp - Việt");
                break;
            case "vi_fr.db":
                txt_dictionary.setText("Từ điển Việt - Pháp");
                break;
        }
    }

    private void initializationDatabase() {
        dataBaseHelper = new DatabaseHelper(Dictionary.this, name_database);
        dataBaseHelper.createDatabase();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
