package learning.moliying.com.strawberrymusic.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.List;

import learning.moliying.com.strawberrymusic.App;
import learning.moliying.com.strawberrymusic.R;
import learning.moliying.com.strawberrymusic.utils.MediaUtils;
import learning.moliying.com.strawberrymusic.utils.MessageEventType;
import learning.moliying.com.strawberrymusic.view.UserDefineScrollView;
import learning.moliying.com.strawberrymusic.vo.MessageEvent;
import learning.moliying.com.strawberrymusic.vo.Mp3Info;

/**
 * Created by Administrator on 2016/8/23.
 */
public class LocalMusicFragment extends BaseFragment implements View.OnTouchListener,View.OnClickListener {


    private static final String TAG = "LocalMusicFragment";

    public static LocalMusicFragment newInstance() {

        Bundle args = new Bundle();

        LocalMusicFragment fragment = new LocalMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private UserDefineScrollView userScrollView;
    private LinearLayout linearLayout_local_music;  //我的音乐
    private LinearLayout linearLayout_lately_play;  //最近播放
    private LinearLayout linearLayout_download_manager;  //下载管理
    private RelativeLayout relativeLayout_like;  //我的歌单
    private LinearLayout linearLayout_new_songsheet;  //新建歌单

    private TextView textView_local_music_count;    //本地音乐数量
    private TextView textView_recent_play;          //最近播放


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, container, false);
        initView(view);
        initData();


        return view;
    }




    @Override
    public void initView(View view) {
        userScrollView = (UserDefineScrollView) view.findViewById(R.id.userScrollView);
        userScrollView.setOnTouchListener(this);
        //查找相应组件
        linearLayout_local_music = (LinearLayout) view.findViewById(R.id.linearLayout_local_music);
        linearLayout_lately_play = (LinearLayout) view.findViewById(R.id.linearLayout_lately_play);
        linearLayout_download_manager = (LinearLayout) view.findViewById(R.id.linearLayout_download_manager);
        relativeLayout_like = (RelativeLayout) view.findViewById(R.id.relativeLayout_like);
        linearLayout_new_songsheet = (LinearLayout) view.findViewById(R.id.linearLayout_new_songsheet);
        //注册点击事件
        linearLayout_local_music.setOnClickListener(this);
        linearLayout_lately_play.setOnClickListener(this);
        linearLayout_download_manager.setOnClickListener(this);
        relativeLayout_like.setOnClickListener(this);
        linearLayout_new_songsheet.setOnClickListener(this);


        //本地音乐歌曲Text（数量）
        textView_local_music_count = (TextView) view.findViewById(R.id.textView_local_music_count);
        textView_recent_play = (TextView) view.findViewById(R.id.textView_recent_play);
    }

    private static int recentPlay=0;
    @Override
    public void initData() {
        int count = MediaUtils.getMp3Count(getContext());
        textView_local_music_count.setText(count+"首");
        try {
            List<Mp3Info> all = App.dbManager.selector(Mp3Info.class).findAll();
            if(all!=null){
                recentPlay = all.size();
                textView_recent_play.setText(recentPlay+"首");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linearLayout_local_music:
                entryLocalMusic();
                break;
            case R.id.linearLayout_lately_play:
                entryLatelyPlayMusic();
                break;
            case R.id.linearLayout_download_manager:
                entryDownloadMusic();
                break;
            case R.id.relativeLayout_like:
                break;
            case R.id.linearLayout_new_songsheet:
                break;
        }

    }



    //进入本地音乐
    private void entryLocalMusic() {
        EventBus.getDefault().post(new MessageEvent(MessageEventType.LOCAL_MUSIC_EVENT));
    }
    //进入最近播放
    private void entryLatelyPlayMusic() {
        EventBus.getDefault().post(new MessageEvent(MessageEventType.LATELY_PLAY_EVENT));
    }
    //进入下载管理
    private void entryDownloadMusic() {
        EventBus.getDefault().post(new MessageEvent(MessageEventType.DOWNLOAD_MANAGER_EVENT));
    }




    boolean isShow=true;
    Animator animator1;
    Animator animator2;

    int mTouchSlop;
    float down=0;
    float move=0;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                down = event.getY();
//                Log.i(TAG, "onTouchEvent:down "+down);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                move =event.getY();
//                if(move-down>mTouchSlop){
//                    show(false);
//                    Log.i(TAG, "onTouchEvent:move "+false);
//                }else if(down-move>mTouchSlop){
//                    show(true);
//                    Log.i(TAG, "onTouchEvent:move "+true);
//                }
//                break;
//
//        }
        return false;
    }

    //显示toolbar
    private void show(boolean flag) {
//        Log.i(TAG, "show: "+flag);
//        if(animator1!=null&&animator1.isRunning()){
//            animator1.cancel();
//        }
//        if(flag){
//            animator1 = ObjectAnimator.ofFloat(userScrollView,"translationY",userScrollView.getTranslationY(),0);
////            animator2 =  ObjectAnimator.ofFloat(tab,"translationY",toolbar.getTranslationY(),-toolbar.getHeight());
//        }else{
//            animator1  = ObjectAnimator.ofFloat(userScrollView,"translationY",userScrollView.getTranslationY(),-userScrollView.getHeight());
////            animator2 = ObjectAnimator.ofFloat(tab,"translationY",toolbar.getTranslationY(),0);
//        }
//
//        animator1.start();
//        animator2.start();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveLocalMusicList(Mp3Info mp3Info){
        try {
            Mp3Info m = App.dbManager.selector(Mp3Info.class).where("mp3InfoId","=",mp3Info.getMp3InfoId()).findFirst();
            mp3Info.setPlayTime(System.currentTimeMillis());
            if(m==null){
                App.dbManager.save(mp3Info);   //保存到数据库
                recentPlay+=1;
                textView_recent_play.setText(recentPlay+"首");
            }else{
                m.setPlayTime(System.currentTimeMillis());
                App.dbManager.update(m,"playTime");
            }


        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveNetMusicList(Mp3Info mp3Info){

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);       //绑定EventBus
        Log.i(TAG, "onAttach: ");
    }


    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);     //解绑
        Log.i(TAG, "onDetach: ");
        super.onDetach();
    }
}
