package com.hss.deeplinktargetapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @Despciption todo
 * @Author hss
 * @Date 29/04/2022 14:15
 * @Version 1.0
 */
public class RouterActivty extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getDataString();
        setContentView(R.layout.activity_router);
        TextView textView = findViewById(R.id.tv_router);
        textView.setText("接收到路由: \n"+getIntent().getDataString());
        if(TextUtils.isEmpty(url)){
            //直接跳到首页
            startActivity(new Intent(this,MainActivity.class));
        }else {
            //执行通用跳转逻辑
            textView.setText("接收到路由: \n"+url+"\n执行通用跳转逻辑..........使用ARouter等框架");
        }
        /*AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("router")
                .setMessage(getIntent().getDataString())
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();

                    }
                }).create();
        dialog.show();*/
    }
}
