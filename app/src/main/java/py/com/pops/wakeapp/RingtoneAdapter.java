package py.com.pops.wakeapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivankoop on 23/3/16.
 */
public class RingtoneAdapter extends RecyclerView.Adapter<RingtoneAdapter.RingtoneViewHolder>  {

    public ArrayList<WakeappRingtone> ringtones;
    public Context context;
    public RingtoneManager ring_man;
    public Ringtone ring;
    public boolean start_ring;
    public Uri uri;
    public String ringtone_title;
    public String ringtone_id;
    public SharedPreferences config_settings;
    public boolean first_changed;
    public String ringtone_uri;

    public RingtoneAdapter(Context context, ArrayList<WakeappRingtone> ringtones){
        this.ringtones = ringtones;
        this.context = context;
        this.ring_man = new RingtoneManager(context);
        this.uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        this.ring = ring_man.getRingtone(context, uri);
        this.ring.setStreamType(AudioManager.USE_DEFAULT_STREAM_TYPE);
        this.start_ring = true;
        this.first_changed = true;
        this.ringtone_title = this.ring.getTitle(context);
        this.config_settings = context.getSharedPreferences("config_settings", 0);
        this.ringtone_uri = this.config_settings.getString("ringtone_uri","null");
        this.ringtone_id = this.config_settings.getString("ringtone_id", "null");


    }

    public class RingtoneViewHolder extends RecyclerView.ViewHolder  {

        public TextView ringtone_name;
        public CheckBox ringtone_checkbox;
        public Ringtone ringtone;

        public RingtoneViewHolder(View itemview){
            super(itemview);
            this.ringtone_name = (TextView) itemview.findViewById(R.id.ringtone_name);
            this.ringtone_checkbox = (CheckBox) itemview.findViewById(R.id.ringtone_radio);

        }



    }

    @Override
    public RingtoneAdapter.RingtoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ringtone_layout, parent, false);
        RingtoneViewHolder view_holder = new RingtoneViewHolder(view);
        return view_holder;
    }

    @Override
    public void onBindViewHolder(final RingtoneViewHolder holder, final int position) {

        final WakeappRingtone myRingtone = this.ringtones.get(position);
        holder.ringtone_name.setText(myRingtone.getTitle());
        holder.ringtone_checkbox.setChecked(false);
        holder.ringtone_checkbox.setEnabled(true);
        holder.ringtone_checkbox.setButtonDrawable(R.drawable.deselected_checkbox);
        if(this.ringtone_id.equalsIgnoreCase(myRingtone.getId()) && first_changed) {
            Log.e("ENTRO", "FIRST CHANGED");
            myRingtone.setIs_checked(true);
        }
        if(myRingtone.is_checked()){
            System.out.println("is checked");
            //holder.ringtone_checkbox.setChecked(true);
            holder.ringtone_checkbox.setEnabled(false);
            holder.ringtone_checkbox.setButtonDrawable(R.drawable.selected_checkbox);

        }



        holder.ringtone_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first_changed = false;
                CheckBox radio = (CheckBox)v;
                for(WakeappRingtone ring : ringtones){
                    if(ring.is_checked()){
                        ring.setIs_checked(false);
                    }
                    //ring.setIs_checked(false);
                }
                myRingtone.setIs_checked(radio.isChecked());
                uri = Uri.parse(myRingtone.getUri() + "/" + myRingtone.getId());
                ringtone_title = myRingtone.getId();
                SharedPreferences.Editor editor = config_settings.edit();
                editor.putString("ringtone_id", myRingtone.getId());
                editor.putString("ringtone_uri",myRingtone.getUri());
                editor.commit();
                if(ring.isPlaying() || start_ring == true){
                    start_ring = false;
                    ring.stop();
                    ring = ring_man.getRingtone(context,uri);
                    ring.play();
                }

                notifyDataSetChanged();

            }
        });


    }

    @Override
    public int getItemCount() {
        return this.ringtones.size();
    }



}