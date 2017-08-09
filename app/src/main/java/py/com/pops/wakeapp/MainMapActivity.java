package py.com.pops.wakeapp;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton locationIcon;

    public double myLatitude = 0;
    public double myLongitude = 0;
    public boolean changelistener = true;
    public Haversine haversine_calc;
    public double distance;
    public SharedPreferences config_settings;
    public double min_distance;
    public LocationManager locationManager;
    public SharedPreferences service_preferences;
    public boolean from_loc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        //finishAffinity();
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        Log.e("MESSAGE", message);
        String value = "from_locations";
        if(message.equalsIgnoreCase(value)){
            from_loc = true;
            Log.e("FROM LOCTIONS", "TRUE");
        } else {
            Log.e("FROM LOCTIONS", "FALSE");
        }

        haversine_calc = new Haversine();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.kitkat_logo);

        service_preferences = getSharedPreferences("service_preferences", 0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        locationIcon = (FloatingActionButton) findViewById(R.id.location_icon);

        locationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMylocation();
                System.out.println("SE APRETO");
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        return true;
    }

    public void onClickStartMonitor(View v){
        System.out.println("se apreto start monitor");
        config_settings = getSharedPreferences("config_settings", 0);
        min_distance = Double.parseDouble(config_settings.getString("config_distance","0.0"));
        LatLng center = mMap.getCameraPosition().target;
        Location myLocation = mMap.getMyLocation();
        double selected_lat = center.latitude;
        double selected_log = center.longitude;
        double my_lat = myLocation.getLatitude();
        double my_log = myLocation.getLongitude();
        distance = haversine_calc.haversine(my_lat,my_log,selected_lat,selected_log);
        if(!checkGps()){
            AlertFragment alert_dialog = new AlertFragment("Atencion!","Debe activar el gps de su dispositivo para continuar","gps_alert");
            alert_dialog.show(getFragmentManager(), "fragment_alert");
        } else {
            if(distance < min_distance){
                AlertFragment alert_dialog = new AlertFragment("Atencion!","Ha seleccionado en el rango minimo de distancia","default");
                alert_dialog.show(getFragmentManager(),"fragment_alert");
            } else {
                Intent monitor_activity = new Intent(this,LocationMonitor.class);
                monitor_activity.putExtra("point2_location_latitude",selected_lat);
                monitor_activity.putExtra("point2_location_longitude", selected_log);
                monitor_activity.putExtra("point1_location_latitude",my_lat);
                monitor_activity.putExtra("point1_location_longitude",my_log);
                monitor_activity.putExtra("from_map_or_locations_activity",true);
                monitor_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                SharedPreferences.Editor editor = service_preferences.edit();
                long unixTime = System.currentTimeMillis();
                editor.putBoolean("from_loc",false);
                editor.putLong("start_time", unixTime);
                editor.commit();
                startActivity(monitor_activity);
                finish();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.on_check:

                if(from_loc){
                    onBackPressed();
                } else {
                    Intent main_locations_activity = new Intent(this, MainLocationsActivity.class);
                    main_locations_activity.putExtra("message", "se inicio el locations");
                    startActivity(main_locations_activity);
                }

                return true;
            case R.id.on_settings:
                Intent appconfig_activity = new Intent(this, AppConfigActivity.class);
                appconfig_activity.putExtra("message", "Se inicio la configuracion");
                appconfig_activity.putExtra("single_config",false);
                startActivity(appconfig_activity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        System.out.println("se ejecuta todo alpedo");

    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!checkGps()){
            AlertFragment alert_dialog = new AlertFragment("Atencion!","Debe activar el gps de su dispositivo para continuar","gps_alert");
            alert_dialog.show(getFragmentManager(), "fragment_alert");
        }
    }

    public boolean checkGps(){
        boolean is_active = false;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            is_active = true;
        }
        return is_active;
    }

    public void getMylocation(){
        LatLng latLng = new LatLng(myLatitude,myLongitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f);
        mMap.animateCamera(cameraUpdate);
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
    }


    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            myLatitude = location.getLatitude();
            myLongitude = location.getLongitude();
            LatLng loc = new LatLng(myLatitude, myLongitude);
            System.out.println("PPPPPPPPPP");
            if(mMap != null && changelistener){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                changelistener = false;
                System.out.println("HHHHHHHHHH");
            }
        }
    };


}
