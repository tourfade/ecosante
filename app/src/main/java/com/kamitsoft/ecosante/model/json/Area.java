package com.kamitsoft.ecosante.model.json;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class Area {


    public List<Point> points;
    public int strokeColor;
    public int fillColor;

    public Area(){
        points = new ArrayList<>();
    }



}
