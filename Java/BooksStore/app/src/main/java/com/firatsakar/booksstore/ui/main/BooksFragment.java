package com.firatsakar.booksstore.ui.main;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.firatsakar.booksstore.Adapters.BookAdapter;
import com.firatsakar.booksstore.model.Book;
import com.firatsakar.booksstore.databinding.FragmentBooksBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BooksFragment extends Fragment {

    private RecyclerView booksRecyclerView;
    private ArrayList<Book> bookArrayList;
    private BookAdapter bookAdapter;
    private EditText searchET;
    private FragmentBooksBinding binding;


    public BooksFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBooksBinding.inflate(inflater, container, false);
        booksRecyclerView = binding.booksRecyclerView;
        //booksRecyclerView = view.findViewById(R.id.booksRecyclerView);
        bookArrayList = new ArrayList<>();

        booksRecyclerView.setHasFixedSize(true);
        booksRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        searchET = binding.searchET;
        loadBookList();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // filtret list
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {

                    bookAdapter.getFilter().filter(charSequence);

                } catch (Exception e) {
                    Log.e("Filter Log: ", e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        booksRecyclerView = null;
        bookAdapter = null;
        bookArrayList = null;
        searchET = null;
    }

    private void loadBookList() {
        bookArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                bookArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Book book = ds.getValue(Book.class);
                    bookArrayList.add(book);
                }
                bookAdapter = new BookAdapter(getContext(), bookArrayList);
                binding.booksRecyclerView.setAdapter(bookAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}