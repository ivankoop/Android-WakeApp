package py.com.pops.wakeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.support.v4.app.ActivityCompat.finishAffinity;
import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by ivankoop on 21/3/16.
 */
public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.itemViewHolder> {

    public ArrayList<SavedLocation> savedLocations;
    public Context context;
    public SharedPreferences service_preferences;
    public SharedPreferences config_preferences;
    public MainLocationsActivity activity;

    public LocationsAdapter(Context context, ArrayList<SavedLocation> savedLocations){
        this.context = context;
        this.savedLocations = savedLocations;
        this.service_preferences = context.getSharedPreferences("service_preferences",0);
        this.config_preferences = context.getSharedPreferences("config_settings",0);
        this.activity = (MainLocationsActivity) context;
    }

    public class itemViewHolder extends RecyclerView.ViewHolder {

        public TextView location_name;
        public TextView location_date;
        public TextView location_count;
        public ImageView location_img;
        public FloatingActionButton location_start_btn;
        public ImageView delete_btn;
        public TextView location_count_txt;



        public itemViewHolder(View itemView){
            super(itemView);

            this.location_name = (TextView) itemView.findViewById(R.id.location_name);
            this.location_date = (TextView) itemView.findViewById(R.id.location_date);
            this.location_count = (TextView) itemView.findViewById(R.id.location_count);
            this.location_img = (ImageView) itemView.findViewById(R.id.google_map_img);
            this.location_start_btn = (FloatingActionButton) itemView.findViewById(R.id.location_start_btn);
            this.delete_btn = (ImageView) itemView.findViewById(R.id.location_delete_btn);
            this.location_count_txt = (TextView) itemView.findViewById(R.id.location_count_txt);
        }


    }

    @Override
    public itemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.locations_layout,parent,false);

        itemViewHolder view_holder = new itemViewHolder(view);
        return view_holder;

    }


    @Override
    public void onBindViewHolder(final itemViewHolder holder, final int position) {
        final SavedLocation myLocation = this.savedLocations.get(holder.getAdapterPosition());

        holder.location_name.setText(myLocation.getLocation_name().toString());
        holder.location_count.setText(String.valueOf(myLocation.getLocation_count()));
        holder.location_date.setText(myLocation.getLocation_date());

        holder.location_img.post(new Runnable() {

            @Override
            public void run() {
                int width = holder.location_img.getMaxWidth();
                int height = holder.location_img.getMaxHeight();
                String google_api_url = "https://maps.googleapis.com/maps/api/staticmap?center=" + myLocation.getPoint2_location_latitude() + "," + myLocation.getPoint2_location_longitude() + "&zoom=16&size=600x400&maptype=roadmap&markers=color:red%7Clabel:S%7C" + myLocation.getPoint2_location_latitude() + "," + myLocation.getPoint2_location_longitude() + "&key=AIzaSyBxy1EaDKJw4o37vOILaPNcJhsGJklp2m8";
                Picasso.with(context).load(google_api_url)
                        .placeholder(R.drawable.mapa_preview).into(holder.location_img);
            }
        });

        if(myLocation.getLocation_count() == 1){
            holder.location_count_txt.setText("Vez");
        }

        holder.location_start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double latitude = activity.my_lat;
                double longitude = activity.my_lng;
                Haversine hav = new Haversine();
                double shared_distance = Double.parseDouble(config_preferences.getString("config_distance","0.5"));
                double distance = hav.haversine(latitude,longitude,myLocation.getPoint2_location_latitude(),myLocation.getPoint2_location_longitude());
                Log.e("View latitude", String.valueOf(latitude));
                Log.e("View longitude", String.valueOf(longitude));
                if(distance <= shared_distance){
                    activity.showDistanceAlert();
                } else {
                    LocationsDatabase db = new LocationsDatabase(context);
                    System.out.println("se ejecuto el numero:" + position);
                    /*Intent intent = new Intent(getApplicationContext(), Home.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);*/
                    Intent monitor_activity = new Intent(context, LocationMonitor.class);
                    monitor_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    DateParser date_parser = new DateParser();
                    myLocation.setLocation_date(date_parser.parseDate());
                    myLocation.setLocation_count(myLocation.getLocation_count() + 1);
                    db.updateLocation(myLocation);
                    monitor_activity.putExtra("point2_location_latitude", myLocation.getPoint2_location_latitude());
                    monitor_activity.putExtra("point2_location_longitude", myLocation.getPoint2_location_longitude());
                    monitor_activity.putExtra("point1_location_latitude",latitude);
                    monitor_activity.putExtra("point1_location_longitude",longitude);
                    monitor_activity.putExtra("from_map_or_locations_activity",true);
                    SharedPreferences.Editor editor = service_preferences.edit();
                    long unixTime = System.currentTimeMillis();
                    editor.putLong("start_time", unixTime);
                    editor.putBoolean("from_loc",true);
                    editor.commit();
                    context.startActivity(monitor_activity);
                    ((Activity)context).finish();
                }

            }
        });

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationsDatabase db = new LocationsDatabase(context);
                db.deleteLocation(myLocation);
                savedLocations.remove(holder.getAdapterPosition());

                notifyItemRemoved(holder.getAdapterPosition());
                System.out.println("SE ESTAN BORRANDO LOS DATOS" + position);
                Log.e("ITEM COUNT", String.valueOf(getItemCount()));
                if(getItemCount() == 0){
                    activity.showMessageEmpty();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.savedLocations.size();
    }

    public void setFilter(ArrayList<SavedLocation> saved_Locations) {
        savedLocations = new ArrayList<>();
        savedLocations.addAll(saved_Locations);
        notifyDataSetChanged();
    }


}
