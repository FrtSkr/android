package com.firatsakar.booksstore.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.firatsakar.booksstore.model.Book;
import com.firatsakar.booksstore.model.FilterBook;
import com.firatsakar.booksstore.databinding.BooksFragmentCardviewBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.CardViewObjects> implements Filterable {

    private Context context;
    public ArrayList<Book> bookArrayList, filterList;

    private FirebaseAuth firebaseAuth;
    boolean isInMyBasket = false;
    private FilterBook filter;
    private BooksFragmentCardviewBinding binding;

    public BookAdapter(Context context, ArrayList<Book> bookArrayList) {
        this.context = context;
        this.bookArrayList = bookArrayList;
        this.filterList = bookArrayList;
    }


    @NonNull
    @Override
    public CardViewObjects onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = BooksFragmentCardviewBinding.inflate(LayoutInflater.from(context),parent,false);


        return new CardViewObjects(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewObjects holder, int position) {
        Book _books = bookArrayList.get(position);





        holder.bookName.setText(_books.getBookName());
        holder.bookAuthor.setText(_books.getBookAuthor());
        holder.bookPrice.setText(_books.getBookPrice());
        Picasso.get()
                .load(_books.getUrl())
                .resize(150,200)
                .into(holder.bookImg);



        holder.books_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "CardView Tıklandı", Toast.LENGTH_SHORT).show();
            }
        });

        holder.bookAddBasketCardViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addToBasket(context, _books.getId());
            }
        });

    }



    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }



    public class CardViewObjects extends RecyclerView.ViewHolder {
        public ImageView bookImg;
        public TextView bookName;
        public TextView bookAuthor;
        public TextView bookPrice;
        public MaterialCardView books_cardview;
        public AppCompatButton bookAddBasketCardViewButton;


        public CardViewObjects(@NonNull View itemView) {
            super(itemView);

            books_cardview = binding.booksCardview;
            bookAddBasketCardViewButton = binding.bookAddBasketCardViewButton;
            bookName = binding.bookName;
            bookAuthor = binding.bookAuthor;
            bookPrice = binding.bookPrice;
            bookImg = binding.bookImg;


        }
    }


    private void addToBasket(Context context, String bookId){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(context, "Sepete eklenemedi", Toast.LENGTH_SHORT).show();
        }else{
            long timestamp = System.currentTimeMillis();

            HashMap<String ,Object> hashMap = new HashMap<>();
            hashMap.put("bookId", ""+bookId);
            hashMap.put("timestamp", ""+timestamp);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.child(firebaseAuth.getUid()).child("Basket").child(bookId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context,"Sepete başarıyla eklendi", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"Sepete eklerken hata oluştu", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }


    @Override
    public Filter getFilter() {
        // gelen filterListimizi FilterBook sınıfımıza ulaştırdık
        if(filter == null){
            filter = new FilterBook(filterList, this);
        }
        return filter;
    }
}