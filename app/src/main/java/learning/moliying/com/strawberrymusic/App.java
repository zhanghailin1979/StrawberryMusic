package learning.moliying.com.strawberrymusic;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.drawee.backends.pipeline.Fresco;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.LinkedList;

import learning.moliying.com.strawberrymusic.utils.Constant;

/**
 * Created by Administrator on 2016/8/23.
 */
public class App extends Application {
    //保存当前的播放状态
    public static SharedPreferences sp;
    //xutils3 的数据库管理操作类
    public static DbManager dbManager;
    public static Context context;

    public static LinkedList<Activity> activities = new LinkedList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        x.Ext.init(this);      //初始化xutils3

        sp = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        //xutils3数据库配置
        DbManager.DaoConfig config = new DbManager.DaoConfig()
                .setDbName(Constant.DB_NAME)
                .setDbVersion(Constant.VERSION);
        dbManager = x.getDb(config);
        context = getApplicationContext();
    }

    public static void release(){
        activities = null;
        context = null;
    }


}
