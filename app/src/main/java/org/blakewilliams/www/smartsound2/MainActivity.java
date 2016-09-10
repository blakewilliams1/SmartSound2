package org.blakewilliams.www.smartsound2;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    //TODO: build a 1x1 widget to toggle threads on/off
    Thread speedThread;
    SoundRunnable runner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AudioManager audio  = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        runner = new SoundRunnable(audio);
        speedThread = new Thread(runner);

        //setup button
        final Button button = (Button) findViewById(R.id.threadButton);
        button.setText(R.string.start);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(speedThread.isInterrupted()){
                    button.setText(R.string.start);
                    speedThread.interrupt();
                }else{
                    button.setText(R.string.stop);
                    speedThread.start();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Activity","Calling interrupt on thread");
        //TODO: make sure you have proper mechanisms for stopping threads
        runner.interrupt();
        speedThread.interrupt();
    }
}
