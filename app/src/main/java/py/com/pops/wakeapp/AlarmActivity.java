package py.com.pops.wakeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class AlarmActivity extends AppCompatActivity {

    public Button close_btn;
    public TextView distance_txt;
    public TextView time_txt;
    public SharedPreferences config_settings;
    public SharedPreferences alarm_config;
    public RingtoneManager manager;
    public Ringtone mringtone;
    public Vibrator vibrator;
    public Uri uri;
    public LocationMonitor location_monitor;
    public SharedPreferences service_preferences;
    public long start_time;
    public String tiempo;
    public String final_time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        this.config_settings = getSharedPreferences("config_settings", 0);
        this.close_btn = (Button) findViewById(R.id.alarm_close_btn);
        this.distance_txt = (TextView) findViewById(R.id.alarm_distance_txt);
        this.time_txt = (TextView) findViewById(R.id.alarm_time_txt);
        this.alarm_config = getSharedPreferences("alarm_config",0);
        this.vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        this.location_monitor = LocationMonitor.me;
        this.service_preferences = getSharedPreferences("service_preferences", 0);
        this.start_time = service_preferences.getLong("start_time", 0);
        Intent intent = getIntent();
        this.final_time = "no tiene nada";
        this.final_time = intent.getStringExtra("final_time");
        String[] time_data = final_time.split(" ");
        System.out.println("---------------------------------->");
        System.out.println(final_time);
        for(int i=0; i<time_data.length; i++){
            System.out.println(time_data[i]);
        }
        Timestamp stamp = new Timestamp(start_time);

        Timestamp stamp2 = new Timestamp(System.currentTimeMillis());

        long time_diference = stamp2.getTime() - stamp.getTime();

        long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(time_diference);

        int hours = (int) (timeSeconds / 3600);
        int minutes = (int) ((timeSeconds % 3600) / 60);

        tiempo = "Tiempo: menos de un minuto";

        if(minutes >= 1){
            tiempo =  "Tiempo: " + minutes + " minutos.";
            if(minutes == 1){
                tiempo =  "Tiempo: " + minutes + " minuto.";
            }
        }

        if(hours >= 1){
            tiempo =  "Tiempo: " + hours + "horas y " + minutes + " minutos.";
            if(hours == 1){
                tiempo = "Tiempo: " + hours + "hora y " + minutes + " minutos.";
                if(minutes == 1){
                    tiempo = "Tiempo: " + hours + "hora y " + minutes + " minuto.";
                }
            }
        }

        if(final_time.equalsIgnoreCase("Calculando..")){
            final_time = "Estimado: 0 minutos.";
        }

        time_txt.setText(final_time);



        Log.e("SEGUNDOS", String.valueOf(timeSeconds));

        String close_distance = config_settings.getString("config_distance", "0.5");

        this.distance_txt.setText(close_distance + " Km.");

        if(config_settings.getBoolean("vibrate_boolean",false) && config_settings.getBoolean("silence_boolean",false)){
            Log.e("ENTRO ", "PRIMERA CONDICION");
            uri = Uri.parse(config_settings.getString("ringtone_uri", "null") + "/" + config_settings.getString("ringtone_id", "null"));
            manager = new RingtoneManager(this);
            mringtone = manager.getRingtone(this,uri);
            mringtone.setStreamType(AudioManager.STREAM_ALARM);
            mringtone.play();
            vibrator.vibrate(5000);

        } else if(config_settings.getBoolean("vibrate_boolean",false)) {
            Log.e("ENTRO ", "SEGUNDA CONDICION");
            vibrator.vibrate(5000);

        } else if(config_settings.getBoolean("silence_boolean",false)){
            Log.e("ENTRO ", "TERCERA CONDICION");
            uri = Uri.parse(config_settings.getString("ringtone_uri", "null") + "/" + config_settings.getString("ringtone_id", "null"));
            manager = new RingtoneManager(this);
            mringtone = manager.getRingtone(this,uri);
            mringtone.setStreamType(AudioManager.STREAM_ALARM);
            mringtone.play();

        } else {
            Log.e("ENTRO ", "CUARTA CONDICION");
            uri = Uri.parse(config_settings.getString("ringtone_uri","null") + "/" + config_settings.getString("ringtone_id", "null"));
            manager = new RingtoneManager(this);
            mringtone = manager.getRingtone(this,uri);
            mringtone.play();
        }

        close_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println("se apreto apagar");
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    close_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    close_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                return false;
            }
        });



    }

    @Override
    public void onBackPressed() {

    }

    public void onClickCloseAlarm(View v){
        Intent finished_monitor_activity = new Intent(this,MonitorFinishedActivity.class);
        finished_monitor_activity.putExtra("time_text",tiempo);
        if(mringtone != null){
            mringtone.stop();
        }
        finishAffinity();
        if(location_monitor != null){
            location_monitor.finish();
        }
        startActivity(finished_monitor_activity);
        finish();

    }

}
