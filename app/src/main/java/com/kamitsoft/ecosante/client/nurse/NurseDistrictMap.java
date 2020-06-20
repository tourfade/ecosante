package com.kamitsoft.ecosante.client.nurse;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.admin.DistrictEditor;
import com.kamitsoft.ecosante.model.DistrictInfo;
import com.kamitsoft.ecosante.model.json.Point;
import com.kamitsoft.ecosante.model.viewmodels.DistrictViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.lifecycle.ViewModelProviders;

public class NurseDistrictMap extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, LocationListener {

    private DistrictViewModel districtViewModel;
    private GoogleMap map;

    private int strokeColor, fillColor;
    private DistrictInfo myDistrictInfo ;
    private Polygon polygon;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nurse_district_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        districtViewModel = ViewModelProviders.of(this).get(DistrictViewModel.class);
        requestSync();
        districtViewModel.getDistrict(app.getCurrentUser().getDistrictUuid())
                .observe(this, districtInfo -> {

            if(map != null && districtInfo != null) {
                map.clear();
                contextActivity.setTitle(districtInfo.getName());
                myDistrictInfo = districtInfo;

                PolygonOptions rectOptions = new PolygonOptions()
                        .clickable(true)
                        .strokeJointType(JointType.ROUND)
                        .strokeColor(districtInfo.getArea().strokeColor)
                        .fillColor(ColorUtils.setAlphaComponent(districtInfo.getArea().fillColor, 30))
                        .strokeWidth(2f)
                        .geodesic(true)
                        .zIndex(0f);

                districtInfo.getArea().points.forEach(a->rectOptions.add(a.toLatLon()));
                polygon = map.addPolygon(rectOptions);
                polygon.setTag(districtInfo);

                center(getCenter(polygon.getPoints()));
                }
        });
        fillColor = Color.parseColor("#ffa911");
        strokeColor  = Color.parseColor("#0494ff");
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private LatLng getCenter(List<LatLng> points) {

        double lat = 0, lon = 0;
        for(LatLng ll:points){
            lat += ll.latitude;
            lon += ll.longitude;
        }
        return new LatLng(lat/points.size(), lon/points.size());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (contextActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && contextActivity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            return;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }



    private void center(LatLng c) {

        if (c != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(c, 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(c)      // Sets the center of the map to location user
                    .zoom(12)                   // Sets the zoom
                    .tilt(15)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }



    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){
        if(requestCode == 101) {
            if (contextActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || contextActivity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }
    public Marker addText(final Context context, final LatLng location,
                          final String text, final int padding,
                          final int fontSize) {
        Marker marker = null;

        if (context == null || map == null || location == null || text == null
                || fontSize <= 0) {
            return marker;
        }

        final TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(fontSize);

        final Paint paintText = textView.getPaint();

        final Rect boundsText = new Rect();
        paintText.getTextBounds(text, 0, textView.length(), boundsText);
        paintText.setTextAlign(Paint.Align.CENTER);

        final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        final Bitmap bmpText = Bitmap.createBitmap(boundsText.width() + 2
                * padding, boundsText.height() + 2 * padding, conf);

        final Canvas canvasText = new Canvas(bmpText);
        paintText.setColor(Color.BLACK);

        canvasText.drawText(text, canvasText.getWidth() / 2,
                canvasText.getHeight() - padding - boundsText.bottom, paintText);

        final MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(bmpText))
                .anchor(0.5f, 1);

        marker = map.addMarker(markerOptions);

        return marker;
    }

    @Override
    protected Class<?> getEntity() {
        return DistrictInfo.class;
    }
}
