package py.com.pops.wakeapp;

/**
 * Created by ivankoop on 31/3/16.
 */
public class Music {

    private String id;
    private String author;
    private String music_name;
    private String music_uri;

    public Music(String id, String author, String music_name,String uri){
        this.id = id;
        this.author = author;
        this.music_name = music_name;
        this.music_uri = uri;
    }

    public String getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMusic_name() {
        return music_name;
    }

    public void setMusic_name(String music_name) {
        this.music_name = music_name;
    }

    public String getMusic_uri() {
        return music_uri;
    }

    public void setMusic_uri(String music_uri) {
        this.music_uri = music_uri;
    }
}
