package learning.moliying.com.strawberrymusic.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.youth.banner.Banner;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import learning.moliying.com.strawberrymusic.R;
import learning.moliying.com.strawberrymusic.utils.HttpUtils;
import learning.moliying.com.strawberrymusic.utils.MessageEventType;
import learning.moliying.com.strawberrymusic.vo.MessageEvent;

/**
 * Created by Administrator on 2016/8/23.
 */
public class NetMusicLibsFragment extends BaseFragment implements View.OnClickListener{


    private static final String TAG = "NetMusicLibsFragment";
    private static final int SUCCESS = 0x1;
    private static final int FAILED = 0x2;

    public static NetMusicLibsFragment newInstance() {

        Bundle args = new Bundle();

        NetMusicLibsFragment fragment = new NetMusicLibsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private Banner banner;
    private ProgressBar progressBar;

    private LinearLayout linearLayout_ndb;
    private LinearLayout linearLayout_gtb;
    private LinearLayout linearLayout_lxb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_libs, container, false);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        banner = (Banner) view.findViewById(R.id.banner);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        linearLayout_ndb = (LinearLayout) view.findViewById(R.id.linearLayout_ndb);
        linearLayout_gtb = (LinearLayout) view.findViewById(R.id.linearLayout_gtb);
        linearLayout_lxb = (LinearLayout) view.findViewById(R.id.linearLayout_lxb);
        linearLayout_ndb.setOnClickListener(this);
        linearLayout_gtb.setOnClickListener(this);
        linearLayout_lxb.setOnClickListener(this);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    ArrayList<String> imageUrls = (ArrayList<String>) msg.obj;
                    banner.setImages(imageUrls);
                    progressBar.setVisibility(View.GONE);
                    break;
                case FAILED:
                    Snackbar.make(banner,"图片加载失败",Snackbar.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    break;
            }

        }
    };


    @Override
    public void initData() {
        new HttpUtils().getImagesList(getActivity(), new HttpUtils.GetNetImageListener() {
            @Override
            public void onSuccess(ArrayList<String> imageUrls) {
                Message message = handler.obtainMessage();
                message.what =SUCCESS;
                message.obj = imageUrls;
                handler.sendMessage(message);
            }
            @Override
            public void onFail(String error) {
                Snackbar.make(banner,"图片加载失败",Snackbar.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linearLayout_ndb:
                EventBus.getDefault().post(new MessageEvent(MessageEventType.NDB_EVENT));
                break;
            case R.id.linearLayout_gtb:
                EventBus.getDefault().post(new MessageEvent(MessageEventType.GTB_EVENT));
                break;
            case R.id.linearLayout_lxb:
                EventBus.getDefault().post(new MessageEvent(MessageEventType.LXB_EVENT));
                break;
        }
    }
}
