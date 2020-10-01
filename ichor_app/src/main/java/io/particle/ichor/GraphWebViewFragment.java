package io.particle.hydroalert;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import io.particle.hydroalert.util.DataHolder;



public class GraphWebViewFragment extends Fragment {
View rootView;
    WebView mWebView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_graph_webview, container, false);
        mWebView = (WebView) rootView.findViewById(R.id.chart_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(DataHolder.getThingspeakUrlString());
        mWebView.requestFocus();
        return rootView;
    }
}
