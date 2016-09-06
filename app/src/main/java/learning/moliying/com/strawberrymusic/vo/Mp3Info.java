package learning.moliying.com.strawberrymusic.vo;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.ex.DbException;

import java.util.List;

@Table(name="Mp3Info")
public class Mp3Info {
    @Column(name="id",isId = true)
    private long id;
    @Column(name = "mp3InfoId")
    private long mp3InfoId;//在收藏音乐时用于保存原始ID
    @Column(name = "playTime")
    private long playTime;//最近播放时间
    @Column(name = "isLike")
    private int isLike; //1 喜欢  0 默认
    @Column(name="title")
    private String title;//歌名
    @Column(name="artist")
    private String artist;//艺术家
    @Column(name="album")
    private String album;//专辑
    @Column(name="albumId")
    private long albumId;//
    @Column(name="duration")
    private long duration;//时长
    @Column(name="size")
    private long size;//大小
    @Column(name="url")
    private String url;//路径
    @Column(name="isMusic")
    private int isMusic;//是否为音乐

    //通过Id查询单首歌曲
    public static Mp3Info getSingleChild(DbManager db,int id) throws DbException {
        return (Mp3Info) db.selector(Mp3Info.class).where("id","=",id).findAll();
    }



    //查询本地所有的歌曲
    public static List<Mp3Info> getChildren(DbManager db) throws DbException {
        return db.selector(Mp3Info.class).findAll();
    }

    //保存歌曲列表
    public static void saveChildren(DbManager db,List<Mp3Info> list) throws DbException {
        for (Mp3Info mp3Info : list) {
            db.save(mp3Info);
        }
    }
    //保存单首歌曲
    public static void saveSingleChild(DbManager db,Mp3Info mp3Info) throws DbException {
        if(mp3Info.getId()!=0){
            db.saveBindingId(mp3Info);
            System.out.println("保存成功");
            return;
        }
        db.save(mp3Info);

    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMp3InfoId() {
        return mp3InfoId;
    }

    public void setMp3InfoId(long mp3InfoId) {
        this.mp3InfoId = mp3InfoId;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
    }

    @Override
    public String toString() {
        return "Mp3Info{" +
                "id=" + id +
                ", mp3InfoId=" + mp3InfoId +
                ", playTime=" + playTime +
                ", isLike=" + isLike +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", albumId=" + albumId +
                ", duration=" + duration +
                ", size=" + size +
                ", url='" + url + '\'' +
                ", isMusic=" + isMusic +
                '}';
    }
}
