package tw.idv.crystalfish.simpleui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DrinkMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_menu);
    }

    public void add(View view)
    {
        Button button = (Button)view;
        int number = Integer.parseInt(button.getText().toString());
        number++;
        button.setText(String.valueOf(number));
    }

    public void done(View view)
    {
        Intent data = new Intent();
        data.putExtra("result", "old order");
        setResult(RESULT_OK, data);
        finish();
    }
    public void cancel(View view)
    {
        finish();
    }
}
