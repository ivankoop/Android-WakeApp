package py.com.pops.wakeapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

public class MainLocationsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public LocationManager locationManager;
    public static double my_lat;
    public static double my_lng;
    public RelativeLayout no_loc_text;
    public MainLocationsActivity me;
    public LocationsAdapter adapter;
    public ArrayList<SavedLocation> my_locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_locations);
        Intent intent = getIntent();

        me = this;

        no_loc_text = (RelativeLayout) findViewById(R.id.no_loc_text);

        no_loc_text.setVisibility(View.GONE);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.kitkat_logo);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lasloc = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        if(lasloc != null){
            my_lat = lasloc.getLatitude();
            my_lng = lasloc.getLongitude();
        }


        Haversine haversine = new Haversine();



        RecyclerView locationlist = (RecyclerView) findViewById(R.id.locations_recycler);
        locationlist.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        locationlist.setLayoutManager(layoutManager);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        my_locations = new ArrayList<>();


        /*my_locations.add(new SavedLocation("Universidad Catolica Nuestra Señora", 21, "www.appelsiini.net/assets/2008/5/26/tartu.png",-25.325287, -57.636378,-25.304739, -57.547932));
        my_locations.add(new SavedLocation("La Ofi", 21, "www.appelsiini.net/assets/2008/5/26/tartu.png",-25.304377,-57.547497,-25.304807,-57.548130));
        my_locations.add(new SavedLocation("La Ofi", 21, "www.appelsiini.net/assets/2008/5/26/tartu.png",-25.323587,-57.636143,-25.304807,-57.548130));*/
        LocationsDatabase db = new LocationsDatabase(this);
        my_locations = db.getLocations();
        if(my_locations.size() < 1){
            no_loc_text.setVisibility(View.VISIBLE);
        }
        adapter = new LocationsAdapter(this,my_locations);
        locationlist.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "GPS is not Enabled in your devide", Toast.LENGTH_SHORT).show();
            AlertFragment alert_dialog = new AlertFragment("Atencion!","Debe activar el gps de su dispositivo para continuar","gps_alert");
            alert_dialog.show(getFragmentManager(), "fragment_alert");

        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.e("STARTEO", "Stardted");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.locations_activity_menu, menu);
        final MenuItem item = menu.findItem(R.id.locations_search_icon);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        adapter.setFilter(my_locations);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final ArrayList<SavedLocation> filteredModelList = filter(my_locations, newText);
        adapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private ArrayList<SavedLocation> filter(ArrayList<SavedLocation> models, String query) {
        query = query.toLowerCase();

        final ArrayList<SavedLocation> filteredModelList = new ArrayList<>();
        for (SavedLocation model : models) {
            final String text = model.getLocation_name().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){


        switch (item.getItemId()){
            case R.id.locations_search_icon:
                System.out.println("salio el search");
                /*String uri = "spotify:track:3891NcnNtOOr79jLoVBA1c";
                Intent launcher = new Intent( Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(launcher);*/
                return true;
            case R.id.locations_map_icon:
                System.out.println("salio el map");
                try {
                    Intent main_map_activity = new Intent(this,MainMapActivity.class);
                    main_map_activity.putExtra("message","from_locations");
                    startActivity(main_map_activity);
                } catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            case R.id.locations_settings_icon:
                System.out.println("salio el settings");
                Intent appconfig_activity = new Intent(this,AppConfigActivity.class);
                appconfig_activity.putExtra("message","Se inicio la configuracion");
                appconfig_activity.putExtra("single_config",false);
                startActivity(appconfig_activity);
                return true;
            case android.R.id.home:
                System.out.println("salio el home");
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void showDistanceAlert(){
        AlertFragment alert_dialog = new AlertFragment("Atencion!","Se encuentra en el rango minimo de distancia","distance_alert");
        alert_dialog.show(getFragmentManager(), "fragment_alert");
    }

    public void showMessageEmpty(){
        no_loc_text.setVisibility(View.VISIBLE);
    }


}
