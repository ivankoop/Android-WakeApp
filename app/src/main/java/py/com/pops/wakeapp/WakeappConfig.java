package py.com.pops.wakeapp;

import android.media.Ringtone;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ivankoop on 29/3/16.
 */
@SuppressWarnings("serial")
public class WakeappConfig implements Parcelable {
    private boolean config_vibrate;
    private boolean config_silence;
    private String config_distance;
    private Ringtone config_rigntone;

    public WakeappConfig(boolean cv, boolean cs, String cd){
        this.config_vibrate = cv;
        this.config_silence = cs;
        this.config_distance = cd;
    }

    protected WakeappConfig(Parcel in) {
        config_vibrate = in.readByte() != 0;
        config_silence = in.readByte() != 0;
        config_distance = in.readString();
    }

    public static final Creator<WakeappConfig> CREATOR = new Creator<WakeappConfig>() {
        @Override
        public WakeappConfig createFromParcel(Parcel in) {
            return new WakeappConfig(in);
        }

        @Override
        public WakeappConfig[] newArray(int size) {
            return new WakeappConfig[size];
        }
    };

    public boolean isConfig_vibrate() {
        return config_vibrate;
    }

    public void setConfig_vibrate(boolean config_vibrate) {
        this.config_vibrate = config_vibrate;
    }


    public boolean isConfig_silence() {
        return config_silence;
    }

    public void setConfig_silence(boolean config_silence) {
        this.config_silence = config_silence;
    }

    public String getConfig_distance() {
        return config_distance;
    }

    public void setConfig_distance(String config_distance) {
        this.config_distance = config_distance;
    }

    public Ringtone getConfig_rigntone() {
        return config_rigntone;
    }

    public void setConfig_rigntone(Ringtone config_rigntone) {
        this.config_rigntone = config_rigntone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (config_vibrate ? 1 : 0));
        parcel.writeByte((byte) (config_silence ? 1 : 0));
        parcel.writeString(config_distance);
    }
}
