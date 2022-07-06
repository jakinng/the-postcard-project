package com.example.thepostcardproject.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.utilities.OnBottomSheetCallbacks;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeBackdropFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeBackdropFragment extends Fragment {
    private static final String TAG = "HomeBackdropFragment";
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private OnBottomSheetCallbacks listener;
    private HomeFragment homeFragment;

    public HomeBackdropFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//         Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_backdrop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        inflateHomeFragment();
        super.onViewCreated(view, savedInstanceState);
    }

    public void setOnBottomSheetCallbacks(OnBottomSheetCallbacks onBottomSheetCallbacks) {
        this.listener = onBottomSheetCallbacks;
    }

    public void closeBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void openBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void inflateHomeFragment() {
        HomeFragment.GoToDetailViewListener goToDetailViewListener = new HomeFragment.GoToDetailViewListener() {
            @Override
            public void goToDetailView(Postcard postcard) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.rl_container, PostcardDetailFragment.newInstance(postcard))
                        .addToBackStack(null)
                        .commit();
            }
        };
        homeFragment = HomeFragment.newInstance(goToDetailViewListener);
//        homeFragment = new HomeFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.rl_home_fragment, homeFragment).commit();

    }

    public void configureBackdrop(View view) {
        bottomSheetBehavior = BottomSheetBehavior.from(view);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                listener.onStateChanged(bottomSheet, newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) { }
        });
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}