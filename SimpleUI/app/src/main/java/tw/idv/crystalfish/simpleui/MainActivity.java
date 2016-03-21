package tw.idv.crystalfish.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MENU_ACITVITY = 0;

    Button button1;
    TextView textView;
    EditText editText;
    CheckBox hidecheckBox;
    ListView listView;
    Spinner spinner;
    //save data in phone
    SharedPreferences sp;
    String menuResult;
    /* if you want save data in the phone,
       you must a editor(pen)
    */
    SharedPreferences.Editor editor;
    List<ParseObject> queryResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);
        button1 = (Button)findViewById(R.id.button1);
        editText = (EditText)findViewById(R.id.editText);
        listView = (ListView)findViewById(R.id.listView);
        spinner = (Spinner)findViewById(R.id.spinner);
        /* create a storage memory in phone its name is "setting" */
        sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sp.edit();

        /* we would get the string of the editText of setting
        *  first time, it is no data in the editText, so we
        *  must be set null to the editText */
        editText.setText(sp.getString("editText", ""));

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                /* get the string from editText of setting */
                editor.putString("editText", editText.getText().toString());
                /* after apply, the editText value is saved */
                editor.apply();
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    submit(v);
                }
                return false;
            }
        });

        //setListView();
        setSpinner();
        setHistory();

        hidecheckBox = (CheckBox)findViewById(R.id.checkBox);
        /* save checkBox status to hideCheckBox, and set the default value "false"*/
        hidecheckBox.setChecked(sp.getBoolean("hideCheckBox", false));
        hidecheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /* get the hideCheckBox status */
                editor.putBoolean("hideCheckBox", hidecheckBox.isChecked());
                editor.apply();
                if (!hidecheckBox.isChecked()) {
                    editText.setText("");
                    textView.setText("");
                }
            }
        });

//        Parse.enableLocalDatastore(this);
//        Parse.initialize(this);
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("student", "iam good student");
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("debug", e.toString());
                }
            }
        });
    }

    private void setSpinner()
    {
        String[] data = getResources().getStringArray(R.array.numberInfo); //{"壹","貳", "參", "肆", "伍"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, data);
        spinner.setAdapter(adapter);
    }

    private void setListView()
    {
//        String[] data = {"壹","貳", "參", "肆", "伍"};
        String[] data = utils.readFile(this, "history.txt").split("\n");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
    }

    private void setHistory()
    {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Order");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null)
                {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                queryResults = list;
                List<Map<String, String>> data = new ArrayList<Map<String, String>>();
                for (int i = 0; i < queryResults.size(); i++) {
                    ParseObject object = queryResults.get(i);
                    String note = object.getString("note");
                    String storeInfo = object.getString("storeInfo");
                    String menu = object.getString("menu");

                    Map<String, String> item = new HashMap<String, String>();
                    item.put("note", note);
                    item.put("storeInfo", storeInfo);
                    item.put("drinkName", "15");

                    data.add(item);
                }

                String[] from = {"note", "storeInfo", "drinkName"};
                int[] to = {R.id.note, R.id.storeInfo, R.id.drinkName};
                SimpleAdapter list_adapter = new SimpleAdapter(MainActivity.this, data,R.layout.listview_item, from, to);
                listView.setAdapter(list_adapter);
            }
        });

    }

    public void submit(View view)
    {
        //Toast.makeText(this, "Hello world", Toast.LENGTH_LONG);
        String text = editText.getText().toString();

/*        ParseObject orderObject = new ParseObject("Order");
        orderObject.put("note", text);
        orderObject.put("storeInfo", spinner.getSelectedItem());
        orderObject.put("menu", menuResult); */
        ParseObject orderObject = new ParseObject("HomeworkParse");
        orderObject.put("sid", text);

        orderObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Toast.makeText(MainActivity.this, "order is success", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "order is fail", Toast.LENGTH_SHORT).show();
            }
        });

        utils.writeFile(this, "history.txt", text + '\n');
        if (hidecheckBox.isChecked()) {
            Toast.makeText(this, text, Toast.LENGTH_LONG);
            textView.setText("xxxxxxxxxxx");
            editText.setText("xxxxxxxxxxx");
            return;
        }
        editText.setText("");
        textView.setText(text);
        setListView();
    }

    public void gotoMenu(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this, DrinkMenuActivity.class);
//        startActivity(intent);
        startActivityForResult(intent, REQUEST_CODE_MENU_ACITVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MENU_ACITVITY)
        {
            if (resultCode == RESULT_OK) {
                menuResult = data.getStringExtra("result");

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
                    textView.setText(text);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
//                textView.setText(data.getStringExtra("result"));
        }
    }
}
