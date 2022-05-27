package com.hss.deeplinkdemo;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import uk.co.alt236.webviewdebug.DebugWebChromeClient;
import uk.co.alt236.webviewdebug.DebugWebViewClient;

/**
 * @Despciption todo
 * @Author hss
 * @Date 27/05/2022 16:54
 * @Version 1.0
 */
public class DemoWebviewUtil {

    public static WebView init(Context context,String url){
        WebView webView = new WebView(context);
        initSettings(webView);
        
        final DebugWebViewClient debugWebViewClient = new DebugWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String scheme = request.getUrl().getScheme();
                    if(!TextUtils.isEmpty(scheme) && !scheme.contains("http")){
                        return DeepLinkJumpUtil.jump(view.getContext(),request.getUrl().toString());
                    }
                    return super.shouldOverrideUrlLoading(view, request);
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        debugWebViewClient.setLoggingEnabled(true);
        debugWebViewClient.setJsDebugPannelEnable(false);
        webView.setWebViewClient(debugWebViewClient);

        DebugWebChromeClient chromeClient = new DebugWebChromeClient(new WebChromeClient());
        chromeClient.setLoggingEnabled(true);
        webView.setWebChromeClient(chromeClient);

        webView.loadUrl(url);
        return webView;
    }

    private static void initSettings(WebView webView) {
        WebSettings mWebSettings = webView.getSettings();
        mWebSettings.setDefaultTextEncodingName("utf-8");//字符编码UTF-8
        //支持获取手势焦点，输入用户名、密码或其他
        webView.requestFocusFromTouch();

        mWebSettings.setSupportZoom(false);//不支持缩放

        mWebSettings.setTextZoom(100);
        //设置自适应屏幕，两者合用
        mWebSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        mWebSettings.setUseWideViewPort(true); //将图片调整到适合webView的大小

        mWebSettings.setNeedInitialFocus(true); //当webView调用requestFocus时为webView设置节点

        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //调用JS方法.安卓版本大于17,加上注解 @JavascriptInterface
        mWebSettings.setJavaScriptEnabled(true);//支持javascript
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过js打开新的窗口
        mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染等级
        mWebSettings.setSupportMultipleWindows(true);
        mWebSettings.setEnableSmoothTransition(true);
        webView.setFitsSystemWindows(true);
        //缓存数据 (localStorage)
        //有时候网页需要自己保存一些关键数据,Android WebView 需要自己设置

        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
       /* if (NetStatusUtil.isConnected(webView.getContext())) {
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        } else {
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
        }*/

        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setSaveFormData(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        String appCachePath = webView.getContext().getCacheDir().getAbsolutePath();
        mWebSettings.setAppCachePath(appCachePath);
        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        mWebSettings.setSupportMultipleWindows(false);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // for remote debug
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }


        //safe
        webView.removeJavascriptInterface("searchBoxJavaBridge_");
        webView.removeJavascriptInterface("accessibility");
        webView.removeJavascriptInterface("accessibilityTraversal");
    }
}
