package com.example.thepostcardproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.ActivityLoginBinding;
import com.example.thepostcardproject.databinding.ActivityPhotoFilterBinding;
import com.example.thepostcardproject.models.FilteredPhoto;
import com.google.android.material.slider.Slider;

import org.parceler.Parcels;

public class PhotoFilterActivity extends AppCompatActivity {

    ActivityPhotoFilterBinding binding;
    FilteredPhoto filteredPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoFilterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        displayImage();
        setupSliders();
        setupSaveButton();
    }

    private void displayImage() {
        filteredPhoto = (FilteredPhoto) Parcels.unwrap(getIntent().getParcelableExtra(FilteredPhoto.class.getSimpleName()));
        filteredPhoto.displayFilteredPhoto(this, binding.ivCoverPhoto);
    }

    private void setupSliders() {
        binding.sliderBrightness.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                filteredPhoto.setBrightness(value);
                filteredPhoto.displayFilteredPhoto(PhotoFilterActivity.this, binding.ivCoverPhoto);
            }
        });

        binding.sliderContrast.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                filteredPhoto.setContrast(value);
                filteredPhoto.displayFilteredPhoto(PhotoFilterActivity.this, binding.ivCoverPhoto);
            }
        });

        binding.sliderSaturation.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                filteredPhoto.setSaturation(value);
                filteredPhoto.displayFilteredPhoto(PhotoFilterActivity.this, binding.ivCoverPhoto);
            }
        });

        binding.sliderWarmth.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                filteredPhoto.setWarmth(value);
                filteredPhoto.displayFilteredPhoto(PhotoFilterActivity.this, binding.ivCoverPhoto);
            }
        });
    }

    private void setupSaveButton() {
        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(FilteredPhoto.class.getSimpleName(), Parcels.wrap(filteredPhoto));
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }
}