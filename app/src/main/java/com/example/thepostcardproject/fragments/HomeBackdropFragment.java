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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeBackdropFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeBackdropFragment extends Fragment {
    private static final String TAG = "HomeBackdropFragment";
    private static final ArrayList<String> SORT_CATEGORIES = new ArrayList<String>(Arrays.asList("Most recent date", "Earliest date", "Location sent to", "Location sent from"));

    private BottomSheetBehavior<View> bottomSheetBehavior;
    private OnBottomSheetCallbacks listener;
    private HomeFragment homeFragment;

    private EditText etDateRange;

    private AutoCompleteTextView actvFilterBy;
    private TextInputLayout iFilterLocation;
    private EditText etFilterLocation;

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
        setupFilterBy();
        setupDateRangePicker();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupViews(View view) {
        actvFilterBy = view.findViewById(R.id.actv_filter_by);
        iFilterLocation = view.findViewById(R.id.i_filter_location);
        etFilterLocation = view.findViewById(R.id.et_filter_location);
        etDateRange = view.findViewById(R.id.et_date_range);
    }

    // ####################################
    // ##      SORT BY CONDITIONS        ##
    // ####################################

    private void setupFilterBy() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, SORT_CATEGORIES);
        actvFilterBy.setAdapter(adapter);
        // Automatically sort by most recent date sent and make sure the autocomplete does not filter out the other sorting possibilities
        actvFilterBy.setText(SORT_CATEGORIES.get(0), false);
        iFilterLocation.setVisibility(View.GONE);
        homeFragment.setSortBy(0);
        actvFilterBy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                homeFragment.setSortBy(position);
                if (position == 0) {
                    iFilterLocation.setVisibility(View.GONE);
                } else if (position == 1) {
                    iFilterLocation.setVisibility(View.GONE);
                } else if (position == 2) {
                    iFilterLocation.setVisibility(View.VISIBLE);
                } else if (position == 3) {
                    iFilterLocation.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // ####################################
    // ##      FILTER BY LOCATION        ##
    // ####################################

    public void displayTargetLocation(Location locationFrom) {
        try {
            etFilterLocation.setText(locationFrom.getLocationName());
        } catch (com.parse.ParseException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    // ################################
    // ##      FILTER BY DATE        ##
    // ################################

    private void setupDateRangePicker() {
        etDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeFragment.launchDateRangePicker();
            }
        });
    }

    public void displayDateRange(Date startDate, Date endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, y");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String datesFromTo = dateFormat.format(startDate) + " - " + dateFormat.format(endDate);
        etDateRange.setText(datesFromTo);
    }

    // ################################
    // ##  CONFIGURE BOTTOM SHEET    ##
    // ################################

    /**
     * Sets a listener for changes to the backdrop
     * For example, selecting a condition to sort the postcards by is a change that HomeFragment needs to be notified of
     * @param onBottomSheetCallbacks The listener implementing the methods upon change on the backdrop
     */
    public void setOnBottomSheetCallbacks(OnBottomSheetCallbacks onBottomSheetCallbacks) {
        this.listener = onBottomSheetCallbacks;
    }

    /**
     * Sets the bottom sheet to be collapsed
     */
    public void closeBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    /**
     * Sets the bottom sheet to be expanded
     */
    public void openBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /**
     * Adds the provided view as a bottom sheet
     * @param view The view to use as a bottom sheet
     */
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