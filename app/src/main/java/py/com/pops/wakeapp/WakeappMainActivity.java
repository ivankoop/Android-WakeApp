package py.com.pops.wakeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.os.Handler;



public class WakeappMainActivity extends AppCompatActivity {

    AnimationDrawable kitkatAnimation;

    public static final String START_SETTINGS = "start_settings";

    public String activity_selector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = getSharedPreferences(START_SETTINGS, 0);

        SharedPreferences.Editor editor = settings.edit();
        LocationsDatabase db = new LocationsDatabase(this);
        if(db.getLocations().size() > 0){
            Log.e("Si","HAY DATOS");

            editor.putBoolean("on_locations",true);
            editor.putBoolean("on_map",false);
            editor.commit();
        } else {
            Log.e("NO", "HAY DATOS");
            editor.putBoolean("on_locations",false);
            editor.putBoolean("on_map",true);
            editor.commit();
        }

        boolean start_boolean = settings.getBoolean("start_boolean", false);
        boolean on_locations = settings.getBoolean("on_locations",false);
        boolean on_map = settings.getBoolean("on_map", true);
        boolean on_service = settings.getBoolean("on_service",false);

        Log.e("ON_MAP",String.valueOf(on_map));
        Log.e("ON_LOCATIONS",String.valueOf(on_locations));
        Log.e("ON_SERVICE",String.valueOf(on_service));

        activity_selector = "on_map";

        if(on_map == false && on_locations == true && on_service == false){
            activity_selector = "on_locations";
        }

        if(on_map == false && on_service == true){
            activity_selector = "on_service";
        }

        if(on_map == true && on_service == true ){
            activity_selector = "on_service";
        }

        final Intent main_map_activty = new Intent(this,MainMapActivity.class);
        main_map_activty.putExtra("message","se inicio el mapa");
        if(!start_boolean){
            editor.putBoolean("start_boolean", true);
            editor.commit();
            new Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            startActivity(main_map_activty);
                            finish();
                        }
                    },
                    3000);
        } else {
            System.out.println("YA ES TRUE");

            switch (activity_selector){
                case "on_map":
                    Log.e("MESSAGE","ON_MAP");
                    startActivity(main_map_activty);
                    finish();
                    break;
                case "on_locations":
                    Log.e("MESSAGE","ON_LOCATIONS");
                    Intent main_locations_activity = new Intent(this,MainLocationsActivity.class);
                    main_locations_activity.putExtra("message","se inicio locations");
                    startActivity(main_locations_activity);
                    finish();
                    break;
                case "on_service":
                    Log.e("MESSAGE","ON_SERVICE");
                    Intent main_monitor_activity = new Intent(this,LocationMonitor.class);
                    main_monitor_activity.putExtra("message","se inicio monitor");
                    startActivity(main_monitor_activity);
                    finish();
            }
            return;
        }

        System.out.println("ACAAA ->");
        System.out.println(start_boolean);

        setContentView(R.layout.activity_wakeapp_main);

        final ImageView logoImage = (ImageView) findViewById(R.id.start_logo);
        logoImage.setBackgroundResource(R.drawable.start_animation);
        kitkatAnimation = (AnimationDrawable) logoImage.getBackground();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        logoImage.setVisibility(View.VISIBLE);
                        kitkatAnimation.start();
                    }
                },
                500);

        if(kitkatAnimation.isVisible()){
            System.out.println("ya esta visible");
        } else {

        }
    }
}
