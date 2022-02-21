package com.firatsakar.booksstore.ui.main;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firatsakar.booksstore.Adapters.BooksBasketAdapter;
import com.firatsakar.booksstore.R;
import com.firatsakar.booksstore.databinding.FragmentBooksBasketBinding;
import com.firatsakar.booksstore.databinding.FragmentBooksBinding;
import com.firatsakar.booksstore.model.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BooksBasketFragment extends Fragment {

    private FragmentBooksBasketBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Book> bookArrayList;
    private BooksBasketAdapter booksBasketAdapter;
    private View satinAlBTN;
    private ProgressDialog progressDialog;


    public BooksBasketFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBooksBasketBinding.inflate(inflater, container, false);
        satinAlBTN = binding.satinAlBTN;
        firebaseAuth = FirebaseAuth.getInstance();
        loadBasketBooks();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        satinAlBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Lütfen Bekleyiniz...");
                progressDialog.setCanceledOnTouchOutside(false);
                purchasingProcess();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void loadBasketBooks() {
        bookArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(firebaseAuth.getUid()).child("Basket")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                           bookArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String bookId = "" + ds.child("bookId").getValue();

                            Book book = new Book();
                            book.setId(bookId);

                            bookArrayList.add(book);
                        }

                        binding.booksBasketRecyclerView.setHasFixedSize(true);
                        binding.booksBasketRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                        booksBasketAdapter = new BooksBasketAdapter(getContext(), bookArrayList);
                        binding.booksBasketRecyclerView.setAdapter(booksBasketAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    private void purchasingProcess()  {

            progressDialog.setMessage("Satın Alma Gerçekleştiriliyor");
            progressDialog.show();
            progressDialog.setMessage("Allah Kaza Bela Vermesin. Hayırlı Günlerde Kullanın.");

        // BURADA DATABASE BAĞLANIP KULLANICIYA AİT SEPET TEMİZLENECEK.

    }

}




