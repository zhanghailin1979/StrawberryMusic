package learning.moliying.com.strawberrymusic.vo;

import learning.moliying.com.strawberrymusic.utils.MessageEventType;

/**
 * Created by Administrator on 2016/8/24.
 */
public class BackEvent {
    public MessageEventType messageEventType;
    public BackEvent(MessageEventType messageEventType){
        this.messageEventType = messageEventType;
    }
}
