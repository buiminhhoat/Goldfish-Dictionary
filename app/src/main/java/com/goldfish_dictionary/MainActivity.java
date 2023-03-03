package com.goldfish_dictionary;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private VocabularyAdapter vocabularyAdapter;
    private RecyclerView recyclerWords;
    private EditText searchBar;
    private DatabaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializationDatabase();

        recyclerWords = findViewById(R.id.recycler_voca);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerWords.setLayoutManager(linearLayoutManager);

        vocabularyAdapter = new VocabularyAdapter(dataBaseHelper);
        recyclerWords.setAdapter(vocabularyAdapter);

//        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        recyclerWords.addItemDecoration(itemDecoration);

        searchBar = findViewById(R.id.action_search);

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

    private void initializationDatabase() {
        dataBaseHelper = new DatabaseHelper(MainActivity.this, "en_vi.db");
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
