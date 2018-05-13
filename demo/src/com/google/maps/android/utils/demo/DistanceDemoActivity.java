/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.maps.android.utils.demo;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


import static com.google.maps.android.MathUtil.arcHav;
import static com.google.maps.android.MathUtil.havDistance;
import static com.google.maps.android.utils.demo.R.drawable.amu_bubble_mask;
import static java.lang.Math.toRadians;

public class DistanceDemoActivity extends BaseDemoActivity  implements GoogleMap.OnMarkerDragListener {
    private TextView mTextView;
    private TextView textDistance;
    private TextView mGradus;

    private TextView latlng;
    private Marker mMarkerA;
    private Marker mMarkerB;
    private Marker mMarkerС;
    private Marker myLocation;
    private Polyline mPolyline;
    private BroadcastReceiver broadcastReceiver;
    private TextView textView;
    private Button btn_start, btn_stop;
    private MapView mapView;
    GoogleMap map;
    private static final String TAG=DistanceDemoActivity.class.getName();
    public double myLat;
    public double myLng;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);

        if(!runtime_permissions())
            enable_buttons();
        if(broadcastReceiver == null){
          broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {


                  // double lat = (double) intent.getExtras().get("latitude");


                    myLat = intent.getExtras().getDouble("latitude");
                    myLng = intent.getExtras().getDouble("longitude");
                   // latlng.append("\n" +intent.getExtras().get("all"));




// Если маркер myLocation пустой то выполняется это условие
if (myLocation == null) {
    double firstLat=myLat;
    double firstLng=myLng;

    myLocation = getMap().addMarker(new MarkerOptions().position(new LatLng(myLat, myLng)).draggable(true).icon(
            BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    myLocation.setTitle("You");
    // окно с подключением к GPS
    latlng.setTextSize(20);
    latlng.setTextColor(Color.parseColor("#2EBA2E"));
    latlng.setText(" GPS подключен.");

    // начальное отображение места при открытии карты и высота от него
    getMap().moveCamera(CameraUpdateFactory
            .newLatLngZoom(new LatLng(firstLat, firstLng), 10));


}

//Вычисляем угол мужду точками и обновляем
                 double heading = Math.round(SphericalUtil.computeHeading(mMarkerA.getPosition(), mMarkerB.getPosition()));
                  mGradus.setText("Угол между my lock and A "+ heading+" градусов ");

                }
           };
        }
        // BroadcastReceiver будет получать Intent-ы подходящие под условия IntentFilter
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }





    private void enable_buttons() {

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getApplicationContext(),GPS_Service.class);
                startService(i);



            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),GPS_Service.class);
                stopService(i);


                latlng.setTextSize(20);
                latlng.setTextColor(Color.parseColor("#A60000"));
                latlng.setText(" GPS Отключен.");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {





            }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }
    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }else {
                runtime_permissions();
            }
        }
    }

//********************************************
    @Override
    protected int getLayoutId() {
        return R.layout.distance_demo;

    }
    @Override
    protected void startDemo() {
        mTextView = (TextView) findViewById(R.id.textView);
        textDistance = (TextView) findViewById(R.id.textDistance);
        mGradus = (TextView) findViewById(R.id.mGradus);
        latlng= (TextView) findViewById(R.id.latlng);
        // начальное отображение места при открытии и высота от него
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.219240, 44.791624), 0));
        getMap().setOnMarkerDragListener(this);

        // прописываем маркеры
        mMarkerA = getMap().addMarker(new MarkerOptions().position(new LatLng(53.355181, 44.831149)).draggable(true));
        mMarkerA.setTitle("A");
        mMarkerB = getMap().addMarker(new MarkerOptions().position(new LatLng(53.140714, 45.023023)).draggable(true));
        mMarkerB.setTitle("B");
        mMarkerС = getMap().addMarker(new MarkerOptions().position(new LatLng(53.191571, 45.025854)).draggable(true));
        mMarkerС.setTitle("C");

        mPolyline = getMap().addPolyline(new PolylineOptions().geodesic(true));

        Toast.makeText(this, "Включите GPS для начала работы.", Toast.LENGTH_LONG).show();
        showDistance();
    }

    //вычисляем расстояние от А до B и от B до С после суммируем
    private void showDistance() {

        double distance = SphericalUtil.computeDistanceBetween(mMarkerA.getPosition(), mMarkerB.getPosition());
        double distance2 = SphericalUtil.computeDistanceBetween(mMarkerB.getPosition(), mMarkerС.getPosition());

        mTextView.setText("Расстаяние между маркерами: " + formatNumber(distance+distance2) + ".");

        //расстояние между 2 и 3 в метрах
        double distance3 = Math.round(SphericalUtil.computeDistanceBetween(mMarkerB.getPosition(), mMarkerС.getPosition()));
        textDistance.setText("Дистанция от 2 до 3: "+ distance3);

        //Вычисляем угол мужду точками
        double distance4 = Math.round(SphericalUtil.computeHeading(mMarkerB.getPosition(), mMarkerС.getPosition()));



    }

    private void computeHeading(){

        double heading = Math.round(SphericalUtil.computeHeading(mMarkerA.getPosition(), mMarkerB.getPosition()));
        mGradus.setText("Угол между my lock and A "+ heading+" градусов ");
    }
    //рисуются линии между точками
    private void updatePolyline() {

        // не рисует линию пока не будет получена координата
        if (myLocation != null) {
            mPolyline.setPoints(Arrays.asList(myLocation.getPosition(), mMarkerA.getPosition(), mMarkerB.getPosition(), mMarkerС.getPosition()));
        }
    }

    private String formatNumber(double distance) {
        String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }

        return String.format("%4.3f%s", distance, unit);
    }


    // Метод срабатывает при ЗАВЕРШЕНИИ перетаскивании маркеров, вызываются те методы что прописаны
    @Override
    public void onMarkerDragEnd(Marker marker) {
        showDistance();
        updatePolyline();
        computeHeading();
    }
    // Метод срабатывает при НАЧАЛЕ перетаскивании маркеров, вызываются те методы что прописаны
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    // Метод срабатывает при перетаскивании маркеров, вызываются те методы что прописаны
    @Override
    public void onMarkerDrag(Marker marker) {
        showDistance();
        updatePolyline();
        computeHeading();
    }


}
