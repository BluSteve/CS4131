package com.stevecao.avportal.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.stevecao.avportal.R;

public class GalleryFragment extends Fragment {
    Context mContext;
    WebView wv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.webview, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mContext = view.getContext();
        wv = view.findViewById(R.id.webView);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                wv.loadUrl(url);
                return false; // then it is not handled by default action
            }
        });
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl("https://drive.google.com/drive/u/0/mobile/folders/1kYzjwvUYMSapBv77UEWqMMUK5u6K8ORh?usp=drive_open");
    }

    @Override
    public void onResume() {
        super.onResume();

    }

}
