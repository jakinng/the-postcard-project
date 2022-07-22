package com.example.thepostcardproject.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.adapters.HomePostcardAdapter;
import com.example.thepostcardproject.databinding.FragmentHomeBinding;
import com.example.thepostcardproject.models.Location;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.models.User;
import com.example.thepostcardproject.utilities.EndlessRecyclerViewScrollListener;
import com.example.thepostcardproject.viewmodels.HomeViewModel;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BottomSheetDialogFragment {
    public static final String TAG = "HomeFragment";
    public static final int SELECT_LOCATION_FROM_REQUEST_CODE = 101;

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    private HomePostcardAdapter adapter;
    private GoToDetailViewListener goToDetailViewListener;
    private EndlessRecyclerViewScrollListener scrollListener;

    // Counters for the infinite scroll
    private int skip = 0;
    private boolean loadMore = true;

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
        // Enable filter icon in menu
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called shortly after onCreateView
     * @param view The view containing the various visual components
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setViewModel();
        displayPostcards();
        reloadPostcards();
        setupSwipeToRefresh();

        observeDateRange();
        observeTargetUser();
        homeBackdropFragment = (HomeBackdropFragment) getParentFragment();
        homeBackdropFragment.configureBackdrop(view);
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Display the menu with a filter icon
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Set the action bar to have the appropriate title and icons
        actionBarOpen();

        // Populate menu items
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_LOCATION_FROM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                ParseGeoPoint coordinates = new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
                viewModel.targetLocation.setValue(new Location(place.getName(), place.getAddress(), coordinates, place.getId()));
                reloadPostcards();
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
                currentState = BottomSheetBehavior.STATE_COLLAPSED;
            } else if (currentState == BottomSheetBehavior.STATE_COLLAPSED) {
                item.setIcon(R.drawable.icon_filter_list);
                actionBarOpen();
                homeBackdropFragment.openBottomSheet();
                currentState = BottomSheetBehavior.STATE_EXPANDED;
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    // ###################################################################
    // ##            FILTER BY DATE, LOCATION, AND TARGET USER          ##
    // ###################################################################

    /**
     * Configure action bar when the backdrop is open
     */
    private void actionBarOpen() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar(); // or getActionBar();
        actionBar.show();
        actionBar.setTitle("Home"); // set the top title
        actionBar.setElevation(0);
    }

    /**
     * Configure action bar when the backdrop is closed
     */
    private void actionBarClosed() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar(); // or getActionBar();
        actionBar.show();
        actionBar.setTitle("Filter Postcards Displayed"); // set the top title
        actionBar.setElevation(0);
    }

    /**
     * Launch the date range picker
     */
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
                viewModel.setDateRange(new Date(selection.first), new Date(selection.second));
            }
        });
    }

    /**
     * Launch the location picker
     */
    public void launchLocationFromPicker() {
        // Sets which fields the API request is asking for
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG, Place.Field.ID);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(getContext());
        startActivityForResult(intent, SELECT_LOCATION_FROM_REQUEST_CODE);
    }

    /**
     * Reload the postcards with the new date range if the dates are changed
     */
    private void observeDateRange() {
        viewModel.dateRange.observe(getViewLifecycleOwner(), new Observer<android.util.Pair<Date, Date>>() {
            @Override
            public void onChanged(android.util.Pair<Date, Date> newDateRange) {
                reloadPostcards();
            }
        });
    }

    /**
     * Reload the postcards with the new date range if the dates are changed
     */
    private void observeTargetUser() {
        viewModel.targetUser.observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    Log.d(TAG, "I'm loading postcards from " + user.getUsername());
                }
                reloadPostcards();
            }
        });
    }

    // ##########################################
    // ##        DISPLAY POSTCARDS             ##
    // ##########################################

    /**
     * Displays the postcards sent by the user
     */
    private void displayPostcards() {
        // Set the adapter
        adapter = new HomePostcardAdapter(getContext(), viewModel.getReceivedPostcards(), goToDetailViewListener);
        binding.rvPostcards.setAdapter(adapter);

        viewModel.getReceivedPostcards().observe(getViewLifecycleOwner(), new Observer<List<Postcard>>() {
            @Override
            public void onChanged(List<Postcard> postcards) {
                adapter.notifyDataSetChanged();
            }
        });

        // Add infinite scroll
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rvPostcards.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (loadMore) {
                    Log.d(TAG, "the scroll listener wants to load more");
                    loadMorePostcards(false);
                }
            }
        };
        binding.rvPostcards.addOnScrollListener(scrollListener);

        // Add snap to center
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.rvPostcards);
    }

    /**
     * Queries the Parse database to load more postcards
     * For the infinite scroll
     */
    private void loadMorePostcards(boolean clear) {
        FindCallback<Postcard> findCallback = new FindCallback<Postcard>() {
            @Override
            public void done(List<Postcard> postcards, ParseException e) {
                if (e == null) {
                    updatePostcards(postcards, viewModel.getSortBy(), clear);
                } else {
                    Log.d(TAG, "There has been an issue retrieving the postcards from the backend: " + e.getMessage());
                }
            }
        };
        if (loadMore) {
            loadMore = false;
            viewModel.loadMorePostcards(skip, findCallback);
        }
    }

    /**
     * Updates the adapter with the new postcards and updates variables for infinite scrolling
     * @param postcards The postcards to add
     */
    private void updatePostcards(List<Postcard> postcards, int sortBy, boolean clear) {
        binding.swipeContainer.setRefreshing(false);
        if (clear) { viewModel.clearPostcards(); }

        if (skip == 0 && postcards.size() == 0) {
            binding.tvHeader.setText("No Postcards!");
        }

        // Sort based on the selected feature
        if (sortBy == viewModel.SORT_MOST_RECENT || sortBy == viewModel.SORT_EARLIEST) {
            skip += postcards.size();
            if (postcards.size() != 0) {
                viewModel.addPostcards(postcards);
                loadMore = true;
            } else {
                // No more postcards to load
                loadMore = false;
            }
        } else if (sortBy == viewModel.SORT_LOCATION_FROM || sortBy == viewModel.SORT_LOCATION_TO) {
            viewModel.addPostcardsByLocation(postcards, sortBy);
        }
    }

    /**
     * Reloads postcards, resetting the infinite scroll
     */
    private void reloadPostcards() {
        binding.tvHeader.setText("Postcard Collection");
        loadMore = true;
        skip = 0;
        scrollListener.resetState();
        viewModel.clearPostcards();
        loadMorePostcards(true);
    }

    // ##################################
    // ##        DETAIL VIEW          ##
    // ##################################

    /**
     * Interface for detail view on click
     */
    public interface GoToDetailViewListener {
        void goToDetailView(int postcardPosition);
    }

    // ##########################################
    // ##        SWIPE TO REFRESH              ##
    // ##########################################

    /**
     * Set up swipe to refresh
     */
    private void setupSwipeToRefresh() {
        // Setup refresh listener which triggers new data loading
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                reloadPostcards();
            }
        });
        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
}