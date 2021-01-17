package com.example.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    MediaPlayer mPlayer;
    Button playButton, pauseButton, stopButton;
    TextView labelTxt;
    int musicPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView loh = findViewById(R.id.loh);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        loh.setLayoutManager(linearLayoutManager);

        MyAdapter adapter = new MyAdapter();

        loh.setAdapter(adapter);

        SeekBar seekBar = findViewById(R.id.seekBar);
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
        pauseButton = (Button) findViewById(R.id.pauseButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);


        Handler mHandler = new Handler();
        mHandler.post(() -> {
            while(mPlayer.isPlaying()) {
//                // Если нужно где то остановить
//                if(mPlayer.getCurrentPosition() >  10 ){
//                    labelTxt.setText("Stop: " + mPlayer.getCurrentPosition() );
//                    mPlayer.stop();
//                    break;
//                }

                // ---------------------------------
                labelTxt.setText("1");
                // ---------------------------------

            }
        });

    }

    private void stopPlay() {
        mPlayer.stop();
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        try {
            mPlayer.prepare();
            mPlayer.seekTo(0);
            playButton.setEnabled(true);
        }
        catch (Throwable t) {
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void play(View view){

        mPlayer.start();
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
    }

    public void pause(View view){

        mPlayer.pause();
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    public void stop(View view){
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


}