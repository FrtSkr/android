package com.firatsakar.booksstore.model;

import android.util.Log;
import android.widget.Filter;
import com.firatsakar.booksstore.Adapters.BookAdapter;
import java.util.ArrayList;


public class FilterBook extends Filter {

    private ArrayList<Book> filterList;

    private BookAdapter booksAdapter;

    public FilterBook(ArrayList<Book> filterList, BookAdapter booksAdapter) {
        this.filterList = filterList;
        this.booksAdapter = booksAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();

        if ( charSequence != null && charSequence.length() > 0){

            charSequence = charSequence.toString().toUpperCase();
            ArrayList<Book> filteredBook = new ArrayList<>();

            for (int i=0; i< filterList.size(); i++){
                if(filterList.get(i).getBookName().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getBookAuthor().toUpperCase().contains(charSequence)){

                    filteredBook.add(filterList.get(i)); // gelen değerler uyuşuyorsa listemizin içerisine atıyoruz.

                }
            }

            results.count = filteredBook.size();
            results.values = filteredBook;
        }else {
            // gelen bir veri yoksa verim yolladığım listedir.
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        // adaptörümüze oluşturduğumuz veriyi gönderiyoruz.
        Log.e("PublishResults","Filter"+ charSequence);
        booksAdapter.bookArrayList = (ArrayList<Book>)filterResults.values;
        booksAdapter.notifyDataSetChanged();

    }
}