package com.example.pacmangame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import com.google.android.material.button.MaterialButton;

public class Game_over_panel extends AppCompatActivity {

    private ImageView gameOverPanel_IMG_replay;
    private MaterialButton gameOverPanel_BTN_topTen;
    private  Intent TopTenActivity;
    private MediaPlayer music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        music = MediaPlayer.create(this, R.raw.pacman_death);
        music.start();

        findView();

        changeView ();

    }

    private void changeView() {
        gameOverPanel_IMG_replay.setOnClickListener(v -> {
            music.stop();
            finish();
        });

        gameOverPanel_BTN_topTen.setOnClickListener(v -> {
            startActivity(TopTenActivity);
            music.stop();
            finish();
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTicker();
    }

    private void stopTicker() {
        music.pause();
    }


    private void findView() {
        gameOverPanel_IMG_replay=findViewById(R.id.gameOverPanel_IMG_replay);
        gameOverPanel_BTN_topTen=findViewById(R.id.gameOverPanel_BTN_topTen);
        ImageView gameOverPanel_IMG_background;
        gameOverPanel_IMG_background=findViewById(R.id.gameOverPanel_IMG_background);
        TopTenActivity = new Intent(this, Top_ten_panel.class);
        Glide
                .with(this)
                .load(R.drawable.game_over)
                .centerCrop()
                .into(gameOverPanel_IMG_background);
    }



}
