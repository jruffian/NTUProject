package tw.idv.crystalfish.simpleui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderDtailActivity extends AppCompatActivity {

    TextView note, storeInfo, menu;
    ImageView photoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_dtail);

        note = (TextView)findViewById(R.id.note);
        storeInfo = (TextView)findViewById(R.id.storeInfo);
        menu = (TextView)findViewById(R.id.menuView);
        photoImage = (ImageView)findViewById(R.id.photoView);

        note.setText(getIntent().getStringExtra("note"));
        storeInfo.setText(getIntent().getStringExtra("storeInfo"));
        String menuResult = getIntent().getStringExtra("menu");

        try {
            JSONArray array = new JSONArray(menuResult);
            String text = "";
            for (int i = 0; i < array.length(); i++)
            {
                JSONObject object  = array.getJSONObject(i);
                String DrinkName = object.getString("name");
                String mNumber = String.valueOf(object.getInt("mNumber"));
                String lNumber = String.valueOf(object.getInt("lNumber"));

                text = text + "Name:" + DrinkName + " m: " + mNumber + " l: " + lNumber + "\n";
            }
            menu.setText(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = getIntent().getStringExtra("photoURL");
        if (url != null)
           Picasso.with(this).load(url).into(photoImage);
    }
}
