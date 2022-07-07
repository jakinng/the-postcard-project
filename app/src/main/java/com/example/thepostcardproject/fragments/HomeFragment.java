package com.example.thepostcardproject.fragments;

import static android.app.Activity.RESULT_OK;
import static com.example.thepostcardproject.utilities.Keys.KEY_USER_TO;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.adapters.HomePostcardAdapter;
import com.example.thepostcardproject.models.Location;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.utilities.EndlessRecyclerViewScrollListener;
import com.example.thepostcardproject.utilities.LocationComparator;
import com.example.thepostcardproject.utilities.OnBottomSheetCallbacks;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BottomSheetDialogFragment implements OnBottomSheetCallbacks {
    public static final String TAG = "HomeFragment";
    public static final int LOAD_AT_ONCE = 5;
    public static final int SELECT_LOCATION_REQUEST_CODE = 101;

    private ArrayList<Postcard> receivedPostcards;
    private HomePostcardAdapter adapter;

    private RecyclerView rvPostcards;

    private GoToDetailViewListener goToDetailViewListener;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeContainer;

    private TextView tvPostcardHeader;
    private ImageView ivFilter;
    private ImageView ivFilterLocation;

    // Counters for the infinite scroll
    private int skip = 0;
    private boolean loadMore = true;

    // Save the dates and location to filter by
    private Date startDate;
    private Date endDate;
    private Location targetLocation;

    // Current state for behavior as a bottom fragment
    private HomeBackdropFragment homeBackdropFragment;
    private int currentState = BottomSheetBehavior.STATE_EXPANDED;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(GoToDetailViewListener goToDetailViewListener) {
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.goToDetailViewListener = goToDetailViewListener;
        return homeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set this as listener for the backdrop fragment
        homeBackdropFragment = (HomeBackdropFragment) getParentFragment();
        homeBackdropFragment.setOnBottomSheetCallbacks(this);
        // Enable filter icon in menu
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * Called shortly after onCreateView
     * @param view The view containing the various visual components
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        displayPostcards();
        setupSwipeToRefresh();
//        setupDateRangePicker();
//        setupLocationPicker();

//        Log.d(TAG, String.valueOf(view.getId() == R.id.home_fragment_container));
        homeBackdropFragment.configureBackdrop(view);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Set the action bar to have the appropriate title and icons
        actionBarOpen();

        // Populate menu items
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // TODO: make sure this lifecycle is okay lmfao
    @Override
    public void onResume() {
        super.onResume();
        skip = 0;
        loadMorePostcards();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                ParseGeoPoint coordinates = new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
                targetLocation = new Location(place.getName(), place.getAddress(), coordinates);
                skip = 0;
                loadMorePostcards();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // If the filter icon is clicked, toggle the backdrop expanded or collapsed
        if (item.getItemId() == R.id.menu_icon_filter) {
            if (currentState == BottomSheetBehavior.STATE_EXPANDED) {
                item.setIcon(R.drawable.icon_close);
                actionBarClosed();
                homeBackdropFragment.closeBottomSheet();
            } else if (currentState == BottomSheetBehavior.STATE_COLLAPSED) {
                item.setIcon(R.drawable.icon_filter_list);
                actionBarOpen();
                homeBackdropFragment.openBottomSheet();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Attaches the views to the corresponding variables
     * @param view The encapsulating view
     */
    private void setupViews(View view) {
//        ivFilter = view.findViewById(R.id.iv_filter);
//        ivFilterLocation = view.findViewById(R.id.iv_filter_location);
        tvPostcardHeader = view.findViewById(R.id.tv_received_postcard_message);
        rvPostcards = view.findViewById(R.id.rv_postcards);
        swipeContainer = view.findViewById(R.id.swipe_container);
    }

    // ######################################################
    // ##            FILTER BY DATE AND LOCATION           ##
    // ######################################################

    private void actionBarOpen() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("Home"); // set the top title
        actionBar.setElevation(0);
    }

    private void actionBarClosed() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("Filter Postcards Displayed"); // set the top title
        actionBar.setElevation(0);
    }

    public void launchDateRangePicker() {
        MaterialDatePicker<Pair<Long, Long>> dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Filter by date range")
                .setSelection(
                        new Pair(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds())
                )
                .build();
        dateRangePicker.show(getParentFragmentManager(), TAG);
        dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                startDate = new Date(selection.first);
                endDate = new Date(selection.second);
                Log.d(TAG, "start: " + startDate + " and end: " + endDate);
                skip = 0;
                loadMorePostcards();
                homeBackdropFragment.displayDateRange(startDate, endDate);
            }
        });
    }
    private void setupDateRangePicker() {
        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Pair<Long, Long>> dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Filter by date range")
                        .setSelection(
                                new Pair(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds())
                        )
                        .build();
                dateRangePicker.show(getChildFragmentManager(), TAG);
                dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        startDate = new Date(selection.first);
                        endDate = new Date(selection.second);
                        Log.d(TAG, "start: " + startDate + " and end: " + endDate);
                        skip = 0;
                        loadMorePostcards();
                    }
                });
            }
        });
    }

    /**
     * Handles the bottom sheet collapsing or expanding
     * @param bottomSheet The view consisting of the bottom sheet, which is the entire HomeFragment
     * @param newState The new state, either STATE_EXPANDED or STATE_COLLAPSED
     */
    @Override
    public void onStateChanged(View bottomSheet, int newState) {
        currentState = newState;
        if (newState == BottomSheetBehavior.STATE_EXPANDED) {

        } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

        }
    }

    // ##########################################
    // ##        DISPLAY POSTCARDS             ##
    // ##########################################

    /**
     * Queries the Parse database to load more postcards
     * For the infinite scroll
     */
    private void loadMorePostcards() {
        // IF THERE IS NO TARGET LOCATION
        if (targetLocation == null) {
            ParseQuery<Postcard> query = ParseQuery.getQuery(Postcard.class);
            query.whereEqualTo(KEY_USER_TO, ParseUser.getCurrentUser());
            query.setLimit(LOAD_AT_ONCE);
            // TODO : add this key as a constant
            query.addDescendingOrder("createdAt");
            // TODO : add a backdrop for the filters
            if (startDate != null && endDate != null) {
                query.whereGreaterThan("createdAt", startDate);
                query.whereLessThan("createdAt", endDate);
            }

            query.setSkip(skip);
            if (skip == 0) {
                scrollListener.resetState();
            }
            query.findInBackground(new FindCallback<Postcard>() {
                @Override
                public void done(List<Postcard> postcards, ParseException e) {
                    if (e == null) {
                        updatePostcards(postcards);
                    } else {
                        Log.d(TAG, "There has been an issue retrieving the postcards from the backend: " + e.getMessage());
                    }
                }
            });
        } else {
            ParseQuery<Postcard> query = ParseQuery.getQuery(Postcard.class);
            query.whereEqualTo(KEY_USER_TO, ParseUser.getCurrentUser());
            query.addDescendingOrder("createdAt");
            query.findInBackground(new FindCallback<Postcard>() {
                @Override
                public void done(List<Postcard> postcards, ParseException e) {
                    Log.d(TAG, String.valueOf(postcards.size()));

                    LocationComparator comparator = new LocationComparator(targetLocation);
                    Collections.sort(postcards, comparator);
                    for (int i = 0; i < postcards.size(); i++) {
                        try {
                            Log.d(TAG, postcards.get(i).getLocationFrom().getLocationName());
                            Log.d(TAG, "COMPARE!!" + Location.getDistanceBetweenLocations(targetLocation, postcards.get(i).getLocationFrom()));

                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                    adapter.clear();
                    adapter.addAll((ArrayList<Postcard>) postcards);
                    try {
                        tvPostcardHeader.setText("Postcard Collection" + targetLocation.getLocationName());
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Updates the adapter with the new postcards and updates variables for infinite scrolling
     * @param postcards The postcards to add
     */
    private void updatePostcards(List<Postcard> postcards) {
        Log.d(TAG, "Number of postcards: " + postcards.size());
        swipeContainer.setRefreshing(false);
        // If this is the first query made, clear the adapter for the swipe to refresh
        if (skip == 0) {
            String datesFromTo = "";
            String targetLocationString = "";
            if (startDate != null && endDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("E, MMMM d, y");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                datesFromTo = "\n" + dateFormat.format(startDate) + " - " + dateFormat.format(endDate);
            }
            if (targetLocation != null) {
                try {
                    targetLocationString = "\n" + targetLocation.getLocationName();
                } catch (ParseException parseException) {
                    Log.d(TAG, parseException.getMessage());
                }
            }
            tvPostcardHeader.setText("Postcard Collection" + datesFromTo + targetLocationString);
            adapter.clear();
            if (postcards.size() == 0) {
                // TODO : format this better
                tvPostcardHeader.setText("Postcard Collection Empty" + datesFromTo + targetLocationString);
            }
        }

        skip += postcards.size();
        if (postcards.size() != 0) {
            adapter.addAll((ArrayList<Postcard>) postcards);
            loadMore = true;
        } else {
            // No more postcards to load
            loadMore = false;
        }
    }

    /**
     * Displays the postcards sent by the user
     */
    private void displayPostcards() {
        // Set the adapter
        adapter = new HomePostcardAdapter(getContext(), new ArrayList<>(), goToDetailViewListener);
        rvPostcards.setAdapter(adapter);

        // Optimizes since the items are static and will not change while scrolling
        rvPostcards.setHasFixedSize(true);

        // Add infinite scroll
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvPostcards.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (loadMore) {
                    loadMorePostcards();
                }
            }
        };
        rvPostcards.addOnScrollListener(scrollListener);

        // Add snap to center
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvPostcards);

        loadMorePostcards();
    }

    public interface GoToDetailViewListener {
        void goToDetailView(Postcard postcard);
    }

    // ##########################################
    // ##        SWIPE TO REFRESH              ##
    // ##########################################

    private void setupSwipeToRefresh() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                skip = 0;
                loadMorePostcards();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
}