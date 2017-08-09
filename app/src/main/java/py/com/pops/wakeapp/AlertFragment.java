package py.com.pops.wakeapp;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by ivankoop on 8/4/16.
 */
public class AlertFragment extends DialogFragment {

    public Button accept_btn;
    private String alert_title;
    private String alert_message;
    private TextView title_txt;
    private TextView message_txt;
    private String error_type;
    private boolean no_back = false;

    public AlertFragment(String t, String m,String error_type){
        this.alert_title = t;
        this.alert_message = m;
        this.error_type = error_type;
    }

    public AlertFragment(){
        this.alert_title = "";
        this.alert_message = "";
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        View view = inflater.inflate(R.layout.fragment_alert, container);
        title_txt = (TextView) view.findViewById(R.id.lbl_dialog_alert);
        message_txt = (TextView) view.findViewById(R.id.msg_dialog_alert);

        if(alert_title != "" && alert_message != ""){
            //TODOO
            title_txt.setText(alert_title);
            message_txt.setText(alert_message);
        }

        accept_btn = (Button) view.findViewById(R.id.spotify_alert_accept_btn);

        accept_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println("se apreto cancel");
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    accept_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    accept_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                return false;
            }
        });

        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                switch (error_type) {
                    case "location_error":
                        android.os.Process.killProcess(android.os.Process.myPid());
                        break;
                    case "gps_alert":
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        no_back = true;
                        break;

                }
            }
        });

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        if(no_back){
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keycode, KeyEvent keyEvent) {
                    if( keycode == KeyEvent.KEYCODE_BACK )
                    {
                        return true;
                    }
                    return false;
                }
            });
        }

    }
}
