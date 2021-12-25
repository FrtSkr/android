package com.firatsakar.booksstore.ui.login;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;


import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firatsakar.booksstore.databinding.FragmentRegisterScreenBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;


public class RegisterScreenFragment extends Fragment {

    private FragmentRegisterScreenBinding binding;

    //Firebase Auth
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    public RegisterScreenFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //view Binding sayesinde findById kullanımından kurtuluyoruz
        binding = FragmentRegisterScreenBinding.inflate(inflater,container,false);


        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Lütfen Bekleyiniz");
        progressDialog.setCanceledOnTouchOutside(false);



        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLogin(view);

            }
        });

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validationData();


            }
        });

    }

    String email="", name="", lastName="",password="",confirmPassword="";

    private void validationData(){

        email = binding.emailEditText.getText().toString().trim(); //trim string ifade de boşluklarıkaldırır
        name = binding.nameEditText.getText().toString();
        lastName = binding.lastNameEditText.getText().toString();
        password = binding.passwordEditText.getText().toString();
        confirmPassword = binding.confirmPasswordEditText.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(getContext(), "Email boş bırakılamaz", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(name)){
            Toast.makeText(getContext(), "İsim boş bırakılamaz", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(lastName)){
            Toast.makeText(getContext(), "Soyisim boş bırakılamaz", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(getContext(), "Şifre boş bırakılamaz", Toast.LENGTH_SHORT).show();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(getContext(), "Geçersiz email adresi girdiniz", Toast.LENGTH_SHORT).show();
        }else if (!password.equals(confirmPassword)){
            Toast.makeText(getContext(), "Girilen şifreler uyuşmuyor", Toast.LENGTH_SHORT).show();
        }else{
            //bütün validation işlemleri gerçekleştiyse
            createUserAccoun();
        }
    }

    private void createUserAccoun() {
        progressDialog.setMessage("Kayıt işlemi gerçekleştiriliyor...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                progressDialog.dismiss();
                //veritabanına giriş yapan kişiyi kaydetme methodu
                saveDatabase();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                
            }
        });

    }

    private void saveDatabase() {
        progressDialog.setMessage("Kullanıcı bilgileri kaydediliyor...");
        progressDialog.show();


        String uid = firebaseAuth.getUid();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("email",email);
        hashMap.put("name",name);
        hashMap.put("lastName",lastName);
        hashMap.put("password",password);
        hashMap.put("userType","user");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"Hesap oluşturuldu",Toast.LENGTH_SHORT).show();
                        goToHome(binding.getRoot());
                        onDestroy();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

    }

    // Home Ekranına Navigation
    public void goToHome(View view){
        NavDirections action = RegisterScreenFragmentDirections.actionRegisterScreenFragmentToHomeFragment();
        Navigation.findNavController(view).navigate(action);
    }

    // Giriş Ekranına Navigation
    public void goToLogin(View view){
        NavDirections action = RegisterScreenFragmentDirections.actionRegisterScreenFragmentToLoginScreenFragment();
        Navigation.findNavController(view).navigate(action);
    }


}