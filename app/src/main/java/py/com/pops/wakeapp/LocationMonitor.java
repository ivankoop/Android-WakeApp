package py.com.pops.wakeapp;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class LocationMonitor extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public double point1_location_latitude;
    public double point1_location_longitude;
    public double point2_location_latitude;
    public double point2_location_longitude;
    public double final_distance;
    public Intent service_intent;
    public TextView monitor_distance;
    public Marker marker;
    public boolean from_map_or_locations_activity = false;
    public SharedPreferences service_preferences;
    public boolean service_is_on = false;
    public boolean FROM_ALARM = false;
    public SharedPreferences alarm_config;
    public boolean startingActivity = false;
    public static LocationMonitor me;
    public double start_point_lat;
    public double start_point_lng;
    public double intermediate_distance;
    public float speed;
    public TextView time_and_speed_txt;
    public boolean from_locations = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_monitor);

        me = this;

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.kitkat_logo);

        service_preferences = getSharedPreferences("service_preferences",0);



    }

    @Override
    protected void onUserLeaveHint() {
        if (startingActivity) {
            startingActivity = false;
        } else {
            finish();
        }
        super.onUserLeaveHint();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        Bundle monitor_bundle = intent.getExtras();


        service_preferences = getSharedPreferences("service_preferences", 0);
        alarm_config = getSharedPreferences("alarm_config", 0);
        FROM_ALARM = alarm_config.getBoolean("FROM_ALARM", false);
        Log.e("FROM ALARM ES ", String.valueOf(FROM_ALARM));
        Haversine haversine_calc = new Haversine();
        from_map_or_locations_activity = false;
        this.from_map_or_locations_activity = monitor_bundle.getBoolean("from_map_or_locations_activity");
        this.monitor_distance = (TextView) findViewById(R.id.monitor_distance);
        this.time_and_speed_txt = (TextView) findViewById(R.id.monitor_time_and_speed);
        this.from_locations = intent.getBooleanExtra("from_locations",false);
        if (from_map_or_locations_activity) {

            Log.e("FROM MAP OR LOCATIONS" ,"TRUE");

            this.point1_location_latitude = monitor_bundle.getDouble("point1_location_latitude");
            this.point1_location_longitude = monitor_bundle.getDouble("point1_location_longitude");
            this.point2_location_latitude = monitor_bundle.getDouble("point2_location_latitude");
            this.point2_location_longitude = monitor_bundle.getDouble("point2_location_longitude");

            this.start_point_lat = this.point1_location_latitude;
            this.start_point_lng = this.point1_location_longitude;

            SharedPreferences.Editor editor = service_preferences.edit();
            long unixTime = System.currentTimeMillis();
            editor.putLong("start_time", unixTime);
            editor.putString("Point1_latitude", Double.toString(this.point1_location_latitude));
            editor.putString("Point1_longitude", Double.toString(this.point1_location_longitude));



            editor.putString("Point2_latitude", Double.toString(this.point2_location_latitude));
            editor.putString("Point2_longitude", Double.toString(this.point2_location_longitude));
            editor.putString("Distance", Double.toString(this.final_distance));
            editor.putBoolean("IsOn", true);
            this.final_distance = haversine_calc.
                    haversine(point1_location_latitude, point1_location_longitude,
                            point2_location_latitude, point2_location_longitude);
            editor.putString("final_distance", String.valueOf(final_distance));
            editor.commit();


            this.monitor_distance.setText("A " + round(this.final_distance, 1) + " Km.");

        } else {

            this.point1_location_latitude = Double.valueOf(service_preferences.getString("Point1_latitude", "0.1"));
            this.point1_location_longitude = Double.valueOf(service_preferences.getString("Point1_longitude", "0.1"));

            this.point2_location_latitude = Double.valueOf(service_preferences.getString("Point2_latitude", "0.1"));
            this.point2_location_longitude = Double.valueOf(service_preferences.getString("Point2_longitude", "0.1"));
            this.service_is_on = service_preferences.getBoolean("IsOn", false);

            Log.e("LATITUDE2", Double.toString(point2_location_latitude));
            Log.e("LONGITUDE2", Double.toString(point2_location_longitude));

        }


        System.out.println(this.point1_location_latitude);
        System.out.println(this.point1_location_longitude);
        System.out.println(this.point2_location_latitude);
        System.out.println(this.point2_location_longitude);


        LocalBroadcastManager.getInstance(LocationMonitor.this).registerReceiver(ErrorReceiver, new IntentFilter("ErrorReceiver"));


        System.out.println("LA DISTANCIA ES DE: " + haversine_calc.haversine(point1_location_latitude,
                point1_location_longitude, point2_location_latitude, point2_location_longitude));


        //Uri.parse(myRingtone.getUri() + "/" + myRingtone.getId());

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.monitor_map);
        mapFragment.getMapAsync(this);

    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(LocationMonitor.this).unregisterReceiver(LatlnReceiver);
        super.onPause();

    }

    private BroadcastReceiver ErrorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean error = intent.getBooleanExtra("ERROR", false);
            if (error) {
                AlertFragment alert_dialog = new AlertFragment("ERROR!", "Ha ocurrido un error con el sistema de localización. Vuelva a intentarlo mas tarde", "location_error");
                alert_dialog.show(getFragmentManager(), "fragment_alert");
            }
            LocalBroadcastManager.getInstance(LocationMonitor.this).unregisterReceiver(ErrorReceiver);
        }
    };


    public BroadcastReceiver LatlnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Bundle receive_latln = intent.getExtras();
            if (marker != null) {
                marker.remove();
            }


            final_distance = intent.getDoubleExtra("final_distance", 0.0);
            point1_location_latitude = intent.getDoubleExtra("latitudeRS", 0.0);
            point1_location_longitude = intent.getDoubleExtra("longitudeRS", 0.0);
            speed = intent.getFloatExtra("speed", 0);
            String final_time = intent.getStringExtra("final_time");
            //System.out.println("POINT1_LAT: " + String.valueOf(point1_location_latitude));
            //System.out.println("POINT1_LNG: " + String.valueOf(point1_location_longitude));

            Log.e("SPEEED", String.valueOf(speed));

            if (point1_location_latitude == 0.0 || point1_location_longitude == 0.0) {
                monitor_distance.setText("Buscando GPS..");
            } else {
                monitor_distance.setText("A " + round(final_distance, 1) + " Km.");
                marker = mMap.addMarker(new MarkerOptions().position(
                        new LatLng(point1_location_latitude, point1_location_longitude)).icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.mylocation_icon)));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(point1_location_latitude, point1_location_longitude), 16f));
            }


            //Haversine haversine = new Haversine();

            //intermediate_distance = haversine.haversine(start_point_lat,start_point_lng,point1_location_latitude,point1_location_longitude);
            Log.e("INTERMEDIATE DISTANCE", String.valueOf(intermediate_distance));
            int kmh_speed = (int) ((speed * 3600) / 1000);
            Log.e("KMH SPEEED", String.valueOf(kmh_speed));
            time_and_speed_txt.setText(final_time + " (" + kmh_speed + " Km/h)");
            /*if(kmh_speed > 4){
                Log.e("PASO","LOS 5km");

                Log.e("START LAT" , String.valueOf(start_point_lat));
                Log.e("START LONG", String.valueOf(start_point_lng));

                Log.e("POINT1 LAT", String.valueOf(point1_location_latitude));
                Log.e("POINT1 LONG",String.valueOf(point1_location_longitude));

                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(start_point_lat, start_point_lng), new LatLng(point1_location_latitude, point1_location_longitude))
                        .width(5)
                        .color(Color.RED));

                start_point_lat = intent.getDoubleExtra("latitudeRS",0.0);
                start_point_lng = intent.getDoubleExtra("longitudeRS",0.0);
            }*/


        }
    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (marker != null) {
            marker.remove();
        }


        marker = mMap.addMarker(new MarkerOptions().position(
                new LatLng(this.point1_location_latitude, this.point1_location_longitude)).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.mylocation_icon)));


        mMap.addMarker(new MarkerOptions().position(
                new LatLng(this.point2_location_latitude, this.point2_location_longitude)).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));

        LocalBroadcastManager.getInstance(LocationMonitor.this).registerReceiver(LatlnReceiver, new IntentFilter("LatlnReceiver"));
        service_intent = new Intent(this, WakeappService.class);
        if (isMyServiceRunning(WakeappService.class)) {
            //Toast.makeText(getBaseContext(), "Service is already running", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getBaseContext(), "Service not running", Toast.LENGTH_SHORT).show();
            midPoint(point1_location_latitude, point1_location_longitude, point2_location_latitude, point2_location_longitude);
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            //Log.i("tag", "This'll run 300 milliseconds later");
                            startService(service_intent);
                        }
                    },
                    4000);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_monitor_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.monitor_settings:
                System.out.println("settings del monitor");
                Intent appconfig_activity = new Intent(this, AppConfigActivity.class);
                appconfig_activity.putExtra("message", "inicio la vista de config");
                startActivity(appconfig_activity);
                return true;
        }
        return true;
    }

    public void midPoint(double lat1, double lon1, double lat2, double lon2) {

        double dLon = Math.toRadians(lon2 - lon1);


        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);


        System.out.println(Math.toDegrees(lat3) + " " + Math.toDegrees(lon3));

        float zoom = 12.0f;
        if (this.final_distance < 0.8) {
            System.out.println("menor a 0.8 km ");
            zoom = 16f;
        } else if (this.final_distance > 0.8 && this.final_distance < 2) {
            System.out.println("menor a 2 km ");
            zoom = 15f;
        } else if (this.final_distance > 2 && this.final_distance < 3) {
            System.out.println("menor a 3 km ");
            zoom = 13f;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3)), zoom));


    }

    public void onClickStopMonitor(View v) {
        SharedPreferences settings = getSharedPreferences("start_settings",0);
        boolean on_map = settings.getBoolean("on_map",false);
        ConfirmFragment confirm_dialog = new ConfirmFragment("Servicio de Monitoreo", "Esta seguro que desea parar el servicio de monitoreo?", "turnoff_service",this);
        confirm_dialog.show(getFragmentManager(), "fragment_alert");

    }

    public static double round(double value, int places) {

        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void onClickSpotifyPlaylist(View v) {
        Intent playlist_activity = new Intent(this, PlaylistActivity.class);
        playlist_activity.putExtra("message", "se inicio el playlist");
        startActivity(playlist_activity);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void startActivity(Intent intent) {
        startingActivity = true;
        super.startActivity(intent);
    }




}
