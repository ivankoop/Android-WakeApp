package py.com.pops.wakeapp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ivankoop on 19/4/16.
 */
public class DateParser {

    public  DateParser(){

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

        return "última vez: " + date_array[0] + " de " + mes + " a las " + time_array[0] + " y " + time_array[1] + "Hs.";
    }
}
