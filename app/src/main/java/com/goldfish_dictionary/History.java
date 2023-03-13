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
import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {
    private DatabaseHelper clientDataBaseHelper;

    private HistoryAdapter historyAdapter;
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

        historyAdapter = new HistoryAdapter(clientDataBaseHelper, this, "", "search_history", true);
        recyclerWords.setAdapter(historyAdapter);
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
                    recyclerWords.setAdapter(historyAdapter);
                }
            }

        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                historyAdapter.getFilter().filter(charSequence);
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

        SQLiteDatabase sqLiteDatabase = clientDataBaseHelper.getReadableDatabase();
        String user_id = clientDataBaseHelper.getUserId();

        String queryDelete = "SELECT * FROM search_history WHERE is_deleted = \"true\"";
        Cursor cursorDelete = sqLiteDatabase.rawQuery(queryDelete,null);
        cursorDelete.moveToFirst();

        List<String> list = new ArrayList<>();
        while (!cursorDelete.isAfterLast()) {
            String word_id = cursorDelete.getString(cursorDelete.getColumnIndex("word_id"));
            ConnectToMySQL.delete("search_history",
                    new String[] {"word_id"},
                    new String[] {word_id});
            list.add(word_id);
            cursorDelete.moveToNext();
        }
        for (int i = 0; i < list.size(); ++i) {
            String word_id = list.get(i);
            clientDataBaseHelper.deleteQuery("search_history",
                    new String[] {"word_id"},
                    new String[] {word_id});
        }
        String query = "SELECT * FROM search_history WHERE is_synced = \"false\"";
        sqLiteDatabase = clientDataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String word_id = cursor.getString(cursor.getColumnIndex("word_id"));
            String name_database = cursor.getString(cursor.getColumnIndex("name_database"));
            String date_search = cursor.getString(cursor.getColumnIndex("date_search"));
            ConnectToMySQL.insert("search_history",
                    new String[] {"user_id", "word_id", "name_database", "date_search"},
                    new String[] {user_id, word_id, name_database, date_search});
            cursor.moveToNext();
        }
    }
}
