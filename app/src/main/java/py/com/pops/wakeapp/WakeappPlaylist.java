package py.com.pops.wakeapp;

/**
 * Created by ivankoop on 22/3/16.
 */

public class WakeappPlaylist {

    private String playlist_name;
    private String playlist_img;
    private String playlist_music_count;
    private String playlist_id;
    private String playlist_owner;
    private String playlist_uri;
    

    public WakeappPlaylist(String mn, String mt, String mc, String id,String own,String uri){
        this.playlist_name = mn;
        this.playlist_img = mt;
        this.playlist_music_count = mc;
        this.playlist_id = id;
        this.playlist_owner = own;
        this.playlist_uri = uri;

    }

    public String getPlaylist_name() {
        return playlist_name;
    }

    public void setPlaylist_name(String music_name) {
        this.playlist_name = music_name;
    }

    public String getPlaylist_img() {
        return playlist_img;
    }

    public void setPlaylist_img(String music_type) {
        this.playlist_img = music_type;
    }

    public String getPlaylist_music_count() {
        return playlist_music_count;
    }

    public void setPlaylist_music_count(String music_count) {
        this.playlist_music_count = music_count;
    }

    public void setPlaylist_id(String playlist_id){
        this.playlist_id = playlist_id;
    }

    public String getPlaylist_id(){
        return this.playlist_id;
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder("id: " + playlist_id);
        str.append("Name: " +playlist_name);
        str.append("Count:" +playlist_music_count);
        str.append("Img: " + playlist_img);
        return str.toString();
    }

    public String getPlaylist_owner() {
        return playlist_owner;
    }

    public void setPlaylist_owner(String playlist_owner) {
        this.playlist_owner = playlist_owner;
    }

    public String getPlaylist_uri() {
        return playlist_uri;
    }

    public void setPlaylist_uri(String playlist_uri) {
        this.playlist_uri = playlist_uri;
    }
}
