package py.com.pops.wakeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ivankoop on 22/3/16.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>  {

    public ArrayList<WakeappPlaylist> playlists;
    public Context context;

    public PlaylistAdapter(Context context, ArrayList<WakeappPlaylist> playlists){
        this.playlists = playlists;
        this.context = context;
    }

    public class PlaylistViewHolder extends  RecyclerView.ViewHolder {

        public TextView playlist_top;
        public ImageView playlist_image;
        public TextView playlist_bot;
        public LinearLayout playlist_cont;

        public PlaylistViewHolder(View itemView){
            super(itemView);
            this.playlist_bot = (TextView) itemView.findViewById(R.id.layout_playlist_botlabel);
            this.playlist_top = (TextView) itemView.findViewById(R.id.layout_playlist_toplabel);
            this.playlist_image = (ImageView) itemView.findViewById(R.id.layout_playlist_img);
            this.playlist_cont = (LinearLayout) itemView.findViewById(R.id.playlist_cont);

        }
    }

    /*  final SavedLocation myLocation = this.savedLocations.get(position);

        holder.location_name.setText(myLocation.getLocation_name().toString());
        holder.location_count.setText(String.valueOf(myLocation.getLocation_count()));
        holder.location_date.setText(myLocation.getLocation_date().toString());

        holder.location_img.post(new Runnable() {

            @Override
            public void run() {
                int width = holder.location_img.getMaxWidth();
                int height = holder.location_img.getMaxHeight();
                String google_api_url = "https://maps.googleapis.com/maps/api/staticmap?center="+myLocation.getLocation_latitude()+","+myLocation.getLocation_longitude()+"&zoom=16&size=600x400&maptype=roadmap&markers=color:red%7Clabel:S%7C"+myLocation.getLocation_latitude()+","+myLocation.getLocation_longitude()+"&key=AIzaSyBxy1EaDKJw4o37vOILaPNcJhsGJklp2m8";
                Picasso.with(context).load(google_api_url)
                        .placeholder(R.drawable.mapapreview).into(holder.location_img);
            }
        });*/

    @Override
    public PlaylistAdapter.PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_layout, parent, false);

        PlaylistViewHolder view_holder = new PlaylistViewHolder(view);
        return view_holder;
    }

    @Override
    public void onBindViewHolder(final PlaylistViewHolder holder, final int position) {
        final WakeappPlaylist myPlaylist = this.playlists.get(position);

        holder.playlist_top.setText(myPlaylist.getPlaylist_name());
        holder.playlist_bot.setText(myPlaylist.getPlaylist_music_count());
        Picasso.with(context).load(myPlaylist.getPlaylist_img()).placeholder(R.drawable.pop_music_type).into(holder.playlist_image);

        holder.playlist_cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent single_playlist_intent = new Intent(view.getContext(), SinglePlaylistActivity.class);
                single_playlist_intent.putExtra("playlist_imageurl",myPlaylist.getPlaylist_img());
                single_playlist_intent.putExtra("playlist_name",myPlaylist.getPlaylist_name());
                single_playlist_intent.putExtra("playlist_count",myPlaylist.getPlaylist_music_count());
                single_playlist_intent.putExtra("playlist_id",myPlaylist.getPlaylist_id());
                single_playlist_intent.putExtra("playlist_owner",myPlaylist.getPlaylist_owner());
                single_playlist_intent.putExtra("playlist_uri", myPlaylist.getPlaylist_uri());
                view.getContext().startActivity(single_playlist_intent);

            }
        });





    }


    @Override
    public int getItemCount() {
        return this.playlists.size();
    }


}
