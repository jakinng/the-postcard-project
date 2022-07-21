package com.example.thepostcardproject.models;

import static com.example.thepostcardproject.utilities.Keys.KEY_BRIGHTNESS;
import static com.example.thepostcardproject.utilities.Keys.KEY_CONTRAST;
import static com.example.thepostcardproject.utilities.Keys.KEY_FILTER;
import static com.example.thepostcardproject.utilities.Keys.KEY_PHOTO_FILE;
import static com.example.thepostcardproject.utilities.Keys.KEY_SATURATION;
import static com.example.thepostcardproject.utilities.Keys.KEY_USER_FROM;
import static com.example.thepostcardproject.utilities.Keys.KEY_WARMTH;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.ImageFilterView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.thepostcardproject.adapters.FilterSuggestionAdapter;
import com.parse.GetDataCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.parceler.Parcel;
import com.example.thepostcardproject.utilities.Keys;
import com.parse.ParseQuery;

import java.io.File;

// Specify for Parceler that only FilteredPhoto and not the superclass ParseObject needs to be analyzed
@Parcel(analyze = FilteredPhoto.class)
@ParseClassName("FilteredPhoto")
public class FilteredPhoto extends ParseObject {
    public static final String TAG = "FilteredPhoto";

    /**
     * Empty constructor for Parceler
     */
    public FilteredPhoto() {}

    public FilteredPhoto(ParseFile photoFile) {
        super();
        Filter filter = Filter.defaultFilter();
        setFilter(filter);
        setPhotoFile(photoFile);
        filter.saveInBackground();
    }

    public FilteredPhoto(ParseFile photoFile, Filter filter) {
        super();
        setFilter(filter);
        setPhotoFile(photoFile);
        filter.saveInBackground();
    }

    public Filter getFilter() throws ParseException {
        return (Filter) fetchIfNeeded().getParseObject(KEY_FILTER);
    }

    public void setFilter(Filter filter) {
        put(KEY_FILTER, filter);
    }

    public ParseFile getPhotoFile() throws ParseException {
        return fetchIfNeeded().getParseFile(KEY_PHOTO_FILE);
    }

    public void setPhotoFile(ParseFile photoFile) {
        put(KEY_PHOTO_FILE, photoFile);
    }

    public void displayFilteredPhoto(Context context, ImageFilterView imageFilterView) {
        try {
            Filter filter = getFilter();
            imageFilterView.setBrightness(filter.getBrightness());
            imageFilterView.setContrast(filter.getContrast());
            imageFilterView.setSaturation(filter.getSaturation());
            imageFilterView.setWarmth(filter.getWarmth());
            Glide.with(context)
                    .load(getPhotoFile().getUrl())
                    .centerCrop()
                    .into(imageFilterView);
        } catch (ParseException e) {
            Log.d(TAG, "I can't get the filter from Parse!");
            e.printStackTrace();
        }
    }

    public void displayFilteredPhoto(Context context, ImageFilterView imageFilterView, RequestListener<Drawable> drawableRequestListener) {
        try {
            Filter filter = getFilter();
            imageFilterView.setBrightness(filter.getBrightness());
            imageFilterView.setContrast(filter.getContrast());
            imageFilterView.setSaturation(filter.getSaturation());
            imageFilterView.setWarmth(filter.getWarmth());
            Glide.with(context)
                    .load(getPhotoFile().getUrl())
                    .listener(drawableRequestListener)
                    .centerCrop()
                    .into(imageFilterView);
        } catch (ParseException e) {
            Log.d(TAG, "Issues retrieving filter from Parse.");
            e.printStackTrace();
        }
    }

    public void displayFilteredPhotoRounded(Context context, ImageFilterView imageFilterView) {
        try {
            displayFilteredPhotoRounded(context, imageFilterView, getFilter());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void displayFilteredPhotoRounded(Context context, ImageFilterView imageFilterView, Filter filter) {
        try {
            imageFilterView.setBrightness(filter.getBrightness());
            imageFilterView.setContrast(filter.getContrast());
            imageFilterView.setSaturation(filter.getSaturation());
            imageFilterView.setWarmth(filter.getWarmth());
            ParseFile photoFile = getPhotoFile();
            Glide.with(context)
                    .load(photoFile.getUrl())
                    .transform(new CenterCrop(), new RoundedCorners(15))
                    .into(imageFilterView);
        } catch (ParseException e) {
            Log.d(TAG, "I can't get the filter from Parse!");
            e.printStackTrace();
        }
    }
}
