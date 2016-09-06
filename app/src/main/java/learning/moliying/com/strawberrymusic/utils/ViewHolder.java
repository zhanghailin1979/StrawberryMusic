package learning.moliying.com.strawberrymusic.utils;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by Administrator on 2016/8/24.
 */
public class ViewHolder {

    public static View getView(View v,int id){
        SparseArray<View> array = (SparseArray<View>) v.getTag();
        if(array==null){
            array = new SparseArray<>();
            v.setTag(array);
        }
        View view = array.get(id);
        if(view ==null){
            view = v.findViewById(id);
            array.put(id,view);
        }
        return view;

    }


}
