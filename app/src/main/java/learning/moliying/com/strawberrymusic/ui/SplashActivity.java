package learning.moliying.com.strawberrymusic.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import java.util.Timer;
import java.util.TimerTask;

import learning.moliying.com.strawberrymusic.R;

/**
 * 闪屏页
 * @author Administrator
 *
 */
public class SplashActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_main);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                start();
            }
        },3000);
	}

    private void start(){
        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}
