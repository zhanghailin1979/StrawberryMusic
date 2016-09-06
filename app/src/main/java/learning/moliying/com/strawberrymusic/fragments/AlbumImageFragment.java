package learning.moliying.com.strawberrymusic.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.siyamed.shapeimageview.CircularImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import learning.moliying.com.strawberrymusic.R;
import learning.moliying.com.strawberrymusic.service.PlayService;
import learning.moliying.com.strawberrymusic.ui.PlayActivity;
import learning.moliying.com.strawberrymusic.utils.MediaUtils;
import learning.moliying.com.strawberrymusic.vo.Mp3Info;
import learning.moliying.com.strawberrymusic.vo.NetMusic;

/**
 * Created by Administrator on 2016/8/26.
 */
public class AlbumImageFragment extends BaseFragment {

    private static final String TAG = "AlbumImageFragment";
    private List<Mp3Info> mp3InfoList;
    public static AlbumImageFragment newInstance() {

        Bundle args = new Bundle();

        AlbumImageFragment fragment = new AlbumImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private CircularImageView simpleImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_image, container, false);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        simpleImage = (CircularImageView) view.findViewById(R.id.imageView1_album);
    }

    @Override
    public void initData() {
        PlayService playService = ((PlayActivity) getActivity()).getService();
        if (playService == null) return;
        mp3InfoList = playService.getMp3Infos();
        int defaultMode = playService.getDefault_mode();
        int currentPosition = playService.currentPosition;
        switch (defaultMode) {
            case PlayService.LOCAL_MODE:
                Mp3Info currentMp3Info = playService.getMp3Infos().get(currentPosition);
                Bitmap bitmap = MediaUtils.getArtwork(getActivity(), currentMp3Info.getMp3InfoId(), currentMp3Info.getAlbumId(), true, false);
                Log.i(TAG, "initData: "+bitmap);
                simpleImage.setImageBitmap(bitmap);
                setAnimator();
                break;
            case PlayService.NET_MODE:
                NetMusic netMusic = playService.getNetMusics().get(currentPosition);
                simpleImage.setImageResource(R.mipmap.default_album_playing);
                setAnimator();
                break;
        }


    }

    private void setAnimator() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(simpleImage, "rotationx", 0f, 360f).
                setDuration(2000);
        animator.setRepeatCount(3000);
        animator.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setNetmusic(NetMusic netmusic){
        simpleImage.setImageResource(R.mipmap.default_album_playing);
        setAnimator();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setLocalmusic(int index){
        Mp3Info mp3Info =mp3InfoList.get(index);
        Bitmap bitmap = MediaUtils.getArtwork(getActivity(), mp3Info.getMp3InfoId(), mp3Info.getAlbumId(), true, false);
        Log.i(TAG, "initData: "+bitmap);
        simpleImage.setImageBitmap(bitmap);
        setAnimator();
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }
}
