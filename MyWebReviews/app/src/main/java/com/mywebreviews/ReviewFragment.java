package com.mywebreviews;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mywebreviews.sqlite.webreviews.WebReviewDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ReviewFragment extends Fragment {

    EditText urlInput;
    RatingBar scoreInput;
    EditText notesInput;

    WebReview currentWebReview;
    WebReviewDB webReviewDB;

    private static final String ARG_PARAM1 = "initial_domain";

    private String initial_domain;

    public ReviewFragment() {
        // Required empty public constructor
    }

    public static ReviewFragment newInstance(String param1, String param2) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            initial_domain = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_review, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        webReviewDB = new WebReviewDB(getActivity());
        webReviewDB.open();

        currentWebReview = new WebReview();

        scoreInput = view.findViewById(R.id.ratingBar);
        notesInput = view.findViewById(R.id.notes_input);
        urlInput = view.findViewById(R.id.search_domain_input);

        bindButtons(view);

        updateWebReview(WebReview.extractDomain(initial_domain));
    }

    private void bindButtons(View view){
        view.findViewById(R.id.validate_web_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonInsertWebReview(view);
            }});
        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ReviewFragment.this)
                        .navigate(R.id.action_viewReview_to_browserFragment);
            }});
        urlInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    String domain = WebReview.extractDomain(urlInput.getText().toString());
                    if (domain.length() == 0){
                        return;
                    }
                    updateWebReview( domain );
                }
            }
        });
    }
    protected void updateWebReview(String domain){
        if( currentWebReview.getDomain() == domain){
            return;
        }
        WebReview webReview = webReviewDB.getByDomain(domain);
        if (webReview == null){
            currentWebReview = new WebReview(domain);
        } else {
            currentWebReview = webReview;
        }
        updateWebReviewDisplay();
    }

    protected void updateWebReview(String domain, int score, String notes){
        WebReview webReview;
        if( currentWebReview.getDomain() == domain){
            webReview = currentWebReview;
        } else {
            webReview = webReviewDB.getByDomain(domain);
            if (webReview == null){
                webReview = new WebReview(domain);
            }
        }
        webReview.setScore(score);
        webReview.setNotes(notes);
        currentWebReview = webReview;
        updateWebReviewDisplay();
    }

    protected void updateWebReviewDisplay(){
        scoreInput.setProgress(currentWebReview.getScore());
        urlInput.setText(currentWebReview.getDomain());
        notesInput.setText(currentWebReview.getNotes());
    }

    public void onButtonInsertWebReview(View view){
        String domain = WebReview.extractDomain(urlInput.getText().toString());
        if (domain.length() == 0){
            return;
        }
        updateWebReview(
                domain,
                scoreInput.getProgress(),
                notesInput.getText().toString()
        );
        if (currentWebReview.getDBId() >= 0 ){
            webReviewDB.update(currentWebReview.getDBId(), currentWebReview);
        } else {
            webReviewDB.insert(currentWebReview);
        }
        Utils.hideKeyboardFrom(getActivity(), view);
    }
}