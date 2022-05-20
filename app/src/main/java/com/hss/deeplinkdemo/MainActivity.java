package com.hss.deeplinkdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void intentZL(View view) {
        DeepLinkJumpUtil.jump(this,"zljnews://news.zhoulujue.com/article/123456/");
    }

    public void intentHttp(View view) {
        DeepLinkJumpUtil.jump(this,"http://news.zhoulujue.com/article/123456/");
    }

    public void webviewZL(View view) {
        H5Activity.start(this,"zljnews://news.zhoulujue.com/article/123456/");
    }

    public void webviewHttp(View view) {
        H5Activity.start(this,"http://news.zhoulujue.com/article/123456/");
    }

    public void inH5(View view) {
       H5Activity.start(this,"file:///android_asset/deeplinktest.html");

       //cr_AwContentsClient: No application can handle intent://zljnews/recipe/100390954#Intent;scheme=zljnews;package=com.hss.deeplinktargetapp;end
    }
}