package org.blakewilliams.www.smartsound2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    //TODO: build a 1x1 widget to toggle threads on/off
    private Thread speedThread;
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
        createThreadButton();
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

    private void createThreadButton() {
        //setup button
        final Button button = (Button) findViewById(R.id.threadButton);
        button.setText(R.string.start);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(speedThread!=null){
                    button.setText(R.string.start);
                    stopThread();
                }else{
                    button.setText(R.string.stop);
                    startThread();
                }
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
        initNotification();
    }

    private void stopThread(){
        speedThread.interrupt();
        speedThread=null;
        notificationManager.cancel(1337);
    }
}
