package com.example.pacmangame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Fragment_Map extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView locationDetails ;

    public void setActivity(AppCompatActivity activity) {
    }
    public void setCallBack_map(CallBack_Map callBack_map) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_map, container, false);
        findViews(view);
        initViews();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        if(mapFragment == null){
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.google_map , mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return view;
    }


    private void initViews() {
    }


    private void findViews(View view) {
        FrameLayout map = view.findViewById(R.id.google_map);
        locationDetails = view.findViewById(R.id.data);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mMap = googleMap;
    }


    public void pointOnMap(double x, double y) {
        if (x==0.0 && y==0.0) {
            locationDetails.setVisibility(View.VISIBLE);
            locationDetails.setText("No information about location");
        }
        else {
            locationDetails.setVisibility(View.INVISIBLE);
            LatLng point = new LatLng(x, y);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(point).title(""));
            moveToCurrentLocation(point);
        }
        }

    private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

    }



}
