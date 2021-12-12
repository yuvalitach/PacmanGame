package com.example.pacmangame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.example.pacmangame.objects.MyDB;
import com.example.pacmangame.objects.Record;
import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

public class Activity_panel extends AppCompatActivity {
    public static final String NAME = "NAME";

    private ImageView[][] route;
    private int[] characters;
    private LogicGame logics;
    private ImageView[] mainCharacter, lives;
    private ImageView panel_IMG_left_arrow, panel_IMG_right_arrow;
    private int numLives, numRoads, playerPosition, score;
    private TextView panel_LBL_score;
    private MediaPlayer music_beginning, eat_fruit_music, eat_ghost_music;
    private Animation animation;
    private Toast mToastToShow;
    private Intent gameOverScreen;
    private float sensorPosition;
    private String playerName;
    private final MyDB myDB = MyDB.initMyDB();
    private Sensors sensors;
    private LocationManager locationManager;
    Timer timer;

    //sensors
    public static final String SENSOR_TYPE = "SENSOR_TYPE";
    private int sensorType;

    //speed
    public static final String SPEED = "SPEED";
    private int speed;
    private int delay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        getDataFromLogin();
        if (sensorType == 1) {
            sensors = new Sensors();
            initSensors();
        }
        else
            movingMainCharacter();
        if (speed == 1)
            delay = 250;
        else
            delay = 500;

        timer = new Timer();
        music_beginning = MediaPlayer.create(Activity_panel.this, R.raw.pacman_beginning);
        eat_ghost_music = MediaPlayer.create(Activity_panel.this, R.raw.pacman_eatghost);
        eat_fruit_music = MediaPlayer.create(Activity_panel.this, R.raw.pacman_eatfruit);
        animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
        mToastToShow = Toast.makeText(this, "Yummy", Toast.LENGTH_SHORT);

        music_beginning.start();
        numRoads = route[0].length;
        logics = new LogicGame(route.length, route[0].length);
        playerPosition = 2;
        score = 0;
        numLives = lives.length;

        //----- Get Location use permission and check -----
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        locationManager = new LocationManager(this);

    }

    private void initSensors() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors.setSensorManager(sensorManager);
        sensors.initSensor();
    }


    private final SensorEventListener accSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            sensorPosition = event.values[0];
            movingCharacterBySensor();
            panel_IMG_left_arrow.setVisibility(View.INVISIBLE);
            panel_IMG_right_arrow.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void getDataFromLogin() {
        Bundle bundle = getIntent().getExtras();
        sensorType = bundle.getInt(SENSOR_TYPE);
        playerName = bundle.getString(NAME);
        speed = bundle.getInt(SPEED);
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
                    logics.runLogic();
//                    runLogic();
                    logics.randNumber(characters.length, mainCharacter.length);
                    updateUI();
                    checkCrash();
                });
            }
        }, 0, delay);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTicker();
    }

    private void stopTicker() {
        timer.cancel();
        music_beginning.pause();
        eat_fruit_music.pause();
        eat_ghost_music.pause();
    }

    private void stopMusic() {
        music_beginning.stop();
        eat_fruit_music.stop();
        eat_ghost_music.stop();
    }

    private void updateUI() {
        for (int i = 0; i < logics.getValues().length; i++) {
            for (int j = 0; j < logics.getValues()[i].length; j++) {
                ImageView im = route[i][j];
                if (logics.getValues()[i][j] == 0)
                    im.setVisibility(View.INVISIBLE);
                    //cherry
                else if (logics.getValues()[i][j] == 1) {
                    im.setVisibility(View.VISIBLE);
                    im.setImageResource(characters[0]);
                    //monster1
                } else if (logics.getValues()[i][j] == 2) {
                    im.setVisibility(View.VISIBLE);
                    im.setImageResource(characters[1]);
                    //monster2
                } else if (logics.getValues()[i][j] == 3) {
                    im.setVisibility(View.VISIBLE);
                    im.setImageResource(characters[2]);
                    //monster3
                } else if (logics.getValues()[i][j] == 4) {
                    im.setVisibility(View.VISIBLE);
                    im.setImageResource(characters[3]);
                }
            }
        }
    }


    private void movingCharacterBySensor() {
            if (sensorPosition < -3 && playerPosition != numRoads - 1) {
            moveRight();
            animation.cancel();
        } else if (sensorPosition > 1 && sensorPosition < 3 && playerPosition != 0) {
            moveLeft();
            animation.cancel();
        } else if (sensorPosition > 3 && playerPosition != 0) {
            moveLeft();
            animation.cancel();
        }
    }

    private void movingMainCharacter() {
        panel_IMG_left_arrow.setOnClickListener(v -> {
            if (playerPosition != 0)
                moveLeft();
            animation.cancel();
        });

        panel_IMG_right_arrow.setOnClickListener(v -> {
            if (playerPosition != numRoads - 1)
                moveRight();
            animation.cancel();
        });
    }

    public void moveLeft() {
        mainCharacter[playerPosition].setVisibility(View.INVISIBLE);
        playerPosition--;
        mainCharacter[playerPosition].setVisibility(View.VISIBLE);
    }

    public void moveRight() {
        mainCharacter[playerPosition].setVisibility(View.INVISIBLE);
        playerPosition++;
        mainCharacter[playerPosition].setVisibility(View.VISIBLE);
    }

    private void checkCrash() {
        for (int i = 0; i < numRoads; i++) {
            if (logics.getValues()[logics.getValues().length - 1][i] == 2 && mainCharacter[i].getVisibility() == View.VISIBLE
                    || logics.getValues()[logics.getValues().length - 1][i] == 3 && mainCharacter[i].getVisibility() == View.VISIBLE
                    || logics.getValues()[logics.getValues().length - 1][i] == 4 && mainCharacter[i].getVisibility() == View.VISIBLE) {
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

                if (numLives == 0) {
                    Record winner = new Record().setName(playerName).setScore(score).setLon(locationManager.getLon()).setLat(locationManager.getLat());
                    myDB.addRecord(winner);

                    String json = new Gson().toJson(myDB);
                    MSPV3.getMe().putString("MY_DB", json);

                    startActivity(gameOverScreen);
                    stopTicker();
                    stopMusic();
                    mToastToShow.cancel();
                    finish();
                }
            }

            if (logics.getValues()[logics.getValues().length - 1][i] == 1 && mainCharacter[i].getVisibility() == View.VISIBLE) {
                eat_fruit_music.start();
                score += 10;
                panel_LBL_score.setText(String.valueOf(score));
                mToastToShow.show();
            }
        }
    }


    private void findViews() {
        ImageView panel_IMG_background;
        mainCharacter = new ImageView[]{
                findViewById(R.id.panel_IMG_pacman_0), findViewById(R.id.panel_IMG_pacman_1), findViewById(R.id.panel_IMG_pacman_2),
                findViewById(R.id.panel_IMG_pacman_3), findViewById(R.id.panel_IMG_pacman_4)};

        route = new ImageView[][]{
                {findViewById(R.id.panel_IMG_monster00), findViewById(R.id.panel_IMG_monster01), findViewById(R.id.panel_IMG_monster02), findViewById(R.id.panel_IMG_monster03), findViewById(R.id.panel_IMG_monster04),},
                {findViewById(R.id.panel_IMG_monster10), findViewById(R.id.panel_IMG_monster11), findViewById(R.id.panel_IMG_monster12), findViewById(R.id.panel_IMG_monster13), findViewById(R.id.panel_IMG_monster14),},
                {findViewById(R.id.panel_IMG_monster20), findViewById(R.id.panel_IMG_monster21), findViewById(R.id.panel_IMG_monster22), findViewById(R.id.panel_IMG_monster23), findViewById(R.id.panel_IMG_monster24),},
                {findViewById(R.id.panel_IMG_monster30), findViewById(R.id.panel_IMG_monster31), findViewById(R.id.panel_IMG_monster32), findViewById(R.id.panel_IMG_monster33), findViewById(R.id.panel_IMG_monster34),},
                {findViewById(R.id.panel_IMG_monster40), findViewById(R.id.panel_IMG_monster41), findViewById(R.id.panel_IMG_monster42), findViewById(R.id.panel_IMG_monster43), findViewById(R.id.panel_IMG_monster44),},};

        panel_IMG_left_arrow = findViewById(R.id.panel_IMG_left_arrow);
        panel_IMG_right_arrow = findViewById(R.id.panel_IMG_right_arrow);

        panel_IMG_background = findViewById(R.id.panel_IMG_background);

        lives = new ImageView[]{
                findViewById(R.id.panel_IMG_heart1), findViewById(R.id.panel_IMG_heart2), findViewById(R.id.panel_IMG_heart3)};

        characters = new int[]{R.drawable.cherry, R.drawable.monster1, R.drawable.monster2, R.drawable.monster3};

        panel_LBL_score = findViewById(R.id.panel_LBL_score);

        gameOverScreen = new Intent(this, Game_over_panel.class);
        Glide
                .with(this)
                .load(R.drawable.three_lanes)
                .centerCrop()
                .into(panel_IMG_background);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (sensorType == 1)
            sensors.getSensorManager().registerListener(accSensorEventListener, sensors.getAccSensor(), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorType == 1)
            sensors.getSensorManager().unregisterListener(accSensorEventListener);
    }
}