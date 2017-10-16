package de.lucasscheuvens.qtk;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by lucas on 23.10.2016.
 */

public class GameClock implements Serializable
{
    private Context context;
    private String mode;        // "stopwatch" or "timer"
    private long timerValue;    // in ms
    private long startTime;
    private long pauseTime;
    private long stackedTime;
    private boolean running;

    public GameClock(Context context, String mode, long timerValue)
    {
        this.context = context;
        this.mode = mode;
        if(!(this.mode.equals("stopwatch")) && !(this.mode.equals("timer"))) this.mode = "stopwatch";
        this.timerValue = timerValue;
        this.startTime = 0;
        this.pauseTime = 0;
        this.stackedTime = 0;
        this.running = false;
    }

    public String getMode()
    {
        return this.mode;
    }
    public void start() // same for both stopwatch and timer
    {
        this.stackedTime = this.getElapsedTime();
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }

    public void pause() // same for both stopwatch and timer
    {
        this.pauseTime = System.currentTimeMillis();
        this.running = false;
    }

    public void reset() // same for both stopwatch and timer
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
    } // same for both stopwatch and timer

    public long getElapsedTime() // in ms  // same for both stopwatch and timer
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
    public long getElapsedTimeSecs() // in s // same for both stopwatch and timer
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

    public String getMinSecString()
    {
        long secs = this.getElapsedTimeSecs();
        long mins = secs/60;
        secs = secs%60;
        String secs_str = Long.toString(secs);
        String mins_str = Long.toString(mins);
        if(secs<10) secs_str = "0"+secs_str;
        if(mins<10) mins_str = "0"+mins_str;
        return mins_str+":"+secs_str;
    }

    public long setDeltaStartTime(long deltaStartTime)
    {
        long realTimeChange;
        if(this.mode.equals("timer"))
        {
            if(deltaStartTime > this.getElapsedTime())
            {
                realTimeChange = this.getElapsedTime();
                this.reset();
            }
            else
            {
                realTimeChange = deltaStartTime;
                this.startTime = this.startTime + deltaStartTime;
            }
            return realTimeChange;
        }
        else
        {
            if(deltaStartTime > -this.getElapsedTime())
            {
                this.stackedTime += deltaStartTime;
                realTimeChange = deltaStartTime;
            }
            else
            {
                realTimeChange = -this.getElapsedTime();
                this.reset();
            }
            return realTimeChange;
        }

    }

    //////////////////////////////////////
    //////////////////////////////////////
    //// ONLY AVAILABLE FOR TIMER ////////
    //////////////////////////////////////
    //////////////////////////////////////

    public long getTimeRemaining() // in ms
    {
        if(this.mode.equals("timer"))
        {
            long value = timerValue - this.getElapsedTime();
            value = (value < 0) ? 0 : value;
            return value;
        }
        return 0;
    }

    public long getTimeRemainingSecs() // in ms
    {
        if(this.mode.equals("timer")) return (timerValue - this.getElapsedTime())/1000;
        return 0;
    }

    public String getTimeRemainingMinSecString()
    {
        if(this.mode.equals("timer"))
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
        return "ERROR";
    }

    public void setTimerValue(long timerValue)
    {
        if(this.mode.equals("timer")) this.timerValue = timerValue;
    }

}
