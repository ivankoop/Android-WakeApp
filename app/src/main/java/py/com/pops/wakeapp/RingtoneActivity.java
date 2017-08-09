package py.com.pops.wakeapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class RingtoneActivity extends AppCompatActivity {

    public RingtoneAdapter adapter;
    public Ringtone selected_ringtone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        System.out.println(message);

        RingtoneManager manager = new RingtoneManager(this);

        Cursor ringtone_cursor = manager.getCursor();

        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(ringtone_cursor));


        ArrayList<WakeappRingtone> ringtones = new ArrayList<>();


        while(!ringtone_cursor.isAfterLast() && ringtone_cursor.moveToNext()){
            int currentPosition = ringtone_cursor.getPosition();
            ringtones.add(new WakeappRingtone(ringtone_cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX),ringtone_cursor.getString(RingtoneManager.URI_COLUMN_INDEX),ringtone_cursor.getString(ringtone_cursor.getColumnIndex("_id")),currentPosition));
        }

        RecyclerView ringtones_ = (RecyclerView) findViewById(R.id.ringtone_recycler);
        ringtones_.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ringtones_.setLayoutManager(layoutManager);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        adapter = new RingtoneAdapter(this,ringtones);
        ringtones_.setAdapter(adapter);




    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void finish() {
        super.finish();
        System.out.println("SE CERRO");
        adapter.ring.stop();
        selected_ringtone = adapter.ring;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                System.out.println("se apreto el home del ringtone");
                super.onBackPressed();
                return true;
        }
        return true;
    }
}
