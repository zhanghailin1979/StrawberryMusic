package learning.moliying.com.strawberrymusic.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;

import java.util.List;

import learning.moliying.com.strawberrymusic.App;
import learning.moliying.com.strawberrymusic.R;
import learning.moliying.com.strawberrymusic.adapter.CommonFragmentAdapter;
import learning.moliying.com.strawberrymusic.fragments.AlbumImageFragment;
import learning.moliying.com.strawberrymusic.fragments.SongWordFragment;
import learning.moliying.com.strawberrymusic.service.PlayService;
import learning.moliying.com.strawberrymusic.utils.MediaUtils;
import learning.moliying.com.strawberrymusic.utils.PlayMode;
import learning.moliying.com.strawberrymusic.vo.Mp3Info;
import learning.moliying.com.strawberrymusic.vo.NetMusic;

public class PlayActivity extends BaseActivity implements View.OnClickListener {
    private static final int ZREO = 0X0;
    private static final String TAG = "PlayActivity";
    private TextView textView1_title;   //歌名
    private TextView textView1_songer;   //歌者

    private TextView textView1_start_time;   //起始时间
    private TextView textView1_end_time;   //歌曲总时间

    private SimpleDraweeView imageView1_album;  //海报
    private SeekBar seekBar;    //进度条
    private int trackProgress;

    private ImageView imageView_play_mode;      //播放模式
    private ImageView imageView_previous;      //上一首
    private ImageView imageView_play_pause;      //播放暂停按钮
    private ImageView imageView_next;      //下一首
    private ImageView imageView_play_list;      //播放清单

    private ViewPager viewPager;        //
    private FragmentManager fm;
    private CommonFragmentAdapter adapter;
    private AlbumImageFragment albumImageFragment;
    private SongWordFragment songWordFragment;
    private TabLayout play_tab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initView();
        initData();
    }


    //初始化组件
    private void initView() {
        textView1_title = (TextView) findViewById(R.id.textView1_title);
        textView1_songer = (TextView) findViewById(R.id.textView1_songer);
        textView1_start_time = (TextView) findViewById(R.id.textView1_start_time);
        textView1_end_time = (TextView) findViewById(R.id.textView1_end_time);
        imageView1_album = (SimpleDraweeView) findViewById(R.id.imageView1_album);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);

        imageView_play_mode = (ImageView) findViewById(R.id.imageView_play_mode);
        imageView_previous = (ImageView) findViewById(R.id.imageView_previous);
        imageView_play_pause = (ImageView) findViewById(R.id.imageView_play_pause);
        imageView_next = (ImageView) findViewById(R.id.imageView_next);
        imageView_play_list = (ImageView) findViewById(R.id.imageView_play_list);
        imageView_play_mode.setOnClickListener(this);
        imageView_previous.setOnClickListener(this);
        imageView_play_pause.setOnClickListener(this);
        imageView_next.setOnClickListener(this);
        imageView_play_list.setOnClickListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager_Play);
        play_tab = (TabLayout) findViewById(R.id.play_tab);
    }

    //初始化数据
    private void initData() {
        fm = getSupportFragmentManager();
        if(albumImageFragment==null) albumImageFragment = AlbumImageFragment.newInstance();
        if(songWordFragment==null) songWordFragment = SongWordFragment.newInstance();
        adapter = new CommonFragmentAdapter(fm,new String[]{"封面","歌词"},new Fragment[]{albumImageFragment,songWordFragment});
        viewPager.setAdapter(adapter);
        play_tab.setupWithViewPager(viewPager);
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ZREO:
                    long time = msg.arg1;
                    textView1_start_time.setText(MediaUtils.formatTime(time));
                    songWordFragment.seekLrcToTime(time);
                    songWordFragment.tipText("---- 暂无歌词 ----");
                    break;
            }
        }
    };
    @Override
    public void publish(int progress) {
        seekBar.setProgress(progress);
        Message message = handler.obtainMessage();
        message.what = ZREO;
        message.arg1 = progress;
        handler.sendMessage(message);
    }

    @Override
    public void change(int position) {
        int default_mode = playService.getDefault_mode();
        switch (default_mode){
            case PlayService.LOCAL_MODE:
                List<Mp3Info> mp3Infos = playService.getMp3Infos();
                Mp3Info mp3Info = mp3Infos.get(position);
                textView1_title.setText(mp3Info.getTitle());
                textView1_songer.setText(mp3Info.getArtist());
                textView1_end_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
                seekBar.setMax((int) mp3Info.getDuration());
                songWordFragment.changeWords(mp3Info.getTitle(),mp3Info.getArtist());
                break;
            case PlayService.NET_MODE:
                List<NetMusic> netMusics = playService.getNetMusics();
                NetMusic netMusic = netMusics.get(position);
                textView1_title.setText(netMusic.getTitle());
                textView1_songer.setText(netMusic.getSinger());
                textView1_end_time.setText(MediaUtils.formatTime(playService.mediaPlayer.getDuration()));
                seekBar.setMax(playService.mediaPlayer.getDuration());
                songWordFragment.changeWords(netMusic.getTitle(),netMusic.getSinger());
                break;
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "onProgressChanged: "+progress);
                trackProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playService.setProgress(trackProgress);
            }
        });


    }


    public Mp3Info getCurrentMp3Info(){
        if(playService!=null){
            int currentPosition =playService.currentPosition;
            return  playService.getMp3Infos().get(currentPosition);
        }
       return null;
    }

    public PlayService getService(){
        return playService;
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateMp3Info(Mp3Info mp3Info){

    }







    /**
     * 返回按钮
     */
    public void backMainClick(View v){
        startActivity(new Intent(this,MainActivity.class));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView_play_mode:
                changePlayMode();
                break;
            case R.id.imageView_previous:
                startPrevious();
                break;
            case R.id.imageView_play_pause:
                pause();
                break;
            case R.id.imageView_next:
                startNext();
                break;
            case R.id.imageView_play_list:
                break;


        }
    }

    //下一首
    private void startNext() {
        playService.next();
    }

    //暂停播放
    private void pause() {
        if(playService.isPlaying()){
            imageView_play_pause.setImageResource(R.drawable.button_play_bg);
            playService.pause();
        }else{
            imageView_play_pause.setImageResource(R.drawable.button_pause_bg);
            playService.play();
        }
    }

    //上一首
    private void startPrevious() {
        playService.prev();
    }

    //切换播放模式
    private void changePlayMode() {
        PlayMode playMode = playService.getPlayMode();
        switch (playMode){
            case ORDER_PLAY:
                playService.setPlayMode(PlayMode.RANDOM_PLAY);
                imageView_play_mode.setImageResource(R.drawable.button_playmode_random_bg);
                break;
            case RANDOM_PLAY:
                playService.setPlayMode(PlayMode.SINGLE_PLAY);
                imageView_play_mode.setImageResource(R.drawable.button_playmode_loop_bg);
                break;
            case SINGLE_PLAY:
                playService.setPlayMode(PlayMode.ORDER_PLAY);
                imageView_play_mode.setImageResource(R.drawable.button_playmode_order_bg);
                break;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playService!=null&&playService.isPlaying()){
            imageView_play_pause.setImageResource(R.drawable.button_pause_bg);
            seekBar.setProgress(playService.getProgress());
        }else if(playService!=null&&!playService.isPlaying()){
            imageView_play_pause.setImageResource(R.drawable.button_play_bg);
            seekBar.setProgress(playService.getProgress());
        }
    }
}
