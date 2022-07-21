package com.example.thepostcardproject.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.thepostcardproject.databinding.ItemFilterSuggestionBinding;
import com.example.thepostcardproject.fragments.PhotoFilterFragment;
import com.example.thepostcardproject.models.Filter;
import com.example.thepostcardproject.models.FilteredPhoto;
import com.parse.ParseException;

import java.util.ArrayList;

public class FilterSuggestionAdapter extends RecyclerView.Adapter<FilterSuggestionAdapter.ViewHolder> {
    public final static String TAG = "FilterSuggestionAdapter";

    private Context context;
    private Drawable drawablePhoto;
    private ArrayList<Filter> filters;
    PhotoFilterFragment.DisplaySelectedFilterListener displaySelectedFilterListener;

    public FilterSuggestionAdapter(Context context, Drawable drawablePhoto, ArrayList<Filter> filters, PhotoFilterFragment.DisplaySelectedFilterListener displaySelectedFilterListener) {
        this.context = context;
        this.drawablePhoto = drawablePhoto;
        this.filters = filters;
        this.displaySelectedFilterListener = displaySelectedFilterListener;
    }

    @NonNull
    @Override
    public FilterSuggestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        ItemFilterSuggestionBinding itemBinding = ItemFilterSuggestionBinding.inflate(inflater, parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Filter filter = filters.get(position);
        holder.bind(drawablePhoto, filter);
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    public void addAll(ArrayList<Filter> addFilters) {
//        int positionStart = filters.size();
        filters.addAll(addFilters);
        notifyDataSetChanged();
    }

    public void add(Filter newFilter) {
        notifyItemInserted(filters.size());
        filters.add(newFilter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemFilterSuggestionBinding binding;

        public ViewHolder(ItemFilterSuggestionBinding itemBinding) {
            super(itemBinding.getRoot());
            itemBinding.getRoot().setOnClickListener(this);
            binding = itemBinding;
        }

        public void bind(Drawable drawablePhoto, Filter filter) {
            filter.addFilterToImageView(binding.ivFilterSuggestion);
            Glide.with(context)
                    .load(drawablePhoto)
                    .transform(new CenterCrop(), new RoundedCorners(15))
                    .into(binding.ivFilterSuggestion);
            if (filter.getDescription() != null) {
                binding.tvFilterDescription.setText(filter.getDescription());
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d(TAG, String.valueOf(filters));
            displaySelectedFilterListener.displaySelectedFilter(filters.get(position));
            Log.d(TAG, "View clicked! " + position);
        }
    }

}
