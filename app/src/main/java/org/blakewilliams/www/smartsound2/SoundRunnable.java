package org.blakewilliams.www.smartsound2;

import android.location.Location;
import android.util.Log;
import android.media.AudioManager;

public class SoundRunnable implements Runnable{
    private Clocation locator;
    private AudioManager audioMan;
    private int maxVolume;
    private int currVolume;
    private volatile boolean running = true;

    public SoundRunnable(AudioManager audio){
        audioMan=audio;
        maxVolume=audioMan.getStreamMaxVolume(AudioManager.STREAM_MUSIC);;
        locator=new Clocation(new Location("MainActivity"));
        currVolume = audioMan.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    //returns 0 to 1 multiplier for how to adjust volume based on speed
    //TODO: Find go on test drives to fine tune these values
    float getSpeedMultiplier(){
        double speed = locator.getSpeed();
        if(speed<10){
            return 0.5f;
        }else if(speed<20){
            return 0.6f;
        }else if(speed<30){
            return 0.7f;
        }else if(speed<40){
            return 0.8f;
        }else if(speed<50){
            return 0.9f;
        }
         return 1f;
    }

    public void run() {
        Log.i("Thread","maxVolume="+maxVolume);
        while(running&&!Thread.currentThread().isInterrupted()) {
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
            } catch (Exception e) {
                e.getLocalizedMessage();
                Thread.currentThread().interrupt();
                return;
            }
        }
        Log.i("Thread","Exiting run loop");
    }

    public void interrupt(){
        running =false;
        Log.i("Thread","Interrupt was called");

    }
}
