package org.blakewilliams.www.smartsound2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
	public static String WIDGET_TOGGLE_ACTION = "WidgetToggleAction";
	public static String NOTIFICATION_CLICK_ACTION = "NotificationClickAction";
	public Thread speedThread;
	private SoundRunnable runner;
	private NotificationManager notificationManager;
	private final int PERSONAL_LOCATION_REQUEST_CODE = 123;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		runner = new SoundRunnable(audio, this);
		initUI();

		Intent i = getIntent();
		String goal = i.getAction();
		if(goal!=null){
			if(goal.equals(WIDGET_TOGGLE_ACTION)){
				startThread();
				toastNotification();
				onBackPressed();
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		if(intent!=null) {
			String action = intent.getAction();
			if(action==null)return;
			else if(action.equals(NOTIFICATION_CLICK_ACTION)){
				//Turn off the thread and exit the app
				stopThread();
				finish();
			}else if(action.equals(WIDGET_TOGGLE_ACTION)){
				//Toggle the thread on/off
				toggleThread();
				toastNotification();
				onBackPressed();
			}
		}
	}

	private void toastNotification(){
		String message = "SmartSound is now ";
		if(speedThread!=null)message += "on.";
		else message += "off.";
		message+= " Press widget again to turn ";
		if(speedThread!=null)message += "off.";
		else message += "on.";
		Toast.makeText(getApplicationContext(),message
				, Toast.LENGTH_LONG).show();
	}

	private void initNotification() {
		Intent i = new Intent(this, MainActivity.class);
		i.setAction(NOTIFICATION_CLICK_ACTION);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_stat_name)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(getString(R.string.notificationDescription))
				.setContentIntent(pi)
				.setAutoCancel(true)
				.build();
		notification.flags = Notification.FLAG_ONGOING_EVENT;

		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(1337, notification);
	}

	private void initUI() {
		//setup threadButton
		final Button threadButton = (Button) findViewById(R.id.threadButton);
		threadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleThread();
			}
		});
		//setup timeout switch
		final Switch timeoutSwitch = (Switch) findViewById(R.id.timeoutSwitch);
		timeoutSwitch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//pass info off to thread
				runner.setTimeout(timeoutSwitch.isChecked());
				//save the new state in prefs for later use on next activity launch
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean("timeOutSwitch",timeoutSwitch.isChecked());    //name is the key so may use a username or whatever you want
				editor.commit();
			}
		});
		timeoutSwitch.setChecked(getSavedSwitchState());
	}

	boolean getSavedSwitchState() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (preferences == null) return false;
		boolean switchEnabled = preferences.getBoolean("timeOutSwitch", false); //false because you probably want that as your default value
		return switchEnabled;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (speedThread != null) {
			speedThread.interrupt();
			speedThread = null;
		}
	}

	//Used to toggle thread on and off. Primarily made to reduce duplicate code
	private void toggleThread(){
		if (speedThread != null) {
			stopThread();
		} else {
			startThread();
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
		if(speedThread==null)return;
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
