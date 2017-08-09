package py.com.pops.wakeapp;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public class ConfirmFragment extends DialogFragment {

    private Button accept_btn;
    private Button cancel_btn;
    private TextView title_txt;
    private TextView content_txt;
    private String title;
    private String message;
    private String type;
    public static final String START_SETTINGS = "start_settings";
    private SharedPreferences settings;
    public Context context;
    private boolean on_map;
    private boolean on_locations;


    public ConfirmFragment(){
        this.title = "";
        this.message = "";
        this.type = "";
    }

    public ConfirmFragment(String title, String message , String type,Context context){
        this.title = title;
        this.message = message;
        this.type = type;
        this.context = context;
        this.settings = context.getSharedPreferences(START_SETTINGS,0);
        on_map = settings.getBoolean("on_map",false);
        on_locations = settings.getBoolean("on_locations",false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_confirm, container);
        this.accept_btn = (Button) view.findViewById(R.id.confirm_dialog_accept_btn);
        this.cancel_btn = (Button) view.findViewById(R.id.confirm_dialog_cancel_btn);
        this.content_txt = (TextView) view.findViewById(R.id.msg_dialog_confirm);
        this.title_txt = (TextView) view.findViewById(R.id.lbl_dialog_confirm);

        if(title != "" || message != ""){
            this.content_txt.setText(message);
            this.title_txt.setText(title);
        }

        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (type){
                    case "turnoff_service":

                        Log.e("ON MAP", String.valueOf(on_map));
                        Log.e("ON LOCATION", String.valueOf(on_locations));
                        LatlngDatabase db = new LatlngDatabase(context);
                        db.clearLatlng();
                        LocationMonitor.me.stopService(LocationMonitor.me.service_intent);
                        LocalBroadcastManager.getInstance(LocationMonitor.me).unregisterReceiver(LocationMonitor.me.LatlnReceiver);
                        if(on_map){
                            Intent main_map_activity = new Intent(context,MainMapActivity.class);
                            main_map_activity.putExtra("message","test");
                            startActivity(main_map_activity);
                        } else if (on_locations){
                            Intent locations_activity = new Intent(context,MainLocationsActivity.class);
                            startActivity(locations_activity);
                        }


                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        accept_btn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    accept_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    accept_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                return false;
            }
        });

        cancel_btn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    cancel_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cancel_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                return false;
            }
        });


        return view;
    }


}
