package com.example.thepostcardproject.models;

import static com.example.thepostcardproject.utilities.Keys.KEY_BRIGHTNESS;
import static com.example.thepostcardproject.utilities.Keys.KEY_CONTRAST;
import static com.example.thepostcardproject.utilities.Keys.KEY_SATURATION;
import static com.example.thepostcardproject.utilities.Keys.KEY_WARMTH;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.ImageFilterView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.parceler.Parcel;

import java.util.Objects;

@Parcel(analyze = Filter.class)
@ParseClassName("Filter")
public class Filter extends ParseObject {
    public static final String TAG = "Filter";
    private static final float DEFAULT_VALUE = 1;

    private String description;

    /**
     * Empty constructor for Parceler
     */
    public Filter() {}

    public Filter(float brightness, float contrast, float saturation, float warmth) {
        super();
        setBrightness(brightness);
        setContrast(contrast);
        setSaturation(saturation);
        setWarmth(warmth);
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getBrightness() {
        float brightness = 1;
        try {
            brightness = fetchIfNeeded().getNumber(KEY_BRIGHTNESS).floatValue();
        } catch (ParseException e) {
            Log.d(TAG, "Issue retrieving brightness.");
        }
        return brightness;
    }

    public void setBrightness(float brightness) {
        put(KEY_BRIGHTNESS, brightness);
    }

    public float getContrast() {
        float contrast = 1;
        try {
            contrast = fetchIfNeeded().getNumber(KEY_CONTRAST).floatValue();
        } catch (ParseException e) {
            Log.d(TAG, "Issue retrieving contrast.");
        }
        return contrast;
    }

    public void setContrast(float contrast) {
        put(KEY_CONTRAST, contrast);
    }

    public float getSaturation() {
        float saturation = 1;
        try {
            saturation = fetchIfNeeded().getNumber(KEY_SATURATION).floatValue();
        } catch (ParseException e) {
            Log.d(TAG, "Issue retrieving saturation.");
        }
        return saturation;
    }

    public void setSaturation(float saturation) {
        put(KEY_SATURATION, saturation);
    }

    public float getWarmth() {
        float warmth = 1;
        try {
            warmth = fetchIfNeeded().getNumber(KEY_WARMTH).floatValue();
        } catch (ParseException e) {
            Log.d(TAG, "Issue retrieving warmth.");
        }
        return warmth;
    }

    public void setWarmth(float warmth) {
        put(KEY_WARMTH, warmth);
    }

    public static Filter defaultFilter() {
        Filter defaultFilter = new Filter(DEFAULT_VALUE, DEFAULT_VALUE, DEFAULT_VALUE, DEFAULT_VALUE);
        return defaultFilter;
    }

    public boolean isDefault() {
        if (getBrightness() != DEFAULT_VALUE || getContrast() != DEFAULT_VALUE || getSaturation() != DEFAULT_VALUE || getWarmth() != DEFAULT_VALUE)  {
            return false;
        }
        return true;
    }

    public void addFilterToImageView(ImageFilterView imageFilterView) {
        imageFilterView.setBrightness(getBrightness());
        imageFilterView.setContrast(getContrast());
        imageFilterView.setSaturation(getSaturation());
        imageFilterView.setWarmth(getWarmth());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Filter)) {
            return false;
        } else if (getBrightness() == ((Filter) obj).getBrightness() && getContrast() == ((Filter) obj).getContrast() && getSaturation() == ((Filter) obj).getSaturation() && getWarmth() == ((Filter) obj).getWarmth())  {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBrightness(), getContrast(), getSaturation(), getWarmth());
    }

    @Override
    public String toString() {
        String original = super.toString();
        return original + "\nbrightness: " + getBrightness() + "contrast: " + getContrast()  + "saturation: " + getSaturation()  + "warmth: " + getWarmth();
    }
}
