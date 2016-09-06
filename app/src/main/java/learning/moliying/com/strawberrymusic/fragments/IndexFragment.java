package learning.moliying.com.strawberrymusic.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;

import learning.moliying.com.strawberrymusic.App;
import learning.moliying.com.strawberrymusic.R;
import learning.moliying.com.strawberrymusic.adapter.CommonFragmentAdapter;
import learning.moliying.com.strawberrymusic.utils.AppUtils;

/**
 * Created by Administrator on 2016/8/23.
 */
public class IndexFragment extends BaseFragment implements View.OnClickListener {
    private static final int REQUEST_CODE = 0x1;
    private static final String TAG = "IndexFragment";
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ViewPager viewpager;
    private TabLayout tab;

    private CommonFragmentAdapter adapter;


    /**
     * 设置头像
     *
     * @return
     */
    private SimpleDraweeView user_face;

    private ImageView modifyProfileButton;
    private TextView usernameText;
    private EditText usernameEdit;
    private boolean isChoose = false;
    public static IndexFragment newInstance() {

        Bundle args = new Bundle();

        IndexFragment fragment = new IndexFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        tab = (TabLayout) view.findViewById(R.id.tab);
        adapter = new CommonFragmentAdapter(getActivity().getSupportFragmentManager(),
                new String[]{"我的音乐", "音乐库"},
                new Fragment[]{LocalMusicFragment.newInstance(), NetMusicLibsFragment.newInstance()});

        viewpager.setAdapter(adapter);
        tab.setupWithViewPager(viewpager);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        navigationView = (NavigationView) view.findViewById(R.id.navigationView);
        drawer = (DrawerLayout) view.findViewById(R.id.drawer);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View contentView = drawer.getChildAt(0);
                View leftView = drawerView;

                float scale = 1 - slideOffset; // 1~0
                float leftScale = (float) (1 - scale * 0.5); // 0.5~1
                float contentScale = (float) (0.5 * scale + 0.5); // 1~0.5

                //content view1~0.5
//                contentView.setScaleX(contentScale);
//                contentView.setScaleX(scale);
//                contentView.setScaleY(contentScale);
                //left view从0.5-1
//                leftView.setScaleX(leftScale);
                leftView.setScaleX(1);

//                leftView.setScaleY(leftScale);

                // content view 平移
                contentView.setTranslationX(leftView.getMeasuredWidth() * slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        //设置开关
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View v = navigationView.getHeaderView(0);
        user_face = (SimpleDraweeView) v.findViewById(R.id.user_face);
        user_face.setOnClickListener(this);

        //给侧滑菜单设置点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    //我的收藏
                    case R.id.menu_item_fav:
                        showToast("我的收藏");
                        break;
                    //播放列表
                    case R.id.menu_item_playinglist:
                        showToast("播放列表");
                        break;
                    //最近播放
                    case R.id.menu_item_listmore:
                        showToast("最近播放");
                        break;
                    //退出
                    case R.id.loginout:
                        exit();
                        showToast("退出");
                        break;
                }
                return true;
            }
        });
        usernameText = (TextView) v.findViewById(R.id.usernameText);
        usernameEdit = (EditText) v.findViewById(R.id.usernameEdit);
        modifyProfileButton = (ImageView) v.findViewById(R.id.modifyProfileButton);
        modifyProfileButton.setOnClickListener(this);
    }


    //退出
    private void exit() {
        int num = App.activities.size();
        for(int i=num-1;i>=0;i--){
            App.activities.get(i).finish();
        }
    }



    @Override
    public void initData() {
//        mTouchSlop = ViewConfiguration.get(getActivity()).getScaledTouchSlop();
        File file = new File(path);
        if(file.exists()){
            user_face.setImageURI(Uri.fromFile(file));
        }
        String username = App.sp.getString("username","admin");
        usernameText.setText(username);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 换头像
             */
            case R.id.user_face:
                seletcUserImage();
                break;

            //修改个人信息
            case R.id.modifyProfileButton:
                updateProfile(modifyProfileButton);
                break;


        }
    }

    /**
     * 换头像方法
     */
    private void seletcUserImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", false);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempImage));
//        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_CODE);
    }


   private String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/caomei.jpg";
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(path)));
                user_face.setImageURI(Uri.fromFile(new File(path)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


    }


    private void showToast(String info) {
        Snackbar.make(drawer,info,Snackbar.LENGTH_SHORT).show();
    }


    //修改个人信息
    public void updateProfile(ImageView image){
        if(!isChoose){
           usernameEdit.setVisibility(View.VISIBLE);
            usernameText.setVisibility(View.GONE);
            String info = usernameText.getText().toString();
            usernameText.setText(info);
            image.setSelected(true);
            isChoose = true;

        }else{
            usernameEdit.setVisibility(View.GONE);
            usernameText.setVisibility(View.VISIBLE);
            AppUtils.hideInputMethod(usernameEdit);
            String info = usernameEdit.getText().toString();
            usernameText.setText(info);
            image.setSelected(false);
            isChoose = false;
            SharedPreferences.Editor edit = App.sp.edit();
            edit.putString("username",info);
            edit.commit();
            Snackbar.make(image,"修改成功",Snackbar.LENGTH_SHORT).show();
        }

    }









}

