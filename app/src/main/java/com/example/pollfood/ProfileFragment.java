package com.example.pollfood;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pollfood.Classes.Users;
import com.example.pollfood.databinding.FragmentLoginBinding;
import com.example.pollfood.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Launcher for requesting permissions
    private ActivityResultLauncher<String[]> requestPermissionsLauncher;

    // Launcher for picking an image from the gallery
    private ActivityResultLauncher<Intent> pickImageLauncher;

    private CircleImageView imageViewSelected;
    private static final int PERMISSION_CODE = 1001;
    private static final int PICK_IMAGE_REQUEST = 1002;
    private String[] currentPermissionsToRequest;
    FragmentProfileBinding binding;
    private FirebaseAuth mAuth;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
       if (user!=null){
           String uid = user.getUid();
           DocumentReference docref = db.collection("users").document(uid);
           docref.get().addOnSuccessListener(documentSnapshot -> {
               if (documentSnapshot.exists()) {
                   String email = documentSnapshot.getString("email");
                   String username = documentSnapshot.getString("username");
                   String fname = documentSnapshot.getString("fname");
                   String sname = documentSnapshot.getString("sname");
                   Long familyid = documentSnapshot.getLong("familyid");

                   String welcome = "Szia " + username + "!";
                   binding.textView4.setText(welcome);
                   binding.inputFirstName.setText(fname);
                   binding.inputSurname.setText(sname);
                   binding.inputUsername.setText(username);
                   binding.inputEmail.setText(email);
               }
           });
       }
        imageViewSelected = binding.profileImage;

        return binding.getRoot();
    }
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            return ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_CODE);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            // Load selected image into ImageView using Glide
            Glide.with(this)
                    .load(imageUri)
                    .into(imageViewSelected);

            // Save URI or upload it as needed
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.buttonLogout.setOnClickListener(l->{
            Context context = getContext();
            if(context != null){
                if (context instanceof MainActivity){
                    MainActivity mainActivity = (MainActivity) context; //type cast, use instance of to avoid ClassCastException
                    mainActivity.logout_user();
                }
            }
        });
        binding.profileImage.setOnClickListener(v -> {
            if (checkPermission()) {
                openGallery();
            } else {
                requestPermission();
            }
        });

        binding.buttonSaveChanges.setOnClickListener(l->{
            if (!binding.inputUsername.getText().equals("") && !binding.inputFirstName.getText().equals("") && !binding.inputSurname.getText().equals("")  ){
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String uid = user.getUid();
                DocumentReference docRef = db.collection("users").document(uid);
                Map<String, Object> updates = new HashMap<>();
                updates.put("username", binding.inputUsername.getText().toString());
                updates.put("fname", binding.inputFirstName.getText().toString());
                updates.put("sname", binding.inputSurname.getText().toString());
                docRef.update(updates)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firestore", "Document successfully updated with new field!");
                        })
                        .addOnFailureListener(e -> {
                            Log.w("Firestore", "Error updating document", e);
                        });
                Fragment currentFragment = this;  // inside the fragment class

                if (getFragmentManager() != null) {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(currentFragment.getId(), new ProfileFragment()) // replace with new instance
                            .commit();
                }
            }else{
                Toast.makeText(getContext(), "Nincs minden mező kitöltve!", Toast.LENGTH_LONG).show();
            }

        });

        binding.buttonDeleteProfile.setOnClickListener(l->{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null){
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
                docRef.delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firestore", "Document successfully deleted!");
                        })
                        .addOnFailureListener(e -> {
                            Log.w("Firestore", "Error deleting document", e);
                        });

                user.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Auth", "User account deleted.");
                                Context context = getContext();
                                if(context != null){
                                    if (context instanceof MainActivity){
                                        MainActivity mainActivity = (MainActivity) context; //type cast, use instance of to avoid ClassCastException
                                        mainActivity.logout_user();
                                    }
                                }

                            } else {
                                Log.e("Auth", "Delete failed", task.getException());
                                // Check if recent login is required
                            }
                        });
            }
        });


    }



    private void init_user(FirebaseFirestore db){

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            String uid = user.getUid();
            DocumentReference docref = db.collection("users").document(uid);
            docref.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()){
                    String email = documentSnapshot.getString("email");
                    String username = documentSnapshot.getString("username");
                    String fname = documentSnapshot.getString("fname");
                    String sname = documentSnapshot.getString("sname");
                    Long familyid = documentSnapshot.getLong("familyid");

                    Users singleton_user = Users.getInstance(uid,email,username);
                    if (fname != null){
                        singleton_user.setFirstname(fname);
                    }
                    if (sname != null){
                        singleton_user.setSecondname(sname);
                    }
                    if (familyid != null){
                        singleton_user.setFamilyid(familyid);
                    }
                    Log.d("users single instance", "kesz");
                    Log.d("users single instance", singleton_user.toString());
                }else {
                    Log.d("Firestore", "No such document");
                }
            }).addOnFailureListener(e ->{
                Log.w("Firestore", "Error fetching document", e);
            });
        }

    }
}