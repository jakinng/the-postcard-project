package com.example.thepostcardproject.models;

import static com.example.thepostcardproject.utilities.Keys.KEY_BRIGHTNESS;
import static com.example.thepostcardproject.utilities.Keys.KEY_CONTRAST;
import static com.example.thepostcardproject.utilities.Keys.KEY_PHOTO_FILE;
import static com.example.thepostcardproject.utilities.Keys.KEY_SATURATION;
import static com.example.thepostcardproject.utilities.Keys.KEY_WARMTH;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.constraintlayout.utils.widget.ImageFilterView;

import com.bumptech.glide.Glide;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.parceler.Parcel;
import com.example.thepostcardproject.utilities.Keys;

import java.io.File;

// Specify for Parceler that only FilteredPhoto and not the superclass ParseObject needs to be analyzed
@Parcel(analyze = FilteredPhoto.class)
@ParseClassName("FilteredPhoto")
public class FilteredPhoto extends ParseObject {
    public static final String TAG = "FilteredPhoto";

    // Properties are between 0 and 2, with a default value of 1
    public static final float DEFAULT_VALUE = 1;

    /**
     * Empty constructor for Parceler
     */
    public FilteredPhoto() {}

    public FilteredPhoto(ParseFile photoFile) {
        setBrightness(DEFAULT_VALUE);
        setContrast(DEFAULT_VALUE);
        setSaturation(DEFAULT_VALUE);
        setWarmth(DEFAULT_VALUE);
        setPhotoFile(photoFile);
    }

    public FilteredPhoto(float brightness, float contrast, float saturation, float warmth, ParseFile photoFile) {
        setBrightness(brightness);
        setContrast(contrast);
        setSaturation(saturation);
        setWarmth(warmth);
        setPhotoFile(photoFile);
    }

    public float getBrightness() throws ParseException {
        return fetchIfNeeded().getNumber(KEY_BRIGHTNESS).floatValue();
    }

    public void setBrightness(float brightness) {
        put(KEY_BRIGHTNESS, brightness);
    }

    public float getContrast() throws ParseException {
        return fetchIfNeeded().getNumber(KEY_CONTRAST).floatValue();
    }

    public void setContrast(float contrast) {
        put(KEY_CONTRAST, contrast);
    }

    public float getSaturation() throws ParseException {
        return fetchIfNeeded().getNumber(KEY_SATURATION).floatValue();
    }

    public void setSaturation(float saturation) {
        put(KEY_SATURATION, saturation);
    }

    public float getWarmth() throws ParseException {
        return fetchIfNeeded().getNumber(KEY_WARMTH).floatValue();
    }

    public void setWarmth(float warmth) {
        put(KEY_WARMTH, warmth);
    }

    public ParseFile getPhotoFile() throws ParseException {
        return fetchIfNeeded().getParseFile(KEY_PHOTO_FILE);
    }

    public void setPhotoFile(ParseFile photoFile) {
        put(KEY_PHOTO_FILE, photoFile);
    }

    public void displayFilteredPhoto(Context context, ImageFilterView imageFilterView) {
        try {
            imageFilterView.setBrightness(getBrightness());
            imageFilterView.setContrast(getContrast());
            imageFilterView.setSaturation(getSaturation());
            imageFilterView.setWarmth(getWarmth());
            Glide.with(context)
                    .load(getPhotoFile().getUrl())
                    .centerCrop()
                    .into(imageFilterView);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
