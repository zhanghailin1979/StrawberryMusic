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

/**
 * Created by Administrator on 2016/8/24.
 */
public class MusicListAdapter extends BaseAdapter {
    private List<Mp3Info> mp3Infos;
    private Context context;

    public MusicListAdapter(List<Mp3Info> mp3Infos, Context context) {
        this.mp3Infos = mp3Infos;
        this.context = context;
    }

    public void setMp3Infos(List<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }

    @Override
    public int getCount() {
        return mp3Infos.size();
    }

    @Override
    public Object getItem(int position) {
        return mp3Infos.get(position);
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
        Mp3Info mp3Info = mp3Infos.get(position);
        SimpleDraweeView image= (SimpleDraweeView) ViewHolder.getView(convertView,R.id.imageView1_icon);
        TextView tv_title = (TextView) ViewHolder.getView(convertView,R.id.textView1_title);
        TextView tv_singer = (TextView) ViewHolder.getView(convertView,R.id.textView2_singer);
        TextView tv_time = (TextView) ViewHolder.getView(convertView,R.id.textView3_time);
        image.setImageBitmap(MediaUtils.getArtwork(context,mp3Info.getMp3InfoId(),mp3Info.getAlbumId(),true,true));
        tv_title.setText(mp3Info.getTitle());
        tv_singer.setText(mp3Info.getArtist());
        tv_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
        return convertView;
    }
}
