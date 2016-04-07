package tw.idv.crystalfish.simpleui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderDtailActivity extends AppCompatActivity {

    TextView note, storeInfo, orderDetail;
    ImageView photoImage, staticMapImageView;
    WebView staticWebView;
    Switch staticMapSwitch;
    private String address;
    MapFragment mapFragment;
    GoogleMap Map;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_dtail);

        note = (TextView) findViewById(R.id.note);
        storeInfo = (TextView) findViewById(R.id.storeInfo);
        orderDetail = (TextView) findViewById(R.id.menuView);
        photoImage = (ImageView) findViewById(R.id.photoView);
        staticMapImageView = (ImageView) findViewById(R.id.staticMapImageView);
        staticWebView = (WebView) findViewById(R.id.webView);
        staticWebView.setVisibility(View.GONE);
        staticMapSwitch = (Switch) findViewById(R.id.staticMapSwitch);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Map = googleMap;
            }
        });

        note.setText(getIntent().getStringExtra("note"));

        String storeInfomation = getIntent().getStringExtra("storeInfo");
        storeInfo.setText(storeInfomation);
        String text = "";
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(getIntent().getStringExtra("menu"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String DrinkName = object.getString("name");
                String mNumber = String.valueOf(object.getInt("mNumber"));
                String lNumber = String.valueOf(object.getInt("lNumber"));

                text = text + "Name:" + DrinkName + " m: " + mNumber + " l: " + lNumber + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        orderDetail.setText(text);
//           Picasso.with(this).load(url).into(photoImage);
        address = storeInfomation.split(",")[1];
        Log.d("debug", address);


        String url = getIntent().getStringExtra("photoURL");
        if (url != null) {
            ImageLoadingTask imageLoadingTask = new ImageLoadingTask(photoImage);
            imageLoadingTask.execute(url);
        }

        GeoCodingTask task = new GeoCodingTask();
        task.execute(address);

        staticMapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    staticMapImageView.setVisibility(View.GONE);
                    staticWebView.setVisibility(View.VISIBLE);
                } else {
                    staticWebView.setVisibility(View.GONE);
                    staticMapImageView.setVisibility(View.VISIBLE);
                }
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "OrderDtail Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://tw.idv.crystalfish.simpleui/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "OrderDtail Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://tw.idv.crystalfish.simpleui/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    class GeoCodingTask extends AsyncTask<String, Void, byte[]> {
        private String url;
        private double[] latLng;

        @Override
        protected byte[] doInBackground(String... params) {
            String address = params[0];
            latLng = utils.addressToLatLng(address);
            url = utils.getStaticMapUrl(latLng, 17);
            return utils.urlToBytes(url);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
//            staticWebView.loadUrl(url);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            staticMapImageView.setImageBitmap(bmp);

            LatLng location = new LatLng(latLng[0], latLng[1]);
            Map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));


            String[] storeInfos = getIntent().getStringExtra("storeInfo").split(",");
            Map.addMarker(new MarkerOptions()
                            .title(storeInfos[0])
                            .snippet(storeInfos[1])
                            .position(location)
            );

            Map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                        Toast.makeText(OrderDtailActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
                       return false;
                }
            });


            super.onPostExecute(bytes);
        }

    }

    class ImageLoadingTask extends AsyncTask<String, Void, byte[]> {
        ImageView imageView;

        @Override
        protected byte[] doInBackground(String... params) {
            String url = params[0];
            return utils.urlToBytes(url);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            photoImage.setImageBitmap(bmp);
            super.onPostExecute(bytes);
        }

        public ImageLoadingTask(ImageView imageView) {
            this.imageView = imageView;
        }
    }

}
