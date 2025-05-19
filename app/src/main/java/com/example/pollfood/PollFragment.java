package com.example.pollfood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public  class PollFragment extends Fragment { // Note the "static" keyword

    public PollFragment() {
        // Required empty public constructor
    }

    // Optional: Factory method
    public static PollFragment newInstance(/* params */) {
        PollFragment fragment = new PollFragment();
        // Bundle args = new Bundle();
        // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poll, container, false);
    }

    // ... other fragment methods ...
}
