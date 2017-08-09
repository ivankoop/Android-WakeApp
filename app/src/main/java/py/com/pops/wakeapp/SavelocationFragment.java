package py.com.pops.wakeapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class SavelocationFragment extends DialogFragment {

    private TextView save_title_txt;
    private Button save_accept_btn;
    private Button save_cancel_btn;
    private EditText save_edit_text;
    private String title;
    private String type;
    private String save_latitude;
    private String save_longitude;
    private SavedLocation saved_location;
    private Context context;
    private SharedPreferences service_preferences;

    public SavelocationFragment(Context context){
        this.title = "";
        this.type = "";
        this.context = context;
    }

    public SavelocationFragment(Context context, String title, String type){
        this.title = title;
        this.type = type;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        View view = inflater.inflate(R.layout.fragment_savelocation, container);
        service_preferences = context.getSharedPreferences("service_preferences", 0);
        this.save_title_txt = (TextView) view.findViewById(R.id.lbl_dialog_savelocation);
        this.save_accept_btn = (Button) view.findViewById(R.id.savelocation_dialog_accept_btn);
        this.save_cancel_btn = (Button) view.findViewById(R.id.savelocation_dialog_cancel_btn);
        this.save_edit_text = (EditText) view.findViewById(R.id.savelocation_dialog_edittext);

        if(title != "" || type != ""){
            save_title_txt.setText(title);
        }

        /*DatabaseHandler db = new DatabaseHandler(this);

        // Inserting Contacts
        Log.d("Insert: ", "Inserting ..");
        db.addContact(new Contact("Ravi", "9100000000"));
        db.addContact(new Contact("Srinivas", "9199999999"));
        db.addContact(new Contact("Tommy", "9522222222"));
        db.addContact(new Contact("Karthik", "9533333333"));

        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<Contact> contacts = db.getAllContacts();

        for (Contact cn : contacts) {
            String log = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
            // Writing Contacts to log
            Log.d("Name: ", log);*/

        save_accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = save_edit_text.getText().toString();

                /*double latitude = LocationMonitor.me.point2_location_latitude;
                double longitude = LocationMonitor.me.point2_location_longitude;*/

                if(!text.equalsIgnoreCase("")){
                    save_latitude = service_preferences.getString("Point2_latitude", "0.1");
                    save_longitude = service_preferences.getString("Point2_longitude", "0.1");

                    double latitude = Double.parseDouble(save_latitude);
                    double longitude = Double.parseDouble(save_longitude);

                    DateParser date_parser = new DateParser();
                    System.out.println("PARSE DATE ------------>");
                    System.out.println(date_parser.parseDate());
                    saved_location = new SavedLocation(0,text, 1, latitude, longitude,date_parser.parseDate());


                    LocationsDatabase db = new LocationsDatabase(context);
                    db.addLocation(saved_location.getLocation_name(), String.valueOf(saved_location.getPoint2_location_latitude()),
                            String.valueOf(saved_location.getPoint2_location_longitude()), saved_location.getLocation_date());

                    Log.e("TEXT", text);
                    Log.e("Latitude", String.valueOf(latitude));
                    Log.e("Longitude", String.valueOf(longitude));
                    Intent main_location_activity = new Intent(context,MainLocationsActivity.class);
                    startActivity(main_location_activity);
                    ((Activity) context).finish();
                }


            }
        });

        save_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

            }
        });

        save_accept_btn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    save_accept_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    save_accept_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                return false;
            }
        });

        save_cancel_btn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    save_cancel_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    save_cancel_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                return false;
            }
        });



        return view;
    }



}


