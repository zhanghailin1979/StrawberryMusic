package learning.moliying.com.strawberrymusic.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import douzi.android.view.DefaultLrcBuilder;
import douzi.android.view.ILrcBuilder;
import douzi.android.view.ILrcView;
import douzi.android.view.LrcRow;
import douzi.android.view.LrcView;
import learning.moliying.com.strawberrymusic.R;
import learning.moliying.com.strawberrymusic.utils.Constant;
import learning.moliying.com.strawberrymusic.utils.DownloadUtils;

/**
 * Created by Administrator on 2016/8/26.
 */
public class SongWordFragment extends BaseFragment {

    private static final String TAG = "SongWordFragment";

    public static SongWordFragment newInstance() {

        Bundle args = new Bundle();

        SongWordFragment fragment = new SongWordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private LrcView lrcView;
    private String songName,artist;
    public void setSongNameAndArtist(String songName,String artist) {
        this.songName = songName;
        this.artist = artist;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_words,container,false);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        lrcView = (LrcView) view.findViewById(R.id.lrcView);
        lrcView.setListener(new ILrcView.LrcViewListener() {
            @Override
            public void onLrcSeeked(int newPosition, LrcRow row) {

            }
        });
        lrcView.setLoadingTipText("正在加载歌词...");
    }

    @Override
    public void initData() {

    }


    public void changeWords(String songName,String artist){
        this.songName = songName;
        this.artist = artist;
        load();
    }



    @Override
    public void onResume() {
        super.onResume();
        if(songName==null&&artist==null){
            load();
        }
    }




    //调用加载歌词方法
    private void load() {
        File lrcDirFile = new File(Environment.getExternalStorageDirectory()+ Constant.DIR_LRC+"/"+songName+"-"+artist+".lrc");
        Log.i(TAG, "load: "+lrcDirFile.getPath());

        if(lrcDirFile.exists()){
            loadLrc(lrcDirFile.getPath());
        }else{
            DownloadUtils.getInstance().downloadLRC(getContext(),songName, artist)
                    .setListener(new DownloadUtils.OnDownloadListener() {
                        @Override
                        public void onDownload(String path) {
                            loadLrc(path);
                        }

                        @Override
                        public void onFailed(String error) {
                            lrcView.setLrc(null);
                            Snackbar.make(lrcView,error,Snackbar.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    //本地加载Lrc歌词
    private void loadLrc(String path) {
        if(path!=null){
            StringBuffer sb = new StringBuffer();
            char[] chars = new char[1024];
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
                int len= -1;
                while ((len=br.read(chars))!=-1){
                    sb.append(new String(chars,0,chars.length));
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ILrcBuilder builder = new DefaultLrcBuilder();

            List<LrcRow> rows = builder.getLrcRows(sb.toString());
            System.out.println(Arrays.toString(rows.toArray()));
            if(lrcView!=null) lrcView.setLrc(rows);
        }


    }


    //设置同步播放的时间进度
    public void seekLrcToTime(long time){
        lrcView.seekLrcToTime(time);
    }
    //设置提示内容
    public void tipText(String text){
        lrcView.setLoadingTipText(text);
    }

}
