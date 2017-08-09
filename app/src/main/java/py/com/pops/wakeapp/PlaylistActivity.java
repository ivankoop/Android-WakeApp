package py.com.pops.wakeapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {

    private SpotifyManager spotifyManager;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.context = getApplicationContext();
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        System.out.println(message);

        findViewById(R.id.playlist_no_conex).setVisibility(View.GONE);

        spotifyManager = new SpotifyManager();
        spotifyManager.setClientId("db96f3247e2749819f70362aafbac7d1");
        spotifyManager.setClientSecret("83b7ce6f59a444f5b4885973a28e6a41");
        final ArrayList<WakeappPlaylist> my_playlists = new ArrayList<>();

        //my_playlists.add(new WakeappPlaylist("Pop Ballads", "Balada", "35 músicas - 01 hr. 45 min."));
        //12160934531
        spotifyManager.addTokenrequestSuccessListener(new Runnable() {
            @Override
            public void run() {
                Log.e("Holaaaa", "holaaaa");
                spotifyManager.requestSotifyAsset("https://api.spotify.com/v1/users/kitkatparaguay/playlists", new Callback() {
                    @Override
                    //success
                    public void run(String result) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                        try {
                            JSONObject json_response = new JSONObject(result);
                            Log.e("success", json_response.toString());
                            System.out.println("---------------------->");
                            Log.e("items:", json_response.getJSONArray("items").opt(0).toString());
                            int total = json_response.optInt("total");
                            System.out.println("TOTAL: " + total);
                            ArrayList<String> collaboratives = new ArrayList<String>();
                            for (int i = 0; i < total; i++) {

                                collaboratives.add(json_response.getJSONArray("items").optJSONObject(i).toString());
                                Log.e("ID:", json_response.getJSONArray("items").optJSONObject(i).opt("id").toString());
                                Log.e("NAMES:", json_response.getJSONArray("items").optJSONObject(i).opt("name").toString());
                                Log.e("IMAGES:", json_response.getJSONArray("items").optJSONObject(i).getJSONArray("images").getJSONObject(0).opt("url").toString());
                                Log.e("TOTAL:", json_response.getJSONArray("items").optJSONObject(i).getJSONObject("tracks").opt("total").toString());
                                Log.e("OWNER", json_response.getJSONArray("items").optJSONObject(i).getJSONObject("owner").opt("id").toString());
                                Log.e("URI:",json_response.getJSONArray("items").getJSONObject(i).opt("uri").toString());

                                String music_string = json_response.getJSONArray("items").optJSONObject(i).getJSONObject("tracks").opt("total").toString() + " Músicas";

                                my_playlists.add(new WakeappPlaylist(json_response.getJSONArray("items").optJSONObject(i).opt("name").toString(),
                                        json_response.getJSONArray("items").optJSONObject(i).getJSONArray("images").getJSONObject(0).opt("url").toString(), music_string,
                                        json_response.getJSONArray("items").optJSONObject(i).opt("id").toString(),json_response.getJSONArray("items").optJSONObject(i).getJSONObject("owner").opt("id").toString(),
                                        json_response.getJSONArray("items").getJSONObject(i).opt("uri").toString()));
                            }


                            RecyclerView playlists = (RecyclerView) findViewById(R.id.playlist_recycler);
                            playlists.setHasFixedSize(true);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                            playlists.setLayoutManager(layoutManager);
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                            PlaylistAdapter adapter = new PlaylistAdapter(context, my_playlists);
                            playlists.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Callback() {
                    @Override
                    //error
                    public void run(String result) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        findViewById(R.id.playlist_no_conex).setVisibility(View.VISIBLE);
                        try {
                            JSONObject json_response = new JSONObject(result);
                            Log.e("error", json_response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        spotifyManager.requestAccessToken();





    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                System.out.println("se apreto el home del playlist");
                super.onBackPressed();
                return true;
        }
        return true;
    }
}
