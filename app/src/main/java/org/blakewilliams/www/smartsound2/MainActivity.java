package org.blakewilliams.www.smartsound2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;


public class MainActivity extends AppCompatActivity {
	public Thread speedThread;
	private SoundRunnable runner;
	private NotificationManager notificationManager;
	private final int PERSONAL_LOCATION_REQUEST_CODE = 123;
	private SpeedWidget widget;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (speedThread!=null)Log.i("Activity","Second call to onCreate");
		widget = new SpeedWidget(this);
		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		runner = new SoundRunnable(audio, this);
		initUI();
	}

	private void initNotification() {
		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_stat_name)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(getString(R.string.notificationDescription))
				.setContentIntent(pi)
				.setAutoCancel(true)
				.build();

		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(1337, notification);
	}

	private void initUI() {
		//setup threadButton
		final Button threadButton = (Button) findViewById(R.id.threadButton);
		threadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (speedThread != null) {
					stopThread();
				} else {
					startThread();
				}
			}
		});
		//setup timeout switch
		final Switch timeoutSwitch = (Switch) findViewById(R.id.timeoutSwitch);
		timeoutSwitch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i("DEBUG", "switch was toggled. set flag in runnable");
				runner.setTimeout(timeoutSwitch.isChecked());
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("Activity", "Calling interrupt on thread");
		if (speedThread != null) {
			speedThread.interrupt();
			speedThread = null;
		}
	}

	public void startThread() {
		speedThread = new Thread(runner);
		speedThread.start();
		final Button threadButton = (Button) findViewById(R.id.threadButton);
		threadButton.setText(R.string.stop);
		initNotification();
	}

	public void stopThread() {
		speedThread.interrupt();
		speedThread = null;
		final Button threadButton = (Button) findViewById(R.id.threadButton);
		threadButton.setText(R.string.start);
		notificationManager.cancel(1337);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERSONAL_LOCATION_REQUEST_CODE: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length <= 0
						|| grantResults[0] != PackageManager.PERMISSION_GRANTED) {
					//permission was not granted. Exit app
					finish();
				}
				return;
			}
		}
	}

	@Override
	public void onBackPressed() {
		//Overrides default action of signalling the app to close (thereby ending the background thread)
		//Launches the intent of returning to home screen to 'minimize' app
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
	}
}
