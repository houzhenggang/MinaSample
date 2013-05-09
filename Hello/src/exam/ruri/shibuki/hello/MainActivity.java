package exam.ruri.shibuki.hello;

import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	TextView txtResult = (TextView)findViewById(R.id.txtResult);
    	outState.putString("txtResult", txtResult.getText().toString());
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	TextView txtResult = (TextView)findViewById(R.id.txtResult);
    	txtResult.setText(savedInstanceState.getString("txtResult"));
    }
    
    public void btnCurrent_onClick(View view)
    {
    	//Toast toast = Toast.makeText(this, new Date().toString(), Toast.LENGTH_LONG);
    	//toast.show();
    	Log.d("currentTime", new Date().toString());
    }
}
