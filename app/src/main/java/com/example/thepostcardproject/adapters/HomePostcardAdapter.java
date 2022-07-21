package com.example.thepostcardproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.ItemHomePostcardBinding;
import com.example.thepostcardproject.fragments.HomeBackdropFragment;
import com.example.thepostcardproject.fragments.HomeFragment;
import com.example.thepostcardproject.models.FilteredPhoto;
import com.example.thepostcardproject.models.Postcard;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class HomePostcardAdapter extends RecyclerView.Adapter<HomePostcardAdapter.ViewHolder> {
    public final static String TAG = "HomePostcardAdapter";

    private Context context;
    private MutableLiveData<List<Postcard>> receivedPostcards;
    private HomeFragment.GoToDetailViewListener goToDetailViewListener;


    public HomePostcardAdapter(Context context, MutableLiveData<List<Postcard>> receivedPostcards, HomeFragment.GoToDetailViewListener goToDetailViewListener) {
        this.context = context;
        this.receivedPostcards = receivedPostcards;
        this.goToDetailViewListener = goToDetailViewListener;
    }

    // **************************************************
    // **     IMPLEMENT RECYCLERVIEW ADAPTER METHODS   **
    // **************************************************

    /**
     * Inflates the layout for each postcard
     */
    @NonNull
    @Override
    public HomePostcardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        ItemHomePostcardBinding itemBinding = ItemHomePostcardBinding.inflate(inflater, parent, false);
        return new ViewHolder(itemBinding);
    }

    /**
     * Binds the information for each postcard to the layout
     */
    @Override
    public void onBindViewHolder(@NonNull HomePostcardAdapter.ViewHolder holder, int position) {
        Postcard postcard = receivedPostcards.getValue().get(position);
        holder.bind(postcard);
    }

    /**
     * @return The number of postcards currently loaded
     */
    @Override
    public int getItemCount() {
        return receivedPostcards.getValue().size();
    }

    // ***********************************************
    // **     VIEWHOLDER TO DISPLAY EACH POSTCARD   **
    // ***********************************************

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemHomePostcardBinding itemBinding;

        public ViewHolder(ItemHomePostcardBinding itemBinding) {
            super(itemBinding.getRoot());
            itemBinding.getRoot().setOnClickListener(this);
            this.itemBinding = itemBinding;
        }

        /**
         * Binds the postcard with a filtered cover photo
         * @param postcard
         */
        public void bind(Postcard postcard) {
            try {
                FilteredPhoto coverPhotoFiltered = postcard.getCoverPhotoFiltered();
                coverPhotoFiltered.displayFilteredPhoto(context, itemBinding.ivHomePostcard);
            } catch (ParseException e) {
                Log.d(TAG, "An error occurred while getting the cover photo: " + e.getMessage());
                e.printStackTrace();
            }
            try {
                String toFrom = "From: " + postcard.getUserFrom().getUsername() + "  &#x1F4CD " + postcard.getLocationFrom().getLocationName() + "\nTo: " + postcard.getUserTo().getUsername() + "  &#x1F4CD " + postcard.getLocationTo().getLocationName();
                itemBinding.tvUsername.setText(toFrom);
                itemBinding.tvMessage.setText(postcard.getMessage());
            } catch (com.parse.ParseException e) {
                Log.d(TAG, "An exception occurred with retrieving the username: " + e.getMessage());
            }
        }

        /**
         * When a postcard is clicked, go to a detail view
         * @param v
         */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                goToDetailViewListener.goToDetailView(position);
            }
        }
    }
}
