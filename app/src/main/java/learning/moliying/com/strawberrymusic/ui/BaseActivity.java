package learning.moliying.com.strawberrymusic.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.xutils.x;

import java.util.List;

import learning.moliying.com.strawberrymusic.App;
import learning.moliying.com.strawberrymusic.service.PlayService;
import learning.moliying.com.strawberrymusic.utils.MediaUtils;
import learning.moliying.com.strawberrymusic.vo.Mp3Info;

/**
 * Created by Administrator on 2016/8/24.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.activities.add(this);
        x.view().inject(this);
        startService(new Intent(this,PlayService.class));//启动服务
    }

    @Override
    protected void onDestroy() {
        App.activities.remove(this);
        super.onDestroy();
    }

    protected PlayService playService;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            PlayService.PlayBinder playBinder=(PlayService.PlayBinder)iBinder;
            playService =playBinder.getPlayService();
            playService.setMusicUpdateListener(musicUpdateListener);
            musicUpdateListener.onChange(playService.currentPosition);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private PlayService.MusicUpdateListener musicUpdateListener = new PlayService.MusicUpdateListener() {
        @Override
        public void onPublish(int progress) {
            publish(progress);
        }

        @Override
        public void onChange(int position) {
            change(position);
        }
    };

    public abstract  void publish(int progress);
    public abstract void change(int position);





    /**
     * 绑定Service 和解绑
     */
    //绑定Service
    protected void bindPlayService() {
            Intent intent = new Intent(this, PlayService.class);
            bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }

    protected void unbindPlayService(){
            unbindService(conn);
    }



    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        bindPlayService();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        unbindPlayService();
        super.onStop();
    }





    private void closeALL() {
        for (Activity activity : App.activities) {
            activity.finish();
        }
        App.release();
    }
}
