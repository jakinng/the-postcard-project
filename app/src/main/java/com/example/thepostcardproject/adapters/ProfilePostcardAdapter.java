package com.example.thepostcardproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.ItemHomePostcardBinding;
import com.example.thepostcardproject.databinding.ItemProfilePostcardBinding;
import com.example.thepostcardproject.fragments.HomeFragment;
import com.example.thepostcardproject.fragments.ProfileFragment;
import com.example.thepostcardproject.models.FilteredPhoto;
import com.example.thepostcardproject.models.Postcard;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class ProfilePostcardAdapter extends RecyclerView.Adapter<ProfilePostcardAdapter.ViewHolder> {
    private static final String TAG = "ProfilePostcardAdapter";

    private Context context;
    private MutableLiveData<List<Postcard>> sentPostcards;
    private ProfileFragment.GoToDetailViewListener goToDetailViewListener;

    public ProfilePostcardAdapter(Context context, MutableLiveData<List<Postcard>> sentPostcards, ProfileFragment.GoToDetailViewListener goToDetailViewListener) {
        this.context = context;
        this.sentPostcards = sentPostcards;
        this.goToDetailViewListener = goToDetailViewListener;
    }

    /**
     * Inflates the layout for each postcard
     */
    @NonNull
    @Override
    public ProfilePostcardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        ItemProfilePostcardBinding itemBinding = ItemProfilePostcardBinding.inflate(inflater, parent, false);
        return new ProfilePostcardAdapter.ViewHolder(itemBinding);
    }

    /**
     * Binds the information for each postcard to the layout
     */
    @Override
    public void onBindViewHolder(@NonNull ProfilePostcardAdapter.ViewHolder holder, int position) {
        Postcard postcard = sentPostcards.getValue().get(position);
        holder.bind(postcard);
    }

    /**
     * Get the number of items displayed in the adapter
     * @return The number of postcards currently displayed
     */
    @Override
    public int getItemCount() {
        return sentPostcards.getValue().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemProfilePostcardBinding itemBinding;

        /**
         * Initializes a viewholder with a binding
         * @param itemBinding
         */
        public ViewHolder(ItemProfilePostcardBinding itemBinding) {
            super(itemBinding.getRoot());
            itemBinding.getRoot().setOnClickListener(this);
            this.itemBinding = itemBinding;
        }

        /**
         * Binds the postcard with a filtered cover photo
         * @param postcard
         */
        public void bind(Postcard postcard) {
            FilteredPhoto coverPhotoFiltered = null;
            try {
                coverPhotoFiltered = postcard.getCoverPhotoFiltered();
                coverPhotoFiltered.displayFilteredPhoto(context, itemBinding.ivProfilePostcard);
            } catch (ParseException e) {
                e.printStackTrace();
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
