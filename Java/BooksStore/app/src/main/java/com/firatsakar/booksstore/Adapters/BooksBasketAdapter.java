package com.firatsakar.booksstore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firatsakar.booksstore.R;
import com.firatsakar.booksstore.databinding.BooksFragmentCardviewBinding;
import com.firatsakar.booksstore.databinding.BooksbasketFragmentCardviewBinding;
import com.firatsakar.booksstore.model.Book;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BooksBasketAdapter extends RecyclerView.Adapter<BooksBasketAdapter.CardViewBasketObject> {

    private Context context;
    private ArrayList<Book> bookArrayList;

    private BooksbasketFragmentCardviewBinding binding;

    public BooksBasketAdapter(Context context, ArrayList<Book> bookArrayList) {
        this.context = context;
        this.bookArrayList = bookArrayList;
    }


    @NonNull
    @Override
    public CardViewBasketObject onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = BooksbasketFragmentCardviewBinding.inflate(LayoutInflater.from(context),parent,false);

        return new CardViewBasketObject(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewBasketObject holder, int position) {
        Book book = bookArrayList.get(position);
        
        loadBookDetails(book,holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,book.getBookName(),Toast.LENGTH_SHORT).show();
            }
        });

        holder.removeBasketCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFromBasket(context,book.getId());
            }
        });


    }

    private void loadBookDetails(Book book, CardViewBasketObject holder) {
        String bookId = book.getId();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // bilgiler çekildi
                        String bookName = ""+snapshot.child("bookName").getValue();
                        String bookAuthor = ""+snapshot.child("bookAuthor").getValue();
                        String bookPrice = ""+snapshot.child("bookPrice").getValue();
                        String bookImageurl = ""+snapshot.child("url").getValue();
                        String uid = ""+snapshot.child("uid").getValue();

                        // bilgiler set edildi
                        book.setFavorite(true);
                        book.setBookAuthor(bookAuthor);
                        book.setBookName(bookName);
                        book.setBookPrice(bookPrice);
                        book.setUrl(bookImageurl);
                        book.setUid(uid);

                        Picasso.get().load(bookImageurl).into(holder.basketBookImage);
                        holder.basketBookAuthor.setText(bookAuthor);
                        holder.basketBookName.setText(bookName);
                        holder.basketBookPrice.setText(bookPrice);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }

    public class CardViewBasketObject extends RecyclerView.ViewHolder {

        public ImageView basketBookImage;
        public TextView basketBookName;
        public TextView basketBookAuthor;
        public TextView basketBookPrice;
        public MaterialCardView removeBasketCardView;


        public CardViewBasketObject(@NonNull View itemView) {
            super(itemView);

            basketBookImage = binding.basketBookImg;
            basketBookAuthor = binding.basketBookAuthor;
            basketBookName = binding.basketBookName;
            basketBookPrice = binding.basketBookPrice;
            removeBasketCardView = binding.basketDeleteCardView;
        }
    }

    private void removeFromBasket(Context context, String bookId){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(context, "Sepetten kaldırılamadı", Toast.LENGTH_SHORT).show();
        }else{

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.child(firebaseAuth.getUid()).child("Basket").child(bookId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context,"Sepetten başarıyla kaldırıldı", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"Sepetten kaldırılamadı", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }


}
