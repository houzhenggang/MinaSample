package com.example.canvastouch;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		CanvasView cv = (CanvasView)findViewById(R.id.canvasView1);
		
		cv.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				String output = String.format("ACTION:%d, X:%f, Y:%f", event.getAction(), event.getX(), event.getY());
				Log.d("Output", output);
				return false;
			}
		});
		cv.drawFromSocket(MotionEvent.ACTION_DOWN, 10, 20);
		cv.drawFromSocket(MotionEvent.ACTION_MOVE, 10, 50);
		cv.drawFromSocket(MotionEvent.ACTION_UP, 50, 100);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
