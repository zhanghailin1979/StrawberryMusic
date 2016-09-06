package learning.moliying.com.strawberrymusic.fragments;

import android.os.Bundle;
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

import java.util.List;

import learning.moliying.com.strawberrymusic.App;
import learning.moliying.com.strawberrymusic.R;
import learning.moliying.com.strawberrymusic.adapter.MusicListAdapter;
import learning.moliying.com.strawberrymusic.ui.BaseActivity;
import learning.moliying.com.strawberrymusic.utils.MessageEventType;
import learning.moliying.com.strawberrymusic.vo.BackEvent;
import learning.moliying.com.strawberrymusic.vo.Mp3Info;

/**
 * Created by Administrator on 2016/8/25.
 */
public class RecentPlayListFragment extends BaseFragment {

    public static RecentPlayListFragment newInstance() {
        Bundle args = new Bundle();

        RecentPlayListFragment fragment = new RecentPlayListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Toolbar toolbar;
    private ListView listView;
    private MusicListAdapter adapter;
    private List<Mp3Info> mp3Infos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_music_list_layout,container,false);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        listView = (ListView) view.findViewById(R.id.listView_my_music);
        toolbar.setTitle("最近播放");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        //给返回键设置事件
        AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new BackEvent(MessageEventType.LATELY_PLAY_EVENT));
            }
        });


    }

    @Override
    public void initData() {
//        mp3Infos =
        try {
            mp3Infos = App.dbManager.selector(Mp3Info.class).orderBy("playTime",true).findAll();
            if(mp3Infos!=null){
                adapter = new MusicListAdapter(mp3Infos,getActivity());
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        EventBus.getDefault().post(String.valueOf(position));
                    }
                });
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

    }
}
