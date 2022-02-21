package com.firatsakar.booksstore.ui.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firatsakar.booksstore.Adapters.BooksBasketAdapter;
import com.firatsakar.booksstore.databinding.FragmentBooksBasketBinding;
import com.firatsakar.booksstore.model.Book;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private ProgressDialog progressDialog;


    public BooksBasketFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBooksBasketBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        loadBasketBooks();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Lütfen Bekleyiniz...");
        progressDialog.setCanceledOnTouchOutside(false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.satinAlBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payFromBasket(getContext());
            }
        });

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

    private void payFromBasket(Context context){
        try {
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        progressDialog.setMessage("Satın Alma Gerçekleştiriliyor");
        progressDialog.show();
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(context, "Bir hata ile karşılaşıldı", Toast.LENGTH_SHORT).show();
        }else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.child(firebaseAuth.getUid()).child("Basket")
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.setMessage("Satın Alma Gerçekleştirildi");
                            progressDialog.show();
                            progressDialog.dismiss();
                            Toast.makeText(context,"Ödeme İşlemi Başarıyla Gerçekleti", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context,"Ödeme işleminde bir sorun ile karşılaşıldı", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

}