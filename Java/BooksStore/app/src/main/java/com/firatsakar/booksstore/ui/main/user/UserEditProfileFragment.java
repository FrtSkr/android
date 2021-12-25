package com.firatsakar.booksstore.ui.main.user;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;


import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firatsakar.booksstore.R;
import com.firatsakar.booksstore.databinding.FragmentUserEditProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class UserEditProfileFragment extends Fragment {

    public UserEditProfileFragment() {
    }

    private FragmentUserEditProfileBinding binding;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private Uri imageUri = null;
    private String name = "";
    private String lastName = "";
    private String email = "";
    private String adress="";





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserEditProfileBinding.inflate(getLayoutInflater());

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Lütfen Bekleyiniz..");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.successImgBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onUserDataControl();

            }
        });

        binding.backImgBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.userFirstName.getText()) ||
                        TextUtils.isEmpty(binding.userLastName.getText()) || TextUtils.isEmpty(binding.userEmail.getText())) {
                    alertMessageShow("Lütfen eksik bilgileri giriniz!",
                            "HATA", "Tamam", "İptal");
                } else {
                    goToHomeFragment(view);
                }
            }
        });

        binding.profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageAttachMenu();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void onUserDataControl() {
        if (TextUtils.isEmpty(binding.userFirstName.getText()) ||
                TextUtils.isEmpty(binding.userLastName.getText()) || TextUtils.isEmpty(binding.userEmail.getText())) {

            alertMessageShow("Lütfen eksik bilgileri giriniz!",
                    "HATA", "Tamam", "İptal");

        } else {
            name = binding.userFirstName.getText().toString().trim();
            lastName = binding.userLastName.getText().toString().trim();
            email = binding.userEmail.getText().toString().trim();
            adress = binding.userAdress.getText().toString().trim();

            if(imageUri == null){
                updateProfile("");

            }else{
                uploadImage();
            }


        }
    }

    private void uploadImage(){
        progressDialog.setMessage("Profil resmi yükleniyor");
        progressDialog.show();

        String filePathAndName = "ProfilImages/"+firebaseAuth.getUid();

        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName);
        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // profil resminin yüklendiği yer
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadedImageUrl= ""+uriTask.getResult();
                        updateProfile(uploadedImageUrl);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"Resim yüklenemdi",Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void updateProfile(String imageUrl){
        progressDialog.setMessage("Profil resmi yükleniyor..");
        progressDialog.show();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("name",""+name);
        hashMap.put("lastName",""+lastName);
        hashMap.put("email",""+email);
        hashMap.put("adres",adress);



        if(imageUri != null){
            hashMap.put("profileImage",""+imageUrl);
        }
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"Veritabanına başarıyla yüklendi",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"Veritabanına yüklenirken hata ile karşılaşıldı",Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void showImageAttachMenu() {
        PopupMenu popupMenu = new PopupMenu(getContext(),binding.profileIv);
        popupMenu.getMenu().add(Menu.NONE,0,0,"Gallery");

        popupMenu.show();

        //tıklandığında
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //hangisine tıklandı
                int which = menuItem.getItemId();
                if(which == 0){
                    Log.e("which","galery");
                    // galeriye tıklandığında
                    pickImageGallery();
                }
                return false;
            }
        });

    }


    private void pickImageGallery(){
        Log.e("pick image galery","galery");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);


    }


    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // kullanıcı galeriye tıkladığınde dönecek olan sonuç
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        imageUri = data.getData();

                        binding.profileIv.setImageURI(imageUri);
                    }
                    else{
                        Toast.makeText(getContext(),"Bir hata oluştu",Toast.LENGTH_SHORT).show();
                    }

                }
            });



    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String email = ""+snapshot.child("email").getValue();
                        String firstName = ""+snapshot.child("name").getValue();
                        String lastName = ""+snapshot.child("lastName").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String adres = ""+snapshot.child("adres").getValue();

                        //değişmeyecek olan kısım
                        binding.editNameTv.setText(firstName.concat(" " + lastName));
                        binding.editEmailTv.setText(email);

                        // değişecek olan kısım
                        binding.userEmail.setText(email);
                        binding.userFirstName.setText(firstName);
                        binding.userLastName.setText(lastName);
                        binding.userAdress.setText(adres);

                        Glide.with(getContext())
                                .load(profileImage)
                                .placeholder(R.drawable.ic_person)
                                .into(binding.profileIv);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void goToHomeFragment(View view) {
        NavDirections action = UserEditProfileFragmentDirections.actionUserEditProfileFragmentToHomeFragment();
        Navigation.findNavController(view).navigate(action);
    }

    private void alertMessageShow(String message, String title, String pozitive, String negative) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(title);


        alert.setPositiveButton(pozitive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alert.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alert.create().show();
    }


}