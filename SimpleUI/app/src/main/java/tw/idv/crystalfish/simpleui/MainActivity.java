package tw.idv.crystalfish.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button button1;
    TextView textView;
    EditText editText;
    CheckBox hidecheckBox;
    ListView listView;
    Spinner spinner;
    //save data in phone
    SharedPreferences sp;
    /* if you want save data in the phone,
       you must a editor(pen)
    */
    SharedPreferences.Editor editor;

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

        setListView();
        setSpinner();

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

    public void submit(View view)
    {
        //Toast.makeText(this, "Hello world", Toast.LENGTH_LONG);
        String text = editText.getText().toString();
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
        startActivity(intent);
    }
}
