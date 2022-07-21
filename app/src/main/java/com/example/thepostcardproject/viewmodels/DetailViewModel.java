package com.example.thepostcardproject.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.models.Postcard;

import java.util.ArrayList;
import java.util.List;

public class DetailViewModel extends ViewModel {
    public static final String TAG = "DetailViewModel";
    public int navigateLeft;
    public int navigateRight;
    public int navigateBack;
    public String backText;

    private MutableLiveData<List<Postcard>> postcards;
    public MutableLiveData<Integer> postcardPosition = new MutableLiveData<>(null);

    public MutableLiveData<List<Postcard>> getPostcards() {
        if (postcards == null) {
            postcards = new MutableLiveData<List<Postcard>>(new ArrayList<Postcard>());
        }
        return postcards;
    }

    /**
     * Increments the position of the selected postcard
     * @return Returns true if there is a next postcard to flip to
     */
    public boolean selectNextPostcard() {
        if (postcardPosition.getValue() < postcards.getValue().size() - 1) {
            postcardPosition.setValue(postcardPosition.getValue() + 1);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Decrements the position of the selected postcard
     * @return Returns true if there is a previous postcard to flip to
     */
    public boolean selectPreviousPostcard() {
        if (postcardPosition.getValue() > 0) {
            postcardPosition.setValue(postcardPosition.getValue() - 1);
            return true;
        } else {
            return false;
        }
    }
}
