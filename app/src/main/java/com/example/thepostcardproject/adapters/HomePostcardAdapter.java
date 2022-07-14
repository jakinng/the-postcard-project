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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.ItemHomePostcardBinding;
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
    private ArrayList<Postcard> receivedPostcards;
    private HomeFragment.GoToDetailViewListener goToDetailViewListener;

    public HomePostcardAdapter(Context context, ArrayList<Postcard> sentPostcards, HomeFragment.GoToDetailViewListener goToDetailViewListener) {
        this.context = context;
        this.receivedPostcards = sentPostcards;
        this.goToDetailViewListener = goToDetailViewListener;
    }

    @NonNull
    @Override
    public HomePostcardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        ItemHomePostcardBinding itemBinding = ItemHomePostcardBinding.inflate(inflater, parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomePostcardAdapter.ViewHolder holder, int position) {
        Postcard postcard = receivedPostcards.get(position);
        holder.bind(postcard);
    }

    @Override
    public int getItemCount() {
        return receivedPostcards.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemHomePostcardBinding binding;

        public ViewHolder(ItemHomePostcardBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }

        public void bind(Postcard postcard) {
            try {
                FilteredPhoto coverPhotoFiltered = postcard.getCoverPhotoFiltered();
                coverPhotoFiltered.displayFilteredPhoto(context, binding.ivHomePostcard);
            } catch (ParseException e) {
                Log.d(TAG, "An error occurred while getting the cover photo: " + e.getMessage());
                e.printStackTrace();
            }
            try {
                String toFrom = "From: " + postcard.getUserFrom().getUsername() + " | " + postcard.getLocationFrom().getLocationName() + "\nTo: " + postcard.getUserTo().getUsername() + " | " + postcard.getLocationTo().getLocationName();
                binding.tvUsername.setText(toFrom);
                binding.tvMessage.setText(postcard.getMessage());
            } catch (com.parse.ParseException e) {
                Log.d(TAG, "An exception occurred with retrieving the username: " + e.getMessage());
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Postcard postcard = receivedPostcards.get(position);
                goDetailView(postcard);
            }
        }
    }

    private void goDetailView(Postcard postcard) {
        goToDetailViewListener.goToDetailView(postcard);
    }

    public void addAll(ArrayList<Postcard> postcards) {
        int positionStart = receivedPostcards.size();
        this.receivedPostcards.addAll(postcards);
//        notifyItemRangeInserted(positionStart, postcards.size());
        notifyDataSetChanged();
    }

    public void clear() {
        receivedPostcards.clear();
        notifyDataSetChanged();
    }
}
