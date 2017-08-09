package py.com.pops.wakeapp;

/**
 * Created by ivankoop on 23/3/16.
 */
public class WakeappRingtone {
    private String title;
    private String id;
    private String uri;
    private boolean is_checked;
    private int pos;

    public WakeappRingtone(String t, String uri,String id, int p){
        this.title = t;
        this.id = id;
        this.setUri(uri);
        this.is_checked = false;
        this.pos = p;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String content) {
        this.id = content;
    }

    public boolean is_checked() {
        return is_checked;
    }

    public void setIs_checked(boolean is_checked) {
        this.is_checked = is_checked;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
