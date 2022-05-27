package com.hss.deeplinkdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.IAgentWebSettings;
import com.just.agentweb.MiddlewareWebChromeBase;
import com.just.agentweb.MiddlewareWebClientBase;

import uk.co.alt236.webviewdebug.DebugWebChromeClient;
import uk.co.alt236.webviewdebug.DebugWebViewClient;
import uk.co.alt236.webviewdebug.DebugWebViewClientLogger;

/**
 * @Despciption todo
 * @Author hss
 * @Date 20/05/2022 14:52
 * @Version 1.0
 */
public class H5Activity extends AppCompatActivity {


    public static void start(Activity activity, String url){
        Intent intent = new Intent(activity, H5Activity.class);
        intent.putExtra("url",url);
        activity.startActivity(intent);
    }

    WebView webView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         webView = DemoWebviewUtil.init(this,getIntent().getStringExtra("url"));
        setContentView(webView);
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }
}
