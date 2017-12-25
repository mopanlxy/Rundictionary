package com.example.mopan.runningdiary.activity;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mopan.runningdiary.MainActivity;
import com.example.mopan.runningdiary.R;
import com.example.mopan.runningdiary.entity.Music;
import com.example.mopan.runningdiary.entity.MusicUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaPlayerActivity extends Activity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener,AdapterView.OnItemClickListener{
    private MediaPlayer mp;
    private ListView lvMusic;
    private ImageButton btnPlay,btnPrevious,btnNext,btnStop;
    private SeekBar seekBar;
    private List<Music> musicList = new ArrayList<Music>();
    MusicAdapter musicAdapter;
    private boolean isPlay = true;// is play? or pause?
    private int state;//播放器的状态
    private static final int IDLE = 0;
    private static final int PLAY = 1;
    private static final int PAUSE = 2;
    private static final int STOP = 3;

    private int currIndex = IDLE;// current state
    private boolean flag= true;//control the thread end condition

    //创建一个单线程执行器
    ExecutorService es = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        lvMusic = (ListView) findViewById(R.id.listview1_music);
        seekBar = (SeekBar) findViewById(R.id.seekBar1_music);
        btnPrevious = (ImageButton) findViewById(R.id.imageButton1_privious);
        btnPlay = (ImageButton) findViewById(R.id.imageButton2_play);
        btnNext = (ImageButton) findViewById(R.id.imageButton3_next);
        btnStop = (ImageButton) findViewById(R.id.imageButton4_stop);
        btnPlay.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        mp = new MediaPlayer();

        //监听播放结束的条件
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Next();//启动下一首
            }
        });

        //监听mediaPlayer异常事件处理
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mp.reset();
                return false;
            }
        });

        //click the list and play the music
        lvMusic.setOnItemClickListener(this);

        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        musicAdapter = new MusicAdapter();
        lvMusic.setAdapter(musicAdapter);

    }

    //release the media
    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;
        if(mp != null ){

            mp.stop();
        }
        mp.release();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageButton2_play:
                if(isPlay){
                    play();
                    btnPlay.setImageResource(R.drawable.ic_media_pause);
                    isPlay = false;
                }else {
                    pause();
                    btnPlay.setImageResource(R.drawable.ic_media_play);
                    isPlay = true;
                }
            break;

            case R.id.imageButton4_stop:
                stop();
                btnPlay.setImageResource(R.drawable.ic_media_pause);
                isPlay = false;
                break;

            case R.id.imageButton1_privious:
                Previous();
                btnPlay.setImageResource(R.drawable.ic_media_pause);
                isPlay = false;
                break;
            case R.id.imageButton3_next:
                Next();
                btnPlay.setImageResource(R.drawable.ic_media_play);
                isPlay = true;
                break;


            default:
                break;
        }


    }

    //control the two ways of playing
    //start with current
    //start with 0
    private void play(){
        if(state == PAUSE){
            mp.start();

        }else{
            start();
        }
        state = PLAY;
    }

    private void pause(){
        mp.pause();
        state = PAUSE;

    }

    private void stop(){
        if(mp.isPlaying()){
            mp.stop();
            state = STOP;
            flag = false;
        }
    }

    //last sing
    private void Previous(){
        if(currIndex - 1 >= 0){
            currIndex--;
        }else{
            currIndex = musicList.size()-1;
        }
        start();

    }

    //next sing
    private void Next(){
        if((currIndex +1)<musicList.size()){
            currIndex++;
        }else{
            currIndex = 0;
        }
        start();
    }






    //start with 0
    private void start(){
        if(currIndex < musicList.size()){
            Music m = musicList.get(currIndex);
            try {
                mp.reset();//reset the mediaplayer, let the media return to Resume state
                mp.setDataSource(m.getUrl());//set the current sing
                mp.prepare();//prepare
                mp.start();//start to play
                initSeekBar();

                es.execute(new SeekBarThread());//开始启动线程，保证只有一个线程在执行
                musicAdapter.notifyDataSetChanged();//方法的回掉

                state = PLAY;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //初始化进度条
    private void initSeekBar(){
        seekBar.setMax(mp.getDuration());//set the progress of duration
        seekBar.setProgress(0);
    }

    //fromUser decide the progress is handled by user or P.C
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            mp.seekTo(progress);
        }
    }

    //开始拖
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        pause();
    }

    //拖完之后放的瞬间
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        play();
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long id) {
            currIndex = position;
            play();
            btnPlay.setImageResource(R.drawable.ic_media_pause);
            isPlay = false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }





    //实现播放进度条
    class SeekBarThread implements Runnable{

        public SeekBarThread(){
            flag = true;
        }

        @Override
        public void run() {


            while (flag){
                if(mp.getCurrentPosition() < mp.getDuration()){
                   seekBar.setProgress(mp.getCurrentPosition());
                }else {
                    flag = false;

                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }



    class MusicAdapter extends BaseAdapter{

        private MusicAdapter(){
            musicList = MusicUtils.getMusicData(MediaPlayerActivity.this);

        }
        @Override
        public int getCount() {
            return musicList.size();
        }

        @Override
        public Object getItem(int i) {
            return musicList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if(view == null){
                view = getLayoutInflater().inflate(R.layout.music_item,null);

            }
            TextView title = (TextView) view.findViewById(R.id.textView_title);
            TextView singer = (TextView) view.findViewById(R.id.textView_singer);
            TextView time = (TextView) view.findViewById(R.id.textView_time);

            view.setBackgroundColor(Color.WHITE);

            if(position == currIndex){
                view.setBackgroundColor(Color.BLUE);
            }



            Music music = musicList.get(position);
            title.setText(music.getTitle());
            singer.setText(music.getSinger());
            time.setText(MusicUtils.toTime((int) music.getTime()));

            return view;
        }
    }


}
