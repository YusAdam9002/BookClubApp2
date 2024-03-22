package com.example.bookclubapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class ReadFragment extends Fragment {
    RecyclerView recyclerView;
    FloatingActionButton add_readbook;
    ArrayList<String> bookStatus;
    TextView readingTitle;
    ArrayList<Book> BookList;

    DBHelper myDB;
    CustomAdapter customAdapter;

    public ReadFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_read, container, false);

        recyclerView = view.findViewById(R.id.readRecycle);
        add_readbook = view.findViewById(R.id.add_read);

        readingTitle = view.findViewById(R.id.TitleRead);
        readingTitle.setPaintFlags(readingTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        add_readbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = requireContext();
                Intent intent = new Intent(context, AddReadBook.class);
                context.startActivity(intent);
            }
        });
        myDB = new DBHelper(requireContext());
        BookList = new ArrayList<>();
        bookStatus = new ArrayList<>();

        storeReadData();
        customAdapter = new CustomAdapter(getActivity(),BookList);

        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        return view;
    }


    void storeReadData() {
        Cursor cursor = myDB.readBookData();
        if (cursor != null && cursor.getCount() > 0) {
            int statusIndex = cursor.getColumnIndexOrThrow("book_status"); // Retrieve index of "status" column
            while (cursor.moveToNext()) {
                String status = cursor.getString(statusIndex); // Retrieve status using index
                if ("Read".equals(status)) { // Check if status is "Read"
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("book_title"));
                    String author = cursor.getString(cursor.getColumnIndexOrThrow("book_author"));
                    String genre = cursor.getString(cursor.getColumnIndexOrThrow("book_GENRE"));
                    String isbn = cursor.getString(cursor.getColumnIndexOrThrow("book_isbn"));
                    bookStatus.add(status);

                    // Create a Book object and add it to BookList
                    Book book = new Book(id, title, author, genre, isbn, status);
                    BookList.add(book);
                }
            }
        } else {
            Toast.makeText(requireContext(), "No data found.", Toast.LENGTH_SHORT).show();
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public void refresh(){
        BookList.clear();
        bookStatus.clear();

        storeReadData();

        customAdapter.notifyDataSetChanged();
    }
}