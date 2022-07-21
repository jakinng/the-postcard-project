package com.example.thepostcardproject.viewmodels;

import android.graphics.drawable.Drawable;
import android.widget.ArrayAdapter;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.thepostcardproject.models.Filter;
import com.example.thepostcardproject.models.FilteredPhoto;
import com.example.thepostcardproject.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateViewModel extends ViewModel {
    public static final String TAG = "CreateViewModel";

    public File photoFile = null;
    public MutableLiveData<Drawable> drawablePhoto =  new MutableLiveData<Drawable>();
    public MutableLiveData<FilteredPhoto> filteredPhoto =  new MutableLiveData<FilteredPhoto>();
    public ArrayList<String> usernames = new ArrayList<>();

    public void setupUsernameAutocomplete(FindCallback<ParseUser> findCallback) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(findCallback);
    }

    public void addUsersToList(ArrayList<ParseUser> users) {
        for (ParseUser parseUser : users) {
            User user = (User) parseUser;
            usernames.add(user.getUsername());
        }
    }

    public MutableLiveData<FilteredPhoto> getFilteredPhoto() {
        if (filteredPhoto == null) {
            return new MutableLiveData<FilteredPhoto>();
        }
        return filteredPhoto;
    }

    public MutableLiveData<Drawable> getDrawablePhoto() {
        if (drawablePhoto == null) {
            return new MutableLiveData<Drawable>();
        }
        return drawablePhoto;
    }
}
