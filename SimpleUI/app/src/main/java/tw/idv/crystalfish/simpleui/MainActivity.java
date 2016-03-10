package tw.idv.crystalfish.simpleui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button button1;
    TextView textView;
    EditText editText;
    CheckBox hidecheckBox;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        button1 = (Button)findViewById(R.id.button1);
        editText = (EditText) findViewById(R.id.editText);

        sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sp.edit();

        editText.setText(sp.getString("editText", ""));

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                editor.putString("editText", editText.getText().toString());
                editor.apply();
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    submit(v);
                }
                return false;
            }
        });
        hidecheckBox = (CheckBox)findViewById(R.id.checkBox);

        hidecheckBox.setChecked(sp.getBoolean("hideCheckBox", false));
        hidecheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hideCheckBox", hidecheckBox.isChecked());
                editor.apply();
            }
        });
    }

    public void submit(View view)
    {
        //Toast.makeText(this, "Hello world", Toast.LENGTH_LONG);
        String text = editText.getText().toString();
        if (hidecheckBox.isChecked()) {
            Toast.makeText(this, text, Toast.LENGTH_LONG);
            textView.setText("xxxxxxxxxxx");
            editText.setText("xxxxxxxxxxx");
            return;
        }
        editText.setText("");
        textView.setText(text);
    }

}
