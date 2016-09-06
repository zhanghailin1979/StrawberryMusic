package learning.moliying.com.strawberrymusic.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.xutils.ex.DbException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import learning.moliying.com.strawberrymusic.App;
import learning.moliying.com.strawberrymusic.utils.MediaUtils;
import learning.moliying.com.strawberrymusic.utils.PlayMode;
import learning.moliying.com.strawberrymusic.vo.Mp3Info;
import learning.moliying.com.strawberrymusic.vo.NetMusic;

/**
 * Created by Administrator on 2016/8/24.
 */
public class PlayService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener,MediaPlayer.OnPreparedListener {


    private static final String TAG = "PlayService";

    /**
     * Binder对象
     */
    public class PlayBinder extends Binder {
        public PlayService getPlayService() {
            return PlayService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }




    //组件和工具类属性
    public MediaPlayer mediaPlayer;
    private ExecutorService es = Executors.newSingleThreadExecutor();





    //数据类属性(本地音乐和网络音乐集合+getter,setter方法）
    private List<Mp3Info> mp3Infos = new ArrayList<>();
    private List<NetMusic> netMusics = new ArrayList<>();
    public List<Mp3Info> getMp3Infos() {
        return mp3Infos;
    }
    public List<NetMusic> getNetMusics() {
        return netMusics;
    }
    public void setNetMusics(List<NetMusic> netMusics) {
        this.netMusics = netMusics;
    }
    public void setMp3Infos(List<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }


    //定义变量
    public int currentPosition;    //当前播放状态的位置
    private int currentProgress;    //保存当前播放进度
    //播放模式
    public static final int LOCAL_MODE = 0X1;
    public static final int NET_MODE = 0X2;
    private int default_mode = LOCAL_MODE;
    public void setDefault_mode(int default_mode) {
        this.default_mode = default_mode;
    }
    public int getDefault_mode() {
        return default_mode;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mp3Infos = MediaUtils.getMp3Infos(this);
        //更新上次退出状态
        currentPosition = App.sp.getInt("currentPosition",0);
//        default_mode = App.sp.getInt("default_mode",0);
        currentProgress = App.sp.getInt("progress",0);
        Log.i(TAG, "onCreate: "+currentPosition);
        //设置监听事件
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        //预加载
//        play(currentPosition);

    }



    @Override
    public void onDestroy() {
        //提交状态
        SharedPreferences.Editor edit = App.sp.edit();
        edit.putInt("currentPosition",currentPosition);
        edit.putInt("default_mode",default_mode);
        edit.putInt("progress",getProgress());
        edit.commit();
        isUpdateStatus = false;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (es != null && !es.isShutdown()) {
            es.shutdown();
            es = null;
        }
        super.onDestroy();
    }



    private Random random = new Random();
    private PlayMode playMode = PlayMode.ORDER_PLAY;    //默认播放模式为顺序播放
    public void setPlayMode(PlayMode playMode){
        this.playMode = playMode;
    }
    public PlayMode getPlayMode() {
        return playMode;
    }

    /**
     * 设置播放模式（枚举PlayMode 三种：ORDER_PLAY,RANDOM_PLAY,SINGLE_PLAY）
     * 其中RANDOM_PLAY分为两种：local ,net
     * 播放模式通过setter方法设置
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, "onCompletion: "+isPlay);
        switch (playMode){
            //顺序播放
            case ORDER_PLAY:
                if(isPlay){
                    play(currentPosition);
                }else{
                    next();
                }
                break;
            //随机播放
            case RANDOM_PLAY:
                if(default_mode==LOCAL_MODE) play(random.nextInt(mp3Infos.size()));
                else if(default_mode == NET_MODE) play(random.nextInt(netMusics.size()));
                break;
            //单曲循环
            case SINGLE_PLAY:
                play(currentPosition);
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediaPlayer.reset();
        return false;
    }

    public void setProgress(int progress){
        if(mediaPlayer!=null){
           mediaPlayer.seekTo(progress);
        }
    }


    //获得当前进度值
    public int getProgress(){
        if(mediaPlayer!=null){
            return mediaPlayer.getCurrentPosition();
        }
      return 0;
    }
    //网络异步加载
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        es.execute(new UpdateRunnable());
        if (musicUpdateListener != null) {
            musicUpdateListener.onChange(currentPosition);
        }
    }


    //播放
    private boolean isPlay =false;
    public void play(int position){
        Log.i(TAG, "play: "+position);
        switch (default_mode){
            case LOCAL_MODE:
                if(position<0||position>mp3Infos.size()){
                    position = 0;
                }
                Mp3Info mp3Info = mp3Infos.get(position);
                EventBus.getDefault().post(mp3Info);    //本地音乐添加记录
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
                    mediaPlayer.prepare();
                    if(isPlay){
                        mediaPlayer.seekTo(currentProgress);
                        isPlay = false;
                    }
                    mediaPlayer.start();
                    currentPosition = position;
                    es.execute(new UpdateRunnable());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (musicUpdateListener != null) {
                    musicUpdateListener.onChange(currentPosition);
                }
                break;
            case NET_MODE:
                if(position<0||position>netMusics.size()){
                    position=0;
                }
                Log.i(TAG, "play: "+netMusics);
                Log.i(TAG, "play: position"+position);
                NetMusic netMusic = netMusics.get(position);
                if (musicUpdateListener != null) {
                    musicUpdateListener.onChange(currentPosition);
                }
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(this, Uri.parse(netMusic.getUrl()));
                    mediaPlayer.prepareAsync();
                    currentPosition = position;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;



        }



    }


    public boolean isPlaying(){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            return true;
        }else {
            return false;
        }
    }




    //播放
    public void play(){
        Log.i(TAG, "play: Duration="+mediaPlayer.getDuration());
        if(mediaPlayer!=null&&mediaPlayer.getDuration()==1){
            isPlay = true;
            play(currentPosition);
        }else if(mediaPlayer!=null&&!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            Log.i(TAG, "play: "+isPlay);
        }

    }
    //暂停
    public void pause(){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    //播放下一首
    public void next(){
        Log.i(TAG, "playNetMusic:service,playmode= "+default_mode);
        currentPosition +=1;
        switch (default_mode){
            case LOCAL_MODE:
                if(currentPosition>=mp3Infos.size()){
                    currentPosition = 0;
                }
                break;
            case NET_MODE:
                if(currentPosition>=netMusics.size()){
                    currentPosition = 0;
                }
                break;
        }
        play(currentPosition);
    }

    //播放上一首
    public void prev(){
        currentPosition-=1;
        switch (currentPosition){
            case LOCAL_MODE:
                if(currentPosition<0){
                    currentPosition = mp3Infos.size()-1;
                }
                break;
            case NET_MODE:
                currentPosition = netMusics.size()-1;
                break;
        }
        play(currentPosition);
    }



    //更新进度值
    private boolean isUpdateStatus = false;
    public class UpdateRunnable implements Runnable{
        public UpdateRunnable(){
            isUpdateStatus = true;
        }
        @Override
        public void run() {
            while (isUpdateStatus){
                if(mediaPlayer.isPlaying()&&musicUpdateListener!=null){
                    int currentProgress = mediaPlayer.getCurrentPosition();
                    musicUpdateListener.onPublish(currentProgress);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    //更新状态的接口
    public interface MusicUpdateListener {
        public void onPublish(int progress);

        public void onChange(int position);
    }

    private MusicUpdateListener musicUpdateListener;

    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
        this.musicUpdateListener = musicUpdateListener;
    }


}
