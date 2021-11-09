package com.example.pacmangame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Activity_panel extends AppCompatActivity {

    final int DELAY = 500;

    private ImageView[][] route;
    private boolean newMonster;
    private int[] characters;
    private int [][] values;
    private ImageView [] mainCharacter, lives;
    private ImageView  panel_IMG_left_arrow, panel_IMG_right_arrow;
    private int numLives, numRoads, playerPosition, score;
    private TextView panel_LBL_score;
    private MediaPlayer music_beginning, eat_fruit_music, eat_ghost_music;
    private Animation animation;
    private Toast mToastToShow;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        timer = new Timer();
        music_beginning = MediaPlayer.create(Activity_panel.this, R.raw.pacman_beginning);
        eat_ghost_music = MediaPlayer.create(Activity_panel.this, R.raw.pacman_eatghost);
        eat_fruit_music = MediaPlayer.create(Activity_panel.this, R.raw.pacman_eatfruit);
        animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
        mToastToShow = Toast.makeText(this, "Yummy", Toast.LENGTH_SHORT);

        music_beginning.start();
        numRoads= route[0].length;
        values = new int[route.length][route[0].length];
        newMonster = true;
        playerPosition=1;
        score=0;
        numLives=lives.length;
        initializationArray();
        movingMainCharacter();

    }

    private void initializationArray() {
        for (int[] value : values) Arrays.fill(value, 0);
    }


    @Override
    protected void onStart() {
        super.onStart();
        startTicker();
    }


    private void startTicker() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    runLogic();
                    randNumber();
                    updateUI();
                    checkCrash();
                });
            }
        }, 0, DELAY);

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTicker();
    }

    private void stopTicker() {
        timer.cancel();
        music_beginning.stop();
        eat_fruit_music.stop();
        eat_ghost_music.stop();
    }

    private void runLogic() {
                for (int i = values.length - 1; i > 0; i--) {
            System.arraycopy(values[i - 1], 0, values[i], 0, values[i].length - 1 + 1);
        }
        Arrays.fill(values[0],0);
        }


    private void updateUI() {
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                ImageView im = route[i][j];
                if (values[i][j] == 0)
                    im.setVisibility(View.INVISIBLE);
                    //cherry
                else if (values[i][j] == 1) {
                    im.setVisibility(View.VISIBLE);
                    im.setImageResource(characters[0]);
                    //monster1
                } else if (values[i][j] == 2) {
                    im.setVisibility(View.VISIBLE);
                    im.setImageResource(characters[1]);
                    //monster2
                } else if (values[i][j] == 3) {
                    im.setVisibility(View.VISIBLE);
                    im.setImageResource(characters[2]);
                    //monster3
                } else if (values[i][j] == 4) {
                    im.setVisibility(View.VISIBLE);
                    im.setImageResource(characters[3]);
                }
            }
        }
    }

        //random number
        private void randNumber () {
            int randomNum, type;
            type = new Random().nextInt(characters.length+1);
            if (type == 0)
                type++;
            if (newMonster) {
                randomNum = new Random().nextInt(3);
                values[0][randomNum] = type;
                newMonster = false;
            } else
                newMonster = true;
        }

    private void movingMainCharacter() {
        panel_IMG_left_arrow.setOnClickListener(v -> {
            if(playerPosition!=0) {
                mainCharacter[playerPosition].setVisibility(View.INVISIBLE);
                playerPosition--;
                mainCharacter[playerPosition].setVisibility(View.VISIBLE);
            }
            animation.cancel();
        });

        panel_IMG_right_arrow.setOnClickListener(v -> {
            if(playerPosition!=numRoads-1) {
                mainCharacter[playerPosition].setVisibility(View.INVISIBLE);
                playerPosition++;
                mainCharacter[playerPosition].setVisibility(View.VISIBLE);
            }
            animation.cancel();
        });
    }

    private void checkCrash() {
        for (int i = 0; i < numRoads; i++) {
            if (values[values.length-1][i]==2 && mainCharacter[i].getVisibility()==View.VISIBLE
            || values[values.length-1][i]==3 && mainCharacter[i].getVisibility()==View.VISIBLE
            || values[values.length-1][i]==4 && mainCharacter[i].getVisibility()==View.VISIBLE) {
                lives[numLives - 1].setVisibility(View.INVISIBLE);
                eat_ghost_music.start();
                numLives--;
                mToastToShow.cancel();
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }

                animation.setDuration(500); //1 second duration for each animation cycle
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
                animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
                mainCharacter[i].startAnimation(animation); //to start animation

                if (numLives==0) {
                    Intent gameOverScreen = new Intent(this, Game_over_panel.class);
                    startActivity(gameOverScreen);
                    stopTicker();
                    mToastToShow.cancel();
                    finish();
                    }
                }

            if (values[values.length-1][i]==1 && mainCharacter[i].getVisibility()==View.VISIBLE) {
                eat_fruit_music.start();
                score+=10;
                panel_LBL_score.setText(String.valueOf(score));
                mToastToShow.show();
            }
            }
        }


    private void findViews() {
        ImageView panel_IMG_background;
        mainCharacter = new ImageView[]{
                findViewById(R.id.panel_IMG_pacman_left), findViewById(R.id.panel_IMG_pacman_center), findViewById(R.id.panel_IMG_pacman_right)};

        route = new ImageView[][]{
                {findViewById(R.id.panel_IMG_monster00), findViewById(R.id.panel_IMG_monster01), findViewById(R.id.panel_IMG_monster02)},
                {findViewById(R.id.panel_IMG_monster10), findViewById(R.id.panel_IMG_monster11), findViewById(R.id.panel_IMG_monster12)},
                {findViewById(R.id.panel_IMG_monster20), findViewById(R.id.panel_IMG_monster21), findViewById(R.id.panel_IMG_monster22)},
                {findViewById(R.id.panel_IMG_monster30), findViewById(R.id.panel_IMG_monster31), findViewById(R.id.panel_IMG_monster32)}};

        panel_IMG_left_arrow = findViewById(R.id.panel_IMG_left_arrow);
        panel_IMG_right_arrow = findViewById(R.id.panel_IMG_right_arrow);

        panel_IMG_background=findViewById(R.id.panel_IMG_background);

        lives = new ImageView[]{
                findViewById(R.id.panel_IMG_heart1), findViewById(R.id.panel_IMG_heart2), findViewById(R.id.panel_IMG_heart3)};

        characters = new int[]{R.drawable.cherry, R.drawable.monster1, R.drawable.monster2, R.drawable.monster3};

        panel_LBL_score = findViewById(R.id.panel_LBL_score);

        Glide
                .with(this)
                .load(R.drawable.three_lanes)
                .centerCrop()
                .into(panel_IMG_background);


    }
}