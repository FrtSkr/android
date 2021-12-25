package com.firatsakar.booksstore.ui.login;

import android.app.ProgressDialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firatsakar.booksstore.databinding.FragmentLoginScreenBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginScreenFragment extends Fragment {

    private FragmentLoginScreenBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    private ConstraintLayout constraintLayout;
    public LoginScreenFragment() {}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLoginScreenBinding.inflate(inflater,container,false);
        constraintLayout = binding.loginLayout;

        // Renk animasyonu
        AnimationDrawable animationDrawable = (AnimationDrawable)constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(500);
        animationDrawable.setExitFadeDuration(1500);
        animationDrawable.start();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Lütfen Bekleyiniz");
        progressDialog.setCanceledOnTouchOutside(false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // kullanıcı önceden giriş yapmışsa ...
        if(firebaseUser != null){
            goToHome(view);
        }

        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignUp(view);
            }
        });

        binding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateData();
            }
        });

    }

    private String email="",password="";
    private void validateData() {

        // veri getirme
        email = binding.emailEditText.getText().toString().trim();
        password = binding.passwordEditText.getText().toString().trim();

        //veriyi kontrolü
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getContext(), "Email boş bırakılamaz", Toast.LENGTH_SHORT).show();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
        Toast.makeText(getContext(), "Geçersiz email adresi girdiniz", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(getContext(), "Şifre boş bırakılamaz", Toast.LENGTH_SHORT).show();
        }else{
            // girilen değerler doğru ise
            loginUser();
        }

}

    private void loginUser() {
        progressDialog.setMessage("Giriş yapılıyor...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    progressDialog.setMessage("Giriş Başarılı");
                    progressDialog.show();
                    //erğe
                    checkUser();
                }
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),"Email ya da parolanız yanlış!",Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void checkUser() {
        progressDialog.setMessage("Kullanıcı kontrol ediliyor...");
        progressDialog.show();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();

                        String userType = ""+snapshot.child("userType").getValue();

                        if(userType.equals("user")){
                            //kullanıcı dashboard'ına git
                            goToHome(binding.getRoot());
                            onDestroy();
                        }else if(userType.equals("admin")){

                            goToAdminDashboard(binding.getRoot());
                            onDestroy();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public void goToSignUp(View view){
        NavDirections action = LoginScreenFragmentDirections.actionLoginScreenFragmentToRegisterScreenFragment();
        Navigation.findNavController(view).navigate(action);
    }

    public void goToHome(View view){
        NavDirections action = LoginScreenFragmentDirections.actionLoginScreenFragmentToHomeFragment();
        Navigation.findNavController(view).navigate(action);
    }

    public void goToAdminDashboard(View view){
        NavDirections action = LoginScreenFragmentDirections.actionLoginScreenFragmentToDashboardAdminFragment();
        Navigation.findNavController(view).navigate(action);
    }

}