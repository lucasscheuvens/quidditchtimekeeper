package de.lucasscheuvens.qtk;

import java.io.Serializable;

/**
 * Created by lucas on 23.10.2016.
 */

public class PenaltyTimer implements Serializable
{
    private long timerValue = 60*1000; // in ms
    private long startTime = 0;
    private long pauseTime = 0;
    private long stackedTime = 0;
    private boolean running = false;
    private boolean sounds = true;
    private long old_remaining_for_sound_play = -1;
    private int countdownSeconds = 5;
    //private boolean[] soundPlayedArray = new boolean[countdownSeconds+1];
    private boolean showRipple = false;
    //private MediaPlayer player_bip;
    // MediaPlayer player_beeep;
    //List<boolean> soundsPlayed = new ArrayList<boolean>();
    //private ArrayList<boolean> soundPlayed = new List<boolean>;

    public PenaltyTimer(long timerValue)
    {
        this.timerValue = timerValue;
    }

    public void start()
    {
        this.stackedTime = this.getElapsedTime();
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }

    public void pause()
    {
        this.pauseTime = System.currentTimeMillis();
        this.running = false;
    }

    public void reset()
    {
        this.startTime = 0;
        this.stackedTime = 0;
        this.pauseTime = 0;
        boolean was_running = this.running;
        this.running = false;
        if(was_running) this.start();
    }

    public boolean isRunning()
    {
        return this.running;
    }

    public long getElapsedTime()
    {
        long elapsed;
        if (running)
        {
            elapsed = (System.currentTimeMillis() - startTime + stackedTime);
        }
        else
        {
            elapsed = (pauseTime - startTime + stackedTime);
        }
        return elapsed;
    }

    public void playSoundIfTime(long remaining)
    {
        /*if(sounds)
        {
            for(int i=0;i<countdownSeconds+1;i++)
            {
                if(remaining<=i*1000 && old_remaining_for_sound_play>i*1000 && !soundPlayedArray[i])
                {
                    //soundPlayedArray[i] = true;
                    if(remaining>(i-1)*1000)
                    {
                        showRipple = true;
                        if(i==0) this.player_beeep.start();
                        else this.player_bip.start();
                    }
                }
            }
            old_remaining_for_sound_play = remaining;
        }*/
    }

    public void setSoundPlayedArray()
    {
        /*if(sounds)
        {
            long remaining = this.getTimeRemaining(false);
            for(int i=0;i<countdownSeconds+1;i++)
            {
                if(remaining>i*1000) soundPlayedArray[i] = false;
            }
            old_remaining_for_sound_play = remaining;
        }*/
    }


    //elaspsed time in seconds
    public long getElapsedTimeSecs()
    {
        long elapsed;
        if (running)
        {
            elapsed = ((System.currentTimeMillis() - startTime + stackedTime) / 1000);
        }
        else
        {
            elapsed = ((pauseTime - startTime + stackedTime) / 1000);
        }
        return elapsed;
    }

    //time remaining in milliseconds
    public long getTimeRemaining()
    {
        return this.getTimeRemaining(true);
    }

    public long getTimeRemaining(boolean playSoundIfTime)
    {
        long value = timerValue - this.getElapsedTime();
        if(playSoundIfTime) playSoundIfTime(value);
        value = (value < 0) ? 0 : value;
        //if(!this.abgelaufen) this.context.resortWithDelay(); // 5 oder 10 Sekunden, nachdem ein Timer abgelaufen ist, soll ein autoamtischer resort der Penalties getriggered werden.
        return value;
    }

    //time remaining in seconds
    public long getTimeRemainingSecs()
    {
        return (timerValue - this.getElapsedTime())/1000;
    }

    public String getTimeRemainingMinSecString()
    {
        long millisecs = this.getTimeRemaining();
        if(millisecs==0) return "00:00";
        else if(millisecs%1000 != 0) millisecs+=1000;// 60 --> 60; 59.9 --> 60; 59.1 --> 60; 59 --> 59

        long secs = millisecs/1000;
        long mins = secs/60;
        secs = secs%60;
        String secs_str = Long.toString(secs);
        String mins_str = Long.toString(mins);
        if(secs<10) secs_str = "0"+secs_str;
        if(mins<10) mins_str = "0"+mins_str;
        return mins_str+":"+secs_str;
    }

    public void setTimerValue(long timerValue)
    {
        this.timerValue = timerValue;
        setSoundPlayedArray();
    }

    public long setDeltaStartTime(long deltaStartTime)
    {
        long realTimeChange;
        //System.out.println(this.timerMode);
        //System.out.println(deltaStartTime);
        //System.out.println(this.getElapsedTime());
        realTimeChange = deltaStartTime;
        this.startTime = this.startTime + deltaStartTime;
        setSoundPlayedArray();
        return realTimeChange;
    }

    public boolean showRipple()
    {
        return showRipple;
    }

    public void rippleShown()
    {
        showRipple = false;
    }

}
