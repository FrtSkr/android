package com.firatsakar.booksstore.ui.admin;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firatsakar.booksstore.R;
import com.firatsakar.booksstore.databinding.FragmentAddBookBinding;
import com.firatsakar.booksstore.databinding.FragmentDashboardAdminBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class AddBookFragment extends Fragment {

    private FragmentAddBookBinding binding;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private static final String TAG = "ADD_IMAGE";
    private static final int IMAGE_PICK_CODE = 1;
    private Uri imageUri = null;
    private String imageUrl = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddBookBinding.inflate(inflater,container,false);
        // authentication çağrıldı
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Lütfen bekleyin");
        progressDialog.setCanceledOnTouchOutside(false);


        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAdminDashboard(binding.getRoot());
            }
        });
        binding.selectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePickIntent();
            }
        });

        binding.addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });


        return binding.getRoot();
    }
    String bookName="", bookPrice="",bookAuthor="";
    private void validateData() {
        bookName = binding.bookName.getText().toString().trim();
        bookPrice = binding.bookPrice.getText().toString().trim();
        bookAuthor = binding.bookAuthor.getText().toString().trim();
        
        if(TextUtils.isEmpty(bookName)){
            Toast.makeText(getContext(), "Kitap adı boş bırakılamaz", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(bookPrice)){
            Toast.makeText(getContext(), "Fiyat boş bırakılamaz", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(bookAuthor)){
            Toast.makeText(getContext(), "Kitap yazarı boş bırakılamaz", Toast.LENGTH_SHORT).show();
        }else if(imageUri == null){
            Toast.makeText(getContext(), "Kitap fotoğrafı seçin...", Toast.LENGTH_SHORT).show();
        }else{
            //tüm validation işlemleri gerçekleştiyse
            uploadImageStorage();
        }
    }

    private void uploadImageStorage() {
        Log.d(TAG, "kitap databasee yüklenirken");
        progressDialog.setMessage(" Resim yükleniyor...");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();
        String filePathAndName = "Books/"+ timestamp;


        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "resim veritabanına yüklendi");

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadedImageUrl = ""+uriTask.getResult();
                        
                        uploadImageInfoDb(uploadedImageUrl,timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "image yüklenemedi"+e.getMessage());
                        Toast.makeText(getContext(),"image yüklenirken bi hata ile karşılaşıldı"+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void uploadImageInfoDb(String uploadedImageUrl,long timestamp) {
        progressDialog.setMessage("Resim bilgileri yükleniyor..");

        String uid = firebaseAuth.getUid();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("id",""+timestamp);
        hashMap.put("bookName",bookName);
        hashMap.put("bookPrice",bookPrice);
        hashMap.put("bookAuthor",bookAuthor);
        hashMap.put("url",uploadedImageUrl);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"Database'e eklendi",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"Database e yüklenemedi"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void imagePickIntent() {
        Log.d(TAG, "ımagePickIntent: kaydedilme logu");
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/jpg");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),IMAGE_PICK_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_CODE){
                Log.d(TAG, "onActivityResult image picked");

                imageUri = data.getData();

                Log.d(TAG,"onActivityResult image uri " + imageUri);

            }else{
                Log.d(TAG , "onActivityResult seçilmedi image  ");
                Toast.makeText(getContext(), "resim seçilmedi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void goToAdminDashboard(View view){
        NavDirections action = AddBookFragmentDirections.actionAddBookFragmentToDashboardAdminFragment();
        Navigation.findNavController(view).navigate(action);
    }
}