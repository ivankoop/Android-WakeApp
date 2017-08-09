package py.com.pops.wakeapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SinglePlaylistActivity extends AppCompatActivity {

    public TextView toolbarTextView;
    public String title;
    public String image_url;
    public String music_count;
    public SpotifyManager spotifyManager;
    public String playlist_id;
    public String playlist_owner;
    public Context context;
    public String playlist_uri;
    public SinglePlaylistActivity me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_playlist);
        me = this;
        context = getApplicationContext();
        Intent intent = getIntent();
        title = intent.getStringExtra("playlist_name");
        image_url = intent.getStringExtra("playlist_imageurl");
        music_count = intent.getStringExtra("playlist_count");
        playlist_id = intent.getStringExtra("playlist_id");
        playlist_owner = intent.getStringExtra("playlist_owner");
        playlist_uri = intent.getStringExtra("playlist_uri");


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.singleplaylist_toolbar);

        this.setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        for (int i = 0; i < toolbar.getChildCount(); ++i) {
            View child = toolbar.getChildAt(i);
            if (child instanceof TextView) {
                toolbarTextView = (TextView)child;
                break;
            }
        }

        toolbarTextView.setText(title);

        ImageView playlist_image = (ImageView) findViewById(R.id.single_playlist_image);
        TextView single_playlist_count = (TextView) findViewById(R.id.single_playlist_count);

        Picasso.with(this).load(image_url).placeholder(R.drawable.pop_music_type).into(playlist_image);
        single_playlist_count.setText(music_count);




        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= (appBarLayout.getTotalScrollRange() - 20) * -1) {
                    toolbarTextView.setVisibility(View.VISIBLE);
                } else {
                    toolbarTextView.setVisibility(View.GONE);

                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }




        final ArrayList<Music> my_musics = new ArrayList<>();

        spotifyManager = new SpotifyManager();
        spotifyManager.setClientId("db96f3247e2749819f70362aafbac7d1");
        spotifyManager.setClientSecret("83b7ce6f59a444f5b4885973a28e6a41");
        spotifyManager.addTokenrequestSuccessListener(new Runnable() {
            @Override
            public void run() {
                Log.e("PLAYLIST_ID", playlist_id);
                Log.e("PLAYLIST_OWNER", playlist_owner);
                spotifyManager.requestSotifyAsset("https://api.spotify.com/v1/users/" + playlist_owner + "/playlists/" + playlist_id + "/tracks?fields=total", new Callback() {
                    @Override
                    //success
                    public void run(String result) {
                        Log.e("Success:", result);
                        findViewById(R.id.loadingSingle).setVisibility(View.GONE);
                        try {
                            JSONObject json_response = new JSONObject(result);
                            int total = json_response.optInt("total");
                            if(total>100){
                                for(int i = 0; i<100; i++){
                                    Log.e("MUSIC NAME", json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").opt("name").toString());
                                    Log.e("MUSIC AUTHOR", json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").getJSONArray("artists").getJSONObject(0).opt("name").toString());
                                    Log.e("MUSIC ID", json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").opt("id").toString());
                                    Log.e("MUSIC URI",json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").opt("uri").toString());
                                    my_musics.add(new Music(json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").opt("id").toString(),
                                            json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").getJSONArray("artists").getJSONObject(0).opt("name").toString(),
                                            json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").opt("name").toString(),
                                            json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").opt("uri").toString()));
                                }
                            } else {
                                for (int i = 0; i <total; i++) {
                                    Log.e("MUSIC NAME", json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").opt("name").toString());
                                    Log.e("MUSIC AUTHOR", json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").getJSONArray("artists").getJSONObject(0).opt("name").toString());
                                    Log.e("MUSIC ID", json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").opt("id").toString());
                                    Log.e("MUSIC URI",json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").opt("uri").toString());

                                    my_musics.add(new Music(json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").opt("id").toString(),
                                            json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").getJSONArray("artists").getJSONObject(0).opt("name").toString(),
                                            json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").opt("name").toString(),
                                            json_response.getJSONArray("items").getJSONObject(i).getJSONObject("track").opt("uri").toString()));
                                }
                            }
                            RecyclerView musics = (RecyclerView) findViewById(R.id.single_playlist_recycler);
                            musics.setHasFixedSize(true);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                            musics.setLayoutManager(layoutManager);

                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            SinglePlaylistAdapter adapter = new SinglePlaylistAdapter(context, my_musics,me);
                            musics.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Callback() {
                    @Override
                    //error
                    public void run(String result) {
                       findViewById(R.id.loadingSingle).setVisibility(View.GONE);
                        Log.e("Error:", result);
                    }
                });
            }
        });
        spotifyManager.requestAccessToken();




    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                System.out.println("se apreto el home del single play");
                super.onBackPressed();
                return true;
        }
        return true;
    }

    public void onClickPlay(View view){
        System.out.println("se apreto play");
        String uri = playlist_uri;
        Intent launcher = new Intent( Intent.ACTION_VIEW, Uri.parse(uri));
        if(isCallable(launcher)){
            System.out.println("EXISTE!!-------------->");
            view.getContext().startActivity(launcher);
        } else {
            System.out.println("NO EXISTE!!-------------->");
            switchContent();
        }

    }

    public void switchContent() {
        AlertFragment alert_frag = new AlertFragment();

        alert_frag.show(getFragmentManager(), "fragment_alert");

    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


}
