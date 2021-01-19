package com.example.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mPlayer;
    Button playButton, b_1;
    TextView labelTxt;
    SeekBar seekBar;
    Thread myThread;

    int musicPosition;
    int playButtonPos = 0;

    String stream = "http://stream.radiorecord.ru:8100/rr_aac";

    boolean prepared = false;
    boolean started = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView loh = findViewById(R.id.loh);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        loh.setLayoutManager(linearLayoutManager);

        MyAdapter adapter = new MyAdapter();

        loh.setAdapter(adapter);

//        seekBar = findViewById(R.id.seekBar);
//        seekBar.setOnSeekBarChangeListener(this);

        labelTxt = (TextView) findViewById(R.id.labelTxt);
        labelTxt.setText("Playing word...");


//        mPlayer = MediaPlayer.create(this, );
//        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                stopPlay();
//            }
//        });

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        playButton = (Button) findViewById(R.id.playButton);

        b_1 = findViewById(R.id.b_1);

        b_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PlayerTask().execute(stream);
                b_1.setEnabled(false);
            }
        });

    }

    class PlayerTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                mPlayer.setDataSource(strings[0]);
                mPlayer.prepare();
                prepared = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            b_1.setEnabled(true);
            b_1.setText("PLAY");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (started) {
            mPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (started) {
            mPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (prepared) {
            mPlayer.release();
        }
    }

//    public void playButton(View view) {
//        if (playButtonPos == 0) {
//            play();
//            playButtonPos = 1;
//        } else if (playButtonPos == 1) {
//            pause();
//            playButtonPos = 0;
//        }
//    }
//
//    public void stopPlay() {
//        mPlayer.stop();
//
//        mPlayer.prepareAsync();
//        mPlayer.seekTo(0);
//        playButtonPos = 0;
//
//
//    }
//
//    public void play() {
//        mPlayer.start();
////        seekBar.setMax(mPlayer.getDuration() / 1000);
////        startThread();
//    }
//
//    public void pause() {
//        mPlayer.pause();
//    }
//
//    public void stop() {
//        stopPlay();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mPlayer.isPlaying()) {
//            stopPlay();
//        }
//    }
//
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//        String seekTo = seekBar.getProgress() + "000";
//        mPlayer.seekTo(Integer.parseInt(seekTo));
//        labelTxt.setText(String.valueOf(mPlayer.getCurrentPosition()));
//    }
//
//    public void seekBarSetProgress() {
//        seekBar.setProgress(musicPosition);
//    }
//
//    public void startThread() {
//        myThread = new Thread() {
//            @Override
//            public void run() {
//                while(mPlayer.isPlaying()) {
//                    try{
//                        Thread.sleep(1000);
//                        musicPosition = mPlayer.getCurrentPosition() / 1000;
//                        seekBarSetProgress();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        };
//
//        myThread.start();
//
//    }
}