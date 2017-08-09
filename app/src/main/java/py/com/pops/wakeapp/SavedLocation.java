package py.com.pops.wakeapp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ivankoop on 21/3/16.
 */
public class SavedLocation {
    private String location_name;
    private int location_count;
    private String location_date;
    private String location_img;
    //private double point1_location_latitude;
    //private double point1_location_longitude;
    private double point2_location_latitude;
    private double point2_location_longitude;
    private String full_date;
    private boolean on_iterator = false;
    private int id;

    public SavedLocation(int id, String ln, int lc,double lat2, double lon2, String date){
        this.location_name = ln;
        this.location_count = lc;
        this.setId(id);
        this.location_date = date;
        //this.location_img = li;
        //this.point1_location_latitude = lat;
        //this.point1_location_longitude = lon;
        this.point2_location_latitude = lat2;
        this.point2_location_longitude = lon2;

    }

    public String parseDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy:HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String[] main_date_array = currentDateandTime.split("\\:");
        String date = main_date_array[0];
        String time = main_date_array[1];
        String[] date_array = date.split("\\-");
        String[] time_array = time.split("\\-");
        String mes = null;
        switch (date_array[1]){
            case "01":
                mes = "Enero";
                break;
            case "02":
                mes = "Febrero";
                break;
            case "03":
                mes = "Marzo";
                break;
            case "04":
                mes = "Abril";
                break;
            case "05":
                mes = "Mayo";
                break;
            case "06":
                mes = "Junio";
                break;
            case "07":
                mes = "Julio";
                break;
            case "08":
                mes = "Agosto";
                break;
            case "09":
                mes = "Septiembre";
                break;
            case "10":
                mes = "Octubre";
                break;
            case "11":
                mes = "Noviembre";
                break;
            case "12":
                mes = "Diciembre";
                break;
            default:

        }
        setFull_date("última vez: " + date_array[0] + " de " + mes + " a las " + time_array[0] + " y " + time_array[1] + "Hs.");
        return getFull_date();
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public int getLocation_count() {
        return location_count;
    }

    public void setLocation_count(int location_count) {
        this.location_count = location_count;
    }

    public String getLocation_date() {
        return location_date;
    }

    public void setLocation_date(String location_date) {
        this.location_date = location_date;
    }



    public String getLocation_img() {
        return location_img;
    }

    public void setLocation_img(String location_img) {
        this.location_img = location_img;
    }

    /*public double getPoint1_location_latitude() {
        return point1_location_latitude;
    }*/

    /*public void setPoint1_location_latitude(double location_latitude) {
        this.point1_location_latitude = location_latitude;
    }

    public double getPoint1_location_longitude() {
        return point1_location_longitude;
    }

    public void setPoint1_location_longitude(double location_longitude) {
        this.point1_location_longitude = location_longitude;
    }*/

    public double getPoint2_location_latitude() {
        return point2_location_latitude;
    }

    public void setPoint2_location_latitude(double point2_location_latitude) {
        this.point2_location_latitude = point2_location_latitude;
    }

    public double getPoint2_location_longitude() {
        return point2_location_longitude;
    }

    public void setPoint2_location_longitude(double point2_location_longitude) {
        this.point2_location_longitude = point2_location_longitude;
    }

    public String getFull_date() {
        return full_date;
    }

    public void setFull_date(String full_date) {
        this.full_date = full_date;
    }

    public boolean isOn_iterator() {
        return on_iterator;
    }

    public void setOn_iterator(boolean on_iterator) {
        this.on_iterator = on_iterator;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
