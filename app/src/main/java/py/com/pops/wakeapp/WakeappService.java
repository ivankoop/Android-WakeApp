package py.com.pops.wakeapp;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by ivankoop on 5/4/16.
 */

public class WakeappService extends Service {

    public boolean running = true;
    public Context context;
    double latService;
    double lngService;
    private LocationManager lm;
    public SharedPreferences settings;
    public Haversine haversine_calc;
    public double point2_latitude;
    public double point2_longitude;
    public double final_distance;
    public boolean flag = false;
    public SharedPreferences service_preferences;
    public SharedPreferences config_settings;
    public boolean ERROR = false;
    public int sumador = 0;
    public float speed;
    public ArrayList<WakeappLatLngPoints> wakeapp_points;
    public int speed_adder = 0;
    public String final_time;
    public LatlngDatabase db;

    @Override
    public void onCreate() {
        System.out.println("Se ejecuto el servicio! ----------->");
        this.sumador = 0;
        super.onCreate();
        this.context = this.getApplicationContext();
        settings = getSharedPreferences("start_settings", 0);
        wakeapp_points = new ArrayList();
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("on_service", true);
        //editor.putBoolean("on_locations", false);
        //editor.putBoolean("on_map", false);
        editor.commit();


        service_preferences = getSharedPreferences("service_preferences", 0);
        this.point2_latitude = Double.valueOf(service_preferences.getString("Point2_latitude", "0.1"));
        this.point2_longitude = Double.valueOf(service_preferences.getString("Point2_longitude", "0.1"));


        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);

        Location lastKnownLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            latService = lastKnownLocation.getLatitude();
            lngService = lastKnownLocation.getLongitude();
            Log.e("ELSE", "NOENTROO");
        } else {
            Log.e("ELSE", "ENTROO");
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);
        }

        /*try {
            Location lastKnownLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            latService = lastKnownLocation.getLatitude();
            lngService = lastKnownLocation.getLongitude();

        } catch (NullPointerException e) {
            e.printStackTrace();
            ERROR = true;
        }*/

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("SERVICIOO");
        this.context = getApplicationContext();

        Log.e("SERVICE LATITUDE", String.valueOf(point2_latitude));
        Log.e("SERVICE LONGITUDE", String.valueOf(point2_longitude));
        Log.e("SERVICE DISTANCE", String.valueOf(final_distance));


        if (!ERROR) {
            new Thread(new Runnable() {
                public void run() {
                    // TODO Auto-generated method stub
                    while (running) {
                        final_distance = haversine_calc.haversine(latService, lngService, point2_latitude, point2_longitude);
                        config_settings = getSharedPreferences("config_settings", 0);
                        sumador++;
                        final_time = "Calculando..";
                        Log.e("SUMADOR", String.valueOf(sumador));
                        /*if(sumador == 10){
                            Intent alarm_activity = new Intent(context,AlarmActivity.class);
                            alarm_activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            alarm_activity.addFlags(Intent.FLAG_FROM_BACKGROUND);
                            context.startActivity(alarm_activity);
                            flag = true;
                            onDestroy();
                        }*/
                        int kmh_speed = (int) ((speed * 3600) / 1000);
                        //kmh_speed = 5;
                        /*if(kmh_speed > 4){
                            wakeapp_points.add(new WakeappLatLngPoints(latService,lngService,kmh_speed));
                        }*/

                        db = new LatlngDatabase(context);
                        /*if(db.getLatlng().size() > 0){
                            db.clearLatlng();
                        }*/
                        db.addLatlng(String.valueOf(latService),String.valueOf(lngService),kmh_speed);
                        wakeapp_points = db.getLatlng();
                        if (wakeapp_points != null) {
                            speed_adder = 0;
                            for (WakeappLatLngPoints point : wakeapp_points) {
                                speed_adder = speed_adder + point.getKmh();
                            }
                            if (kmh_speed == 0 && speed_adder == 0) {
                                final_time = "Calculando..";
                            } else {
                                double speed_average = (speed_adder / wakeapp_points.size());

                                double average_time_mili = (final_distance / speed_average);

                                double totalSecs = (average_time_mili * 3600);

                                double hours = totalSecs / 3600;
                                double minutes = (totalSecs % 3600) / 60;

                                int minutes_r = (int) Math.round(minutes);
                                int hour_r = (int) Math.round(hours);

                                if (minutes_r >= 1) {
                                    final_time = "Estimado: " + minutes_r + " minutos";
                                    if (minutes_r == 1) {
                                        final_time = "Estimado: " + minutes_r + " minuto";
                                    }
                                }


                                if (hour_r >= 1) {
                                    final_time = "Estimado: " + hour_r + " horas con " + minutes_r + " minutos";
                                    if (minutes_r == 1) {
                                        final_time = "Estimado: " + hour_r + " horas con " + minutes_r + " minuto";
                                    }
                                    if (hour_r == 1) {
                                        final_time = "Estimado: " + hour_r + " hora con " + minutes_r + " minutos";
                                        if (minutes_r == 1) {
                                            final_time = "Estimado: " + hour_r + " hora con " + minutes_r + " minuto";
                                        }
                                    }
                                }
                                Log.e("TIME AVERAGE", String.valueOf(average_time_mili));
                                Log.e("SPEED AVERAGE", String.valueOf(speed_average));
                            }

                        }


                       // Log.e("FINAL TIME", final_time);

                        Log.e("DISTANCE AVERAGE", String.valueOf(final_distance));


                        if (final_distance < Double.parseDouble(config_settings.getString("config_distance", "0.5")) && flag == false) {

                            Intent alarm_activity = new Intent(context, AlarmActivity.class);
                            alarm_activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            alarm_activity.addFlags(Intent.FLAG_FROM_BACKGROUND);
                            alarm_activity.putExtra("final_time", final_time);
                            context.startActivity(alarm_activity);
                            flag = true;
                            onDestroy();
                        }
                        Log.e("LAT2 ", String.valueOf(point2_latitude));
                        Log.e("LONG2 ", String.valueOf(point2_longitude));
                        Log.e("DISTANCE ", String.valueOf(final_distance));
                        sendLatln(latService, lngService, final_distance, speed, final_time);
                        try {
                            Thread.sleep(1200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }).start();
        } else {
            Log.e("ENTRO", "ERROR");
            sendError();
        }


        return START_STICKY;
    }

    int updatecount = 0;

    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            latService = location.getLatitude();
            lngService = location.getLongitude();
            updatecount += 1;
            speed = location.getSpeed();
            Log.e("GPS LATITUDE ", String.valueOf(location.getLatitude()));
            Log.e("GPS LONGITUDE", String.valueOf(location.getLongitude()));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.e("ON STATUS CHANGED", s);
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.e("PROVIDER ENABLED", s);
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.e("PROVIDER DISABLED", s);
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendLatln(double lat, double lng, double distance, float speed, String final_time) {

        System.out.println("SE EJECUTAAA???");

        Intent intent = new Intent("LatlnReceiver");
        intent.putExtra("final_distance", distance);
        intent.putExtra("latitudeRS", lat);
        intent.putExtra("longitudeRS", lng);
        intent.putExtra("speed", speed);
        intent.putExtra("final_time", final_time);
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
    }

    private void sendError() {
        Intent intent = new Intent("ErrorReceiver");
        intent.putExtra("ERROR", true);
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
        running = false;
        stopSelf();
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        System.out.println("on destroy");
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("on_service", false);
        editor.commit();
        running = false;
        stopSelf();
    }




}
