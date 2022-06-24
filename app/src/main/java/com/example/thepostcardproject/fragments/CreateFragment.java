package com.example.thepostcardproject.fragments;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.thepostcardproject.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateFragment extends Fragment {
    private final static String TAG = "CreateFragment";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    EditText etMessage;
    EditText etSendTo;
    ImageButton ibSendPostcard;
    ImageView ivCoverPhoto;
    ImageView ivAddButton;

    ActivityResultLauncher<String> requestPermissionLauncher;
    String photoFilePath;

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
        setupTakePhoto();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap capturedImage = BitmapFactory.decodeFile(photoFilePath);
                ivCoverPhoto.setImageBitmap(capturedImage);
            } else {
                Snackbar.make(ivAddButton, "Picture wasn't taken!", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Attaches the views to the corresponding variables
     * @param view The encapsulating view
     */
    private void setupViews(View view) {
        etMessage = view.findViewById(R.id.et_message);
        etSendTo = view.findViewById(R.id.et_sendto);
        ibSendPostcard = view.findViewById(R.id.ib_send_postcard);
        ivCoverPhoto = view.findViewById(R.id.iv_cover_photo);
        ivAddButton = view.findViewById(R.id.iv_add_button);
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
                            Snackbar.make(ivAddButton, "Camera access granted!", Snackbar.LENGTH_SHORT).show();
                            launchCamera();
                        } else {
                            // PERMISSION NOT GRANTED
                            Snackbar.make(ivAddButton, "Camera access denied! Enable the in-app camera in Settings.", Snackbar.LENGTH_SHORT)
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
                Snackbar.make(ivAddButton, "Sorry, an error occurred while taking the photo.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * When the "add photo" button is clicked, checks for permission and launches the camera
     */
    private void setupTakePhoto() {
        ivCoverPhoto.setOnClickListener(new View.OnClickListener() {
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
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        photoFilePath = image.getAbsolutePath();
        return image;
    }
}