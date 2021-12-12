package com.example.pacmangame;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class Login_panel extends AppCompatActivity {


    private MaterialButton login_BTN_play_buttons;
    private MaterialButton login_BTN_BTN_play_sensors;
    private MaterialButton login_BTN_top_ten;
    private TextInputEditText text_name;
    private Intent activity_panel;
    //1 - for fast 0 - for slow
    private int speed;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        findView();
        initView();
        changeViewByBTN();

    }

    private void initView() {
        bundle = new Bundle();
        activity_panel = new Intent(this, Activity_panel.class);
    }

    void changeViewByBTN(){

        login_BTN_play_buttons.setOnClickListener(v -> {
            bundle.putInt(Activity_panel.SENSOR_TYPE,0);
            sendDataToActivity ();
            startActivity(activity_panel);
        });

        login_BTN_top_ten.setOnClickListener(v -> startActivity(new Intent(this, Top_ten_panel.class)));


        login_BTN_BTN_play_sensors.setOnClickListener(v -> {
            bundle.putInt(Activity_panel.SENSOR_TYPE,1);
            sendDataToActivity();
            startActivity(activity_panel);
        });
    }


    public void sendDataToActivity () {
        String namePlayer;
        if (text_name.getText().toString()!="")
            namePlayer = text_name.getText().toString();
        else
            namePlayer ="null";

        //Intent topTenScreen = new Intent(this, Top_ten_panel.class);
        bundle.putString(Activity_panel.NAME, namePlayer);
        bundle.putInt(Activity_panel.SPEED, speed);
        activity_panel.putExtras(bundle);

    }

    private void findView() {

//        gameOverPanel_IMG_replay=findViewById(R.id.gameOverPanel_IMG_replay);

        login_BTN_play_buttons = findViewById(R.id.login_BTN_buttons);
        login_BTN_BTN_play_sensors=findViewById(R.id.login_BTN_sensors);
        login_BTN_top_ten=findViewById(R.id.login_BTN_topTen);

        text_name = findViewById(R.id.login_name);

        ImageView login_IMG_background;
        login_IMG_background = findViewById(R.id.login_IMG_background);

        Glide
                .with(this)
                .load(R.drawable.login_background)
                .centerCrop()
                .into(login_IMG_background);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_fast:
                if (checked)
                    speed=1;
                    break;
            case R.id.radio_slow:
                if (checked)
                    speed=0;
                    break;
        }

    }
}