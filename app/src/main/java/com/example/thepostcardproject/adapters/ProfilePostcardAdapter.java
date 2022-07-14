package com.example.thepostcardproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
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
    private Context context;
    private List<Postcard> sentPostcards;
    private ProfileFragment.GoToDetailViewListener goToDetailViewListener;

    public ProfilePostcardAdapter(Context context, List<Postcard> sentPostcards, ProfileFragment.GoToDetailViewListener goToDetailViewListener) {
        this.context = context;
        this.sentPostcards = sentPostcards;
        this.goToDetailViewListener = goToDetailViewListener;
    }

    @NonNull
    @Override
    public ProfilePostcardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        ItemProfilePostcardBinding itemBinding = ItemProfilePostcardBinding.inflate(inflater, parent, false);
        return new ProfilePostcardAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfilePostcardAdapter.ViewHolder holder, int position) {
        Postcard postcard = sentPostcards.get(position);
        holder.bind(postcard);
    }

    @Override
    public int getItemCount() {
        return sentPostcards.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemProfilePostcardBinding binding;

        public ViewHolder(ItemProfilePostcardBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }

        public void bind(Postcard postcard) {
            FilteredPhoto coverPhotoFiltered = null;
            try {
                coverPhotoFiltered = postcard.getCoverPhotoFiltered();
                coverPhotoFiltered.displayFilteredPhoto(context, binding.ivProfilePostcard);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Postcard postcard = sentPostcards.get(position);
                goDetailView(postcard);
            }
        }
    }

    private void goDetailView(Postcard postcard) {
        goToDetailViewListener.goToDetailView(postcard);
    }

    public void addAll(ArrayList<Postcard> postcards) {
        int positionStart = sentPostcards.size();
        this.sentPostcards.addAll(postcards);
        notifyItemRangeInserted(positionStart, postcards.size());
//        notifyDataSetChanged();
    }

    public void clear() {
        sentPostcards.clear();
        notifyDataSetChanged();
    }
}
