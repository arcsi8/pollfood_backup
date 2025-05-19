package com.example.pollfood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pollfood.Base.BaseActivity;
import com.example.pollfood.Classes.Users;
import com.example.pollfood.databinding.ActivityLoginBinding;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;
    private Button btn_login;
    private Button btn_register;

    private MaterialSwitch show_pass;

    private FragmentContainerView frag_view;


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug_login", "debug1");
        new Handler().postDelayed(() -> {
            FirebaseUser user = mAuth.getCurrentUser();
            Log.d("debug_login", "debug2");
            if (user != null){
                Log.d("debug_login", "debug3");
                Intent succesfulLogin = new Intent(LoginActivity.this, MainActivity.class);

                startActivity(succesfulLogin);
                finish();
            }
        },800);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);



        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);






       /* if (savedInstanceState != null){
            boolean buttonsHidden = savedInstanceState.getBoolean("buttonsHidden",false);
            if (buttonsHidden){
                binding.loginButton.setVisibility(View.INVISIBLE);
                binding.registerButton2.setVisibility(View.INVISIBLE);
                binding.registerButton.setVisibility(View.INVISIBLE);
            }
        }*/




        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getSupportFragmentManager().popBackStack();
                }else {
                    finish();
                }

            }
        });

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }


    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        fragmentTransaction.addToBackStack(null); // Optional: Allows back button to remove fragment
        fragmentTransaction.commit();


    }

}

