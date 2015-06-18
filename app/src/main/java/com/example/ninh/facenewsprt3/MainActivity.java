package com.example.ninh.facenewsprt3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    private CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;
    TextView tv;
    Profile profile;
    ProfilePictureView img;
    AccessToken accessToken;
    ImageView coverpic;

    private static String ID = "id";
    private static String COVER = "cover";
    private static String SOURCE = "source";
    private static String NEW_FEED_PATH= "/me/home";
    private static String FIELDS= "fields";
    private static String PUBLIC_PROFILE= "public_profile";
    private static String USER_PHOTO= "user_photos";
    private static String USER_POST= "user_posts";
    private static String READ_STREAM= "read_stream";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        img = (ProfilePictureView)findViewById(R.id.picture);
        tv = (TextView)findViewById(R.id.name);
        coverpic = (ImageView)findViewById(R.id.cover);
        loginButton.setReadPermissions(PUBLIC_PROFILE,  USER_PHOTO, USER_POST, READ_STREAM);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                accessToken = loginResult.getAccessToken();
                profile = Profile.getCurrentProfile();



                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                Log.i("JSON", object.toString());
                                try {
                                    String id = object.getString(ID);
                                    String url = object.getJSONObject(COVER).getString(SOURCE);

                                    LoadPicture load = new LoadPicture();
                                    load.execute(url);
                                    img.setProfileId(id);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString(FIELDS, ID + "," + COVER);
                request.setParameters(parameters);
                request.executeAsync();


//                Bundle params = new Bundle();
//                params.putString("filter", "nf");
                new GraphRequest(
                        accessToken,
                        NEW_FEED_PATH,
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Log.i("JSON1", response.toString());
                            }
                        }
                ).executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(profile!=null)
            tv.setText(profile.getName());
        else tv.setText(getString(R.string.null_text));

    }

    private class LoadPicture extends AsyncTask<String, Void, String> {
        Bitmap bitmap;
        @Override
        protected String doInBackground(String... params) {

            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(params[0]).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            coverpic.setImageBitmap(bitmap);
        }

    }



}
