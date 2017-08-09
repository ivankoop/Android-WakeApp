package py.com.pops.wakeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AppConfigActivity extends AppCompatActivity {

    public final String CONFIG_SETTINGS = "config_settings";
    public TextView distance_txt;
    public com.kyleduo.switchbutton.SwitchButton config_vibrate_switch;
    public com.kyleduo.switchbutton.SwitchButton config_silence_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_config);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        System.out.println(message);

        //this.startConfig();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        config_silence_switch = (com.kyleduo.switchbutton.SwitchButton ) findViewById(R.id.config_silence_switch);
        config_vibrate_switch = (com.kyleduo.switchbutton.SwitchButton) findViewById(R.id.config_vibrate_switch);


    }

    @Override
    protected void onResume() {
        super.onResume();
        startConfig();
    }


    public void startConfig(){

        distance_txt = (TextView) findViewById(R.id.config_distance_txt);
        RingtoneManager manager = new RingtoneManager(this);

        Uri muri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Ringtone mringtone = manager.getRingtone(this, muri);


        final SharedPreferences config_settings = getSharedPreferences(CONFIG_SETTINGS, 0);
        boolean vibrate_boolean = config_settings.getBoolean("vibrate_boolean", false);
        boolean silence_boolean = config_settings.getBoolean("silence_boolean", false);
        String ringtone_id = config_settings.getString("ringtone_id", mringtone.getTitle(this));
        String ringtone_uri = config_settings.getString("ringtone_uri","null");
        String config_distance = config_settings.getString("config_distance", "0.5");

        distance_txt.setText(config_distance + " Km.");

        System.out.println("ACA ABAJO ESTAN LAS CONFIGURACIONES");

        System.out.println("vibrate_boolean: " + vibrate_boolean);
        System.out.println("silence_boolean: " + silence_boolean);
        System.out.println("ringtone_id: " + ringtone_id);
        System.out.println("config_distance: " + config_distance);
        System.out.println("ringtone_uri:" + ringtone_uri);



        //ACA SE CONTINUA


        Cursor ringtone_cursor = manager.getCursor();

        String config_uri_string = null;

        while(!ringtone_cursor.isAfterLast() && ringtone_cursor.moveToNext()){
            //int currentPosition = ringtone_cursor.getPosition();
            String cursor_id = ringtone_cursor.getString(ringtone_cursor.getColumnIndex("_id"));
            System.out.println(cursor_id);
            //System.out.println(ringtone_id);
            if(cursor_id.equalsIgnoreCase(ringtone_id)){
                config_uri_string = ringtone_cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
            }
            //ringtones.add(new WakeappRingtone(ringtone_cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX), ringtone_cursor.getString(RingtoneManager.URI_COLUMN_INDEX), ringtone_cursor.getString(ringtone_cursor.getColumnIndex("_id")), currentPosition));
        }

        Uri uri = Uri.parse(config_uri_string + "/" + ringtone_id);



        Ringtone selected_ringtone = manager.getRingtone(this, uri);


        if(vibrate_boolean){
            config_vibrate_switch.setChecked(true);
            config_vibrate_switch.setThumbColorRes(R.color.colorPrimary);
        } else {
            config_vibrate_switch.setChecked(false);
            config_vibrate_switch.setThumbColorRes(R.color.white);
        }

        if(silence_boolean){
            config_silence_switch.setChecked(true);
            config_silence_switch.setThumbColorRes(R.color.colorPrimary);
        } else {
            config_silence_switch.setChecked(false);
            config_silence_switch.setThumbColorRes(R.color.white);
        }

        TextView config_ringtone_text = (TextView) findViewById(R.id.ringtone_text);

        config_ringtone_text.setText(selected_ringtone.getTitle(this));

        config_vibrate_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    System.out.println("vibrate true");
                    config_vibrate_switch.setThumbColorRes(R.color.colorPrimary);
                    config_vibrate_switch.setBackColorRes(R.color.darkred);
                    SharedPreferences.Editor editor = config_settings.edit();
                    editor.putBoolean("vibrate_boolean", true);
                    editor.commit();
                } else {
                    System.out.println("vibrate false");
                    config_vibrate_switch.setThumbColorRes(R.color.white);
                    config_vibrate_switch.setBackColorRes(R.color.darkred);
                    SharedPreferences.Editor editor = config_settings.edit();
                    editor.putBoolean("vibrate_boolean", false);
                    editor.commit();
                }
            }
        });

        config_silence_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    System.out.println("silence true");
                    config_silence_switch.setThumbColorRes(R.color.colorPrimary);
                    config_silence_switch.setBackColorRes(R.color.darkred);
                    SharedPreferences.Editor editor = config_settings.edit();
                    editor.putBoolean("silence_boolean", true);
                    editor.commit();
                } else {
                    System.out.println("silence false");
                    config_silence_switch.setThumbColorRes(R.color.white);
                    config_silence_switch.setBackColorRes(R.color.darkred);
                    SharedPreferences.Editor editor = config_settings.edit();
                    editor.putBoolean("silence_boolean", false);
                    editor.commit();
                }
            }
        });


    }

    public void onClickRingtone(View view){
        System.out.println("Se apreto el ringtone");
        Intent ringtone_activity = new Intent(this,RingtoneActivity.class);
        ringtone_activity.putExtra("message", "Se inicio el ringtone");
        startActivity(ringtone_activity);
    }

    public void onClickDistanceChanger(View view){
        Showchager();
    }


    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                System.out.println("se apreto el home del config");
                super.onBackPressed();
                return true;
        }
        return true;
    }

    private void Showchager(){
        DistanceChangerFragment editNameDialog = new DistanceChangerFragment(this);
        editNameDialog.show(getFragmentManager(),"fragment_distance_changer");
    }

}
