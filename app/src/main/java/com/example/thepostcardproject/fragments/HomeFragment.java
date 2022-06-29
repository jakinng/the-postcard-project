package com.example.thepostcardproject.fragments;

import static com.example.thepostcardproject.utilities.Keys.KEY_USER_FROM;
import static com.example.thepostcardproject.utilities.Keys.KEY_USER_TO;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.adapters.HomePostcardAdapter;
import com.example.thepostcardproject.adapters.ProfilePostcardAdapter;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.models.User;
import com.example.thepostcardproject.utilities.EndlessRecyclerViewScrollListener;
import com.example.thepostcardproject.utilities.Keys;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    public static final String TAG = "HomeFragment";
    public static final int LOAD_AT_ONCE = 2;

    private ArrayList<Postcard> receivedPostcards;
    private HomePostcardAdapter adapter;

    private RecyclerView rvPostcards;

    private GoToDetailViewListener goToDetailViewListener;
    private EndlessRecyclerViewScrollListener scrollListener;

    // Counters for the infinite scroll
    private int limit = 0;
    private boolean loadMore = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(GoToDetailViewListener goToDetailViewListener) {
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.goToDetailViewListener = goToDetailViewListener;
        return homeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * Called shortly after onCreateView
     * @param view The view containing the various visual components
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        displayPostcards();
    }

    /**
     * Attaches the views to the corresponding variables
     * @param view The encapsulating view
     */
    private void setupViews(View view) {
        rvPostcards = view.findViewById(R.id.rv_postcards);
    }

    private void loadMorePostcards() {
        ParseQuery<Postcard> query = ParseQuery.getQuery(Postcard.class);
        query.whereEqualTo(KEY_USER_TO, ParseUser.getCurrentUser());
        query.setLimit(LOAD_AT_ONCE);
        query.addDescendingOrder("createdAt");

        query.setSkip(limit);
        // TODO : if limit == 0 and whatever, show "no postcards!"
        query.findInBackground(new FindCallback<Postcard>() {
            @Override
            public void done(List<Postcard> postcards, ParseException e) {
                if (e == null) {
                    limit += postcards.size();
                    if (postcards.size() != 0) {
                        loadMore = true;
                        Log.d(TAG, "the description of the first post is: " + String.valueOf(postcards.get(0).getMessage()));
                        adapter.addAll((ArrayList<Postcard>) postcards);
                    } else {
                        loadMore = false;
                        Log.d(TAG, "no posts!!!!");
                    }
                } else {
                    Log.d(TAG, "I can't get the posts..." + e.getMessage());
                }
            }
        });
    }

    /**
     * Displays the postcards sent by the user
     */
    private void displayPostcards() {
        // Set the adapter
        adapter = new HomePostcardAdapter(getContext(), new ArrayList<>(), goToDetailViewListener);
        rvPostcards.setAdapter(adapter);

        // Optimizes since the items are static and will not change while scrolling
        rvPostcards.setHasFixedSize(true);

        // Add infinite scroll
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvPostcards.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMorePostcards();
            }
        };
        rvPostcards.addOnScrollListener(scrollListener);

        // Add snap to center
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvPostcards);

        loadMorePostcards();
    }


    public interface GoToDetailViewListener {
        void goToDetailView(Postcard postcard);
    }
}