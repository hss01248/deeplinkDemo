package com.hss.deeplinkdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hss01248.dialog.ActivityStackManager;


import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Despciption https://blog.csdn.net/Jaden_hool/article/details/60873044
 * https://juejin.cn/post/6844903620287135752
 * @Author hss
 * @Date 29/04/2022 15:07
 * @Version 1.0
 */
public class DeepLinkJumpUtil {

    /**
     * 可以跳转 : uu://m.xxx.com/?actionKey=BCIMVAIAAAA%3D&a=yyy
     * 可以跳转: yy://yy.xxx.com?actionKey=BCIMVAIAAAA%3D&a=yyy&#Intent;scheme=yy;package=io.yy.zzzzz
     * 这种无法跳转: intent://m.xxx.com/?actionKey=BCIMVAIAAAA%3D&a=yyy
     *
     * js里怎么跳app: 本身的zljnews://zljnews/article/123456/,用js写是:
     * <a href="intent://zljnews/recipe/100390954#Intent;scheme=zljnews;package=com.zhoulujue.news;end"> Intent scheme </a>  点击后,chrome会将url转义?
     * 参考  https://developer.chrome.com/docs/multidevice/android/intents/
     * @param context
     * @param newurl
     */
    public static boolean jump(Context context,String newurl){

        if(newurl.startsWith("intent://")){
            return jumpByIntent2(context,newurl);
            //return jumpByIntent(context,newurl);
        }
        //弹窗选择:
        //if (!newurl.startsWith("http")) {
            try {
                context = ActivityUtils.getTopActivity();
                // 以下固定写法
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newurl));
                List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
                //  //context.getPackageManager().resolveActivity(intent, 0);
                if(resolveInfos == null || resolveInfos.isEmpty()){
                    ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, 0);
                    if(resolveInfo == null){
                        ToastUtils.showLong("没有对应的activity,跳去应用市场吧");
                        return false;
                    }else {
                        resolveInfos = new ArrayList<>();
                        resolveInfos.add(resolveInfo);
                    }
                }
                LogUtils.i(newurl,resolveInfos);
                String name = "是否跳转到外部app?";
                if(resolveInfos.size() == 1){
                    ResolveInfo resolveInfo = resolveInfos.get(0);
                    if(resolveInfo.activityInfo != null && !TextUtils.isEmpty(resolveInfo.activityInfo.packageName)){
                        name = "是否跳转到 " +AppUtils.getAppName(resolveInfo.activityInfo.packageName) +"?";
                    }
                }
                Context finalContext = context;
                Context finalContext1 = context;
                String finalName = name;
                ThreadUtils.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {

                        AlertDialog dialog = new AlertDialog.Builder(finalContext1)
                                .setTitle("跳转")
                                .setMessage(finalName)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            finalContext.startActivity(intent);
                                        }catch (Throwable throwable){
                                            LogUtils.w(throwable);
                                            ToastUtils.showLong("没有对应的activity,跳去应用市场吧");
                                        }

                                    }
                                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).create();
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }
                });

                return true;

            } catch (Exception e) {
                // 防止没有安装的情况
                LogUtils.w(e);
                ToastUtils.showLong("没有对应的activity,跳去应用市场吧");
                //No Activity found to handle Intent { act=android.intent.action.VIEW
                // dat=tunaikumobile://open?_branch_referrer=H4sIAAAAAAAAA8soKSkottLXLynNS8zMLtVLLCjQy8nMy9Z3zC7NScwuDYGIG9mrGpkYF9gmxidCxAHgNy05OAAAAA==&link_click_id=991275456483771349 flg=0x30000000 }

                //{ act=android.intent.action.VIEW dat=intent:// flg=0x30000000 }
                return false;

            }

        //}
    }

    private static boolean jumpByIntent2(Context context, String newurl) {
        Intent intent = null;
        try {
            intent = Intent.parseUri(newurl, Intent.URI_INTENT_SCHEME);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);

            intent.setComponent(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                intent.setSelector(null);
            }
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, 0);
            if(resolveInfo == null){
                ToastUtils.showLong("没有对应的activity,跳去应用市场吧: " );//map.get("package")
                return false;
            }

            //todo 弹窗提示
            ActivityStackManager.getInstance().getTopActivity().startActivityIfNeeded(intent, -1);
            //return jump(context, realUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * chrome不会帮我们处理,构建系统intent也找不到对应的activity,需要自己改成标准格式: zljnews://news.zhoulujue.com/recipe/100390954
     * @param context
     * @param newurl
     * @return
     */
    private static boolean jumpByIntent(Context context,String newurl) {
        int idx = newurl.indexOf("#");
        if(idx<0){
            LogUtils.w("非法intent,不带#",newurl);
            return false;
        }
        String path0 = newurl.substring(0,idx);
        String next = newurl.substring(idx+1);
        if(!next.startsWith("Intent;") || !next.endsWith(";end")){
            LogUtils.w("非法intent,不以end结尾",newurl);
            return false;
        }
        Map<String,String> map = new HashMap<>();

        String[] strings = next.split(";");
        for (String string : strings) {
            if(string.contains("=")){
                String[] split = string.split("=");
                map.put(split[0], URLDecoder.decode(split[1]));
            }
        }
        if(map.containsKey("scheme") && map.containsKey("package")){
            String realUrl = path0.replace("intent://",map.get("scheme")+"://");
            LogUtils.w("realUrl",realUrl,map);
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(realUrl));
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, 0);
            if(resolveInfo == null){
                ToastUtils.showLong("没有对应的activity,跳去应用市场吧: "+ map.get("package"));
                return false;
            }
            return jump(context, realUrl);
        }
        LogUtils.w("非法intent,不含scheme 和 package",newurl);
        return false;
    }
}
