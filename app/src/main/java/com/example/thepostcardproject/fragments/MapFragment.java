package com.example.thepostcardproject.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.FragmentMapBinding;
import com.example.thepostcardproject.models.Location;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.viewmodels.HomeViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import java.io.File;
import java.io.FileInputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    public final static String TAG = "MapFragment";

    FragmentMapBinding binding;
    private HomeViewModel viewModel;

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

        // Initialize map fragment
        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async map
        supportMapFragment.getMapAsync(this);

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        setViewModel();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        Log.d(TAG, "total postcards: " + viewModel.receivedPostcards.getValue().size());
        for (Postcard postcard : viewModel.receivedPostcards.getValue()) {
            Location locationFrom = postcard.getLocationFrom();
            ParseGeoPoint coordinates = locationFrom.getCoordinates();
            LatLng postcardLatLng = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
            String locationName = locationFrom.getLocationName();
            Log.d(TAG, "location: " + locationName);
            try {
                File imageFile = postcard.getCoverPhotoFiltered().getPhotoFile().getFile();
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 40, 30, true);
                googleMap.addMarker(new MarkerOptions()
                        .position(postcardLatLng)
                        .title(locationName))
                        .setIcon(BitmapDescriptorFactory.fromBitmap(scaledBitmap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }
}