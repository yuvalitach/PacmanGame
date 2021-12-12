package com.example.pacmangame;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pacmangame.objects.MyDB;
import com.google.gson.Gson;

public class Top_ten_panel extends AppCompatActivity {

    private Fragment_List fragmentList;
    private Fragment_Map fragmentMap;
    private MyDB md;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_ten_activity);

        fragmentList = new Fragment_List();
        fragmentList.setActivity(this);
        fragmentList.setCallBackList(callBackList);
        getSupportFragmentManager().beginTransaction().add(R.id.frame1, fragmentList).commit();

        fragmentMap = new Fragment_Map();
        fragmentMap.setActivity(this);
        fragmentMap.setCallBack_map(callBack_map);
        getSupportFragmentManager().beginTransaction().add(R.id.frame2, fragmentMap).commit();

        String js = MSPV3.getMe().getString("MY_DB", "");
        md = new Gson().fromJson(js, MyDB.class);
    }


    CallBack_Map callBack_map = new CallBack_Map() {
        @Override
        public void mapClicked(double lat, double lon) {

        }
    };

    CallBack_List callBackList = new CallBack_List() {
        @Override
        public void locateOnMap(double lat, double lon, String playerName) {
            fragmentMap.pointOnMap(lat,lon);
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
    }
}
