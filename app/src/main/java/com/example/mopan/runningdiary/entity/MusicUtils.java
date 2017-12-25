package com.example.mopan.runningdiary.entity;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mopan on 2017/10/10.
 */

public class MusicUtils {

    //query the library of audio
    public static List<Music> getMusicData(Context context){
        List<Music> list = new ArrayList<Music>();
        //query the database of mp3
        Cursor c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        while (c.moveToNext()){
            Music m = new Music();
            m.setTitle(c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            m.setName(c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
            String singer = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            if(singer == null || "".equals(singer) || "<unknown>".equals(singer)){
                singer = "未知";
            }
            m.setSinger(singer);

            m.setAlbum(c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            m.setSize(c.getLong(c.getColumnIndex(MediaStore.Audio.Media.SIZE)));
            m.setTime(c.getLong(c.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            m.setUrl(c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA)));
            list.add(m);


        }
        c.close();
        return list;

    }

    //时间格式转换
    public static String toTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }


}


