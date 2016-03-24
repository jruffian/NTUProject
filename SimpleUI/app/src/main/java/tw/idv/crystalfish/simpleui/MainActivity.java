package tw.idv.crystalfish.simpleui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
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
    private static final int REQUEST_CODE_CAMERA = 1;

    private boolean hasPhoto = false;

    Button button1;
    TextView textView;
    EditText editText;
    CheckBox hidecheckBox;
    ListView listView;
    Spinner spinner;
    //save data in phone
    SharedPreferences sp;
    String menuResult;
    ImageView imageView;
    ProgressDialog progressDialog;
    ProgressBar progressBar;

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
        imageView = (ImageView)findViewById(R.id.imageView);
        /* create a storage memory in phone its name is "setting" */
        sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sp.edit();
        progressDialog = new ProgressDialog(this);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

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

                if (isChecked) {
                    imageView.setVisibility(View.INVISIBLE);
                } else {
                    imageView.setVisibility(View.VISIBLE);
                }

                if (!hidecheckBox.isChecked()) {
                    editText.setText("");
                    textView.setText("");
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToDetailOrder(position);
            }
        });

    }

    private void setSpinner()
    {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("StoreInfo");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] stores = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    ParseObject object = list.get(i);
                    stores[i] = object.getString("name") + ", " + object.getString("address");
                }
                ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, stores);
                spinner.setAdapter(storeAdapter);
            }
        });

        /*        String[] data = getResources().getStringArray(R.array.numberInfo); //{"壹","貳", "參", "肆", "伍"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, data);
        spinner.setAdapter(adapter);*/
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
                if (e != null) {
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
                    String count;
                    int number = 0;
                    //
                    for (int j = 0; j < menu.length(); j++) {
                        final char c = menu.charAt(j);
                        if (Character.isDigit(c)) {
                            number += (int) c - 0x30;
                        }
                    }
                    //
/*
                    int drirkNum = 0;
                    try {
                        JSONArray array = new JSONArray(menu);
                        for (int j = 0; j <array.length();j++) {
                            JSONObject order = JSONObject.get(j);
                            drirkNum += order.getInt("mNumber") + order.getInt("lNumber");
                        }
                    } catch (JSONException err) {
                        err.printStackTrace();
                    }
*/
                    count = String.valueOf(number);
                    Log.d("debug", "number = " + number);
                    Map<String, String> item = new HashMap<String, String>();
                    item.put("note", note);
                    item.put("storeInfo", storeInfo);
                    item.put("drinkName", count);

                    data.add(item);
                }

                String[] from = {"note", "storeInfo", "drinkName"};
                int[] to = {R.id.note, R.id.storeInfo, R.id.drinkName};
                SimpleAdapter list_adapter = new SimpleAdapter(MainActivity.this, data, R.layout.listview_item, from, to);
                listView.setAdapter(list_adapter);
                listView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void submit(View view)
    {
        //Toast.makeText(this, "Hello world", Toast.LENGTH_LONG);
        String text = editText.getText().toString();

       ParseObject orderObject = new ParseObject("Order");
        orderObject.put("note", text);
        orderObject.put("storeInfo", spinner.getSelectedItem());
        orderObject.put("menu", menuResult);
/*        ParseObject orderObject = new ParseObject("HomeworkParse");
        orderObject.put("sid", text);*/

        if (hasPhoto) {
            Uri uri = utils.getPhotoUti();
            ParseFile file = new ParseFile("photo.png", utils.uriToBytes(this, uri));

            orderObject.put("photo", file);
        }
        progressDialog.setTitle("Loaging...");
        progressDialog.show();
        orderObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    Toast.makeText(MainActivity.this, "order is success", Toast.LENGTH_LONG).show();
                    imageView.setImageResource(0);
                    editText.setText("");
                    textView.setText("");
                    hasPhoto = false;
                    setHistory();
                } else
                    Toast.makeText(MainActivity.this, "order is fail", Toast.LENGTH_SHORT).show();
            }
        });

         utils.writeFile(this, "history.txt", text + '\n');
        /*if (hidecheckBox.isChecked()) {
            Toast.makeText(this, text, Toast.LENGTH_LONG);
            textView.setText("xxxxxxxxxxx");
            editText.setText("xxxxxxxxxxx");
            return;
        }*/
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
        Toast.makeText(MainActivity.this, "PHOTO0", Toast.LENGTH_SHORT).show();
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
        } else if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == RESULT_OK)
            {
                imageView.setImageURI(utils.getPhotoUti());
                hasPhoto = true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.actio_take_photo)
        {
            Toast.makeText(this, "take photo", Toast.LENGTH_SHORT);
            goToCamera();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToCamera()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
//            if (checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
              if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 0);
                return;
            }
        }

        Intent new_intent = new Intent();
        new_intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        new_intent.putExtra(MediaStore.EXTRA_OUTPUT, utils.getPhotoUti());
        startActivityForResult(new_intent, REQUEST_CODE_CAMERA);
  //      startActivity(new_intent);
    }

    public void goToDetailOrder(int position)
    {
        ParseObject object = queryResults.get(position);

        Intent intent = new Intent();
        intent.setClass(this, OrderDtailActivity.class);

        intent.putExtra("note", object.getString("note"));
        intent.putExtra("storeInfo", object.getString("storeInfo"));
        intent.putExtra("menu", object.getString("menu"));

        if (object.getParseFile("photo") != null)
        {
            intent.putExtra("photoURL", object.getParseFile("photo").getUrl());
        }

        startActivity(intent);
    }
}
