package com.mywebreviews;

import android.content.Context;
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

import com.mywebreviews.sqlite.webreviews.WebReviewDB;



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
        String intentDomain = getActivity().getIntent().getDataString();
        Toast.makeText(getActivity(), intentDomain, Toast.LENGTH_LONG).show();
        if (intentDomain  != null){
            //Android want to force apps to use encryption
            if (intentDomain.startsWith("http://")){
                intentDomain = intentDomain.replaceFirst("http://", "https://");
            }
            setWebView(intentDomain);
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