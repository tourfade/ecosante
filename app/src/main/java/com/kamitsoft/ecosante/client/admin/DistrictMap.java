package com.kamitsoft.ecosante.client.admin;

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
import com.kamitsoft.ecosante.model.DistrictInfo;
import com.kamitsoft.ecosante.model.json.Point;
import com.kamitsoft.ecosante.model.viewmodels.DistrictViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

public class DistrictMap extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, LocationListener, GoogleMap.OnMarkerDragListener {

    private DistrictViewModel districtViewModel;
    private GoogleMap map;

    private List<Marker> editingMarks;
    private Polygon editingPoly;
    private int strokeColor, editingStrokeColor, fillColor;
    private Stream<DistrictInfo> data;
    private DistrictInfo editingDistrict;
    private List<Polygon> polygons;
    private long timer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.district_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        districtViewModel = ViewModelProviders.of(this).get(DistrictViewModel.class);
        editingMarks = new ArrayList<>();
        polygons = new ArrayList<>();
        requestSync();
        districtViewModel.getDistricts().observe(this, districtInfos -> {

            data =  districtInfos
                    .stream()
                    .filter(d -> !d.isDeleted());


            if(map != null) {
                polygons.forEach(p->p.remove());
                polygons.clear();
                data.forEach(d -> addPolygon(d));
                data = null;
            }
        });
        editingStrokeColor =   Color.parseColor("#f76c05");
        fillColor = Color.parseColor("#ffa911");
        strokeColor  = Color.parseColor("#0494ff");
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        view.findViewById(R.id.newPolygon).setOnClickListener(v -> {
            addNewPolygon();
        });
        view.findViewById(R.id.delSelectedPolygons).setOnClickListener(v -> {
            if(editingPoly != null && editingDistrict !=null){
                editingPoly.remove();
                districtViewModel.delete(editingDistrict);
            }
            endEditing();

        });
        view.findViewById(R.id.editPolygons).setOnClickListener(v -> {
            if(editingPoly == null || editingDistrict == null){
               return;
            }
            openEditor();
        });

    }

    private void displayActions(boolean dis){
        int visibility = dis ? View.VISIBLE : View.GONE;
        getView().findViewById(R.id.delSelectedPolygons).setVisibility(visibility);
        getView().findViewById(R.id.editPolygons).setVisibility(visibility);
    }

    public void openEditor(){
        app.setCurrentDistrict(editingDistrict);
        getView().post(()-> endEditing());
        contextActivity.showFragment(DistrictEditor.class,
                R.animator.card_flip_left_in,
                R.animator.card_flip_left_out,
                R.animator.card_flip_right_in,
                R.animator.card_flip_right_out);
    }


    @Override
    public void onResume() {
        super.onResume();
        endEditing();
        contextActivity.setTitle("Carte des Districts");

    }

    private void addPolygon(DistrictInfo districtInfo) {
        if(map == null){return;}
        PolygonOptions rectOptions = new PolygonOptions()
                                            .clickable(true)
                                            .strokeJointType(JointType.ROUND)
                                            .strokeColor(districtInfo.getArea().strokeColor)
                                            .fillColor(districtInfo.getArea().fillColor)
                                            .strokeWidth(2.5f)
                                            .geodesic(true)
                                            .zIndex(0f);

        districtInfo.getArea().points.forEach(a->rectOptions.add(a.toLatLon()));
        Polygon p = map.addPolygon(rectOptions);
        p.setTag(districtInfo);
        polygons.add(p);
        if(editingPoly != null){
            editingPoly.setTag(editingDistrict);
            editPolygon(editingPoly);
        }
    }
    private void endEditing(){
        if(editingMarks != null) {
            editingMarks.forEach(m -> m.remove());
            editingMarks.clear();
        }
        editingPoly = null;
        editingDistrict = null;
        displayActions(false);
        contextActivity.setTitle("Carte des Districts");

    }
    private void editPolygon(Polygon polygon) {


        endEditing();
        editingDistrict = (DistrictInfo) polygon.getTag();

        contextActivity.setTitle((editingDistrict==null || Utils.isNullOrEmpty(editingDistrict.getName()))? "Nouveau District":editingDistrict.getName());
        editingPoly = polygon;

        int i = 0;
        for(LatLng p:editingPoly.getPoints()){

            if(i >= editingPoly.getPoints().size() - 1){
                break;
            }
            editingMarks.add(map.addMarker(new MarkerOptions()
                    .position(p)
                    .icon(i == 0? BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE):null)
                    .draggable(true)));
            i++;
        }
        displayActions(true);
    }

    private void addNewPolygon() {
        if(connectedUser == null){
            return;
        }
        DistrictInfo distric = new DistrictInfo();
        distric.setAccountID(connectedUser.getAccountID());
        distric.setMaxNurse(20);
        distric.setMaxPhysist(20);
        LatLng center = map.getCameraPosition().target;
        float zoom = map.getCameraPosition().zoom;
        float delta = 0.008f*zoom;

        distric.getArea().points.add(new Point(center.latitude - delta, center.longitude - delta));
        distric.getArea().points.add(new Point(center.latitude - delta, center.longitude + delta));
        distric.getArea().points.add(new Point(center.latitude + delta, center.longitude + delta));
        distric.getArea().points.add(new Point(center.latitude + delta, center.longitude - delta));
        distric.getArea().points.add(new Point(center.latitude - delta, center.longitude - delta));

        districtViewModel.insert(distric);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        center();
        map.setOnMarkerDragListener(this);
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

        map.setOnMapClickListener(latLng -> {
            if(editingPoly!=null && !PolyUtil.containsLocation(latLng, editingPoly.getPoints(), true)){
               endEditing();
            }

        });
        map.setOnMapLongClickListener(latLng -> {
            if(editingPoly == null){
                return;
            }
            addVertex(latLng);

        });

        map.setOnPolygonClickListener(this::editPolygon);

        if(data != null) {
            map.clear();
            data.forEach(this::addPolygon);
        }

    }

    private void addVertex(LatLng latLng){
        if(editingPoly == null){
            return;
        }
        int idx = PolyUtil.locationIndexOnEdgeOrPath(latLng, editingPoly.getPoints(), true, true, 250);

        if(idx >= 0){
            PolygonOptions rectOptions = new PolygonOptions();
            rectOptions.clickable(true)
                    .strokeColor(editingStrokeColor)
                    .geodesic(true)
                    .strokeWidth(2f)
                    .strokeJointType(JointType.ROUND)
                    .zIndex(0f);
            idx = (idx+1) % editingMarks.size();
            Marker newMarker = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
            editingMarks.add(idx, newMarker);
            for(Marker m: editingMarks){
                rectOptions.add(m.getPosition());
            }
            editingPoly.remove();
            editingPoly = map.addPolygon(rectOptions);
            editingPoly.setTag(editingDistrict);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        editingPoly.setStrokeColor(editingStrokeColor);
    }

    @Override
    public void onMarkerDrag(Marker marker) {

        PolygonOptions rectOptions = new PolygonOptions();
        rectOptions.clickable(true)
                .strokeColor(editingStrokeColor)
                .geodesic(true)
                .strokeWidth(2f)
                .strokeJointType(JointType.ROUND)
                .zIndex(0f);
        editingPoly.remove();
        editingPoly.getPoints().clear();
        editingMarks.stream().forEach(m-> {
            rectOptions.add(marker.equals(m) ? marker.getPosition():m.getPosition());
        });
        editingPoly = map.addPolygon(rectOptions);
        editingPoly.setTag(editingDistrict);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        editingPoly.remove();
        editingPoly.getPoints().clear();
        editingDistrict.getArea().points.clear();
        editingMarks.stream().forEach(m-> {
            if(marker.equals(m)) {
                editingDistrict.getArea().points.add(new Point(marker.getPosition().latitude, marker.getPosition().longitude));
            }
            else{
                editingDistrict.getArea().points.add(new Point(m.getPosition().latitude, m.getPosition().longitude));

            }
        });

        districtViewModel.update(editingDistrict);

    }

    private void center() {
        LocationManager locationManager = (LocationManager) contextActivity.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (contextActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && contextActivity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(10)                   // Sets the zoom
                    .tilt(15)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }



    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){
        if(requestCode == 101) {
            if (contextActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || contextActivity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                center();
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
