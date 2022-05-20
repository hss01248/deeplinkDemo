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

    String url  = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h5);
        url = getIntent().getStringExtra("url");
        initWebView();

    }

    private void initWebView() {
        AgentWeb agentWeb = AgentWeb.with(this)//传入Activity or Fragment
                .setAgentWebParent(findViewById(R.id.ll_root),
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                //传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .setAgentWebWebSettings(new IAgentWebSettings() {
                    WebSettings settings;

                    @Override
                    public IAgentWebSettings toSetting(WebView webView) {
                        settings = webView.getSettings();
                        settings.setAllowFileAccess(true);
                        return this;
                    }

                    @Override
                    public WebSettings getWebSettings() {
                        return settings;
                    }
                })
            .createAgentWeb().go(url);

        WebView webView = agentWeb.getWebCreator().getWebView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WebViewClient webViewClient = webView.getWebViewClient();
            DebugWebViewClient client = new DebugWebViewClient(webViewClient){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    //LogUtils.i("shouldOverrideUrlLoading", request);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (!request.getUrl().getScheme().contains("http")) {
                           /* if("intent".equals(request.getUrl().getScheme())){
                                LogUtils.i("intent协议让chrome自己处理,但是tmd不处理",request.getUrl());
                                return super.shouldOverrideUrlLoading(view, request);
                            }*/
                           boolean hasHandled =  DeepLinkJumpUtil.jump(view.getContext(), request.getUrl().toString());
                           return hasHandled;

                        }
                    }

                    return super.shouldOverrideUrlLoading(view, request);
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (request.isForMainFrame()) {
                            LogUtils.i("shouldInterceptRequest--> main frame", request.getUrl());
                           /* if("intent".equals(request.getUrl().getScheme())){
                                LogUtils.i("intent协议让chrome自己处理",request.getUrl());
                                return null;
                            }*/
                            boolean hasHandled =  DeepLinkJumpUtil.jump(view.getContext(), request.getUrl().toString());
                            return null;
                        }
                    }
                    return super.shouldInterceptRequest(view, request);
                }
            };
            client.setLoggingEnabled(true);
            webView.setWebViewClient(client);

            /*WebChromeClient webChromeClient = webView.getWebChromeClient();
            DebugWebChromeClient chromeClient = new DebugWebChromeClient(webChromeClient);
            chromeClient.setLoggingEnabled(true);*/

        }


    }

}
