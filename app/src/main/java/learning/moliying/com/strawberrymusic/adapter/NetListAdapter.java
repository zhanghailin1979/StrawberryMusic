package learning.moliying.com.strawberrymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import learning.moliying.com.strawberrymusic.R;
import learning.moliying.com.strawberrymusic.utils.MediaUtils;
import learning.moliying.com.strawberrymusic.utils.ViewHolder;
import learning.moliying.com.strawberrymusic.vo.Mp3Info;
import learning.moliying.com.strawberrymusic.vo.NetMusic;

/**
 * Created by Administrator on 2016/8/27.
 */
public class NetListAdapter extends BaseAdapter {
    private List<NetMusic> netMusics;
    private Context context;

    public NetListAdapter(List<NetMusic> netMusics, Context context) {
        this.netMusics = netMusics;
        this.context = context;
    }

    public void setNetMusics(List<NetMusic> netMusics) {
        this.netMusics = netMusics;
    }

    @Override
    public int getCount() {
        return netMusics.size();
    }

    @Override
    public Object getItem(int position) {
        return netMusics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView ==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_music_list,null);
        }
        NetMusic netMusic = netMusics.get(position);
//        SimpleDraweeView image= (SimpleDraweeView) ViewHolder.getView(convertView,R.id.imageView1_icon);
        TextView tv_title = (TextView) ViewHolder.getView(convertView,R.id.textView1_title);
        TextView tv_singer = (TextView) ViewHolder.getView(convertView,R.id.textView2_singer);
        TextView tv_time = (TextView) ViewHolder.getView(convertView,R.id.textView3_time);
        tv_title.setText(netMusic.getTitle());
        tv_singer.setText(netMusic.getSinger());
        tv_time.setText("");
//        tv_time.setText(MediaUtils.formatTime(netMusic.getTime()));
        return convertView;
    }
}
