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

    // **************************************************
    // **     IMPLEMENT RECYCLERVIEW ADAPTER METHODS   **
    // **************************************************

    /**
     * Inflates the layout for each filter
     */
    @NonNull
    @Override
    public FilterSuggestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        ItemFilterSuggestionBinding itemBinding = ItemFilterSuggestionBinding.inflate(inflater, parent, false);
        return new ViewHolder(itemBinding);
    }

    /**
     * Binds the information for each filter to the layout
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Filter filter = filters.get(position);
        holder.bind(drawablePhoto, filter);
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    // **************************************************
    // **     ADD NEW FILTERS TO THE LIST DISPLAYED    **
    // **************************************************

    /**
     * Add a list of filters to the list displayed
     * @param addFilters The list of new filters to display
     */
    public void addAll(ArrayList<Filter> addFilters) {
        filters.addAll(addFilters);
        notifyDataSetChanged();
    }

    /**
     * Add a filter to the list displayed
     * @param newFilter The new filter to display
     */
    public void add(Filter newFilter) {
        notifyItemInserted(filters.size());
        filters.add(newFilter);
    }

    // *********************************************
    // **     VIEWHOLDER TO DISPLAY EACH FILTER   **
    // *********************************************

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemFilterSuggestionBinding binding;

        public ViewHolder(ItemFilterSuggestionBinding itemBinding) {
            super(itemBinding.getRoot());
            itemBinding.getRoot().setOnClickListener(this);
            binding = itemBinding;
        }

        /**
         * Binds the photo and the filter to the ImageView
         * @param drawablePhoto The photo to display
         * @param filter The filter to apply on the photo
         */
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

        /**
         * When a filter is clicked, display the result on the image preview
         * @param view The view selected
         */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            displaySelectedFilterListener.displaySelectedFilter(filters.get(position));
        }
    }
}
