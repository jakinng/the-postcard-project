package com.example.thepostcardproject.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.models.Location;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.utilities.OnBottomSheetCallbacks;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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

    private EditText etDateRange;
    private EditText etLocationFrom;
    private EditText etLocationTo;


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
        setupViews(view);
        inflateHomeFragment();
        setupDateRangePicker();
        setupLocationFromPicker();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupViews(View view) {
        etDateRange = view.findViewById(R.id.et_date_range);
        etLocationFrom = view.findViewById(R.id.et_filter_location_from);
        etLocationTo = view.findViewById(R.id.et_filter_location_to);
    }

    private void setupDateRangePicker() {
        etDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeFragment.launchDateRangePicker();
            }
        });
    }

    private void setupLocationFromPicker() {
        etLocationFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeFragment.launchLocationFromPicker();
            }
        });
    }

    // ################################
    // ##  CONFIGURE BOTTOM SHEET    ##
    // ################################

    public void setOnBottomSheetCallbacks(OnBottomSheetCallbacks onBottomSheetCallbacks) {
        this.listener = onBottomSheetCallbacks;
    }

    public void closeBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void openBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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

    public void displayDateRange(Date startDate, Date endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, y");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String datesFromTo = dateFormat.format(startDate) + " - " + dateFormat.format(endDate);
        etDateRange.setText(datesFromTo);
    }

    public void displayLocationFrom(Location locationFrom) {
        try {
            etLocationFrom.setText(locationFrom.getLocationName());
        } catch (com.parse.ParseException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * Adds the home fragment with the postcards received
     */
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
        getChildFragmentManager().beginTransaction().replace(R.id.rl_home_fragment, homeFragment).commit();
    }
}