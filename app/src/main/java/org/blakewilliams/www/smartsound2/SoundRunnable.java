package org.blakewilliams.www.smartsound2;

import android.content.Context;
import android.util.Log;
import android.media.AudioManager;

public class SoundRunnable implements Runnable{
    private GPSpeed locator;
    private AudioManager audioMan;
    private int maxVolume;
    private int currVolume;
    private int originalVolume;
    private Context mContext;

    public SoundRunnable(AudioManager audio, Context context){
        mContext=context;
        audioMan=audio;
        maxVolume=audioMan.getStreamMaxVolume(AudioManager.STREAM_MUSIC);;
        locator=new GPSpeed(context);
        currVolume = audioMan.getStreamVolume(AudioManager.STREAM_MUSIC);
        originalVolume=currVolume;
    }

    //returns 0 to 1 multiplier for how to adjust volume based on speed
    //TODO: Find go on test drives to fine tune these values
    float getSpeedMultiplier(){
        double speed = locator.getImperialSpeed();
        if(speed<10){
            return 0.6f;
        }else if(speed<20){
            return 0.7f;
        }else if(speed<30){
            return 0.8f;
        }else if(speed<40){
            return 0.9f;
        }else if(speed<50){
            return 0.95f;
        }
         return 1f;
    }

    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                Log.i("Thread", "loop: ");
                Thread.sleep(1500);
                float multi = getSpeedMultiplier();
                int newVolume = Math.round(multi * maxVolume);
                if (newVolume != currVolume) {
                    currVolume = Math.round(multi * maxVolume);
                    audioMan.setStreamVolume(AudioManager.STREAM_MUSIC, currVolume, 0);
                    Log.i("Thread", "Reassigned volume to be: " + currVolume);
                }
            } catch(InterruptedException e){
                Log.i("Thread","INTERUPTION");
                audioMan.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                return;
            } catch(Exception e) {
                Log.i("Thread","Caught a general exception");
                audioMan.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                return;
            }
        }
        Log.i("Thread","Exiting run loop");
    }

}
