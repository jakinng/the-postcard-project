package com.example.thepostcardproject.fragments;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

import static com.example.thepostcardproject.utilities.Keys.KEY_USERNAME;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.content.FileProvider;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.thepostcardproject.R;
import com.example.thepostcardproject.models.Location;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.models.User;
import com.example.thepostcardproject.utilities.BitmapScaler;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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


    private EditText etMessage;
//    private EditText etSendTo;
    private ImageButton ibSendPostcard;
    private ImageFilterView ivCoverPhoto;
    private ImageView ivOpenCamera;
    private ImageView ivOpenGallery;
    private AutoCompleteTextView actvUsernameTo;


    ActivityResultLauncher<String> requestPermissionLauncher;
    String photoFilePath;
    File photoFile;
    private ArrayAdapter<String> usernameAdapter;

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addRequestPermissionLauncher();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false);
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
        setupCameraButton();
        setupGalleryButton();
        setupSendButton();
        setupUsernameAutocomplete();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // TODO : figure out whether i need this file stuff LOL i forget why i put it there
//                Bitmap capturedImage = BitmapFactory.decodeFile(photoFilePath);
//                // See BitmapScaler.java: https://gist.github.com/nesquena/3885707fd3773c09f1bb
//                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(capturedImage, 400);

                Glide.with(getContext())
                        .load(photoFile)
                        .centerCrop()
                        .into(ivCoverPhoto);

//                // Save the smaller image to disk
//                // Configure byte output stream
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                // Compress the image further
//                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
//                // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
//                File resizedFile = null;
//                try {
//                    // TODO : make it so you can user createImageFileFromName
//                    resizedFile = createImageFile();
//                    resizedFile.createNewFile();
//                    FileOutputStream fos = new FileOutputStream(resizedFile);
//                    // Write the bytes of the bitmap to file
//                    fos.write(bytes.toByteArray());
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            } else {
                Snackbar.make(ivCoverPhoto, "Picture wasn't taken!", Snackbar.LENGTH_SHORT).show();
            }
        }
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
//            Bitmap photoBitmap = loadFromUri(photoUri);

//            ImageProcessor processor = new ImageProcessor();
//            Bitmap tintBitmap = processor.applySnowEffect(photoBitmap);

            // Load the selected image into a preview
            ivCoverPhoto.setWarmth(2);
            Glide.with(getContext())
                    .load(photoUri)
                    .centerCrop()
                    .into(ivCoverPhoto);
//            Snackbar.make(ivCoverPhoto, "S:FJ", Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Attaches the views to the corresponding variables
     * @param view The encapsulating view
     */
    private void setupViews(View view) {
        etMessage = view.findViewById(R.id.et_message);
//        etSendTo = view.findViewById(R.id.et_sendto);
        ibSendPostcard = view.findViewById(R.id.ib_send_postcard);
        ivCoverPhoto = view.findViewById(R.id.iv_cover_photo);
        ivOpenCamera = view.findViewById(R.id.iv_open_camera);
        ivOpenGallery = view.findViewById(R.id.iv_open_gallery);
        actvUsernameTo = view.findViewById(R.id.actv_username);
    }

    // ***************************************************
    // **      HELPER METHODS FOR SENDING A POSTCARD    **
    // ***************************************************

    private void setupSendButton() {
        ibSendPostcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString();
//                String userTo = etSendTo.getText().toString();
                String userTo = actvUsernameTo.getText().toString();
                userTo = userTo.substring(5);
                if (message == null) {
                    Snackbar.make(ibSendPostcard, "Your postcard message is empty!", Snackbar.LENGTH_SHORT).show();
                } else if (userTo == null) {
                    Snackbar.make(ibSendPostcard, "Please specify a recipient!", Snackbar.LENGTH_SHORT).show();
                } else if (photoFile != null) {
                    sendToUser(userTo, message, new ParseFile(photoFile));
                    // TODO : make the gallery intent and camera intent work in the same way (saving a file)
                } else if (ivCoverPhoto.getDrawable() != null) {
                    sendToUser(userTo, message, parseFileFromDrawable(ivCoverPhoto.getDrawable()));
                } else {
                    Snackbar.make(ibSendPostcard, "Please attach a cover photo!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    // TODO : check logic in here
    private void sendToUser(String username, String message, ParseFile coverPhoto) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(KEY_USERNAME, username); // find adults
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    if (users.size() == 1) {
                        User userFrom = (User) ParseUser.getCurrentUser();
                        User userTo = (User) users.get(0);
                        Postcard postcard = new Postcard(coverPhoto, userFrom, userTo, userFrom.getCurrentLocation(), userTo.getCurrentLocation(), message);
                        postcard.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Snackbar.make(ibSendPostcard, "Postcard has been sent!", Snackbar.LENGTH_SHORT).show();
                                    // Clear the visual fields
                                    etMessage.setText(null);
//                                    etSendTo.setText(null);
                                    actvUsernameTo.setText(null);
                                    ivCoverPhoto.setImageResource(0);
                                } else {
                                    Log.d(TAG, e.getMessage());
                                    Snackbar.make(ibSendPostcard, "An error occurred!", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Snackbar.make(ibSendPostcard, "There are no users with this username!", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    // Something went wrong.
                    Log.d(TAG, "Error retrieving list of users: " + e.getMessage());
                    Snackbar.make(ibSendPostcard, "An error occurred while finding users to send this postcard to!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    // ***************************************************
    // **     AUTOCOMPLETE FOR SENDING BY USERNAME      **
    // ***************************************************

    private void setupUsernameAutocomplete() {
        // TODO : make this to stuff look better
        String to = "@to: ";
        actvUsernameTo.setText(to);
        actvUsernameTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() < to.length()){
                    actvUsernameTo.setText(to); //set editext with "To" again like has been initialized
                    actvUsernameTo.setSelection(actvUsernameTo.getText().length()); // to make cursor in end of text
                }
            }
        });

        ArrayList<String> usernames = new ArrayList<>();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    for (ParseUser parseUser : users) {
                        User user = (User) parseUser;
                        usernames.add(to + user.getUsername());
                    }
                    usernameAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, usernames);
                    actvUsernameTo.setAdapter(usernameAdapter);
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
                            Snackbar.make(ivCoverPhoto, "Camera access granted!", Snackbar.LENGTH_SHORT).show();
                            launchCamera();
                        } else {
                            // PERMISSION NOT GRANTED
                            Snackbar.make(ivCoverPhoto, "Camera access denied! Enable the in-app camera in Settings.", Snackbar.LENGTH_SHORT)
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
        ivOpenCamera.setOnClickListener(new View.OnClickListener() {
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
     * Launch the in-app camera
     */
    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create a File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Uri photoURI = FileProvider.getUriForFile(getContext(), "com.postcard.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            } catch (IOException exception) {
                Snackbar.make(ivCoverPhoto, "Sorry, an error occurred while taking the photo.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Testing whether the intent chooser works or not
     * TODO : figure out whether I can launch camera & gallery with one button or not (for later)
     */
    private void launchCameraAndGallery() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create a File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Uri photoURI = FileProvider.getUriForFile(getContext(), "com.postcard.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            } catch (IOException exception) {
                Snackbar.make(ivCoverPhoto, "Sorry, an error occurred while taking the photo.", Snackbar.LENGTH_SHORT).show();
            }
        } // TODO : add an error handler
        // Create intent for picking a photo from the gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
//        startActivityForResult(intent, PICK_PHOTO_CODE);
        Intent chooser = Intent.createChooser(galleryIntent, "Some text here");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePictureIntent });
        startActivityForResult(chooser, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
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
        return createImageFileFromName(imageFileName);
    }

    private File createImageFileFromName(String imageFileName) throws IOException {
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        photoFile = image;
        photoFilePath = image.getAbsolutePath();
        return image;
    }

    // ***************************************************
    // **  HELPER METHODS FOR DEALING WITH THE GALLERY  **
    // ***************************************************

    /**
     * When the "add picture from gallery" button is clicked, the gallery intent is launched
     */
    public void setupGalleryButton() {
        ivOpenGallery.setOnClickListener(new View.OnClickListener() {
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

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
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
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * Given a drawable, returns a ParseFile associated with the same image
     * @param drawable The drawable to convert to a ParseFile
     * @return The ParseFile containing the associated image
     */
    private ParseFile parseFileFromDrawable(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        bitmap.compress(format, quality, stream);
        byte[] bitmapBytes = stream.toByteArray();

        ParseFile image = new ParseFile(bitmapBytes);
        return image;
    }

}