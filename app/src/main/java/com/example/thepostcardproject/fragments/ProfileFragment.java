package com.example.thepostcardproject.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import static com.example.thepostcardproject.utilities.Keys.KEY_PROFILE_PHOTO;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.thepostcardproject.R;
import com.example.thepostcardproject.adapters.ProfilePostcardAdapter;
import com.example.thepostcardproject.databinding.FragmentProfileBinding;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.utilities.EndlessRecyclerViewScrollListener;
import com.example.thepostcardproject.viewmodels.DetailViewModel;
import com.example.thepostcardproject.viewmodels.ProfileViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final int NUM_PROFILE_COLUMNS = 3;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1000;
    private static final int PICK_PHOTO_CODE = 1567;

    ProfileViewModel viewModel;
    private FragmentProfileBinding binding;

    public ProfilePostcardAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    // Counters for the infinite scroll
    private int skip = 0;
    private boolean loadMore = true;

    public ProfileFragment() {
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
        binding = FragmentProfileBinding.inflate(inflater, container, false);
//        setupSwipeGesture(binding.getRoot());
        return binding.getRoot();
    }

    /**
     * Called shortly after onCreateView
     * @param view The view containing the various visual components
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addViewModel();
        setCurrentUser();
        setupProfilePicture();
        displayUsername();
        displayUserLocation();
        displayPostcards();
        reloadPostcards();
        setupSwipeToRefresh();
        configureActionBar();
    }

    private void addViewModel() {
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }


    /**
     * Called after startActivityForResult is completed
     * @param requestCode The request code of the intent launched
     * @param resultCode The result code (RESULT_OK means there were no errors)
     * @param data The data received
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                saveNewLocation(place);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, "Error in picking location. Try again! Error message: " + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            Bitmap selectedImage = loadFromUri(photoUri);
            selectedImage = selectedImage.copy(Bitmap.Config.ARGB_8888, true);
            ParseFile profilePhoto = parseFileFromBitmap(selectedImage);
            profilePhoto.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    viewModel.profilePhoto.setValue(profilePhoto);
                }
            });
            viewModel.currentUser.put(KEY_PROFILE_PHOTO, profilePhoto);
            viewModel.currentUser.saveInBackground();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // ********************************************************
    // **    HELPER METHODS DEALING WITH THE CURRENT USER    **
    // ********************************************************
    /**
     * Sets the current Parse User
     */
    private void setCurrentUser() {
        viewModel.setCurrentUser();
    }

    /**
     * Displays the current ParseUser's name and username
     */
    private void displayUsername() {
        binding.tvName.setText(viewModel.getName());
        binding.tvUsername.setText(viewModel.getUsername());
    }

    // ********************************************************
    // **            HELPER METHODS FOR LOCATION             **
    // ********************************************************

    /**
     * Displays the current user location and lets the user edit their location
     */
    private void displayUserLocation() {
        viewModel.getLocationName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String locationName) {
                binding.tvLocation.setText(locationName);
            }
        });

        // Set an onclick listener on the location icon
        binding.ivLocationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sets which fields the API request is asking for
                List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG, Place.Field.ID);
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    /**
     * Saves the provided Place as a Location and updates the currentLocation of the current user
     * @param place The Place to save as the new location
     */
    private void saveNewLocation(Place place) {
        viewModel.saveNewLocation(place);
    }

    // ********************************************************
    // **         HELPER METHODS FOR POSTCARD FEED           **
    // ********************************************************

    /**
     * Displays the postcards sent by the user
     */
    private void displayPostcards() {
        // Set the adapter with a grid layout manager
        adapter = new ProfilePostcardAdapter(getContext(), viewModel.getSentPostcards(), new GoToDetailViewListener() {
            @Override
            public void goToDetailView(int postcardPosition) {
                DetailViewModel detailViewModel = new ViewModelProvider(requireActivity()).get(DetailViewModel.class);
                detailViewModel.postcardPosition.setValue(postcardPosition);
                detailViewModel.getPostcards().setValue(viewModel.getSentPostcards().getValue());
                detailViewModel.navigateLeft = R.id.action_postcard_detail_fragment_from_profile_self_left;
                detailViewModel.navigateRight = R.id.action_postcard_detail_fragment_from_profile_self_right;
                detailViewModel.navigateBack = R.id.action_postcard_detail_fragment_to_profile_fragment;
                detailViewModel.backText = "PROFILE";
                NavController navController = NavHostFragment.findNavController(ProfileFragment.this);
                navController.navigate(R.id.action_profile_fragment_to_postcard_detail_fragment);
            }
        });
        binding.rvPostcards.setAdapter(adapter);

        viewModel.getSentPostcards().observe(getViewLifecycleOwner(), new Observer<List<Postcard>>() {
            @Override
            public void onChanged(List<Postcard> postcards) {
                adapter.notifyDataSetChanged();
            }
        });

        // Add infinite scroll
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), NUM_PROFILE_COLUMNS);
        binding.rvPostcards.setLayoutManager(gridLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (loadMore) {
                    loadMorePostcards();
                }
            }
        };
        binding.rvPostcards.addOnScrollListener(scrollListener);
    }

    /**
     * Loads more postcards sent by the current user into the variable sentPostcards
     * Used for infinite scroll and for loading the initial postcards
     */
    private void loadMorePostcards() {
        FindCallback<Postcard> findCallback = new FindCallback<Postcard>() {
            @Override
            public void done(List<Postcard> postcards, ParseException e) {
                if (e == null) {
                    binding.swipeContainer.setRefreshing(false);
                    if (skip == 0 && postcards.size() == 0) {
                        Snackbar.make(binding.rvPostcards, "You have not sent any postcards yet!", Snackbar.LENGTH_SHORT).show();
                    }
                    skip += postcards.size();
                    if (postcards.size() != 0) {
                        viewModel.addPostcards(postcards);
                    } else {
                        // No more postcards to load
                        loadMore = false;
                    }
                } else {
                    Log.d(TAG, "Error retrieving postcards: " + e.getMessage());
                }
            }
        };
        viewModel.loadMorePostcards(skip, findCallback);
    }

    /**
     * Load all the postcards starting from the beginning
     */
    private void reloadPostcards() {
        loadMore = true;
        skip = 0;
        scrollListener.resetState();
        viewModel.clearPostcards();
        loadMorePostcards();
    }

    private void setupSwipeToRefresh() {
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadPostcards();
            }
        });
        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(R.color.postcard_dark_blue, R.color.postcard_dark_orange);
    }

    // ********************************************************
    // **        HELPER METHODS FOR CLICK TO DETAIL VIEW     **
    // ********************************************************

    public interface GoToDetailViewListener {
        void goToDetailView(int postcardPosition);
    }

    /**
     * Set the action bar to have the appropriate title and icons
     */
    private void configureActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("Profile"); // set the top title
    }

    // ****************************************
    // **        DISPLAY PROFILE PICTURE     **
    // ****************************************

    private void setupProfilePicture() {
        viewModel.profilePhoto.observe(getViewLifecycleOwner(), new Observer<ParseFile>() {
            @Override
            public void onChanged(ParseFile profilePhoto) {
                Log.d(TAG, "new profile photo uploaded!" + profilePhoto);
                if (profilePhoto == null) {
                    Glide.with(getContext())
                            .load(R.drawable.icon_account_circle)
                            .circleCrop()
                            .into(binding.ivProfile);
                } else {
                    Glide.with(getContext())
                            .load(profilePhoto.getUrl())
                            .placeholder(R.drawable.icon_account_circle)
                            .circleCrop()
                            .into(binding.ivProfile);
                }
            }
        });
        viewModel.profilePhoto.setValue(null);
        binding.ivProfile.setClickable(true);
        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent for picking a photo from the gallery
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_PHOTO_CODE);
            }
        });
    }

    /**
     * Given a photo URI, returns a Bitmap
     * @param photoUri The URI to get the Bitmap from
     * @return A Bitmap loaded from the URI
     */
    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            if (Build.VERSION.SDK_INT > 27){
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * Returns a ParseFile, given a Bitmap
     * @param bitmap A bitmap to convert to a ParseFile
     * @return The equivalent parse file
     */
    private ParseFile parseFileFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        bitmap.compress(format, quality, stream);
        byte[] bitmapBytes = stream.toByteArray();
        return new ParseFile(bitmapBytes);
    }
}