package com.example.thepostcardproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thepostcardproject.R;
import com.example.thepostcardproject.models.Postcard;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class ProfilePostcardAdapter extends RecyclerView.Adapter<ProfilePostcardAdapter.ViewHolder> {
    private Context context;
    private List<Postcard> sentPostcards;

    public ProfilePostcardAdapter(Context context, List<Postcard> sentPostcards) {
        this.context = context;
        this.sentPostcards = sentPostcards;
    }

    @NonNull
    @Override
    public ProfilePostcardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postcardView = inflater.inflate(R.layout.item_profile_postcard, parent, false);
        ViewHolder viewHolder = new ViewHolder(postcardView);
        return viewHolder;
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfilePostcard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePostcard = (ImageView) itemView.findViewById(R.id.iv_profile_postcard);
        }

        public void bind(Postcard postcard) {
            ParseFile coverPhoto = postcard.getCoverPhoto();
            Glide.with(context)
                    .load(coverPhoto.getUrl())
                    .into(ivProfilePostcard);
        }
    }

    public void addAll(ArrayList<Postcard> sentPostcards) {
        this.sentPostcards.addAll(sentPostcards);
        notifyDataSetChanged();
    }
}
