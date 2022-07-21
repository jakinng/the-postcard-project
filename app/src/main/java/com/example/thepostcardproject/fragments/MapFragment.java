package com.example.thepostcardproject.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.FragmentMapBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    public final static String TAG = "MapFragment";

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


        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        Log.d(TAG, String.valueOf(mapFragment));
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Log.d(TAG, String.valueOf(fragmentManager));
        fragmentManager
                .beginTransaction()
                .add(R.id.rl_map_container, mapFragment)
                .commit();
//        getChildFragmentManager()
//                .beginTransaction()
//                .add(R.id.rl_map_container, mapFragment)
//                .commit();
//        mapFragment.getMapAsync(this);
    }

    /**
     * Set the action bar to have the appropriate title and icons
     */
    private void configureActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("Explore"); // set the top title
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        Log.d(TAG, String.valueOf(googleMap));
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
    }
}