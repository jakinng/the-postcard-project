package com.example.thepostcardproject.fragments;

import static com.example.thepostcardproject.utilities.Keys.KEY_USERNAME;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.FragmentHomeBackdropBinding;
import com.example.thepostcardproject.models.Location;
import com.example.thepostcardproject.models.User;
import com.example.thepostcardproject.viewmodels.DetailViewModel;
import com.example.thepostcardproject.viewmodels.HomeViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeBackdropFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeBackdropFragment extends Fragment {
    private static final String TAG = "HomeBackdropFragment";
    private static final ArrayList<String> SORT_CATEGORIES = new ArrayList<String>(Arrays.asList("Most recent date (default)", "Earliest date", "Location sent to", "Location sent from"));

    private BottomSheetBehavior bottomSheetBehavior;
    private FragmentHomeBackdropBinding binding;
    private HomeViewModel viewModel;
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
        binding = FragmentHomeBackdropBinding.inflate(inflater, container, false);
//        setupSwipeGesture(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        inflateHomeFragment();
        setViewModel();
        setupFilterBy();
        setupLocationPicker();
        setupDateRangePicker();
        setupUsernameAutocomplete();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    // ####################################
    // ##      SORT BY CONDITIONS        ##
    // ####################################

    private void setupFilterBy() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, SORT_CATEGORIES);
        binding.actvFilterBy.setAdapter(adapter);
        // Automatically sort by most recent date sent and make sure the autocomplete does not filter out the other sorting possibilities
        binding.actvFilterBy.setText(SORT_CATEGORIES.get(0), false);
        binding.iFilterLocation.setVisibility(View.GONE);
        viewModel.setSortBy(0);
        binding.actvFilterBy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.valueOf(position));
                viewModel.setSortBy(position);
                homeFragment.reloadPostcards();
                if (position == 0) {
                    binding.iFilterLocation.setVisibility(View.GONE);
                } else if (position == 1) {
                    binding.iFilterLocation.setVisibility(View.GONE);
                } else if (position == 2) {
                    binding.iFilterLocation.setVisibility(View.VISIBLE);
                    binding.iFilterLocation.setHint("Enter location sent to");
                } else if (position == 3) {
                    binding.iFilterLocation.setVisibility(View.VISIBLE);
                    binding.iFilterLocation.setHint("Enter location sent from");
                }
            }
        });
    }

    // ####################################
    // ##      FILTER BY LOCATION        ##
    // ####################################

    private void setupLocationPicker() {
        binding.etFilterLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeFragment.launchLocationFromPicker();
            }
        });
        displayTargetLocation();
    }

    public void displayTargetLocation() {
        viewModel.targetLocation.observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location locationFrom) {
                if (locationFrom == null) {
                    binding.etFilterLocation.setText(null);
                } else {
                    binding.etFilterLocation.setText(locationFrom.getLocationName());
                }
            }
        });
    }

    // ################################
    // ##      FILTER BY DATE        ##
    // ################################

    private void setupDateRangePicker() {
        binding.etDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeFragment.launchDateRangePicker();
            }
        });
        binding.iDateRange.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.clearDateRange();
            }
        });
        displayDateRange();
    }

    public void displayDateRange() {
        viewModel.dateRange.observe(getViewLifecycleOwner(), new Observer<Pair<Date, Date>>() {
            @Override
            public void onChanged(Pair<Date, Date> newDateRange) {
                if (newDateRange == null) {
                    binding.etDateRange.setText(null);
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, y");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String datesFromTo = dateFormat.format(newDateRange.first) + " - " + dateFormat.format(newDateRange.second);
                    binding.etDateRange.setText(datesFromTo);
                }
            }
        });
    }

    // ####################################
    // ##      FILTER BY USERNAME        ##
    // ####################################

    /**
     * Autocompletes the possible users to filter by
     */
    private void setupUsernameAutocomplete() {
        ArrayList<String> usernames = new ArrayList<>();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null && getActivity() != null) {
                    for (ParseUser parseUser : users) {
                        User user = (User) parseUser;
                        usernames.add(user.getUsername());
                    }
                    ArrayAdapter<String> usernameAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, usernames);
                    binding.actvFilterUsername.setAdapter(usernameAdapter);
                }
            }
        });
        binding.actvFilterUsername.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String username = binding.actvFilterUsername.getText().toString();
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo(KEY_USERNAME, username);
                query.findInBackground(new FindCallback<ParseUser>() {
                    public void done(List<ParseUser> users, ParseException e) {
                        if (e == null) {
                            if (users.size() == 1) {
                                User targetUser = (User) users.get(0);
                                viewModel.getTargetUser().setValue(targetUser);
                            }
                        } else {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                });
            }
        });

        /**
         * Allow users to clear username by clicking the x
         */
        binding.iFilterUsername.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getTargetUser().setValue(null);
            }
        });

        displayUsername();
    }

    /**
     * Display the current target user
     */
    public void displayUsername() {
        viewModel.targetUser.observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user == null) {
                    binding.actvFilterUsername.setText(null);
                } else {
                    binding.actvFilterUsername.setText(user.getUsername());
                }
            }
        });
    }

    // ################################
    // ##  CONFIGURE BOTTOM SHEET    ##
    // ################################

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
            public void onStateChanged(@NonNull View bottomSheet, int newState) { }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) { }
        });
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    // #################################
    // ##  CONFIGURE HOME FRAGMENT    ##
    // #################################

    /**
     * Adds the home fragment with the postcards received
     * Adds a listener to go to a detail view
     */
    private void inflateHomeFragment() {
        HomeFragment.GoToDetailViewListener goToDetailViewListener = new HomeFragment.GoToDetailViewListener() {
            @Override
            public void goToDetailView(int postcardPosition) {
                DetailViewModel detailViewModel = new ViewModelProvider(requireActivity()).get(DetailViewModel.class);
                detailViewModel.postcardPosition.setValue(postcardPosition);
                detailViewModel.getPostcards().setValue(viewModel.getReceivedPostcards().getValue());
                detailViewModel.navigateLeft = R.id.action_postcard_detail_fragment_from_home_self_left;
                detailViewModel.navigateRight = R.id.action_postcard_detail_fragment_from_home_self_right;
                detailViewModel.navigateBack = R.id.action_postcard_detail_fragment_to_home_backdrop_fragment;
                detailViewModel.backText = "HOME";
                NavController navController = NavHostFragment.findNavController(HomeBackdropFragment.this);
                navController.navigate(R.id.action_home_backdrop_fragment_to_postcard_detail_fragment);
            }
        };
        homeFragment = HomeFragment.newInstance(goToDetailViewListener);
        getChildFragmentManager().beginTransaction().replace(R.id.rl_home_fragment, homeFragment).commit();
    }
}