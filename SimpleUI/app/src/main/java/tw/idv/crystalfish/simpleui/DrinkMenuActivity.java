package tw.idv.crystalfish.simpleui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DrinkMenuActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_menu);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void add(View view) {
        Button button = (Button) view;
        int number = Integer.parseInt(button.getText().toString());
        number++;
        button.setText(String.valueOf(number));
    }

    public void done(View view) {
        JSONArray array = getData();
        Intent data = new Intent();
//        data.putExtra("result", "old order");
        data.putExtra("result", array.toString());
        setResult(RESULT_OK, data);
        finish();
    }




    public JSONArray getData() {
        LinearLayout rootLinearLayout = (LinearLayout) findViewById(R.id.root);
        int count = rootLinearLayout.getChildCount();

        JSONArray array = new JSONArray();

        for (int i = 0; i < count - 1; i++) {
            LinearLayout ll = (LinearLayout) rootLinearLayout.getChildAt(i);
            TextView drinkText = (TextView) ll.getChildAt(0);
            Button MiddleCount = (Button) ll.getChildAt(1);
            Button LargeCount = (Button) ll.getChildAt(2);

            String DrinkName = drinkText.getText().toString();
            int mNumber = Integer.parseInt(MiddleCount.getText().toString());
            int lNumber = Integer.parseInt(LargeCount.getText().toString());

            try {
                JSONObject object = new JSONObject();
                object.put("name", DrinkName);
                object.put("mNumber", mNumber);
                object.put("lNumber", lNumber);
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }


    public void cancel(View view) {
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DrinkMenu Page", // TODO: Define a title for the content shown.
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
                "DrinkMenu Page", // TODO: Define a title for the content shown.
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
}
