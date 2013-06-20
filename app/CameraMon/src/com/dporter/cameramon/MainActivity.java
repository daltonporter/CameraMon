package com.dporter.cameramon;


import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Images;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		CameraObserver co = new CameraObserver(new Handler(), this);
		this.getContentResolver().registerContentObserver(Images.Media.EXTERNAL_CONTENT_URI, true, co);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
