package com.example.bookclubapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private final Context context;
    private static final String DATABASE_NAME = "BookClubDB.db";
    private static final int DATABASE_VERSION = 1;

    //User Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_USER_PASSWORD = "password";

    //Library Table
    private static final String TABLE_LIBRARY = "library";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "book_title";
    private static final String COLUMN_AUTHOR = "book_author";
    private static final String COLUMN_GENRE = "book_GENRE";
    private static final String COLUMN_ISBN = "book_isbn";
    private static final String COLUMN_STATUS = "book_status";


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        String CreateUserTable = "CREATE TABLE " + TABLE_USERS +
                " (" + COLUMN_USERNAME +
                " TEXT PRIMARY KEY, " +
                COLUMN_USER_PASSWORD + " TEXT)";
        String CreateLibraryTable = "CREATE TABLE " + TABLE_LIBRARY +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_GENRE + " TEXT, " +
                COLUMN_ISBN + " TEXT, " +
                COLUMN_STATUS + " TEXT)";

        sqLiteDatabase.execSQL(CreateUserTable);
        sqLiteDatabase.execSQL(CreateLibraryTable);
    }

    public void addBook(String title, String author, String genre, String ISBN, String status){
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_AUTHOR, author);
        cv.put(COLUMN_GENRE, genre);
        cv.put(COLUMN_ISBN, ISBN);
        cv.put(COLUMN_STATUS, status);

        long result = myDB.insert(TABLE_LIBRARY, null, cv);
        if(result == -1){
            Toast.makeText(context, "Could not add book.",Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(context,"Book added Successfully!",Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor readBookData(){
        String query = "SELECT * FROM " + TABLE_LIBRARY;
        SQLiteDatabase myDB = this.getReadableDatabase();

        Cursor cursor = null;
        if(myDB != null){
            cursor = myDB.rawQuery(query, null);
        }
        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LIBRARY);
    }

    //This method inserts the data from the sign up page into the database.
    public boolean insertData(String username, String password){
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_USER_PASSWORD, password);
        long result = myDB.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    //This method is called when checking the username in order to reset the password.
    public boolean checkUserName(String username){
        SQLiteDatabase myDB = this.getWritableDatabase();
        Cursor cursor = myDB.rawQuery("select * from " + TABLE_USERS + " where username=?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    //This method will be used to fetch the username, this is useful for wanting to display the user's username once they have logged in.
    public String getUserName(){
        SQLiteDatabase myDB = this.getReadableDatabase();
        Cursor cursor = myDB.rawQuery("SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USERS + " LIMIT 1", null);
        String username = null;
        if(cursor.moveToFirst()){
            username = cursor.getString(0);
        }
        cursor.close();
        return username;
    }

    //Checks if there is an existing user in the database
    public boolean checkUser(String username, String password){
        SQLiteDatabase myDB = this.getWritableDatabase();
        Cursor cursor = myDB.rawQuery("select * from " + TABLE_USERS +  " where username=? and password=?", new String[]{username,password});
        return cursor.getCount() > 0;
    }

    //Updates the password to a new one if the user has forgotten.
    public boolean updatepassword(String username, String password){
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_PASSWORD, password);
        long result = myDB.update(TABLE_USERS, contentValues, COLUMN_USERNAME + "=?", new String[] {username});
        return result != -1;
    }

    void deleteUser(String username){
        SQLiteDatabase myDB = this.getWritableDatabase();
        long result = myDB.delete(TABLE_USERS, "username=?", new String[]{username});
        if (result == -1){
            Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully deleted user from database.", Toast.LENGTH_SHORT).show();
        }
    }

    //Updates the library with new values.
    void updateLibraryData(String row_id, String title, String author, String genre, String ISBN, String status) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_AUTHOR, author);
        cv.put(COLUMN_GENRE, genre);
        cv.put(COLUMN_ISBN, ISBN);
        cv.put(COLUMN_STATUS, status);

        long rowsAffected = myDB.update(TABLE_LIBRARY, cv, COLUMN_ID + "=?", new String[]{row_id});
        if (rowsAffected == -1){
            Toast.makeText(context, "Failed to update library. :(", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully updated library.", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteLibraryData(String row_id){
        SQLiteDatabase myDB = this.getWritableDatabase();
        long result = myDB.delete(TABLE_LIBRARY, "_id=?", new String[]{row_id});
        if (result == -1){
            Toast.makeText(context, "Failed to delete book.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully deleted book from database.", Toast.LENGTH_SHORT).show();
        }
    }

    //This method will be used to search the recycle view in the SearchFragment.
    public ArrayList<Book> librarySearch(String searchText){
        ArrayList<Book> bookList = new ArrayList<>();
        SQLiteDatabase myDB = this.getReadableDatabase();
        Cursor cursor = myDB.rawQuery("SELECT * FROM " + TABLE_LIBRARY + " WHERE " + COLUMN_TITLE + " LIKE ?", new String[]{"%" + searchText + "%"});

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int authorIndex = cursor.getColumnIndex(COLUMN_AUTHOR);
                int genreIndex = cursor.getColumnIndex(COLUMN_GENRE);
                int isbnIndex = cursor.getColumnIndex(COLUMN_ISBN);
                int statusIndex = cursor.getColumnIndex(COLUMN_STATUS);

                // Check if column indices are valid
                if (idIndex >= 0 && titleIndex >= 0 && authorIndex >= 0 && genreIndex >= 0 && isbnIndex >= 0 && statusIndex >= 0) {
                    int id = cursor.getInt(idIndex);
                    String title = cursor.getString(titleIndex);
                    String author = cursor.getString(authorIndex);
                    String genre = cursor.getString(genreIndex);
                    String isbn = cursor.getString(isbnIndex);
                    String status = cursor.getString(statusIndex);

                    // Create a Book object and add it to the list
                    Book book = new Book(id, title, author, genre, isbn, status);
                    bookList.add(book);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        myDB.close();

        return bookList;
    }
}
