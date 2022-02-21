package com.firatsakar.booksstore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.firatsakar.booksstore.databinding.FragmentHomeBinding;
import com.firatsakar.booksstore.ui.main.BooksBasketFragment;
import com.firatsakar.booksstore.ui.main.BooksFragment;
import com.firatsakar.booksstore.ui.main.user.UserProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class HomeFragment extends Fragment {
    public HomeFragment() {

    }

    private FragmentHomeBinding binding;
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment = new BooksFragment();
    private FrameLayout frameLayout;
    private Animation anim;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(LayoutInflater.from(this.getContext()));

        frameLayout = binding.frameLayout;
        bottomNavigationView = binding.bottomNav;


        getChildFragmentManager().beginTransaction().add(R.id.frameLayout, new BooksFragment()).commit();   // uygulamamızın açılış ekranını booksFragment olarak ayarladık

        anim = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_enter_anim);

        return binding.getRoot();
    }



    @Override
    public void onStart() {
        super.onStart();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) { // bottom nav. da gerçekleşen aksiyonları aldık
                if (item.getItemId() == R.id.userSetting) {
                    fragment = new UserProfileFragment();
                } else if (item.getItemId() == R.id.basket) {
                    fragment = new BooksBasketFragment();
                } else if (item.getItemId() == R.id.books) {
                    fragment = new BooksFragment();
                }
                frameLayout.startAnimation(anim);
                getChildFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit(); // bottom navigation'ın aksiyonuna göre fragmentları frame layout'a gönderdik.
                return true;
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        bottomNavigationView = null;
        fragment = null;
        frameLayout = null;
        anim = null;
    }


}