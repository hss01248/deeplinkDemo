package com.hss.deeplinkdemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import androidx.multidex.MultiDexApplication;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyItemDialogListener;
import com.hss01248.dokit.IDokitConfig;
import com.hss01248.dokit.MyDokit;

import java.util.ArrayList;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 20/05/2022 15:11
 * @Version 1.0
 */
public class BaseApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        StyledDialog.init(this);
        MyDokit.setConfig(new IDokitConfig() {
            @Override
            public void loadUrl(Context context, String url) {
                List<String> strings = new ArrayList<>();
                strings.add("使用app内webview加载");
                strings.add("使用外部浏览器加载");
                StyledDialog.buildIosSingleChoose(strings, new MyItemDialogListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        if(position ==0){
                            H5Activity.start(ActivityUtils.getTopActivity(),url);
                        }else if(position ==1){
                            try {
                                Uri uri = Uri.parse(url);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                                ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, 0);
                                if(resolveInfo == null){
                                    ToastUtils.showLong("没有对应的应用来打开这个链接:\n"+url);
                                    return;
                                }
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ActivityUtils.getTopActivity().startActivity(intent);
                            }catch (Throwable throwable){
                                throwable.printStackTrace();
                              ToastUtils.showLong(throwable.getMessage());
                            }
                        }
                    }
                }).setActivity(ActivityUtils.getTopActivity()).show();
            }

            @Override
            public void report(Object o) {
                LogUtils.d(o);

            }
        });
    }
}
