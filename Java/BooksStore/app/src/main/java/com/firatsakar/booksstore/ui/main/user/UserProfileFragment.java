package com.firatsakar.booksstore.ui.main.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firatsakar.booksstore.HomeFragment;
import com.firatsakar.booksstore.HomeFragmentDirections;
import com.firatsakar.booksstore.R;
import com.firatsakar.booksstore.databinding.FragmentUserProfileBinding;
import com.firatsakar.booksstore.ui.login.LoginScreenFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class UserProfileFragment extends Fragment {
    public UserProfileFragment() {
    }

    private FragmentUserProfileBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(getLayoutInflater());

        firebaseAuth = FirebaseAuth.getInstance();

        loadUserInfo();
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.profileEditIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                goToUserEditFragment(view); //en son bunu koydu
            }
        });


        // Home to go login
        binding.exitIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //refactor et sonra
                firebaseAuth.signOut(); // çıkış işlemini gerçekleştirmek için veritabanıyla ilişkisini de kestik.
                                        // fakat geri tuşuna basarsak nullpointer hatası alırız onu düzelteceğiz.
                goToLoginFragment(View.inflate(getContext(),R.layout.fragment_home, binding.getRoot()));
            }
        });



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

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

                        // set data to ui
                        binding.emailTv.setText(email);
                        binding.nameTv.setText(firstName.concat(" " + lastName));

                        Glide.with(binding.getRoot().getContext())
                                .load(profileImage)
                                .placeholder(R.drawable.ic_person)
                                .into(binding.profileIv);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        }




    public void goToLoginFragment(View view) {
        NavDirections action = HomeFragmentDirections.actionHomeFragmentToLoginScreenFragment();
        Navigation.findNavController(view).navigate(action);
    }

    public void goToUserEditFragment(View view) {
        NavDirections action = HomeFragmentDirections.actionHomeFragmentToUserEditProfileFragment();
        Navigation.findNavController(view).navigate(action);
    }
}