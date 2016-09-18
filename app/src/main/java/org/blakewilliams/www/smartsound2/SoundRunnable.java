package org.blakewilliams.www.smartsound2;

import android.app.NotificationManager;
import android.content.Context;
//import android.util.Log;
import android.media.AudioManager;
import android.widget.Button;

public class SoundRunnable implements Runnable{
    private GPSpeed locator;
    private AudioManager audioMan;
    private int maxVolume;
    private int currVolume;
    private int originalVolume;
    private MainActivity mContext;
    private volatile boolean timeoutFlag;
    private long timeOutTime;

    public SoundRunnable(AudioManager audio, Context context){
        mContext=(MainActivity)context;
        audioMan=audio;
        maxVolume=audioMan.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        locator=new GPSpeed(context);
        currVolume = audioMan.getStreamVolume(AudioManager.STREAM_MUSIC);
        originalVolume=currVolume;
        timeOutTime=System.currentTimeMillis();
    }

    int getNewVolume(){
        double speed = locator.getImperialSpeed();
        if(speed<8){
            return maxVolume-3;
        }else if(speed<25){
            return maxVolume-2;
        }else if(speed<55){
            return maxVolume-1;
        }else return maxVolume;
    }

    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                //Log.i("Thread", "loop: ");
                Thread.sleep(1000);
                if(timeoutFlag)checkTimeout();
                //float multi = getSpeedMultiplier();
                //int newVolume = Math.round(multi * maxVolume);
                int newVolume = getNewVolume();
                if (newVolume != currVolume) {
                    currVolume = newVolume;
                    audioMan.setStreamVolume(AudioManager.STREAM_MUSIC, currVolume, 0);
                   // Log.i("Thread", "Reassigned volume to be: " + currVolume);
                }
            } catch(InterruptedException e){
                //Log.i("Thread","INTERUPTION");
                audioMan.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                return;
            } catch(Exception e) {
                //Log.i("Thread","Caught a general exception");

                audioMan.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                return;
            }
        }
        //Log.i("Thread","Exiting run loop");
    }
    
    private void checkTimeout(){
        double speed = locator.getImperialSpeed();
        if( locator.getImperialSpeed()>5){
            timeOutTime = System.currentTimeMillis();
        }
        float timeOutMinutes = System.currentTimeMillis()-timeOutTime;
        timeOutMinutes = ((timeOutMinutes)/1000)/60;
        if(timeOutMinutes>5){
            //signal thread interrupt
            mContext.runOnUiThread(new Runnable() {
                public void run() {
                    mContext.speedThread.interrupt();
                    final Button threadButton = (Button) mContext.findViewById(R.id.threadButton);
                    threadButton.setText(R.string.start);
                    NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1337);
                }
            });
        }
    }
    
    public void setTimeout(boolean flag){
        timeoutFlag=flag;
        if(timeoutFlag){
            timeOutTime=System.currentTimeMillis();
        }
    }

}
