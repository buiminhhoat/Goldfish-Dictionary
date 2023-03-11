package com.goldfish_dictionary;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;

public class History extends AppCompatActivity {
    private DatabaseHelper clientDataBaseHelper;

    private VocabularyAdapter vocabularyAdapter;
    private RecyclerView recyclerWords;
    private EditText searchBar;
    Connection connection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initializationDatabase();
        try {
            syncServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        eventRecyclerWord();
        eventSearchBar();
    }

    private void eventRecyclerWord() {
        recyclerWords = findViewById(R.id.recycler_history);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerWords.setLayoutManager(linearLayoutManager);

        vocabularyAdapter = new VocabularyAdapter(clientDataBaseHelper, this, "", "search_history", true);
        recyclerWords.setAdapter(vocabularyAdapter);
    }

    private void eventSearchBar() {
        searchBar = findViewById(R.id.search_history);

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

    private void syncServer() throws Exception {
        connection = ConnectToMySQL.getConnection();
        if (connection == null) return;
//        Statement statement = null;
//        try {
//            statement = connection.createStatement();
//        } catch (SQLException e) {
//            throw new Exception("recycler_saved_vocabularyo connect to database!");
//        }
//        if (statement == null) return;

        SQLiteDatabase sqLiteDatabase = clientDataBaseHelper.getReadableDatabase();
        String user_id = clientDataBaseHelper.getUserId();

        String query = "SELECT * FROM search_history WHERE is_synced = \"false\"";
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String word_id = cursor.getString(cursor.getColumnIndex("id"));
            String nameDatabase = cursor.getString(cursor.getColumnIndex("nameDatabase"));
            String date_search = cursor.getString(cursor.getColumnIndex("date_search"));
            ConnectToMySQL.insert("search_history",
                    new String[] {"user_id", "id", "nameDatabase", "date_search"},
                    new String[] {user_id, word_id, nameDatabase, date_search});
            cursor.moveToNext();
        }
    }
}
