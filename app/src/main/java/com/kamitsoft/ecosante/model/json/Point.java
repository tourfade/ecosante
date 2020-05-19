package com.kamitsoft.ecosante.model.json;

import com.google.android.gms.maps.model.LatLng;

public class Point{
    public double lat;
    public double lon;
    public Point(){}
    public Point(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public LatLng toLatLon() {
        return new LatLng(lat, lon);
    }
}
