package com.example.thepostcardproject.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.thepostcardproject.R;
import com.example.thepostcardproject.databinding.FragmentPostcardDetailBinding;
import com.example.thepostcardproject.databinding.FragmentProfileBinding;
import com.example.thepostcardproject.models.Postcard;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostcardDetailFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostcardDetailFragment extends Fragment {

    public static final String TAG = "PostcardDetailFragment";
    private FragmentPostcardDetailBinding binding;

    private Postcard postcard;

    public PostcardDetailFragment() {
        // Required empty public constructor
    }

    public static PostcardDetailFragment newInstance(Postcard postcard) {
        PostcardDetailFragment postcardDetailFragment = new PostcardDetailFragment();
        postcardDetailFragment.postcard = postcard;
        return postcardDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPostcardDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayPostcard();
    }

    private void displayPostcard() {
        Glide.with(getContext())
                .load(postcard.getCoverPhoto().getUrl())
                .into(binding.ivHomePostcard);
        try {
            String toFrom = "From: " + postcard.getUserFrom().getUsername() + " | " + postcard.getLocationFrom().getLocationName() + "\nTo: " + postcard.getUserTo().getUsername() + " | " + postcard.getLocationTo().getLocationName();
            binding.tvUsername.setText(toFrom);
        } catch (com.parse.ParseException e) {
            Log.d(TAG, "An exception occurred with retrieving the username: " + e.getMessage());
        }
        binding.tvMessage.setText(postcard.getMessage());
    }
}