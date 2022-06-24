package com.example.thepostcardproject.fragments;

import static com.example.thepostcardproject.utilities.Keys.KEY_USER_FROM;
import static com.example.thepostcardproject.utilities.Keys.KEY_USER_TO;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thepostcardproject.R;
import com.example.thepostcardproject.adapters.HomePostcardAdapter;
import com.example.thepostcardproject.adapters.ProfilePostcardAdapter;
import com.example.thepostcardproject.models.Postcard;
import com.example.thepostcardproject.models.User;
import com.example.thepostcardproject.utilities.Keys;
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

    ArrayList<Postcard> receivedPostcards;
    HomePostcardAdapter adapter;

    RecyclerView rvPostcards;

    GoToDetailViewListener goToDetailViewListener;

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

    /**
     * Sets the variable sentPostcards to be the list of postcards sent by the current user
     */
    private void queryReceivedPostcards() {
        ParseQuery<Postcard> query = ParseQuery.getQuery(Postcard.class);
        query.whereEqualTo(KEY_USER_TO, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Postcard>() {
            @Override
            public void done(List<Postcard> postcards, ParseException e) {
                if (e == null) {
                    if (postcards.size() == 0) {
                        Log.d(TAG, "No received postcards yet!");
                    } else {
                        String firstItem = postcards.get(0).getMessage();
                        Log.d(TAG, "Message in first postcard: " + firstItem);
                        receivedPostcards = (ArrayList<Postcard>) postcards;
                        adapter.addAll(receivedPostcards);
                    }
                } else {
                    Log.d(TAG, "Error in querying for received postcards: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Displays the postcards sent by the user
     */
    private void displayPostcards() {
        adapter = new HomePostcardAdapter(getContext(), new ArrayList<>(), goToDetailViewListener);
        rvPostcards.setAdapter(adapter);
        rvPostcards.setLayoutManager(new LinearLayoutManager(getContext()));
        queryReceivedPostcards();
    }

    public interface GoToDetailViewListener {
        public void goToDetailView(Postcard postcard);
    }
}