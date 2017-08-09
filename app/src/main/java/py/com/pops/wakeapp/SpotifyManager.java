package py.com.pops.wakeapp;


import android.util.Log;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by ivankoop on 7/4/16.
 */

public class SpotifyManager {
    private SpotifyManager me = this;
    private String clientId;
    private String clientSecret;
    private String accessToken;
    private String tokenType;
    private int expiresIn;
    private ArrayList<Runnable> tokenrequestSuccessListeners;
    private ArrayList<Runnable> tokenrequestErrorListeners;

    public SpotifyManager() {

    }

    public SpotifyManager(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public void requestAccessToken() {
        if(clientId == null || clientSecret == null) {
            Log.e("Error", "Deber proveer de un clientId y un clientSecret");
            return;
        }

        HttpSpotify spotifyRequest = new HttpSpotify();
        spotifyRequest.setURL("https://accounts.spotify.com/api/token");
        String authString = clientId+":"+clientSecret;
        spotifyRequest.setAuthString(HttpSpotify.encodeAuthHeader(authString,"Basic"));
        spotifyRequest.setParams("grant_type=client_credentials");
        spotifyRequest.setMethod("POST");
        spotifyRequest.setSuccessCallback(new Callback() {
            @Override
            public void run(String response) {
                Log.e("Request success", response);
                try{
                    JSONObject responseJson = new JSONObject(response);
                    me.accessToken = responseJson.getString("access_token");
                    me.tokenType = responseJson.getString("token_type");
                    me.expiresIn = responseJson.getInt("expires_in");

                    for(Runnable listener : me.tokenrequestSuccessListeners) {
                        listener.run();
                    }
                    me.tokenrequestSuccessListeners = null;
                    me.tokenrequestErrorListeners = null;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        spotifyRequest.setErrorCallback(new Callback() {
            @Override
            public void run(String response) {
                Log.e("Request error", response);
                for(Runnable listener : me.tokenrequestErrorListeners) {
                    listener.run();
                }
                me.tokenrequestSuccessListeners = null;
                me.tokenrequestErrorListeners = null;
            }
        });
        spotifyRequest.execute();
    }

    public void requestSotifyAsset(String url, final Callback success, final Callback error) {
        HttpSpotify spotifyRequest = new HttpSpotify();
        spotifyRequest.setURL(url);
        spotifyRequest.setAuthString(me.tokenType+" "+me.accessToken);
        spotifyRequest.setMethod("GET");
        spotifyRequest.setSuccessCallback(new Callback() {
            @Override
            public void run(String response) {
                Log.e("Request success", response);
                success.run(response);
            }
        });
        spotifyRequest.setErrorCallback(new Callback() {
            @Override
            public void run(String response) {
                Log.e("Request error", response);
                error.run(response);
            }
        });
        spotifyRequest.execute();
    }

    public void addTokenrequestSuccessListener(Runnable listener) {
        if(tokenrequestSuccessListeners == null) {
            tokenrequestSuccessListeners = new ArrayList<>();
        }

        tokenrequestSuccessListeners.add(listener);
    }

    public void addTokenrequestErrorListener(Runnable listener) {
        if(tokenrequestErrorListeners == null) {
            tokenrequestErrorListeners = new ArrayList<>();
        }

        tokenrequestErrorListeners.add(listener);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
