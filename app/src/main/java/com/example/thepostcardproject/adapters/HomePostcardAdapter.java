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

public class HomePostcardAdapter extends RecyclerView.Adapter<HomePostcardAdapter.ViewHolder> {
    private Context context;
    private List<Postcard> receivedPostcards;

    public HomePostcardAdapter(Context context, List<Postcard> sentPostcards) {
        this.context = context;
        this.receivedPostcards = sentPostcards;
    }

    @NonNull
    @Override
    public HomePostcardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postcardView = inflater.inflate(R.layout.item_home_postcard, parent, false);
        ViewHolder viewHolder = new ViewHolder(postcardView);
        return viewHolder;
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivHomePostcard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHomePostcard = (ImageView) itemView.findViewById(R.id.iv_home_postcard);
        }

        public void bind(Postcard postcard) {
            ParseFile coverPhoto = postcard.getCoverPhoto();
            Glide.with(context)
                    .load(coverPhoto.getUrl())
                    .centerCrop()
                    .into(ivHomePostcard);
        }
    }

    public void addAll(ArrayList<Postcard> postcards) {
        this.receivedPostcards.addAll(postcards);
        notifyDataSetChanged();
    }
}
