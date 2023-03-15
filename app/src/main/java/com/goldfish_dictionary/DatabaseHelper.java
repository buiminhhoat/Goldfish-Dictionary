package com.goldfish_dictionary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String TAG = "DatabaseHelper";
    // Tag just for the LogCat window
    //destination path (location) of our database on device
    private static String DATABASE_PATH = "";
    //private static String name_database ="(students).sqlite";// Database name
//    private static String name_database = "en_vi.db";
    private String name_database = "";
    private SQLiteDatabase sqLiteDatabase;
    private final Context context;

    public DatabaseHelper(Context context, String name_database) {
        super(context, name_database, null, 1);// 1? its Database Version
        DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        Log.i(TAG, DATABASE_PATH);
        this.context = context;
        this.name_database = name_database;
    }

    public void createDatabase() {
        // If database not exists copy it from the assets
        boolean sqLiteDatabaseExist = checkDatabase();
        if (!sqLiteDatabaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                // Copy the database from assests
                copyDatabase();
                Log.e(TAG, "createDatabase database created");
            } catch (IOException ioException) {
                Log.i(TAG, "createDatabase " + ioException + "");
            }
        }
    }

    // Check that the database exists here: /data/data/your package/databases/name_database
    private boolean checkDatabase() {
        File databaseFile = new File(DATABASE_PATH + name_database);
        //Log.v("databaseFile", databaseFile + "   "+ databaseFile.exists());
        return databaseFile.exists();
    }

    //Copy the database from assets
    private void copyDatabase() throws IOException {
        try {
            InputStream mInput = context.getAssets().open(name_database);
            String outFileName = DATABASE_PATH + name_database;
            new File(DATABASE_PATH).mkdirs();
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = mInput.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            mInput.close();
        } catch (IOException ioException) {
            Log.i(TAG, "copyDatabase " + ioException + "");
        }
    }

    // Open the database, so we can query it
    public boolean openDataBase() throws SQLException {
        String path = DATABASE_PATH + name_database;
        sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return sqLiteDatabase != null;
    }

    @Override
    public synchronized void close() {
        if (sqLiteDatabase != null)
            sqLiteDatabase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertTableUser(long user_id, String username, String first_name, String last_name, String email, String password_hash) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "INSERT INTO user(user_id, username, first_name, last_name, email, password_hash) " +
                "VALUES ("+ user_id + ", \""+ username + "\", \"" + first_name + "\", \"" + last_name + "\", \"" + email + "\", \"" + password_hash + "\");";

        sqLiteDatabase.execSQL(query);
    }

    public void clearTable(String table) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "DELETE FROM " + table;
        sqLiteDatabase.execSQL(query);
    }

    public boolean isEmpty(String table) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<User> users = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + table, null);
        cursor.moveToFirst();
        return cursor.isAfterLast();
    }

    public ArrayList getAllProfile(String table) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<User> users = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + table, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User user = new User();
            user.user_id = cursor.getInt(cursor.getColumnIndex("user_id"));
            user.username = cursor.getString(cursor.getColumnIndex("username"));
            user.first_name = cursor.getString(cursor.getColumnIndex("first_name"));
            user.last_name = cursor.getString(cursor.getColumnIndex("last_name"));
            user.email = cursor.getString(cursor.getColumnIndex("email"));
            user.password_hash = cursor.getString(cursor.getColumnIndex("password_hash"));
            users.add(user);
            cursor.moveToNext();
        }
        return users;
    }

    public ArrayList getAllVocabulary(String table) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Vocabulary> vocabularyArrayList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + table, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.word_id = cursor.getString(cursor.getColumnIndex("word_id"));
            vocabulary.word = cursor.getString(cursor.getColumnIndex("word"));
            vocabulary.ipa = cursor.getString(cursor.getColumnIndex("ipa"));
            vocabulary.meaning = cursor.getString(cursor.getColumnIndex("meaning"));
            if (cursor.getColumnIndex("name_database") != -1) {
                vocabulary.name_database = cursor.getString(cursor.getColumnIndex("name_database"));
            }
            else {
                vocabulary.name_database = name_database;
            }
            vocabularyArrayList.add(vocabulary);
            cursor.moveToNext();
        }
        return vocabularyArrayList;
    }

    public List<Vocabulary> getFilterVocabulary(String table, String word, int limit) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
//        ArrayList<Vocabulary> vocabularyArrayList = new ArrayList<>();
        List<Vocabulary> vocabularyList = new ArrayList<>();
        String query = "SELECT * FROM " + table + " WHERE word LIKE " + "\"" + word + "%\" " + "LIMIT " + limit;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.word_id = cursor.getString(cursor.getColumnIndex("word_id"));
            vocabulary.word = cursor.getString(cursor.getColumnIndex("word"));
            vocabulary.ipa = cursor.getString(cursor.getColumnIndex("ipa"));
            vocabulary.meaning = cursor.getString(cursor.getColumnIndex("meaning"));
            if (cursor.getColumnIndex("name_database") != -1) {
                vocabulary.name_database = cursor.getString(cursor.getColumnIndex("name_database"));
            }
            else {
                vocabulary.name_database = name_database;
            }
            vocabularyList.add(vocabulary);
            cursor.moveToNext();
        }
        return vocabularyList;
    }

    public Vocabulary getVocabulary(String word) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM vocabulary WHERE word LIKE " + "\"" + word + "\" ";
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.word_id = cursor.getString(cursor.getColumnIndex("word_id"));
            vocabulary.word = cursor.getString(cursor.getColumnIndex("word"));
            vocabulary.ipa = cursor.getString(cursor.getColumnIndex("ipa"));
            vocabulary.meaning = cursor.getString(cursor.getColumnIndex("meaning"));
            if (cursor.getColumnIndex("name_database") != -1) {
                vocabulary.name_database = cursor.getString(cursor.getColumnIndex("name_database"));
            }
            else {
                vocabulary.name_database = name_database;
            }
            return vocabulary;
        }
        return null;
    }

    public void addQuery(String table, String[] key, String[] value) throws SQLiteException {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < key.length; ++i) {
            contentValues.put(key[i], value[i]);
        }
        database.insert(table, null, contentValues);
        database.close();
    }

    public void updateQuery(String table, String[] key, String[] value, String[] key_update, String [] value_update) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for (int i = 0; i < key.length; ++i) {
            contentValues.put(key[i], value[i]);
        }

        String whereClause = "";
        for (int i = 0; i < key_update.length; ++i) {
            whereClause += key_update[i] + "?";
        }
        database.update(table, contentValues, whereClause, value_update);
        database.close();
    }

    // below is the method for deleting our course.
    public void deleteQuery(String table, String[] key_delete, String [] value_delete) {
        SQLiteDatabase database = this.getWritableDatabase();

        String whereClause = "";
        for (int i = 0; i < key_delete.length; ++i) {
            whereClause += key_delete[i] + "=?";
        }
        database.delete(table, whereClause, value_delete);
        database.close();
    }

    public String getUserId() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM user";
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            return cursor.getString(cursor.getColumnIndex("user_id"));
        }
        return null;
    }

    public String getVocabularyFromWordId(String word_id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM vocabulary WHERE word_id = " + word_id;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            return cursor.getString(cursor.getColumnIndex("word"));
        }
        return null;
    }
}
