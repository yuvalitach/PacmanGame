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
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    final int DELAY = 500;

    private ImageView[][] route;
    private boolean newMonster;
    private int[] characters;
    private int [][] values;
    private ImageView [] mainCharacter, lives;
    private ImageView  panel_IMG_left_arrow, panel_IMG_right_arrow;
    private int numLives, numRoads, playerPosition;
    private MediaPlayer music;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        timer = new Timer();
        music = MediaPlayer.create(MainActivity.this, R.raw.pacman_beginning);
        music.start();
        numRoads= route[0].length;
        values = new int[route.length][route[0].length];
        newMonster = true;
        playerPosition=1;
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
        music.stop();
    }

    private void runLogic() {
        for (int i = values.length - 1; i > 0; i--) {
            System.arraycopy(values[i - 1], 0, values[i], 0, values[i].length - 1 + 1);
        }
        Arrays.fill(values[0],0);
//            values[0][0]=0;
//            values[0][1]=0;
//            values[0][2]=0;
        }
//    private void updateUIRow(int index){
//        for (int i=0;i<values[index].length;index++)
//        {
//            ImageView im = route[i][j];
//            if (values[i][j] == 0)
//                im.setVisibility(View.INVISIBLE);
//            else if (values[i][j] == 1)
//                im.setVisibility(View.VISIBLE);
//        }
//    }

    private void updateUI() {
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                ImageView im = route[i][j];
                if (values[i][j] == 0)
                    im.setVisibility(View.INVISIBLE);
                else if (values[i][j] == 1)
                    im.setVisibility(View.VISIBLE);
//                    im.setImageResource(R.drawable.cherry);
            }
        }
    }

    //random number
    private void randNumber() {
        int randomNum;
        
        if (newMonster) {
            randomNum = new Random().nextInt(3);
            values[0][randomNum] = 1;
            newMonster=false;
        }
        else
            newMonster=true;
    }

    private void movingMainCharacter() {
        panel_IMG_left_arrow.setOnClickListener(v -> {
            if(playerPosition!=0) {
                mainCharacter[playerPosition].setVisibility(View.INVISIBLE);
                playerPosition--;
                mainCharacter[playerPosition].setVisibility(View.VISIBLE);
            }
            //            for (int i=1;i<numRoads;i++)
//            {
//                if (mainCharacter[i].getVisibility() == View.VISIBLE) {
//                    mainCharacter[i].setVisibility(View.INVISIBLE);
//                    mainCharacter[i - 1].setVisibility(View.VISIBLE);
//                }
//            }
//            if (mainCharacter[1].getVisibility() == View.VISIBLE) {
//                mainCharacter[1].setVisibility(View.INVISIBLE);
//                mainCharacter[0].setVisibility(View.VISIBLE);
//            }
//            else if (mainCharacter[2].getVisibility() == View.VISIBLE) {
//                mainCharacter[2].setVisibility(View.INVISIBLE);
//                mainCharacter[1].setVisibility(View.VISIBLE);
//            }
        });

        panel_IMG_right_arrow.setOnClickListener(v -> {
            if(playerPosition!=numRoads-1) {
                mainCharacter[playerPosition].setVisibility(View.INVISIBLE);
                playerPosition++;
                mainCharacter[playerPosition].setVisibility(View.VISIBLE);
            }

            //            for (int i=0;i<numRoads-1;i++)
//            {
//                if (mainCharacter[i].getVisibility() == View.VISIBLE) {
//                    mainCharacter[i].setVisibility(View.INVISIBLE);
//                    mainCharacter[i + 1].setVisibility(View.VISIBLE);
//                }
//            }
//            if (mainCharacter[1].getVisibility() == View.VISIBLE) {
//                mainCharacter[1].setVisibility(View.INVISIBLE);
//                mainCharacter[2].setVisibility(View.VISIBLE);
//            }
//            else if (mainCharacter[0].getVisibility() == View.VISIBLE) {
//                mainCharacter[0].setVisibility(View.INVISIBLE);
//                mainCharacter[1].setVisibility(View.VISIBLE);
//            }
        });
    }
    private void checkCrash() {
        for (int i = 0; i < numRoads; i++) {
            if (values[values.length-1][i]==1 && mainCharacter[i].getVisibility()==View.VISIBLE) {
                lives[numLives - 1].setVisibility(View.INVISIBLE);
                MediaPlayer music = MediaPlayer.create(MainActivity.this, R.raw.pacman_eatghost);
                music.start();
                numLives--;

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
                if (numLives==0) {
                    Intent gameOverScreen = new Intent(this, gameOverPanel.class);
                    startActivity(gameOverScreen);
                    stopTicker();
                    finish();
                    }
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

        Glide
                .with(this)
                .load(R.drawable.three_lanes)
                .centerCrop()
                .into(panel_IMG_background);


    }
}