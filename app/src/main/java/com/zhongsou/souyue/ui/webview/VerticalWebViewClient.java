///*
// * Zirco Browser for Android
// *
// * Copyright (C) 2010 - 2012 J. Devauchelle and contributors.
// *
// * This program is free software; you can redistribute it and/or modify it under the terms of the
// * GNU General Public License version 3 as published by the Free Software Foundation.
// *
// * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
// * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * General Public License for more details.
// */
//
//package com.zhongsou.souyue.ui.webview;
//
//import android.annotation.TargetApi;
//import android.graphics.Bitmap;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import com.zhongsou.souyue.activity.VerticalSearchActivity;
//
///**
// * Convenient extension of WebViewClient.
// */
//public class VerticalWebViewClient extends WebViewClient {
//
//    private VerticalSearchActivity mMainActivity;
//
//    public VerticalWebViewClient(VerticalSearchActivity mainActivity) {
//        super();
//        mMainActivity = mainActivity;
//    }
//
//    @Override
//    public void onPageFinished(WebView view, String url) {
//        ((CustomWebView) view).notifyPageFinished();
//        mMainActivity.onPageFinished(view, url);
//
//        super.onPageFinished(view, url);
//    }
//
//    @Override
//    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//        ((CustomWebView) view).notifyPageStarted();
//        mMainActivity.onPageStarted(view, url);
//        super.onPageStarted(view, url, favicon);
//    }
//
//    @TargetApi(8)
//    @Override
//    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//        super.onReceivedError(view, errorCode, description, failingUrl);
//        view.stopLoading();
//        view.clearView();
//        mMainActivity.onReceivedError();
//    }
//
//    @Override
//    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        view.loadUrl(url);
//        mMainActivity.shouldOverrideUrlLoading(view, url);
//        return false;
//    }
//}
