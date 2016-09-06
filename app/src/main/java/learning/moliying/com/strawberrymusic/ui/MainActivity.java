package learning.moliying.com.strawberrymusic.ui;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.ViewUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import learning.moliying.com.strawberrymusic.App;
import learning.moliying.com.strawberrymusic.R;
import learning.moliying.com.strawberrymusic.fragments.IndexFragment;
import learning.moliying.com.strawberrymusic.fragments.MyMusicListFragment;
import learning.moliying.com.strawberrymusic.fragments.NetMusicFragment;
import learning.moliying.com.strawberrymusic.fragments.RecentPlayListFragment;
import learning.moliying.com.strawberrymusic.service.PlayService;
import learning.moliying.com.strawberrymusic.utils.HttpUtils;
import learning.moliying.com.strawberrymusic.utils.MediaUtils;
import learning.moliying.com.strawberrymusic.utils.MessageEventType;
import learning.moliying.com.strawberrymusic.vo.BackEvent;
import learning.moliying.com.strawberrymusic.vo.MessageEvent;
import learning.moliying.com.strawberrymusic.vo.Mp3Info;
import learning.moliying.com.strawberrymusic.vo.NetMusic;


@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private SimpleDraweeView image;
    private TextView tv_songName;
    private TextView tv_singer;
    private ImageButton listButton;
    private ImageButton playButton;
    private ImageButton nextButton;


    private static final String TAG = "MainActivity";
    private IndexFragment indexFragment;
    private MyMusicListFragment myMusicListFragment; //本地音乐Fragment
    private RecentPlayListFragment recentPlayListFragment;//最近播放Fragment
    private android.support.v4.app.FragmentManager fm;
    private NetMusicFragment netMusicFragment;      //网络音乐Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获得Fragment管理器
        fm = getSupportFragmentManager();
        indexFragment = IndexFragment.newInstance();
        fm.beginTransaction().add(R.id.fram_layout_main, indexFragment).commit();
        initUi();

    }

    /**
     * 实现BaseActivity的两个抽象方法
     *
     * @param progress
     */
    @Override
    public void publish(int progress) {

    }
    public static final int LOCAL_TYPE=0X1;
    public static final int NET_TYPE = 0X2;
    private int type = 0;

    @Override
    public void change(int position) {
        switch (type){
            case LOCAL_TYPE:
                Mp3Info mp3Info = playService.getMp3Infos().get(position);
                Bitmap bitmap = MediaUtils.getArtwork(this, mp3Info.getMp3InfoId(), mp3Info.getAlbumId(), true, true);
                image.setImageBitmap(bitmap);
                Log.i(TAG, "change: " + bitmap);
                tv_songName.setText(mp3Info.getTitle());
                tv_singer.setText(mp3Info.getArtist());
                playButton.setSelected(true);
                break;
            case NET_TYPE:
                List<NetMusic> netMusics = playService.getNetMusics();
                int size = netMusics.size();
                if(position<0||position>=size){
                    position = 0;
                }
                Log.i(TAG, "change: "+position);
                NetMusic netMusic = netMusics.get(position);
                image.setImageURI(Uri.parse("res:///"+R.mipmap.app_logo));
                tv_songName.setText(netMusic.getTitle());
                tv_singer.setText(netMusic.getSinger());
                playButton.setSelected(true);
                break;
        }

    }


    public void initUi() {
        tv_songName = (TextView) findViewById(R.id.textView1_song_name);
        tv_singer = (TextView) findViewById(R.id.textView2_singer);
        image = (SimpleDraweeView) findViewById(R.id.imageView1_album);
        listButton = (ImageButton) findViewById(R.id.imageButton1_play_list);
        playButton = (ImageButton) findViewById(R.id.imageButton1_play_pause);
        nextButton = (ImageButton) findViewById(R.id.imageButton2_next);
        listButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        image.setOnClickListener(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeFragment(MessageEvent messageEvent) {
        switch (messageEvent.eventType) {
            //本地音乐
            case LOCAL_MUSIC_EVENT:
                changeLocalFragment();
                break;
            //最近播放
            case LATELY_PLAY_EVENT:
                changeRecentPlayFragment();
                break;
            //下载管理
            case DOWNLOAD_MANAGER_EVENT:
                break;
            //内地榜
            case NDB_EVENT:
                changeNetFragment(1);
                break;
            case GTB_EVENT:
                changeNetFragment(2);
                break;
            case LXB_EVENT:
                changeNetFragment(3);
                break;

        }
    }

    private void changeNetFragment(int type) {
        netMusicFragment = NetMusicFragment.newInstance(type);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fram_layout_main, netMusicFragment).addToBackStack(null);
        fragmentTransaction.hide(indexFragment);
        fragmentTransaction.commit();
    }


    //切换本地音乐Fragment
    private void changeLocalFragment() {
        if (myMusicListFragment == null) {
            myMusicListFragment = MyMusicListFragment.newInstance();

        }
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fram_layout_main, myMusicListFragment).addToBackStack(null);
        fragmentTransaction.hide(indexFragment);
        fragmentTransaction.commit();
    }

    //切换最近播放
    private void changeRecentPlayFragment() {
        if (recentPlayListFragment == null) {
            recentPlayListFragment = RecentPlayListFragment.newInstance();

        }
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fram_layout_main, recentPlayListFragment).addToBackStack(null);
        fragmentTransaction.hide(indexFragment);
        fragmentTransaction.commit();
    }
    //切换内地榜


    /**
     * 弹出当前Fragment
     *
     * @param backEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void popFragment(BackEvent backEvent) {
        switch (backEvent.messageEventType) {
            case LOCAL_MUSIC_EVENT:
                hide();
                break;
            case LATELY_PLAY_EVENT:
                hide();
                break;

        }

    }

    private void hide() {
        fm.popBackStack();
        fm.beginTransaction().show(indexFragment);
    }


    /**
     * 播放歌曲
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void palyMusic(String index) {
        int currentIndex = Integer.parseInt(index);
        playService.play(currentIndex);
        playButton.setSelected(true);
        playService.setDefault_mode(PlayService.LOCAL_MODE);
        type = LOCAL_TYPE;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setMp3Infos(List<Mp3Info> mp3Infos) {
        playService.setMp3Infos(mp3Infos);
        playService.setDefault_mode(PlayService.LOCAL_MODE);
        Log.i(TAG, "setMp3Infos: " + mp3Infos);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void playNetMusic(NetMusic netMusic) {
        List<NetMusic> netMusics = playService.getNetMusics();
        int position = netMusics.size();
        if(!netMusics.contains(netMusic)){
            netMusics.add(netMusic);
            playService.setNetMusics(netMusics);
        }else {
           position = netMusics.indexOf(netMusic);
        }
        Log.i(TAG, "playNetMusic: "+position);
        playService.setDefault_mode(PlayService.NET_MODE);
        Log.i(TAG, "playNetMusic:mode= "+playService.getDefault_mode());
        playService.play(position);
        type = NET_TYPE;
    }


    //显示歌曲列表
    public void showSongList() {

    }

    public void playSong() {
        if (playService.isPlaying()) {
            playButton.setSelected(false);
            playService.pause();
        } else {
            playButton.setSelected(true);
            playService.play();
        }


    }

    public void playNext() {
        Log.i(TAG, "playNext:下一首歌曲");
        playService.next();
        playButton.setSelected(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton1_play_list:
                showSongList();
                break;
            case R.id.imageButton1_play_pause:
                playSong();
                break;
            case R.id.imageButton2_next:
                playNext();
                break;
            case R.id.imageView1_album:
                Intent intent = new Intent(this, PlayActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(playService!=null&&playService.isPlaying()){
            playButton.setSelected(true);
        }else if(playService!=null&&!playService.isPlaying()){
            playButton.setSelected(false);
        }
    }


    @Override
    protected void onDestroy() {
        stopService(new Intent(this,PlayService.class));
        super.onDestroy();
    }
}
