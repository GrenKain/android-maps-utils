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

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.Arrays;


import static com.google.maps.android.MathUtil.arcHav;
import static com.google.maps.android.MathUtil.havDistance;
import static java.lang.Math.toRadians;

public class DistanceDemoActivity extends BaseDemoActivity implements GoogleMap.OnMarkerDragListener {
    private TextView mTextView;
    private TextView textDistance;
    private TextView mGradus;
    private TextView textComputeOf;
    private Marker mMarkerA;
    private Marker mMarkerB;
    private Marker mMarkerС;
    private Polyline mPolyline;

    @Override
    protected int getLayoutId() {
        return R.layout.distance_demo;
    }

    @Override
    protected void startDemo() {
        mTextView = (TextView) findViewById(R.id.textView);
        textDistance = (TextView) findViewById(R.id.textDistance);
        mGradus = (TextView) findViewById(R.id.mGradus);
        textComputeOf = (TextView) findViewById(R.id.textComputeOf);

        // начальное отображение места при открытии и высота от него
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.219240, 44.791624), 7));
        getMap().setOnMarkerDragListener(this);

        mMarkerA = getMap().addMarker(new MarkerOptions().position(new LatLng(53.219240, 44.791624)).draggable(true));
        mMarkerB = getMap().addMarker(new MarkerOptions().position(new LatLng(53.140714, 45.023023)).draggable(true));
        mMarkerС = getMap().addMarker(new MarkerOptions().position(new LatLng(53.191571, 45.025854)).draggable(true));
        mPolyline = getMap().addPolyline(new PolylineOptions().geodesic(true));

        Toast.makeText(this, "Перетащите маркеры", Toast.LENGTH_LONG).show();
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
        mGradus.setText("Угол между B и А:(omputeHeading) "+ distance4+" градусов ");
        
    }
    //рисуются линии между точками
    private void updatePolyline() {
        mPolyline.setPoints(Arrays.asList(mMarkerA.getPosition(), mMarkerB.getPosition(),mMarkerС.getPosition()));
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

    @Override
    public void onMarkerDragEnd(Marker marker) {
        showDistance();
        updatePolyline();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        showDistance();
        updatePolyline();
    }
}
