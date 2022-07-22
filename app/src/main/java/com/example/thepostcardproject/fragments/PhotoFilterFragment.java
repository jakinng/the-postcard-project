package com.example.thepostcardproject.fragments;

import static com.example.thepostcardproject.utilities.Keys.KEY_USER_FROM;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.thepostcardproject.R;
import com.example.thepostcardproject.adapters.FilterSuggestionAdapter;
import com.example.thepostcardproject.databinding.FragmentPhotoFilterBinding;
import com.example.thepostcardproject.models.Filter;
import com.example.thepostcardproject.models.FilteredPhoto;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.models.User;
import com.example.thepostcardproject.viewmodels.CreateViewModel;
import com.example.thepostcardproject.viewmodels.DetailViewModel;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotoFilterFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoFilterFragment extends Fragment {
    public static final String TAG = "PhotoFilterFragment";

    private FragmentPhotoFilterBinding binding;
    private CreateViewModel createViewModel;
    private FilterSuggestionAdapter adapter;

    public PhotoFilterFragment() {
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
        binding = FragmentPhotoFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewModel();
        displayImage();
        setupSliders();
        setupSaveButton();
        displayFilterSuggestions();
        addOriginal();
        getMostUsed();
    }

    // ******************************
    // **       INITIAL SETUP      **
    // ******************************

    /**
     * Initialize the ViewModel for the detail fragment
     */
    private void setViewModel() {
        createViewModel = new ViewModelProvider(requireActivity()).get(CreateViewModel.class);
    }

    // *****************************************
    // **      DISPLAY SUGGESTED FILTERS      **
    // *****************************************

    private void addOriginal() {
        Filter original = Filter.defaultFilter();
        original.setDescription("Original");
        adapter.add(original);
    }

    /**
     * Get the filters most often used by this user
     */
    private void getMostUsed() {
        User currentUser = (User) ParseUser.getCurrentUser();
        ArrayList<Filter> filtersUsed = new ArrayList<>();

        ParseQuery<Postcard> query = ParseQuery.getQuery(Postcard.class);
        query.whereEqualTo(KEY_USER_FROM, currentUser);
        query.findInBackground(new FindCallback<Postcard>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void done(List<Postcard> postcards, ParseException e) {
                Map<Filter, Integer> frequencyMap = new HashMap<Filter, Integer>();

                // Create a frequency map for the filters used
                for (Postcard postcard : postcards) {
                    try {
                        Filter filterUsed = postcard.getCoverPhotoFiltered().getFilter();
                        if (!filterUsed.isDefault()) {
                            if (!frequencyMap.containsKey(filterUsed)) {
                                frequencyMap.put(filterUsed, 1);
                            } else {
                                frequencyMap.put(filterUsed, frequencyMap.get(filterUsed) + 1);
                            }
                        }
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }

                // Sort the filters by their frequency of usage
                List<Filter> mostUsed = frequencyMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                Collections.reverse(mostUsed);

                // Add the filters used
                for (int i = 0; i < mostUsed.size(); i++) {
                    if (i < 3) {
                        mostUsed.get(i).setDescription("Commonly Used");
                    } else {
                        mostUsed.get(i).setDescription("Preset Filter");
                    }
                }
                adapter.addAll((ArrayList<Filter>) mostUsed);
            }
        });
    }

    /**
     * Displays the filter suggestions with an adapter
     */
    private void displayFilterSuggestions() {
        DisplaySelectedFilterListener displaySelectedFilterListener = new DisplaySelectedFilterListener() {
            @Override
            public void displaySelectedFilter(Filter filter) {
                binding.sliderBrightness.setValue(filter.getBrightness());
                binding.sliderContrast.setValue(filter.getContrast());
                binding.sliderSaturation.setValue(filter.getSaturation());
                binding.sliderWarmth.setValue(filter.getWarmth());
                filter.addFilterToImageView(binding.ivCoverPhoto);
                Glide.with(getContext())
                        .load(createViewModel.drawablePhoto.getValue())
                        .transform(new CenterCrop(), new RoundedCorners(30))
                        .into(binding.ivCoverPhoto);
            }
        };
        adapter = new FilterSuggestionAdapter(getContext(), createViewModel.drawablePhoto.getValue(), new ArrayList<>(), displaySelectedFilterListener);
        binding.rvFilterSuggestions.setAdapter(adapter);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvFilterSuggestions.setLayoutManager(horizontalLayoutManager);
    }

    public interface DisplaySelectedFilterListener {
        public void displaySelectedFilter(Filter filter);
    }

    // *****************************************
    // **        DISPLAY IMAGE PREVIEW        **
    // *****************************************

    private void displayImage() {
        Glide.with(getContext())
                .load(createViewModel.drawablePhoto.getValue())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource
                            dataSource, boolean isFirstResource) {
                        Bitmap imageBitmap = ((BitmapDrawable) resource).getBitmap();
                        int perceivedBrightness = calculateBrightness(imageBitmap);
                        Log.d(TAG, "perceived brightness: " + perceivedBrightness);
                        if (perceivedBrightness <= 90) { // Cutoff value: 55
                            Snackbar.make(binding.ivCoverPhoto, "The image is dark. Try increasing the brightness!", Snackbar.LENGTH_LONG).show();
                        } else if (perceivedBrightness >= 155) { // Cutoff value: 200
                            Snackbar.make(binding.ivCoverPhoto, "The image is bright. Try decreasing the brightness!", Snackbar.LENGTH_LONG).show();
                        }
                        return false;
                    }})
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(binding.ivCoverPhoto);
        try {
            createViewModel.filteredPhoto.getValue().getFilter().addFilterToImageView(binding.ivCoverPhoto);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * When the sliders are changed, the image preview is adjusted
     */
    private void setupSliders() {
        binding.sliderBrightness.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                try {
                    Filter filter = createViewModel.filteredPhoto.getValue().getFilter();
                    filter.setBrightness(value);
                    filter.addFilterToImageView(binding.ivCoverPhoto);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Glide.with(getContext())
                        .load(createViewModel.drawablePhoto.getValue())
                        .transform(new CenterCrop(), new RoundedCorners(30))
                        .into(binding.ivCoverPhoto);
            }
        });
        binding.sliderContrast.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                try {
                    Filter filter = createViewModel.filteredPhoto.getValue().getFilter();
                    filter.setContrast(value);
                    filter.addFilterToImageView(binding.ivCoverPhoto);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Glide.with(getContext())
                        .load(createViewModel.drawablePhoto.getValue())
                        .transform(new CenterCrop(), new RoundedCorners(30))
                        .into(binding.ivCoverPhoto);
            }
        });
        binding.sliderSaturation.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                try {
                    Filter filter = createViewModel.filteredPhoto.getValue().getFilter();
                    filter.setSaturation(value);
                    filter.addFilterToImageView(binding.ivCoverPhoto);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Glide.with(getContext())
                        .load(createViewModel.drawablePhoto.getValue())
                        .transform(new CenterCrop(), new RoundedCorners(30))
                        .into(binding.ivCoverPhoto);
            }
        });
        binding.sliderWarmth.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                try {
                    Filter filter = createViewModel.filteredPhoto.getValue().getFilter();
                    filter.setWarmth(value);
                    filter.addFilterToImageView(binding.ivCoverPhoto);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Glide.with(getContext())
                        .load(createViewModel.drawablePhoto.getValue())
                        .transform(new CenterCrop(), new RoundedCorners(30))
                        .into(binding.ivCoverPhoto);
            }
        });
    }

    private void setupSaveButton() {
        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    Filter chosenFilter = createViewModel.filteredPhoto.getFilter();
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                createViewModel.filteredPhoto.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
                        NavHostFragment.findNavController(PhotoFilterFragment.this).navigate(R.id.action_photo_filter_fragment_to_create_fragment);
//                    }
//                });
            }
        });
    }

    // **********************************************
    // **      CALCULATE PERCEIVED BRIGHTNESS      **
    // **********************************************

    /**
     * Calculate the perceived brightness of a color from 0 to 255
     * @param color The color of the pixel to analyze
     * @return The perceived brightness of the color
     */
    private static int pixelPerceivedBrightness(int color) {
        int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};

        // min value: 0, max value: 255
        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .299 + rgb[1]
                * rgb[1] * .587 + rgb[2] * rgb[2] * .114);

        return brightness;
    }

    /**
     * Calculates the estimated brightness of an Android Bitmap.
     * pixelSpacing tells how many pixels to skip each pixel. Higher values result in better performance, but a more rough estimate.
     * When pixelSpacing = 1, the method actually calculates the real average brightness, not an estimate.
     * This is what the calculateBrightness() shorthand is for.
     * Do not use values for pixelSpacing that are smaller than 1.
    */
    public static int calculateBrightnessEstimate(android.graphics.Bitmap bitmap, int pixelSpacing) {
        int totalBrightness = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int n = 0;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i += pixelSpacing) {
            int color = pixels[i];
            totalBrightness += pixelPerceivedBrightness(color);
            n++;
        }
        return totalBrightness / n;
    }

    /**
     * Calculate the average perceived brightness in the bitmap
     * @param bitmap The bitmap to analyze
     * @return The average perceived brightness of the image, between 0 to 255, inclusive
     */
    public static int calculateBrightness(android.graphics.Bitmap bitmap) {
        return calculateBrightnessEstimate(bitmap, 1);
    }
}