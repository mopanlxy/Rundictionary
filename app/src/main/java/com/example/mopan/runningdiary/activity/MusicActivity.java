package com.example.mopan.runningdiary.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.mopan.runningdiary.R;

import java.io.File;
import java.io.IOException;

public class MusicActivity extends AppCompatActivity {
    Button btn_play,btn_stop;
    SeekBar seekBar;
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        btn_play = (Button) findViewById(R.id.btn_play);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        mp = MediaPlayer.create(this,R.raw.test);

        try {
            mp.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        seekBar.setMax(mp.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(b){//用户手动滑动到想要进度
                    mp.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



//        Intent intent = new Intent();
//        String action = Intent.ACTION_VIEW;
//        intent.setAction(action);
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//        Uri data = Uri.fromFile(new File(path,"test.mp3"));
//        intent.setDataAndType(data,"audio/mp3");
//        startActivity(intent);


        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                h.post(r);//开始执行run()
            }
        });




        btn_stop.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mp.pause();
            }
        });

    }

    Handler h = new Handler();
    Runnable r = new Runnable() {//线程对象
        @Override
        public void run() {
            h.postDelayed(r,1000);//每隔1秒调用1次handler
            seekBar.setProgress(mp.getCurrentPosition());
        };
    };


}
