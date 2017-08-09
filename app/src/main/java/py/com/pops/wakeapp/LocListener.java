package py.com.pops.wakeapp;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by ivankoop on 6/4/16.
 */
public class LocListener implements LocationListener {
    private static double lat =0.0;
    private static double lon = 0.0;
    private static double alt = 0.0;
    private static double speed = 0.0;

    public static double getLat() {
        return lat;
    }

    public static void setLat(double lat) {
        LocListener.lat = lat;
    }

    public static double getLon() {
        return lon;
    }

    public static void setLon(double lon) {
        LocListener.lon = lon;
    }

    public static double getAlt() {
        return alt;
    }

    public static void setAlt(double alt) {
        LocListener.alt = alt;
    }

    public static double getSpeed() {
        return speed;
    }

    public static void setSpeed(double speed) {
        LocListener.speed = speed;
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
        alt = location.getAltitude();
        speed = location.getSpeed();
        System.out.println("SIAPAAA");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
