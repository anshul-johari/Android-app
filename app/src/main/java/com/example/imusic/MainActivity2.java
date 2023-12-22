package com.example.imusic;

import static java.util.logging.Logger.global;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity2 extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView name , cTime , tTime;
    ImageView previous , pause , next , replay , back;
    SeekBar seekbar;
    ArrayList<File> songList;
    String currentSong;
    int position;
    Thread updateSeek;

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_activity);

        name = findViewById(R.id.name);
        seekbar = findViewById(R.id.seekBar);
        tTime = findViewById(R.id.tTime);
        cTime = findViewById(R.id.cTime);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        previous=findViewById(R.id.previous);
        replay = findViewById(R.id.replay);
        back = findViewById(R.id.back);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songList = (ArrayList) bundle.getParcelableArrayList("songList");
        currentSong = bundle.getString("currentSong");
        name.setText(currentSong);
        position = intent.getIntExtra("position" , 0);
        playsong(position);


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cTime.setText(timer(i));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekbar.getProgress());
            }
        });

        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try{
                    while(currentPosition< mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekbar.setProgress(currentPosition);
                        sleep(1000);
                    }

                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();





        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    pause.setImageResource(R.drawable.play);
                }
                else{
                    mediaPlayer.start();
                    pause.setImageResource(R.drawable.pause);

                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position==songList.size()-1){
                    playsong(0);
                    position=0;
                }
                else {
                    playsong(position+1);
                    position= position+1;
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position==0) {
                    playsong(songList.size() - 1);
                    position = songList.size() - 1;
                }
                else{
                    playsong(position-1);
                    position = position - 1;
                }
            }
        });

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                playsong(position);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next.callOnClick();
            }
        });







    }
    public String timer(int position){
        String Text = "";
        int min = position/1000/60;
        int sec = position/1000%60;
        Text += min + ":";
        if(sec<10) Text += "0";
        Text += sec;
        return Text;
    }

    public void playsong(int index){
        try{
            name.setText(songList.get(index).getName().replace(".mp3" , ""));
            Uri uri = Uri.parse(songList.get(index).toString());
            mediaPlayer = MediaPlayer.create(this , uri);
            mediaPlayer.start();
            seekbar.setProgress(0);
            seekbar.setMax(mediaPlayer.getDuration());
            tTime.setText(timer(mediaPlayer.getDuration()));
            pause.setImageResource(R.drawable.pause);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}