package de.lucasscheuvens.qtk;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by lucas on 23.10.2016.
 */

public class Stopwatch implements Serializable
{
    private Context context;
    private long startTime;
    private long pauseTime;
    private long stackedTime;
    private boolean running;

    public Stopwatch(Context context)
    {
        this.context = context;
        this.startTime = 0;
        this.pauseTime = 0;
        this.stackedTime = 0;
        this.running = false;
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
    public long getElapsedTime() // in ms
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
    public long getElapsedTimeSecs() // in s
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
