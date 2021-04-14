package com.example.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    ArrayList<String> fileNames = new ArrayList<>();

    MediaPlayer mPlayer;
    Button playButton;
    TextView labelTxt;
    SeekBar seekBar;
    Thread myThread;

    FirebaseStorage storage;
    FirebaseDatabase firebaseDatabase;

    int musicPosition;
    int playButtonPos = 0;
    int musicPosForPlaying = 1;
    boolean getReady = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        labelTxt = findViewById(R.id.labelTxt);
        labelTxt.setText("Playing word...");

        RecyclerView previewRecyclerView = findViewById(R.id.loh);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        previewRecyclerView.setLayoutManager(linearLayoutManager);
        previewRecyclerView.scrollToPosition(1);

        MyAdapter adapter = new MyAdapter();
        previewRecyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(previewRecyclerView);

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setLooping(true);

        StorageReference listRef = storage.getReference().child("mp3");

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            String fileName = item.toString();
                            fileName = fileName.substring(37);
                            System.out.println(fileName);
                            fileNames.add(fileName);



                        }

                        String fileNamesGet = fileNames.get(musicPosForPlaying);
                        System.out.println(fileNamesGet);
                        String setDataSourceURL = "https://firebasestorage.googleapis.com/v0/b/firstproj-d32ba.appspot.com/o/mp3%2F" + fileNamesGet + "?alt=media&token=ff61bf38-2c8c-4ffc-bff3-3e39fca0497b";
                        System.out.println(setDataSourceURL);

                        try {
                            mPlayer.setDataSource(setDataSourceURL);
                            mPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        getReady = true;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });


        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });

        playButton = findViewById(R.id.playButton);

    }

    public void nextTrack(View view) {
        musicPosForPlaying += 1;
        stop();
        mPlayer.reset();
        String fileNamesGet = fileNames.get(musicPosForPlaying);
        String setDataSourceURL = "https://firebasestorage.googleapis.com/v0/b/firstproj-d32ba.appspot.com/o/mp3%2F" + fileNamesGet + "?alt=media&token=ff61bf38-2c8c-4ffc-bff3-3e39fca0497b";
        try {
            mPlayer.setDataSource(setDataSourceURL);
            mPlayer.prepare();
            play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void previousTrack(View view) {
        musicPosForPlaying -= 1;
        stop();
        mPlayer.reset();
        String fileNamesGet = fileNames.get(musicPosForPlaying);
        String setDataSourceURL = "https://firebasestorage.googleapis.com/v0/b/firstproj-d32ba.appspot.com/o/mp3%2F" + fileNamesGet + "?alt=media&token=ff61bf38-2c8c-4ffc-bff3-3e39fca0497b";
        try {
            mPlayer.setDataSource(setDataSourceURL);
            mPlayer.prepare();
            play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playButton(View view) {
        play();
    }

    public void stop() {
        mPlayer.stop();
        mPlayer.seekTo(0);
        playButtonPos = 0;

    }

    public void play() {
        try {
            if (getReady) {
                if (playButtonPos == 0) {
                    mPlayer.start();
                    seekBar.setMax(mPlayer.getDuration() / 1000);
                    startThread();
                    playButtonPos = 1;
                } else if (playButtonPos == 1) {
                    pause();
                    playButtonPos = 0;
                }
            }
        } catch (Exception ignored) { }
    }

    public void pause() {
        mPlayer.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer.isPlaying()) {
            stop();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //code
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //code
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