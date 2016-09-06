package learning.moliying.com.strawberrymusic.utils;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.view.annotation.Event;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import learning.moliying.com.strawberrymusic.vo.NetMusic;

/**
 * Created by Administrator on 2016/8/23.
 */
public class HttpUtils {
    private static final String MP3_ROOT_URL = "http://mp3.sogou.com/";
    private static final int GET_BANNER_SUCCESS = 0x1;
    private static final int GET_BANNER_ERROR = 0x2;
    private static final int GET_NET_SUCCESS = 0x3;
    private static final int GET_NET_ERROR = 0x4;

    public static final int TYPE_NDB = 0x1;
    public static final int TYPE_GTB = 0x2;
    public static final int TYPE_LXB = 0x3;

    private static final String TAG = "JsoupUtils";

    private GetNetImageListener getNetImageListener;
    private GetNetMusicListener getNetMusicListener;
    OkHttpClient mOkHttpClient = new OkHttpClient();

    private static ExecutorService es = Executors.newCachedThreadPool();


    public static interface GetNetMusicListener{
        public void onSuccess(ArrayList<NetMusic> data);
        public void onFail(String info);
    }
    public static interface GetNetImageListener{
        public void onSuccess(ArrayList<String> imageUrls);
        public void onFail(String error);
    }

    //查询网络音乐
    public void getNetMusic(Context context,final int type, GetNetMusicListener listener){
        if(!AppUtils.isNetworkConnected(context)){
            getNetImageListener.onFail("当前网络状态不可用");
        }
        this.getNetMusicListener = listener;

        es.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(MP3_ROOT_URL).
                            userAgent(Constant.USER_AGENT).timeout(100*1000).get();
                    String data="";
                    ArrayList<NetMusic> netMusics;
                    switch (type){
                        case TYPE_NDB:
                            data =doc.select("a[uigs=out_home_hot_list_china_playall]").attr("onclick").trim();
                            data = data.substring(data.indexOf("'") + 1, data.lastIndexOf("'"));
                            data = data.replaceAll("\\#", "\"");
                            netMusics =parse(data);
                            getNetMusicListener.onSuccess(netMusics);
                            break;
                        case TYPE_GTB:
                            data =doc.select("a[uigs=out_home_hot_list_jp_playall]").attr("onclick");
                            data = data.substring(data.indexOf("'") + 1, data.lastIndexOf("'"));
                            data = data.replaceAll("\\#", "\"");
                            netMusics =parse(data);
                            getNetMusicListener.onSuccess(netMusics);
                            break;
                        case TYPE_LXB:
                            data =doc.select("a[uigs=out_home_hot_list_pop_playall]").attr("onclick");
                            data = data.substring(data.indexOf("'") + 1, data.lastIndexOf("'"));
                            data = data.replaceAll("\\#", "\"");
                             netMusics =parse(data);
                            getNetMusicListener.onSuccess(netMusics);
                            break;
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private ArrayList<NetMusic> parse(String data) {
        ArrayList<NetMusic> list = new ArrayList<>();
        String[] split = data.split("\"");
        int length = split.length;
        ArrayList<String> lists = new ArrayList<>();
        StringBuilder buider=null;
        for(int i=0;i<length;i++){
            if(split[i].contains("[")||split[i].contains("]")){
                if(buider!=null){
                   lists.add(buider.toString()) ;
                    buider=null;
                }

                buider = new StringBuilder();
            }
            buider.append(split[i]);
        }
        ArrayList<String> musics = new ArrayList();
        for (String s : lists) {
           int start = s.indexOf("http:");
            int last = s.length();
            musics.add(s.substring(start,last));

        }
        NetMusic netMusic =null;
        for (String music : musics) {
            Log.i(TAG, "parse: "+music);
            netMusic =  new NetMusic();
            String[] sss = music.split(",");
            netMusic.setUrl(sss[0]);
            netMusic.setTitle(sss[1]);
            netMusic.setSinger(sss[3]);
            netMusic.setAlbum(sss[5]);
            list.add(netMusic);
        }

//        if(data!=null) {
//            StringReader in = new StringReader(data);
//            JsonReader jr = new JsonReader(in);
//            try {
//                jr.beginArray();
//
//                while (jr.hasNext()){
//                    NetMusic netMusic = new NetMusic();
//                    jr.beginArray();
//                    int x=0;
//                    while (jr.hasNext()){
//                        Log.i(TAG, "parse: "+x+jr.nextString());
//                        if(jr.peek() != JsonToken.NULL) {
////                            Log.i(TAG, "parse: "+x+jr.nextString());
//                            switch (x) {
//                                case 2:
//                                    netMusic.setUrl(jr.nextString());
//                                    break;
//                                case 3:
//                                    netMusic.setTitle(jr.nextString());
//                                    break;
//                                case 5:
//                                    netMusic.setSinger(jr.nextString());
//                                    break;
//                                case 7:
//                                    netMusic.setAlbum(jr.nextString());
//                                    break;
//                            }
//
//                        }else{
//
//                        }
//                        x++;
//                    }
//                    list.add(netMusic);
//                    jr.endArray();
//
//                }
//                jr.endArray();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        //Log.i(TAG, "parseJson: --"+list);
        return list;

    }


    //查询网络音乐图片
    public void  getImagesList(Context context, final GetNetImageListener getNetImageListener){
        if(!AppUtils.isNetworkConnected(context)){
            getNetImageListener.onFail("当前网络状态不可用");
        }
        this.getNetImageListener = getNetImageListener;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(MP3_ROOT_URL).
                            userAgent(Constant.USER_AGENT).timeout(100*1000).get();
                    String data="";
                    ArrayList<String> imageUrls = new ArrayList<>();
                    Element elements =doc.select("div.m_banner_l").first();
                    Iterator<Element> iterator = elements.select("div.item").iterator();
                    while (iterator.hasNext()){
                        Element element =iterator.next();
                        String imageUrl =element.select("img").attr("src").trim();
                        imageUrls.add(imageUrl);
                    }
                    getNetImageListener.onSuccess(imageUrls);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();



    }




    /**
     * get请求无参请求
     * @param url
     * @param listener
     */
    public void get(String url,final RequestListener listener){
        //创建一个Request
        final Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                listener.response(response.body().bytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public interface RequestListener{
        void response(byte[] bytes);
    }

}
