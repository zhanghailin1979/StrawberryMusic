package learning.moliying.com.strawberrymusic.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import learning.moliying.com.strawberrymusic.R;
import learning.moliying.com.strawberrymusic.adapter.NetListAdapter;
import learning.moliying.com.strawberrymusic.utils.HttpUtils;
import learning.moliying.com.strawberrymusic.utils.MessageEventType;
import learning.moliying.com.strawberrymusic.vo.BackEvent;
import learning.moliying.com.strawberrymusic.vo.NetMusic;

/**
 * Created by Administrator on 2016/8/27.
 */
public class NetMusicFragment extends BaseFragment {

    private static final int NDB_TYPE = 0x1;
    private static final int GTB_TYPE = 0x2;
    private static final int LXB_TYPE = 0x3;


    private static final String TAG = "NetMusicFragment";
    private static final int SUCCESS = 0X1;
    private static final int FAILED = 0X2;

    OkHttpClient mOkHttpClient = new OkHttpClient();

    public static NetMusicFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        NetMusicFragment fragment = new NetMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private Toolbar toolbar;
    private LinearLayout linearLayout;
    private ImageView image_net;
    private TextView title_item_net;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView_net;
    private ProgressBar progressBarNet;

    private List<NetMusic> netMusics;
    private NetListAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_net_music, container, false);
        Bundle arguments = getArguments();
        int type = arguments.getInt("type");
        initView(view);
        initType(type);
        return view;
    }

    @Override
    public void initView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbarNet);
        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout_net);
        image_net = (ImageView) view.findViewById(R.id.image_net);
        title_item_net = (TextView) view.findViewById(R.id.title_item_net);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        listView_net = (ListView) view.findViewById(R.id.listView_net);
        progressBarNet = (ProgressBar) view.findViewById(R.id.progressBarNet);
    }

    private void initType(int type) {
        switch (type) {
            case NDB_TYPE:
                initNDBData();
                break;
            case GTB_TYPE:
                initGTBData();
                break;
            case LXB_TYPE:
                initLXBData();
                break;
        }
    }


    private void initNDBData() {
        toolbar.setBackgroundResource(R.mipmap.ic_classify_img03);
        setBackEvent(toolbar);
        linearLayout.setBackgroundResource(R.mipmap.ic_classify_img03);
        image_net.setImageResource(R.drawable.img_k_chinese);
        title_item_net.setText("内地榜");
        new HttpUtils().getNetMusic(getActivity(), NDB_TYPE, new HttpUtils.GetNetMusicListener() {
            @Override
            public void onSuccess(ArrayList<NetMusic> data) {
                netMusics = data;
                handler.sendEmptyMessage(SUCCESS);

            }

            @Override
            public void onFail(String info) {
                handler.sendEmptyMessage(FAILED);
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    progressBarNet.setVisibility(View.GONE);
                    adapter = new NetListAdapter(netMusics,getActivity());
                    listView_net.setAdapter(adapter);
                    listView_net.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final NetMusic netMusic = netMusics.get(position);
                            final String path = netMusic.getTitle()+"-"+netMusic.getSinger();
                            String ss =getActivity().getExternalCacheDir()+ File.separator+"NetMusic"+File.separator+path+".mp3";
                            File file = new File(ss);
                            if(!file.exists()){
                                String url = netMusic.getUrl();
                                Request request = new Request.Builder().url(url).build();
                                Call call = mOkHttpClient.newCall(request);
                                call.enqueue(new com.squareup.okhttp.Callback() {
                                    @Override
                                    public void onFailure(Request request, IOException e) {
                                        handler.sendEmptyMessage(FAILED);
                                    }
                                    @Override
                                    public void onResponse(Response response) throws IOException {
                                        String ss = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+path+".mp3";
                                        File file  = new File(ss);
//                                        if(!file.exists()){
//                                            Log.i(TAG, "onResponse: "+file.getPath());
//                                            file.createNewFile();
//                                            Log.i(TAG, "onResponse: "+file.getPath());
//                                            BufferedInputStream bis = new BufferedInputStream(response.body().byteStream());
////                                            String p =getActivity().getExternalCacheDir()+ File.separator+path+".mp3";
////                                            Log.i(TAG, "onResponse: "+p);
//                                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
//                                            byte[] bytes = new byte[1024];
//                                            int len = -1;
//                                            while ((len=bis.read(bytes))!=-1){
//                                                bos.write(bytes,0,len);
//                                            }
//                                            bos.close();
//                                            bis.close();
//                                        }
                                        EventBus.getDefault().post(netMusic);
                                    }
                                });
                            }else{
                                EventBus.getDefault().post(netMusic);
                            }

                        }
                    });
                    break;
                case FAILED:
                    progressBarNet.setVisibility(View.GONE);
                    Snackbar.make(listView_net,"网络音乐加载失败",Snackbar.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private void initGTBData() {
        toolbar.setBackgroundResource(R.mipmap.ic_classify_img01);
        setBackEvent(toolbar);
        linearLayout.setBackgroundResource(R.mipmap.ic_classify_img01);
        image_net.setImageResource(R.drawable.img_k_man);
        title_item_net.setText("港台榜");
        new HttpUtils().getNetMusic(getActivity(), GTB_TYPE, new HttpUtils.GetNetMusicListener() {
            @Override
            public void onSuccess(ArrayList<NetMusic> data) {
                netMusics = data;
                handler.sendEmptyMessage(SUCCESS);

            }

            @Override
            public void onFail(String info) {
                handler.sendEmptyMessage(FAILED);
            }
        });
    }

    private void initLXBData() {
        toolbar.setBackgroundResource(R.mipmap.ic_classify_img02);
        setBackEvent(toolbar);
        linearLayout.setBackgroundResource(R.mipmap.ic_classify_img02);
        image_net.setImageResource(R.drawable.img_k_band);
        title_item_net.setText("流行榜");
        new HttpUtils().getNetMusic(getActivity(), LXB_TYPE, new HttpUtils.GetNetMusicListener() {
            @Override
            public void onSuccess(ArrayList<NetMusic> data) {
                netMusics = data;
                handler.sendEmptyMessage(SUCCESS);

            }

            @Override
            public void onFail(String info) {
                handler.sendEmptyMessage(FAILED);
            }
        });
    }

    //给Toolbar设置返回事件
    private void setBackEvent(Toolbar toolbar) {
        toolbar.setTitle("");
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

    }
}
