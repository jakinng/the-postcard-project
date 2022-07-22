package com.example.thepostcardproject.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

import static com.example.thepostcardproject.utilities.Keys.KEY_USERNAME;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.FragmentCreateBinding;
import com.example.thepostcardproject.models.Filter;
import com.example.thepostcardproject.models.FilteredPhoto;
import com.example.thepostcardproject.models.Location;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.models.User;
import com.example.thepostcardproject.viewmodels.CreateViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateFragment extends Fragment {
    private final static String TAG = "CreateFragment";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_PHOTO_CODE = 1046;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1001;

    private FragmentCreateBinding binding;
    private CreateViewModel viewModel;

    ActivityResultLauncher<String> requestPermissionLauncher;
    PlacesClient placesClient;

    private ArrayAdapter<String> usernameAdapter;

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addRequestPermissionLauncher();
        initializePlaceClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateBinding.inflate(inflater, container, false);
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
        configureActionBar();
        setViewModel();
        displayCoverPhoto();
        setupCameraButton();
        setupGalleryButton();
        setupSendButton();
        setupFilterButton();
        setupUsernameAutocomplete();
        displayPlacePhoto();
        displayLocation();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            loadImageFromCamera(resultCode);
        }
        // ACTIVITY COMES FROM GALLERY
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            loadImageFromGallery(resultCode, data);
        }
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            setNewLocation(resultCode, data);
        }
    }

    // ******************************
    // **       INITIAL SETUP      **
    // ******************************

    /**
     * Set the action bar to have the appropriate title and icons
     */
    private void configureActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.hide();
        actionBar.setTitle("Create"); // set the top title
    }

    /**
     * Add a client to run Google Places
     */
    private void initializePlaceClient() {
        Places.initialize(getActivity().getApplicationContext(), "AIzaSyAVrhwVJs0zsb_X8HcFuWBkqhp4LTIsJ2g");
        placesClient = Places.createClient(getContext());
    }

    /**
     * Initialize the ViewModel
     */
    private void setViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CreateViewModel.class);
    }

    private void displayCoverPhoto() {
        viewModel.filteredPhoto.observe(getViewLifecycleOwner(), new Observer<FilteredPhoto>() {
            @Override
            public void onChanged(FilteredPhoto filteredPhoto) {
                if (filteredPhoto != null) {
                    try {
                        filteredPhoto.getFilter().addFilterToImageView(binding.ivCoverPhoto);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        viewModel.drawablePhoto.observe(getViewLifecycleOwner(), new Observer<Drawable>() {
            @Override
            public void onChanged(Drawable drawable) {
                if (drawable == null) {
                    binding.ivCoverPhoto.setImageResource(0);
                } else {
                    Glide.with(getContext())
                            .load(drawable)
                            .transform(new CenterCrop())
                            .into(binding.ivCoverPhoto);
                }
            }
        });
    }


    // ***************************************************
    // **      HELPER METHODS FOR SENDING A POSTCARD    **
    // ***************************************************

    /**
     * Send a postcard when the send button is clicked
     */
    private void setupSendButton() {
        binding.buttonSendPostcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.etMessage.getText().toString();
                String userTo = binding.actvUsername.getText().toString();
                if (message == null) {
                    Snackbar.make(binding.buttonSendPostcard, "Your postcard message is empty!", Snackbar.LENGTH_SHORT).show();
                } else if (userTo == null) {
                    Snackbar.make(binding.buttonSendPostcard, "Please specify a recipient!", Snackbar.LENGTH_SHORT).show();
                } else if (viewModel.filteredPhoto != null) {
                    viewModel.filteredPhoto.getValue().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            sendToUser(userTo, message, viewModel.filteredPhoto.getValue());
                        }
                    });
                } else {
                    Snackbar.make(binding.buttonSendPostcard, "Please attach a cover photo!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Send a postcard with the specified recipient, message, and photo, and save in Parse
     */
    private void sendToUser(String username, String message, FilteredPhoto filteredPhoto) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(KEY_USERNAME, username);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    if (users.size() == 1) {
                        User userFrom = (User) ParseUser.getCurrentUser();
                        User userTo = (User) users.get(0);
                        Postcard postcard = new Postcard(userFrom, userTo, userFrom.getCurrentLocation(), userTo.getCurrentLocation(), message, filteredPhoto);
                        postcard.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Snackbar.make(binding.buttonSendPostcard, "Postcard has been sent!", Snackbar.LENGTH_SHORT).show();
                                    // Clear the visual fields
                                    binding.etMessage.setText(null);
                                    binding.actvUsername.setText(null);
                                    viewModel.filteredPhoto.setValue(null);
                                    viewModel.drawablePhoto.setValue(null);
                                } else {
                                    Log.d(TAG, e.getMessage());
                                    Snackbar.make(binding.buttonSendPostcard, "An error occurred!", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Snackbar.make(binding.buttonSendPostcard, "There are no users with this username!", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "Error retrieving list of users: " + e.getMessage());
                }
            }
        });
    }

    // ***************************************************
    // **     AUTOCOMPLETE FOR SENDING BY USERNAME      **
    // ***************************************************

    /**
     * Displays a list of usernames to send the postcard to
     */
    private void setupUsernameAutocomplete() {
        viewModel.setupUsernameAutocomplete(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    viewModel.usernames.clear();
                    viewModel.addUsersToList((ArrayList<ParseUser>) users);
                    usernameAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, viewModel.usernames);
                    binding.actvUsername.setAdapter(usernameAdapter);
                } else {
                    Log.d(TAG, "Issues retrieving usernames from backend.");
                }
            }
        });
    }

    // ***************************************************
    // **  HELPER METHODS FOR DEALING WITH THE CAMERA   **
    // ***************************************************
    /**
     * Defines the request permission launcher
     * Upon success, calls launchCamera
     * Upon failure, displays a Toast
     */
    private void addRequestPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            // PERMISSION GRANTED
                            Snackbar.make(binding.ivCoverPhoto, "Camera access granted!", Snackbar.LENGTH_SHORT).show();
                            launchCamera();
                        } else {
                            // PERMISSION NOT GRANTED
                            Snackbar.make(binding.ivCoverPhoto, "Camera access denied! Enable the in-app camera in Settings.", Snackbar.LENGTH_SHORT)
                                .setAction("Settings", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                                    }
                                })
                                .show();
                        }
                    }
                });
    }

    /**
     * When the "add photo" button is clicked, checks for permission and launches the camera
     */
    private void setupCameraButton() {
        binding.ivOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        != PermissionChecker.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                } else {
                    launchCamera();
                }
            }
        });
    }

    /**
     * Launch the camera in-app
     */
    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create a File where the photo should go
            try {
                viewModel.photoFile = createImageFile();
                Uri photoURI = FileProvider.getUriForFile(getContext(), "com.postcard.fileprovider", viewModel.photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            } catch (IOException exception) {
                Snackbar.make(binding.ivCoverPhoto, "Sorry, an error occurred while taking the photo.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Creates an image file with a unique name
     * @return Returns a new image stored in a unique file path
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "postcard_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path
        viewModel.photoFile = image;
        return image;
    }

    /**
     * Once the picture has been taken, load the image into a preview
     * @param resultCode A code indicating whether the result was successful
     */
    private void loadImageFromCamera(int resultCode) {
        if (resultCode == RESULT_OK) {
            // Load the selected image into a preview
            Filter defaultFilter = Filter.defaultFilter();

            viewModel.filteredPhoto.setValue(new FilteredPhoto(new ParseFile(viewModel.photoFile), defaultFilter));
            defaultFilter.addFilterToImageView(binding.ivCoverPhoto);
            Glide.with(getContext())
                    .load(viewModel.photoFile)
                    .centerCrop()
                    .into(binding.ivCoverPhoto);
        } else {
            Snackbar.make(binding.ivCoverPhoto, "Picture wasn't taken successfully. Try again!", Snackbar.LENGTH_SHORT).show();
        }
    }

    // ***************************************************
    // **  HELPER METHODS FOR DEALING WITH THE GALLERY  **
    // ***************************************************

    /**
     * When the "add picture from gallery" button is clicked, the gallery intent is launched
     */
    public void setupGalleryButton() {
        binding.ivOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGallery();
            }
        });
    }

    /**
     * Triggers gallery selection for a photo
     */
    public void launchGallery() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_PHOTO_CODE);
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
     * Given a bitmap, returns a ParseFile associated with the same image
     * @param bitmap The bitmap to convert to a ParseFile
     * @return The ParseFile containing the associated image
     */
    private ParseFile parseFileFromBitmap(Bitmap bitmap) {
        ParseFile image = new ParseFile(photoDataFromBitmap(bitmap));
        return image;
    }

    private byte[] photoDataFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        bitmap.compress(format, quality, stream);
        byte[] bitmapBytes = stream.toByteArray();
        return bitmapBytes;
    }

    /**
     * Once the picture has been taken, load the image into a preview
     * @param resultCode A code indicating whether the result was successful
     * @param data The data in the image
     */
    private void loadImageFromGallery(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Load the selected image into a preview
            Uri photoUri = data.getData();
            Filter defaultFilter = Filter.defaultFilter();
            viewModel.filteredPhoto.setValue(new FilteredPhoto(new ParseFile(photoDataFromBitmap(loadFromUri(photoUri))), defaultFilter));
            defaultFilter.addFilterToImageView(binding.ivCoverPhoto);
            Glide.with(getContext())
                    .load(photoUri)
                    .centerCrop()
                    .into(binding.ivCoverPhoto);
        } else {
            Snackbar.make(binding.ivCoverPhoto, "Picture wasn't selected successfully. Try again!", Snackbar.LENGTH_SHORT).show();
        }
    }

    // ***********************************************************
    // **  HELPER METHODS FOR ADDING PHOTO ENHANCEMENT FILTERS  **
    // ***********************************************************

    private void launchPhotoFilter() {
        if (viewModel.filteredPhoto.getValue() == null) {
            Snackbar.make(binding.ivCoverPhoto, "Select or take a picture to edit!", Snackbar.LENGTH_SHORT).show();
        } else {
            viewModel.drawablePhoto.setValue(binding.ivCoverPhoto.getDrawable());
            NavHostFragment.findNavController(CreateFragment.this).navigate(R.id.action_create_fragment_to_photo_filter_fragment);
        }
    }

    private void setupFilterButton() {
        binding.ivFilterPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPhotoFilter();
            }
        });
    }

    // ***************************************************
    // **       RETRIEVE PHOTOS FROM PLACE PHOTOS       **
    // ***************************************************

    /**
     * Displays a place photo of the location of the current user upon button press
     */
    public void displayPlacePhoto() {
        binding.buttonPlacePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPlacePhoto(viewModel.currentLocation.getValue().getId());
            }
        });
    }

    /**
     * Displays a place photo of the location with the provided placeId
     * @param placeId The Google Places ID of the place to display the photo of
     */
    public void displayPlacePhoto(String placeId) {
        // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        final List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);

        // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, fields);

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            final Place place = response.getPlace();

            // Get the photo metadata.
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Snackbar.make(binding.buttonPlacePhoto, "No photos found!", Snackbar.LENGTH_SHORT).show();
                Log.w(TAG, "No photo metadata.");
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Get the attribution text.
            final String attributions = photoMetadata.getAttributions();
            if (attributions != null) {
                Snackbar.make(binding.buttonPlacePhoto, "Attributions: " + attributions, Snackbar.LENGTH_SHORT).show();
            }

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                viewModel.filteredPhoto.setValue(new FilteredPhoto(parseFileFromBitmap(bitmap)));
                Glide.with(getContext())
                        .load(bitmap)
                        .centerCrop()
                        .into(binding.ivCoverPhoto);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Snackbar.make(binding.buttonPlacePhoto, "No photos found!", Snackbar.LENGTH_SHORT).show();
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                }
            });
        });
    }

    // ***************************************************
    // **        DISPLAY LOCATION OF THE POSTCARD       **
    // ***************************************************

    /**
     * Display the location of the postcard
     */
    private void displayLocation() {
        viewModel.currentLocation.observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                binding.tvLocationFrom.setText(location.getLocationName().toUpperCase());
            }
        });
        binding.buttonAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG, Place.Field.ID);
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    /**
     * Sets the current location to be the result of the autocomplete picker
     * @param resultCode The result of the intent
     * @param data Data containing the location picked
     */
    private void setNewLocation(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            viewModel.saveNewLocation(place);
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i(TAG, "Error in picking location. Try again! Error message: " + status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
        return;
    }
}