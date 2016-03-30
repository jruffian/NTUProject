package tw.idv.crystalfish.simpleui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderDtailActivity extends AppCompatActivity {

    TextView note, storeInfo, menu;
    ImageView photoImage, staticMapImageView;
    WebView staticWebView;
    Switch staticMapSwitch;
    private String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_dtail);

        note = (TextView)findViewById(R.id.note);
        storeInfo = (TextView)findViewById(R.id.storeInfo);
        menu = (TextView)findViewById(R.id.menuView);
        photoImage = (ImageView)findViewById(R.id.photoView);
        staticMapImageView = (ImageView)findViewById(R.id.staticMapImageView);
        staticWebView = (WebView)findViewById(R.id.webView);
        staticWebView.setVisibility(View.GONE);
        staticMapSwitch = (Switch)findViewById(R.id.staticMapSwitch);

        note.setText(getIntent().getStringExtra("note"));

        String storeInfomation = getIntent().getStringExtra("storeInfo");
        storeInfo.setText(storeInfomation);
        String menuResult = getIntent().getStringExtra("menu");
        String text = "";

        try {
            JSONArray array = new JSONArray(menuResult);
            for (int i = 0; i < array.length(); i++)
            {
                JSONObject object  = array.getJSONObject(i);
                String DrinkName = object.getString("name");
                String mNumber = String.valueOf(object.getInt("mNumber"));
                String lNumber = String.valueOf(object.getInt("lNumber"));

                text = text + "Name:" + DrinkName + " m: " + mNumber + " l: " + lNumber + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        menu.setText(text);
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
    }



    class GeoCodingTask extends AsyncTask<String, Void, byte[]>
    {
        private String url;
        private double[] latLng;
        @Override
        protected byte[] doInBackground(String... params)
        {
            String address = params[0];
            Log.d("debug", "api" + address);
            latLng = utils.addressToLatLng(address);
            url = utils.getStaticMapUrl(latLng, 17);
            Log.d("debug", url);
            return utils.urlToBytes(url);
        }

        @Override
        protected void onPostExecute(byte[] bytes)
        {
//            staticMapImageView.loadUrl(url);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            staticMapImageView.setImageBitmap(bmp);
            super.onPostExecute(bytes);


        }

    }

    class ImageLoadingTask extends AsyncTask<String, Void, byte[]>
    {
        ImageView imageView;
        @Override
        protected byte[] doInBackground(String... params)
        {
            String url = params[0];
            return utils.urlToBytes(url);
        }

        @Override
        protected void onPostExecute(byte[] bytes)
        {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            photoImage.setImageBitmap(bmp);
            super.onPostExecute(bytes);
        }

        public ImageLoadingTask(ImageView imageView)
        {
            this.imageView = imageView;
        }
    }

}
