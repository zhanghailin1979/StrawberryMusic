package learning.moliying.com.strawberrymusic.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import java.io.File;

import learning.moliying.com.strawberrymusic.App;

/**
 * descreption:
 * company: moliying.com
 * Created by vince on 16/7/20.
 */
public class AppUtils {
    public static final String ACTION_MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";

    //隐藏输入法
    public static void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) App.context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void scannerMedia(Context ctx){
        Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
        intent.setData(Uri.parse("file://"
                + Environment.getExternalStorageDirectory()));
        ctx.sendBroadcast(intent);
    }


    public static void scanDirAsync(Context ctx, String dir) {
        Intent scanIntent = new Intent(ACTION_MEDIA_SCANNER_SCAN_DIR);
        scanIntent.setData(Uri.fromFile(new File(dir)));
        ctx.sendBroadcast(scanIntent);
    }



    //检查网络是否可用
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            //获取连接管理器(系统服务)
            ConnectivityManager mConnectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取当前激活的网络信息
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
