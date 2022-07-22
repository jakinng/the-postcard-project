package com.example.thepostcardproject.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.FragmentPostcardDetailBinding;
import com.example.thepostcardproject.databinding.FragmentProfileBinding;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.viewmodels.DetailViewModel;
import com.example.thepostcardproject.viewmodels.HomeViewModel;
import com.example.thepostcardproject.viewmodels.ProfileViewModel;
import com.parse.ParseException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostcardDetailFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostcardDetailFragment extends Fragment {

    public static final String TAG = "PostcardDetailFragment";
    private FragmentPostcardDetailBinding binding;

    private DetailViewModel viewModel;

    public PostcardDetailFragment() {
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
        binding = FragmentPostcardDetailBinding.inflate(inflater, container, false);
        setupSwipeGesture(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewModel();
        displaySelectedPostcard();
        setupBackButton();
        setupActionBar();
    }

    /**
     * Initialize the ViewModel for the detail fragment
     */
    private void setViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(DetailViewModel.class);
    }

    /**
     * Configures the look of the actionbar
     */
    private void setupActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar(); // or getActionBar();
        actionBar.hide();
        actionBar.setTitle("Detail"); // set the top title
    }

    /**
     * Configures button to go back to the original view
     */
    private void setupBackButton() {
        binding.buttonHome.setText(viewModel.backText);
        binding.buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(PostcardDetailFragment.this);
                navController.navigate(viewModel.navigateBack);
            }
        });
    }

    // ********************************************
    // **      DISPLAY SELECTED POSTCARD         **
    // ********************************************

    private void displaySelectedPostcard() {
        displayPostcard(viewModel.getPostcards().getValue().get(viewModel.postcardPosition.getValue()));
    }

    public void displayPostcard(Postcard postcard) {
        try {
            postcard.getCoverPhotoFiltered().displayFilteredPhoto(getContext(), binding.ivHomePostcard);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            String toFrom = "from: " + postcard.getUserFrom().getUsername() + "\nto: " + postcard.getUserTo().getUsername();
            binding.tvUsername.setText(toFrom);
        } catch (com.parse.ParseException e) {
            Log.d(TAG, "An exception occurred with retrieving the username: " + e.getMessage());
        }
        binding.tvMessage.setText(postcard.getMessage());
        binding.tvMessage.setMovementMethod(new ScrollingMovementMethod());
        binding.tvLocationFrom.setText("\uD83D\uDCCD " + postcard.getLocationFrom().getLocationName().toUpperCase());
    }

    // ********************************************
    // **            ADD SWIPE GESTURE           **
    // ********************************************

    /**
     * Adds a swipe gesture to the provided view
     * On right swipe, navigate from the current create page to the home page
     * On left swipe, navigate from the current create page to the profile page
     * @param view The view to attach the swipe gesture to
     */
    private void setupSwipeGesture(View view) {
        view.setLongClickable(true);
        view.setOnTouchListener(new View.OnTouchListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            GestureDetectorCompat gestureDetector = new GestureDetectorCompat(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    boolean result = false;
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }
                    return result;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    /**
     * Action to take upon swiping left
     */
    private void onSwipeLeft() {
        Log.d(TAG, "Swipe left");
        if (viewModel.selectNextPostcard()) {
            NavHostFragment.findNavController(PostcardDetailFragment.this).navigate(viewModel.navigateLeft);
        }
    }

    /**
     * Action to take upon swiping right
     */
    private void onSwipeRight() {
        Log.d(TAG, "Swipe right");
        if (viewModel.selectPreviousPostcard()) {
            NavHostFragment.findNavController(PostcardDetailFragment.this).navigate(viewModel.navigateRight);
        }
    }
}