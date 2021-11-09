package com.example.pacmangame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class Game_over_panel extends AppCompatActivity {

    private ImageView gameOverPanel_IMG_replay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        MediaPlayer music = MediaPlayer.create(this, R.raw.pacman_death);
        music.start();

        findView();

        gameOverPanel_IMG_replay.setOnClickListener(v -> {
            Intent MainActivity = new Intent(this, Activity_panel.class);
            startActivity(MainActivity);
            music.stop();
            finish();
        });
    }

    private void findView() {
        gameOverPanel_IMG_replay=findViewById(R.id.gameOverPanel_IMG_replay);

        ImageView gameOverPanel_IMG_background;
        gameOverPanel_IMG_background=findViewById(R.id.gameOverPanel_IMG_background);

        Glide
                .with(this)
                .load(R.drawable.game_over)
                .centerCrop()
                .into(gameOverPanel_IMG_background);
    }
}
