package learning.moliying.com.strawberrymusic.utils;

import android.os.Environment;

/**
 * descreption:
 * company: moliying.com
 * Created by vince on 16/7/20.
 */
public class Constant {




    public static final String SP_NAME = "caomei";  //私有属性文件名
    public static final String DB_NAME = "caomei.db"; //数据库名
    public static final int VERSION = 1; //数据库版本
    public static final int PLAY_RECORD_NUM = 20;//最近播放显示的最大条数

//    //内地榜
//    public static final String NDB_URL = "http://mp3.sogou.com/list/song?type=%C4%DA%B5%D8%B0%F1&ie=gbk";
//    //港台榜
//    public static final String GTB_URL = "http://mp3.sogou.com/list/song?type=%B8%DB%CC%A8%B0%F1&ie=gbk";
//    //流行榜
//    public static final String LXB_URL = "http://mp3.sogou.com/list/song?type=%C1%F7%D0%D0%B0%F1&ie=gbk";
//    //美国公告榜
//    public static final String USA_GG_URL = "http://mp3.sogou.com/list/song?type=%C1%F7%D0%D0%B0%F1&ie=gbk";
//    //iTunes榜
//    public static final String ITUNES_URL = "http://mp3.sogou.com/list/song?type=iTunes%B0%F1&ie=gbk";
//    //韩国MNET榜
//    public static final String KOREA_URL = "http://mp3.sogou.com/list/song?type=%BA%AB%B9%FAMNET%B0%F1&ie=gbk";
//    //日本公信榜
//    public static final String JAPAN_URL = "http://mp3.sogou.com/list/song?type=%C8%D5%B1%BE%B9%AB%D0%C5%B0%F1&ie=gbk";
//    //KTV榜
//    public static final String KTV_URL = "http://mp3.sogou.com/list/song?type=KTV%C5%C5%D0%D0%B0%F1&ie=gbk";
//    //英国UK榜
//    public static final String ENGLISH_UK_URL = "http://mp3.sogou.com/list/song?type=%D3%A2%B9%FAUK%B0%F1&ie=gbk";


//    Locale

    //百度音乐地址
    public static final String BAIDU_URL = "http://music.baidu.com";
    //热歌榜
    public static final String BAIDU_DAYHOT = "/top/dayhot/?pst=shouyeTop";
    //搜索
    public static final String BAIDU_SEARCH = "/search/song";

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.22 Safari/537.36";

    //成功标记
    public static final int SUCCESS = 1;
    //失败标记
    public static final int FAILED = 2;


    public static final String CM_MUSIC_MUSIC = "/caomei_music/music";
    public static final String DIR_LRC = "/caomei_music/music/lrc";

    //音乐路径
    public static final String DIR_MLY_MUSIC = "/caomei_music/music";
    public static final String DIR_DOWNLOAD_MUSIC = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    public static final String DIR_MUSIC = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();
}
