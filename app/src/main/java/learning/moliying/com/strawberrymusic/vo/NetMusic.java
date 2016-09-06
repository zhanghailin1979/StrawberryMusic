package learning.moliying.com.strawberrymusic.vo;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/8/23.
 */

public class NetMusic {
    private String url;     //歌曲Url地址
    private String singer;  //歌手
    private String title;   //歌名
    private String album;   //专辑名称

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public String toString() {
        return "NetMusic{" +
                "url='" + url + '\'' +
                ", singer='" + singer + '\'' +
                ", title='" + title + '\'' +
                ", album='" + album + '\'' +
                '}';
    }
}
