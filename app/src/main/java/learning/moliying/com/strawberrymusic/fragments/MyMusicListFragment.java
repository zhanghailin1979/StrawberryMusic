package learning.moliying.com.strawberrymusic.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;

import java.util.List;

import learning.moliying.com.strawberrymusic.R;
import learning.moliying.com.strawberrymusic.adapter.MusicListAdapter;
import learning.moliying.com.strawberrymusic.utils.MediaUtils;
import learning.moliying.com.strawberrymusic.utils.MessageEventType;
import learning.moliying.com.strawberrymusic.vo.BackEvent;
import learning.moliying.com.strawberrymusic.vo.Mp3Info;

/**
 * Created by Administrator on 2016/8/24.
 */

public class MyMusicListFragment extends BaseFragment  {

    private static final String TAG = "MyMusicListFragment";

    public static MyMusicListFragment newInstance() {

        Bundle args = new Bundle();

        MyMusicListFragment fragment = new MyMusicListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Toolbar toolbar;
    private ListView listView;
    private MusicListAdapter adapter;
    private List<Mp3Info> mp3Infos;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_music_list_layout,container,false);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        listView = (ListView) view.findViewById(R.id.listView_my_music);

        toolbar.setTitle("我的音乐");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        //给返回键设置事件
        AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new BackEvent(MessageEventType.LOCAL_MUSIC_EVENT));
            }
        });

    }

    @Override
    public void initData() {

            mp3Infos = MediaUtils.getMp3Infos(getActivity());
            EventBus.getDefault().post(mp3Infos);
             //更新本地音乐
            if(mp3Infos==null){
                toast("本地暂无歌曲");
                return;
            }
            adapter = new MusicListAdapter(mp3Infos,getActivity());
            listView.setAdapter(adapter);
            Log.i(TAG, "initData: "+listView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    EventBus.getDefault().post(String.valueOf(position));
                    Log.i(TAG, "onItemClick: "+position);

                }
            });



    }









    private void toast(String info) {
        Snackbar.make(listView,info,Snackbar.LENGTH_SHORT).show();
    }



}
