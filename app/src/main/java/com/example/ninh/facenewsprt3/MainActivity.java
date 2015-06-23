package com.example.ninh.facenewsprt3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends ActionBarActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ImageView img;
    private AccessToken accessToken;
    private ImageView coverpic;
    private LoginButton loginButton;
    private MenuItem menuItem;
    private LinearLayout mainlayout;
    private OverScrollListView newfeed;
    private String id;
    private ProgressBar test;

    private static String ID = "id";
    private static String TITLE = "title";
    private static String TIME = "updated_time";
    private static String LINK = "link";
    private static String FROM = "from";
    private static String DATA = "data";
    private static String COVER = "cover";
    private static String SOURCE = "source";
    private static String NOTIFICATIONS_PATH= "/me/notifications";
    private static String FIELDS= "fields";
    private static String PER_NOTIFICATION= "manage_notifications";
    private static String INCLUDE_READ= "include_read";
    private static String TRUE= "true";
    private static String PROFILE= "profile";
    private int width;
    private ArrayList<Item> list;
    private MyArrayAdapter adapter;
    private DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton)findViewById(R.id.login_button);
        img = (ImageView)findViewById(R.id.picture);
        coverpic = (ImageView)findViewById(R.id.cover);
        mainlayout = (LinearLayout)findViewById(R.id.mainlayout);
        newfeed = (OverScrollListView)findViewById(R.id.newfeed);
        test = (ProgressBar)findViewById(R.id.test);
        test.setVisibility(View.GONE);

        mydb = new DBHelper(this);

        loginButton.setPublishPermissions(PER_NOTIFICATION);

        list = new ArrayList<Item>();

        newfeed.setOverscrollHeader(getResources().getDrawable(R.drawable.load_more));

        newfeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri link = Uri.parse(list.get(position).getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);
            }
        });


        newfeed.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.i("SCROLL", ""+newfeed.getStatus());
                if(newfeed.getStatus() == true) {
                    updateListviewfromFace();
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                Log.i("CHANGE", "huigjnv");

                if(AccessToken.getCurrentAccessToken()==null)
                {
                    list.clear();
                    loginButton.setVisibility(View.VISIBLE);
                    mainlayout.setVisibility(View.INVISIBLE);
                    mydb.deleteAll();
                    img.setImageBitmap(null);
                    coverpic.setImageBitmap(null);
                }
                else {
                    loginButton.setVisibility(View.INVISIBLE);
                    mainlayout.setVisibility(View.VISIBLE);
                }
            }
        };

        if(AccessToken.getCurrentAccessToken()!=null)
        {
            //Get Data from DB
            GetFromDB();
            loginButton.setVisibility(View.INVISIBLE);
            //Get Picture from DB
            GetImageFromDB getImage = new GetImageFromDB();
            getImage.execute();

            updateListviewfromFace();

        }
        else
            mainlayout.setVisibility(View.INVISIBLE);


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                accessToken = loginResult.getAccessToken();
                loginButton.setVisibility(View.GONE);

        // Get Profile & Cover Photo
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                try {
                                    id = object.getString(ID);
                                    String url = object.getJSONObject(COVER).getString(SOURCE);

                                    LoadPicture load = new LoadPicture();
                                    load.execute(url);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString(FIELDS, ID + "," + COVER);
                request.setParameters(parameters);
                request.executeAsync();

           //Get Notification
                GraphRequest request1= new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        NOTIFICATIONS_PATH,
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                            try {
                                Log.i("JSON1", "" + response.toString());
                                JSONArray jsonArray = response.getJSONObject().getJSONArray(DATA);

                                for(int i = 0; i<jsonArray.length(); i++)
                                {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Item item = new Item(null, jsonObject.getString(ID), jsonObject.getString(TITLE),
                                            jsonObject.getString(TIME), jsonObject.getString(LINK),
                                            jsonObject.getJSONObject(FROM).getString(ID));
                                    list.add(item);

                                    mydb.insertList(item.getIDn(), item.getTitle(),
                                                    item.getTime(), item.getLink(), item.getPersonId());
//                                        Log.i("JSON1", "" + jsonObject);
                                    }

                                Collections.sort(list, new Comparator<Item>() {
                                    @Override
                                    public int compare(Item lhs, Item rhs) {

                                        return (int) (rhs.getTime().compareTo(lhs.getTime()));
                                    }
                                });

                                adapter = new MyArrayAdapter(MainActivity.this, R.layout.mylistview, list);
                                newfeed.setAdapter(adapter);

                                GetImageItem get = new GetImageItem();
                                get.execute();
//
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            }


                        }
                );

                Bundle parameters1 = new Bundle();
                parameters1.putString(INCLUDE_READ, TRUE);
                request1.setParameters(parameters1);
                request1.executeAsync();
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

    private void GetFromDB() {

        list = mydb.getList();
        Collections.sort(list, new Comparator<Item>() {
            @Override
            public int compare(Item lhs, Item rhs) {

                return (int) (rhs.getTime().compareTo(lhs.getTime()));
            }
        });
        adapter = new MyArrayAdapter(MainActivity.this, R.layout.mylistview, list);
        newfeed.setAdapter(adapter);

    }


    private void updateListviewfromFace() {
        test.setVisibility(View.VISIBLE);
        newfeed.setOverScrollMode(View.OVER_SCROLL_NEVER);

        GraphRequest request1= new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                NOTIFICATIONS_PATH,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            Log.i("JSON1", "SUCCESS");
                            JSONArray jsonArray = response.getJSONObject().getJSONArray(DATA);

                            for(int i = 0; i<jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                boolean add = true;

                                for (int k = 0; k < list.size(); k++) {
                                    if (jsonObject.getString(ID).equals(list.get(k).getIDn())) {
                                        add = false;
                                        break;
                                    }
                                }
                                if (add) {
                                    Item item = new Item(null, jsonObject.getString(ID), jsonObject.getString(TITLE),
                                            jsonObject.getString(TIME), jsonObject.getString(LINK),
                                            jsonObject.getJSONObject(FROM).getString(ID));
                                    list.add(item);

                                    mydb.insertList(item.getIDn(), item.getTitle(),
                                            item.getTime(), item.getLink(), item.getPersonId());
                                    Log.i("JSON1", "" + jsonObject);
                                }

                            }

                            Collections.sort(list, new Comparator<Item>() {
                                @Override
                                public int compare(Item lhs, Item rhs) {

                                    return (int) (rhs.getTime().compareTo(lhs.getTime()));
                                }
                            });

                            adapter.notifyDataSetChanged();
                            test.setVisibility(View.GONE);
                            newfeed.setOverScrollMode(View.OVER_SCROLL_ALWAYS);

                            GetImageItem get = new GetImageItem();
                            get.execute();

                        } catch (Exception e) {

                            e.printStackTrace();
                            test.setVisibility(View.GONE);
                            newfeed.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
                        }
                    }

                }
        );

        Bundle parameters1 = new Bundle();
        parameters1.putString(INCLUDE_READ, TRUE);
        request1.setParameters(parameters1);
        request1.executeAsync();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItem = menu.findItem(R.id.action_logout);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(AccessToken.getCurrentAccessToken()==null)
            menuItem.setTitle(getString(R.string.action_login));
        else
            menuItem.setTitle(getString(R.string.action_logout));
        return super.onPrepareOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            loginButton.performClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private class GetImageFromDB extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            try {
                ArrayList<String> ar = mydb.getProfileCoverPic();
                FileInputStream profile = new FileInputStream(ar.get(0));
                FileInputStream cover = new FileInputStream(ar.get(1));

            // Read a bitmap from the file (which presumable contains bitmap in PNG format, since
            // that's how files are created)
                Bitmap pr = BitmapFactory.decodeStream(profile);
                Bitmap cv = BitmapFactory.decodeStream(cover);

                img.setImageBitmap(pr);
                coverpic.setImageBitmap(cv);

                }

            catch (Exception e)
            {
                e.printStackTrace();
            }
            try{

            boolean ifnull = false;

            for (int i = 0; i<list.size(); i++) {
                if(getImageFromIDPerson(list.get(i).getPersonId())!=null)
                list.get(i).setImage(getImageFromIDPerson(list.get(i).getPersonId()));
                else
                {
                    ifnull = true;
                    break;
                }
                publishProgress();
            }
                if(ifnull == true)
                {
                    GetImageItem get = new GetImageItem();
                    get.execute();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            // Application code
                            try {
                                if(object!=null) {
                                    id = object.getString(ID);
                                    String url = object.getJSONObject(COVER).getString(SOURCE);

                                    LoadPicture load = new LoadPicture();
                                    load.execute(url);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString(FIELDS, ID + "," + COVER);
            request.setParameters(parameters);
            request.executeAsync();

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {
            adapter.notifyDataSetChanged();
        }
    }

    private class GetImageItem extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            Bitmap bm;

            for (int i = 0; i<list.size(); i++) {
                try {
                    ArrayList<String> arrayList = mydb.getPersonId();
                    if(!arrayList.contains(list.get(i).getPersonId())) {
                        URL imageURL = new URL("https://graph.facebook.com/" + list.get(i).getPersonId() +"/picture?type=large");

                        bm = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                        list.get(i).setImage(bm);

                        String imagePath = putBitmapInDiskCache(list.get(i).getPersonId(), bm);
                        mydb.insertImage(list.get(i).getPersonId(), imagePath);
                    }
                    else list.get(i).setImage(getImageFromIDPerson(list.get(i).getPersonId()));


                publishProgress();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {
            adapter.notifyDataSetChanged();
        }
    }

    private Bitmap getImageFromIDPerson(String id_person) {
        FileInputStream file = null;
        String imagePath = mydb.getImagePathFromIDPerson(id_person);
        try {
            file = new FileInputStream(imagePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap image = BitmapFactory.decodeStream(file);
        return image;
    }

    private class LoadPicture extends AsyncTask<String, Void, String> {
        Bitmap bitmap1, bitmap2;
        Bitmap cover, profile;
        @Override
        protected String doInBackground(String... params) {

            try {
                bitmap1 = BitmapFactory.decodeStream((InputStream) new URL(params[0]).getContent());
                URL imageURL = null;

                try {
                    imageURL = new URL("https://graph.facebook.com/" +id +"/picture?type=large");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                    bitmap2 = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

            Log.i("SIZE", bitmap1.getHeight() + ";" + bitmap1.getWidth());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Log.i("JSON", "success");
                cover = Bitmap.createScaledBitmap(bitmap1, width, width * bitmap1.getHeight() / bitmap1.getWidth(), false);
                coverpic.setImageBitmap(cover);
                profile = Bitmap.createScaledBitmap(getRoundedCornerBitmap(bitmap2, 100), width / 4, width / 4, false);
                img.setImageBitmap(profile);

                String profilePath = putBitmapInDiskCache(PROFILE, profile);
                String coverPath = putBitmapInDiskCache(COVER, cover);

                mydb.insertProfileCoverPic(profilePath, coverPath);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        rectF.set(rectF.left+5,rectF.top+5,rectF.right-5,rectF.bottom-5);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private String putBitmapInDiskCache(String url, Bitmap image) {

        File cacheFile = new File(getBaseContext().getCacheDir(), url);

        Log.i("DIRECTOR1", cacheFile.getAbsolutePath());
        try {
            cacheFile.getParentFile().mkdirs();
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(cacheFile);
                image.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            Log.e("ERROR", "Error when saving image to cache. ");
        }
        return cacheFile.getAbsolutePath();
    }


}
