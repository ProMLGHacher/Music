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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    ArrayList<String> fileNames = new ArrayList<>();

    MediaPlayer mPlayer;

    FloatingActionButton playButton;
    SeekBar seekBar;
    Button nextTrack;
    Button previousTrack;
    ImageView like;
    ImageView disLike;
    ImageView reply;

    Thread myThread;

    FirebaseStorage storage;
    FirebaseDatabase firebaseDatabase;

    int musicPosition;
    int playButtonPos = 0;
    int musicPosForPlaying = 0;
    boolean getReady = false;
    boolean isLike = false;
    boolean isDisLike = false;
    String lastMusicName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        reply = findViewById(R.id.reply);
        disLike = findViewById(R.id.disLike);
        like = findViewById(R.id.like);
        nextTrack = findViewById(R.id.nextTrack);
        previousTrack = findViewById(R.id.previousTrack);
        playButton = findViewById(R.id.playButton);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        RecyclerView previewRecyclerView = findViewById(R.id.coverRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        previewRecyclerView.setLayoutManager(linearLayoutManager);
        previewRecyclerView.scrollToPosition(1);

        MyAdapter adapter = new MyAdapter();
        previewRecyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(previewRecyclerView);

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

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

//                        String fileNamesGet = fileNames.get(musicPosForPlaying);

                        DatabaseReference lastFileNameFromFirebase = firebaseDatabase.getReference("lastMusicName");

                        lastFileNameFromFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                lastMusicName = dataSnapshot.getValue().toString();

                                System.out.println(lastMusicName);

                                String setDataSourceURL = "https://firebasestorage.googleapis.com/v0/b/firstproj-d32ba.appspot.com/o/mp3%2F" + lastMusicName + ".mp3?alt=media&token=ff61bf38-2c8c-4ffc-bff3-3e39fca0497b";

                                try {
                                    mPlayer.setDataSource(setDataSourceURL);
                                    mPlayer.prepareAsync();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                getReady = true;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
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
                if (!mPlayer.isLooping()) {
                    nextTrack();
                }
            }
        });

        nextTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTrack();
            }
        });

        previousTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousTrack();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLike) {
                    takeAwayLike();
                } else {
                    setLike();
                }
            }
        });

        disLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDisLike) {
                    takeAwayDisLike();
                } else {
                    setDisLike();
                }
            }
        });

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer.isLooping()) {
                    mPlayer.setLooping(false);
                    reply.setImageResource(R.drawable.replay_disable);
                } else {
                    mPlayer.setLooping(true);
                    reply.setImageResource(R.drawable.replay_enable);
                }
            }
        });



    }

    private void setDisLike() {
        disLike.setImageResource(R.drawable.dislike_enable);
        isDisLike = true;
    }

    private void takeAwayDisLike() {
        disLike.setImageResource(R.drawable.dislike_disble);
        isDisLike = false;
    }


    public void setLike() {
        like.setImageResource(R.drawable.like_enable);
        isLike = true;
    }

    public void takeAwayLike() {
        like.setImageResource(R.drawable.like_disble);
        isLike = false;
    }

    public void nextTrack() {

        if (musicPosForPlaying + 1 != fileNames.size()) {
            musicPosForPlaying += 1;
        } else {
            musicPosForPlaying = 0;
        }

        stop();
        mPlayer.reset();
        System.out.println(musicPosForPlaying);
        String fileNamesGet = fileNames.get(musicPosForPlaying);
        String setDataSourceURL = "https://firebasestorage.googleapis.com/v0/b/firstproj-d32ba.appspot.com/o/mp3%2F" + fileNamesGet + "?alt=media&token=ff61bf38-2c8c-4ffc-bff3-3e39fca0497b";

        firebaseDatabase.getReference("lastMusicName").setValue(fileNamesGet);

        try {
            mPlayer.setDataSource(setDataSourceURL);

            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    play();
                }
            });

            mPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void previousTrack() {

        if (musicPosForPlaying == 0) {
            musicPosForPlaying = fileNames.size() - 1;
        } else {
            musicPosForPlaying -= 1;
        }

        stop();
        mPlayer.reset();
        String fileNamesGet = fileNames.get(musicPosForPlaying);
        String setDataSourceURL = "https://firebasestorage.googleapis.com/v0/b/firstproj-d32ba.appspot.com/o/mp3%2F" + fileNamesGet + "?alt=media&token=ff61bf38-2c8c-4ffc-bff3-3e39fca0497b";

        firebaseDatabase.getReference("lastMusicName").setValue(fileNamesGet);

        try {
            mPlayer.setDataSource(setDataSourceURL);

            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    play();
                }
            });

            mPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
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