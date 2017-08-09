package py.com.pops.wakeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MonitorFinishedActivity extends AppCompatActivity implements OnMapReadyCallback {

    public SharedPreferences shared_data;
    public double point1_latitude;
    public double point1_longitude;
    public double point2_latitude;
    public double point2_longitude;
    public Marker marker_1;
    public Marker marker_2;
    public GoogleMap mMap;
    public double final_distance;
    public Haversine haversine;
    public FloatingActionButton save_btn;
    public SharedPreferences config_settings;
    public boolean from_loc = false;
    public LatlngDatabase db;
    public ArrayList<WakeappLatLngPoints> points;
    public ArrayList<WakeappLatLngPoints> puntitos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_finished);

        shared_data = getSharedPreferences("service_preferences", 0);
        config_settings = getSharedPreferences("config_settings",0);
        Intent intent = getIntent();
        String tiempo = intent.getStringExtra("time_text");
        this.haversine = new Haversine();
        this.save_btn = (FloatingActionButton) findViewById(R.id.finish_save_btn);
        this.point1_latitude = Double.valueOf(shared_data.getString("Point1_latitude", "0.1"));
        this.point1_longitude = Double.valueOf(shared_data.getString("Point1_longitude", "0.1"));
        this.point2_latitude = Double.valueOf(shared_data.getString("Point2_latitude", "0.1"));
        this.point2_longitude = Double.valueOf(shared_data.getString("Point2_longitude", "0.1"));
        this.final_distance = haversine.haversine(point1_latitude, point1_longitude, point2_latitude, point2_longitude);

        from_loc = shared_data.getBoolean("from_loc", false);
            if(from_loc){
                save_btn.setVisibility(View.GONE);
            }


        TextView tiempo_txt = (TextView) findViewById(R.id.finished_monitor_time);
        tiempo_txt.setText(tiempo);

        TextView distance_txt = (TextView) findViewById(R.id.finished_monitor_distance);
        String distance_string = "Distancia recorrida " + round(Double.valueOf(shared_data.getString("final_distance","0.0")),1) + " Km";
        distance_txt.setText(distance_string);


        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.finished_monitor_map);
        mapFragment.getMapAsync(this);
        points = new ArrayList<>();
        db = new LatlngDatabase(this);
        points = db.getLatlng();

        for(WakeappLatLngPoints point : points){
            System.out.println("ID: " + point.getId());
            System.out.println("Latitude: " + point.getLatitude());
            System.out.println("Longitude: " + point.getLongitude());
            System.out.println("kmh: " + point.getKmh());
        }

        puntitos = latlngFilter(points);

        for(WakeappLatLngPoints punto : puntitos){
            System.out.println("punto_ID: " + punto.getId());
            System.out.println("punto_Latitude: " + punto.getLatitude());
            System.out.println("punto_Longitude: " + punto.getLongitude());
            System.out.println("punto_kmh: " + punto.getKmh());
        }
        db.clearLatlng();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(from_loc){
            Intent intent = new Intent(this,MainLocationsActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this,MainMapActivity.class);
            intent.putExtra("message","test");
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker_1 = googleMap.addMarker(new MarkerOptions().position(
                new LatLng(point1_latitude, point1_longitude)).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.mylocation_icon)));

        /*marker_2 = googleMap.addMarker(new MarkerOptions().position(
                new LatLng(point2_latitude, point2_longitude)).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.map_marker)
        ));*/

        /*Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(point1_latitude,point1_longitude), new LatLng(point2_latitude,point2_longitude))
                .width(5)
                .color(Color.RED));*/


        for(int i = 0; i<puntitos.size() - 1; i++){

            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(puntitos.get(i).getLatitude(),puntitos.get(i).getLongitude()), new LatLng(puntitos.get(i + 1).getLatitude(),puntitos.get(i + 1).getLongitude()))
                    .width(5)
                    .color(Color.RED));
        }

        if(puntitos != null){
            WakeappLatLngPoints last_point = puntitos.get(0);
            marker_2 = googleMap.addMarker(new MarkerOptions().position(
                    new LatLng(last_point.getLatitude(), last_point.getLongitude())).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.map_marker)
            ));
            midPoint(point1_latitude, point1_longitude, last_point.getLatitude(), last_point.getLongitude());
        }











    }

    public void onSaveLocationClick(View v){
        SavelocationFragment savelocation_dialog = new SavelocationFragment(this);
        savelocation_dialog.show(getFragmentManager(), "fragment_savelocation");
    }

    public void midPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);


        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        //print out in degrees
        System.out.println(Math.toDegrees(lat3) + " " + Math.toDegrees(lon3));

        float zoom = 12.0f;
        if(this.final_distance < 0.8){
            System.out.println("menor a 0.8 km ");
            zoom = 16f;
        } else if (this.final_distance > 0.8 && this.final_distance < 2){
            System.out.println("menor a 2 km ");
            zoom = 15f;
        } else if (this.final_distance > 2 && this.final_distance < 3){
            System.out.println("menor a 3 km ");
            zoom = 13f;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3)), zoom));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_monitor_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.monitor_settings:
                System.out.println("settings del monitor");
                Intent appconfig_activity = new Intent(this, AppConfigActivity.class);
                appconfig_activity.putExtra("message", "inicio la vista de config");
                startActivity(appconfig_activity);
                return true;
        }
        return true;
    }

    public void onClickSpotifyPlaylistF(View v){
        Intent playlist_activity = new Intent(this, PlaylistActivity.class);
        playlist_activity.putExtra("message", "se inicio el playlist");
        startActivity(playlist_activity);
    }

    public ArrayList<WakeappLatLngPoints> latlngFilter(ArrayList<WakeappLatLngPoints> points) {

        ArrayList<WakeappLatLngPoints> filtered_points = new ArrayList<>();

        Haversine hav = new Haversine();
        double min_distance = 0.01;
        double max_distance = 0.4;
        double distance = 0.0;
        ArrayList<Double> distances = new ArrayList<>();
        for(int i = 0; i<points.size() - 1; i++){
            WakeappLatLngPoints point1 = points.get(i);
            WakeappLatLngPoints point2 = points.get(i + 1);
            distance = hav.haversine(point1.getLatitude(),point1.getLongitude(),point2.getLatitude(),point2.getLongitude());
            distances.add(distance);
            if(distance < max_distance){
                filtered_points.add(point1);
                filtered_points.add(point2);
                Log.e("CARGO", "EL PUNTO");
            }
        }
        int c = 0;
        for(Double distance_ : distances){
            c++;
            System.out.println("Distancia numero: " + c + " = " + distance_);
        }


        return filtered_points;
    }

    public static double round(double value, int places) {

        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
