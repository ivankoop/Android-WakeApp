package py.com.pops.wakeapp;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public class DistanceChangerFragment extends DialogFragment {

    public TextView mEditText;
    public Button AlertCancel;
    public Button AlertAccept;
    public Button UpButton;
    public Button DownButton;
    public TextView AlertDistance;
    public double adder = 0.1;
    public double adition;
    public double minimun_distance = 0.1;
    public double maximun_distance = 2.0;
    public AppConfigActivity activity;

    public DistanceChangerFragment(AppConfigActivity activity) {
        // Empty constructor required for DialogFragment
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_distance_changer, container);
        mEditText = (TextView) view.findViewById(R.id.lbl_dialog_alarm);
        getTag();

        AlertCancel = (Button) view.findViewById(R.id.alert_dialog_cancel_btn);
        AlertAccept = (Button) view.findViewById(R.id.alert_dialog_accept_btn);
        UpButton = (Button) view.findViewById(R.id.alert_dialog_up_btn);
        DownButton = (Button) view.findViewById(R.id.alert_dialog_down_btn);
        AlertDistance = (TextView) view.findViewById(R.id.alert_dialog_distance);


        final SharedPreferences settings = activity.getSharedPreferences("config_settings", 0);
        final double distance = Double.parseDouble(settings.getString("config_distance", "0.5"));
        adition = distance;
        AlertDistance.setText(Double.toString(distance) + " km");

        UpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                DownButton.setEnabled(true);
                System.out.println("se apreto up");
                adition = adition + adder;
                if (round(adition, 2) > maximun_distance) {
                    adition = 2.0;
                    UpButton.setEnabled(false);
                    System.out.println("ENTROOO UP");
                } else {
                    AlertDistance.setText(Double.toString(round(adition, 2))+ " km");
                }
            }
        });

        DownButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                UpButton.setEnabled(true);
                adition = adition - adder;
                if(round(adition,2) < minimun_distance){
                    adition = 0.1;
                    System.out.println("ENTROOO DOWN");
                    DownButton.setEnabled(false);
                } else {
                    AlertDistance.setText(Double.toString(round(adition,2))+ " km");
                }
                System.out.println("se apreto down");
            }
        });

        AlertCancel.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                System.out.println("se apreto cancel");
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    AlertCancel.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AlertCancel.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                return false;
            }
        });

        AlertCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        AlertAccept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                System.out.println("se apreto accept");
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("config_distance", Double.toString(round(adition, 2)));
                editor.commit();
                dismiss();
                TextView txt = (TextView) activity.findViewById(R.id.config_distance_txt);
                txt.setText(Double.toString(round(adition,2)) + " Km.");
            }
        });


        AlertAccept.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    AlertAccept.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AlertAccept.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                return false;
            }
        });

        return view;
    }

    public static double round(double value, int places) {

        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }



}
