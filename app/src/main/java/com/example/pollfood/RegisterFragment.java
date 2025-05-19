package com.example.pollfood;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pollfood.databinding.FragmentRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FragmentRegisterBinding binding;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.regContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.regInputUsername.getText().toString().trim();
                String password = binding.regInputPassword.getText().toString().trim();
                String email = binding.regInputEmail.getText().toString().trim();

                Log.i("username",username);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("signin", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null){
                                    UserProfileChangeRequest profileupdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(username).build();
                                    user.updateProfile(profileupdates).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()){
                                            Log.i("profileupdate","display name set");
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            Map<String, Object> document = new HashMap<>();
                                            document.put("uid", user.getUid());
                                            document.put("username",user.getDisplayName());
                                            document.put("email",user.getEmail());


                                            db.collection("users").document(user.getUid()).set(document)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.d("d", "DocumentSnapshot successfully merged/written with ID: " + user.getUid());
                                                        Toast.makeText(getContext(), "User merged/added with ID: " + user.getUid(), Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.w("w", "Error merging document", e);
                                                        Toast.makeText(getContext(), "Error merging user: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                    });
                                        }
                                    });

                                    }
                                    ObjectAnimator slideOut = ObjectAnimator.ofFloat(binding.getRoot(),"translationX",0f,500f);
                                    ObjectAnimator fadeOut = ObjectAnimator.ofFloat(binding.getRoot(), "alpha",1f,0f);
                                    AnimatorSet animset = new AnimatorSet();
                                    animset.setDuration(800);
                                    animset.playTogether(slideOut,fadeOut);
                                    animset.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            binding.getRoot().setVisibility(View.GONE);
                                            binding.getRoot().setTranslationX(0);
                                            binding.getRoot().setAlpha(1f);
                                        }
                                    });
                                    animset.start();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            startActivity(intent);

                                        }
                                    }, 1000);

                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("signin", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });


            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(getLayoutInflater());


        return binding.getRoot();
    }
}