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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.google.android.material.snackbar.Snackbar;
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
    public static final int LOAD_AT_ONCE = 5;

    private ArrayList<Postcard> receivedPostcards;
    private HomePostcardAdapter adapter;

    private RecyclerView rvPostcards;

    private GoToDetailViewListener goToDetailViewListener;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeContainer;

    // Counters for the infinite scroll
    private int limit = 0;
    private boolean loadMore = true;

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
        setupSwipeToRefresh();
    }

    /**
     * Attaches the views to the corresponding variables
     * @param view The encapsulating view
     */
    private void setupViews(View view) {
        rvPostcards = view.findViewById(R.id.rv_postcards);
        swipeContainer = view.findViewById(R.id.swipe_container);
    }

    private void loadMorePostcards() {
        ParseQuery<Postcard> query = ParseQuery.getQuery(Postcard.class);
        query.whereEqualTo(KEY_USER_TO, ParseUser.getCurrentUser());
        query.setLimit(LOAD_AT_ONCE);
//        query.addDescendingOrder("createdAt");

        query.setSkip(limit);
        // TODO : if limit == 0 and postcards.size() == 0, show "no postcards!"
        if (limit == 0) {
            scrollListener.resetState();
        }
        query.findInBackground(new FindCallback<Postcard>() {
            @Override
            public void done(List<Postcard> postcards, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Number of postcards: " + postcards.size());
                    swipeContainer.setRefreshing(false);
                    // If this is the first query made, clear the adapter for the swipe to refresh
                    if (limit == 0) {
                        adapter.clear();
                        if (postcards.size() == 0) {
                            Snackbar.make(rvPostcards, "You have not been sent any postcards!", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    limit += postcards.size();
                    if (postcards.size() != 0) {
                        adapter.addAll((ArrayList<Postcard>) postcards);
                        loadMore = true;
                    } else {
                        // No more postcards to load
                        loadMore = false;
                    }
                } else {
                    Log.d(TAG, "There has been an issue retrieving the postcards from the backend: " + e.getMessage());
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
                if (loadMore) {
                    loadMorePostcards();
                }
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

    // ##########################################
    // ##        SWIPE TO REFRESH              ##
    // ##########################################

    private void setupSwipeToRefresh() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                limit = 0;
                loadMorePostcards();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
}