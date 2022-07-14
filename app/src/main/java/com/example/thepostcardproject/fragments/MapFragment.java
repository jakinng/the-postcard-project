package com.example.thepostcardproject.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.FragmentMapBinding;
import com.example.thepostcardproject.databinding.FragmentPostcardDetailBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {
    FragmentMapBinding binding;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureActionBar();
    }

    /**
     * Set the action bar to have the appropriate title and icons
     */
    private void configureActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("Explore"); // set the top title
    }
}