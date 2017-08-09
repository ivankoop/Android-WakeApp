package py.com.pops.wakeapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivankoop on 31/3/16.
 */
public class SinglePlaylistAdapter extends RecyclerView.Adapter<SinglePlaylistAdapter.itemViewHolder>  {

    public ArrayList<Music> musics;
    public Context context;
    public SinglePlaylistActivity activity;

    public SinglePlaylistAdapter(Context context, ArrayList<Music> Musics, SinglePlaylistActivity activity){
        this.musics = Musics;
        this.context = context;
        this.activity = activity;
    }

    public class itemViewHolder extends RecyclerView.ViewHolder{

        public TextView singleplaylist_authortxt;
        public TextView singleplaylist_musictxt;
        public LinearLayout singleplaylist_cont;

        public itemViewHolder(View itemView) {
            super(itemView);

            this.singleplaylist_authortxt = (TextView) itemView.findViewById(R.id. single_playlist_authortxt);
            this.singleplaylist_musictxt = (TextView) itemView.findViewById(R.id.single_playlist_musictxt);
            this.singleplaylist_cont = (LinearLayout) itemView.findViewById(R.id.single_playlist_cont);


        }
    }

    @Override
    public SinglePlaylistAdapter.itemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleplaylist_layout,parent,false);

        itemViewHolder view_holder = new itemViewHolder(view);
        return view_holder;
    }

    @Override
    public void onBindViewHolder(SinglePlaylistAdapter.itemViewHolder holder, int position) {
        final Music my_music = musics.get(position);
        holder.singleplaylist_musictxt.setText(my_music.getMusic_name());
        holder.singleplaylist_authortxt.setText(my_music.getAuthor());

        holder.singleplaylist_cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = my_music.getMusic_uri();
                Intent launcher = new Intent( Intent.ACTION_VIEW, Uri.parse(uri));
                if(isCallable(launcher)){
                    System.out.println("EXISTE!!-------------->");
                    view.getContext().startActivity(launcher);
                } else {
                    System.out.println("NO EXISTE!!-------------->");
                    activity.switchContent();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.musics.size();
    }


    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
