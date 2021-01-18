package com.example.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    MediaPlayer mPlayer;
    Button playButton;
    TextView labelTxt;
    int musicPosition;
    SeekBar seekBar;
    Thread myThread;
    int playButtonPos = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView loh = findViewById(R.id.loh);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        loh.setLayoutManager(linearLayoutManager);

        MyAdapter adapter = new MyAdapter();

        loh.setAdapter(adapter);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(138);

        labelTxt = (TextView) findViewById(R.id.labelTxt);
        labelTxt.setText("Playing word...");




        mPlayer = MediaPlayer.create(this, R.raw.music);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlay();
            }
        });

        playButton = (Button) findViewById(R.id.playButton);





    }

    public void playButton(View view) {
//        //rsfgsfgs
        if (playButtonPos == 0) {
            play();
            playButtonPos = 1;
        } else if (playButtonPos == 1) {
            pause();
            playButtonPos = 0;
        }



    }

    public void stopPlay() {
        mPlayer.stop();

        try {
            mPlayer.prepare();
            mPlayer.seekTo(0);
        }
        catch (Throwable t) {
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void play() {
        mPlayer.start();
        startThread();

//        new Thread(myThread).start();

//        threadStartForSeekBar();
    }

    public void pause() {
        mPlayer.pause();
    }

    public void stop() {
        stopPlay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer.isPlaying()) {
            stopPlay();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        String seekTo = seekBar.getProgress() + "000";
        mPlayer.seekTo(Integer.parseInt(seekTo));
        labelTxt.setText(String.valueOf(mPlayer.getCurrentPosition()));
    }

    public void seekBarSetProgress() {
        seekBar.setProgress(musicPosition);
    }


//    public Runnable myThread = new Runnable() {
//        @Override
//        public void run() {
//            while (mPlayer.isPlaying()) {
//
//                try {
//                    Thread.sleep(1000);
//                    musicPosition = mPlayer.getCurrentPosition() / 1000;
//                    seekBarSetProgress();
//                } catch (InterruptedException e) {
//
//                }
//
//                }
//
//
//
//            }
//        };

    public void startThread() {
        myThread = new Thread() {
            @Override
            public void run() {
                while(mPlayer.isPlaying()) {
                    try{
                        Thread.sleep(1000);
                        musicPosition = mPlayer.getCurrentPosition() / 1000;
                        seekBarSetProgress();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        };

        myThread.start();
    }





}