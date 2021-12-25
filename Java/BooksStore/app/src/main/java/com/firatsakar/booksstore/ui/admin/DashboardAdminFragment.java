package com.firatsakar.booksstore.ui.admin;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firatsakar.booksstore.R;
import com.firatsakar.booksstore.databinding.FragmentDashboardAdminBinding;
import com.firatsakar.booksstore.databinding.FragmentLoginScreenBinding;
import com.firatsakar.booksstore.ui.login.LoginScreenFragmentDirections;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class DashboardAdminFragment extends Fragment {

    private FragmentDashboardAdminBinding binding;

    private FirebaseAuth firebaseAuth;

    public DashboardAdminFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardAdminBinding.inflate(inflater,container,false);

        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();

        binding.exitImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        binding.addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddBook(binding.getRoot());
            }
        });


        return binding.getRoot();
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            goToHome(binding.getRoot());
            onDestroy();
        }else{
            String email=firebaseUser.getEmail();
            binding.adminEmail.setText(email);
        }
    }

    public void goToHome(View view){
        NavDirections action = DashboardAdminFragmentDirections.actionDashboardAdminFragmentToLoginScreenFragment();
        Navigation.findNavController(view).navigate(action);
    }
    public void goToAddBook(View view){
        NavDirections action = DashboardAdminFragmentDirections.actionDashboardAdminFragmentToAddBookFragment();
        Navigation.findNavController(view).navigate(action);
    }
}
