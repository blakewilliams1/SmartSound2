package org.blakewilliams.www.smartsound2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    //TODO: build a 1x1 widget to toggle threads on/off
    public Thread speedThread;
    private SoundRunnable runner;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AudioManager audio  = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        runner = new SoundRunnable(audio,this);
        startThread();
        initUI();
    }

    private void initNotification(){
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
        threadButton.setText(R.string.stop );
        threadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(speedThread!=null){
                    stopThread();
                }else{
                    startThread();
                }
            }
        });
        //setup timeout switch
        //setup timeoutSwitch
        final Switch timeoutSwitch = (Switch) findViewById(R.id.timeoutSwitch);
        timeoutSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("DEBUG","switch was toggled. set flag in runnable");
                runner.setTimeout(timeoutSwitch.isChecked());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Activity","Calling interrupt on thread");
        //TODO: make sure you have proper mechanisms for stopping threads
        if(speedThread!=null){
            speedThread.interrupt();
            speedThread=null;
        }
    }

    private void startThread(){
        speedThread =  new Thread(runner);
        speedThread.start();
        final Button threadButton = (Button) findViewById(R.id.threadButton);
        threadButton.setText(R.string.stop);
        initNotification();
    }

    public void stopThread(){
        speedThread.interrupt();
        speedThread=null;
        final Button threadButton = (Button) findViewById(R.id.threadButton);
        threadButton.setText(R.string.start);
        notificationManager.cancel(1337);
    }

    class ThreadStopper implements Runnable{
        public void run() {
            stopThread();
        }
    }
}
