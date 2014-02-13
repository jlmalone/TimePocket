package com.techventus.timefly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.techventus.timefly.R;

public class Splash extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        Handler h = new Handler();
        h.postDelayed(proceed, 3000);
        
        setTitle("TimeFly");
    }
    
    Runnable proceed = new Runnable()
    {

		@Override
		public void run() {
			startActivity(new Intent(Splash.this,TabViewPagerFragmentActivity.class));
			Splash.this.finish();
		}
    	
    };
}
