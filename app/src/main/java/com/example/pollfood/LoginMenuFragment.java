package com.example.pollfood;

import android.os.Bundle;

import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import androidx.activity.OnBackPressedCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pollfood.databinding.FragmentLoginMenuBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginMenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FragmentLoginMenuBinding binding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginMenuFragment newInstance(String param1, String param2) {
        LoginMenuFragment fragment = new LoginMenuFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.loginButton.setOnClickListener(l ->{
            Fragment newFrag = new LoginFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView,newFrag);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        binding.registerButton.setOnClickListener(l ->{
            Fragment newFrag = new RegisterFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView,newFrag);
            transaction.addToBackStack(null);
            transaction.commit();
        });



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLoginMenuBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}