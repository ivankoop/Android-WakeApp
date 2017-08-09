package py.com.pops.wakeapp;

import java.io.Serializable;
import java.security.Timestamp;
import java.sql.Time;

/**
 * Created by ivankoop on 14/4/16.
 */
public class WakeappLatLngPoints implements Serializable {
    private double latitude;
    private double longitude;
    private int kmh;
    private long id;


    public WakeappLatLngPoints(long id, double latitude, double longitude, int kmh){
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setKmh(kmh);
        this.setId(id);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getKmh() {
        return kmh;
    }

    public void setKmh(int time) {
        this.kmh = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
