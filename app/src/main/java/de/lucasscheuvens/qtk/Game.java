package de.lucasscheuvens.qtk;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static java.lang.StrictMath.abs;

import static java.lang.StrictMath.abs;

public class Game extends AppCompatActivity implements Serializable
{
    public Context context;
    private long dbId;
    public Team[] teams = new Team[2];
    public Calendar datetime;
    public Locale locale;
    public long firstOvertimeLength;
    public long[][] scores;                 // first index = team; second index = term (regular, first overtime, second overtime)
    public boolean[][] catches;             // first index = team; second index = term (regular, first overtime, second overtime)
    public GameClock[] clocks;
    public long timeOutLength;
    public GameClock[] timeOuts;
    public boolean[] timeOutsTaken;
    public List<Penalty> penalties;
    public List<Long> firstOvertimeReminderTimes;
    public List<String> firstOvertimeReminderInstructionTitles;
    public List<String> firstOvertimeReminderInstructionMessages;
    public int activeTime;
    public long timeToGetSnitchReady;       // in seconds, 990=16.5*60
    public long timeToReleaseSnitch;        // in seconds, 1020=17*60
    public long timeToGetSeekersReady;      // in seconds, 1050=17.5*60
    public long timeToShowSnitches;         // in seconds, 1080=18*60
    public long timeToReleaseSeekers;       // in seconds, 1080=18*60

    public Game()
    {
        this.teams[0] = new Team(0);
        this.teams[1] = new Team(1);
        this.scores = new long[2][3];
        this.catches = new boolean[2][3];
        this.clocks = new GameClock[3];
        this.firstOvertimeLength = 5*60*1000L; // in ms
        this.timeOutLength = 1*60*1000L; // in ms
        this.clocks[0] = new GameClock(this.context, "stopwatch", 0);
        this.clocks[1] = new GameClock(this.context, "timer", this.firstOvertimeLength);
        this.clocks[2] = new GameClock(this.context, "stopwatch", 0);
        this.timeOuts = new GameClock[2];
        this.timeOuts[0] = new GameClock(this.context, "timer", this.timeOutLength);
        this.timeOuts[1] = new GameClock(this.context, "timer", this.timeOutLength);
        this.timeOutsTaken = new boolean[2];
        this.penalties = new ArrayList<>();
        this.activeTime = 0;
        //this.timeToGetSnitchReady = 990;    // in s
        //this.timeToReleaseSnitch = 1020;    // in s
        //this.timeToGetSeekersReady = 1050;  // in s
        //this.timeToShowSnitches = 1080;     // in s
        //this.timeToReleaseSeekers = 1080;   // in s
        this.timeToGetSnitchReady = 5;    // in s
        this.timeToReleaseSnitch = 10;    // in s
        this.timeToGetSeekersReady = 15;  // in s
        this.timeToShowSnitches = 20;     // in s
        this.timeToReleaseSeekers = 20;   // in s
        this.datetime = Calendar.getInstance();
        this.locale = Locale.getDefault();
        // FIRST OVERTIME REMINDERS
        this.firstOvertimeReminderTimes = new ArrayList<>();
        this.firstOvertimeReminderInstructionTitles = new ArrayList<>();
        this.firstOvertimeReminderInstructionMessages = new ArrayList<>();
        this.firstOvertimeReminderTimes.add(40*1000L);
        this.firstOvertimeReminderInstructionTitles.add("Shout out the following times:");
        this.firstOvertimeReminderInstructionMessages.add("\u2022 00:30\n\u2022 00:15\n\u2022 each second from 00:10 to 00:00 as countdown");
        this.firstOvertimeReminderTimes.add(70*1000L);
        this.firstOvertimeReminderInstructionTitles.add("Shout out remaining time at 01:00!");
        this.firstOvertimeReminderInstructionMessages.add(null);
        this.firstOvertimeReminderTimes.add(130*1000L);
        this.firstOvertimeReminderInstructionTitles.add("Shout out remaining time at 02:00!");
        this.firstOvertimeReminderInstructionMessages.add(null);
        this.firstOvertimeReminderTimes.add(190*1000L);
        this.firstOvertimeReminderInstructionTitles.add("Shout out remaining time at 03:00!");
        this.firstOvertimeReminderInstructionMessages.add(null);
        this.firstOvertimeReminderTimes.add(250*1000L);
        this.firstOvertimeReminderInstructionTitles.add("Shout out remaining time at 04:00!");
        this.firstOvertimeReminderInstructionMessages.add(null);
    }

    public void setDBId(long id) {this.dbId = id;}
    public long getDBId() {return this.dbId;}
    public String getScoreStr()
    {
        long score0 = this.scores[0][0]+this.scores[0][1]+this.scores[0][2];
        long score1 = this.scores[1][0]+this.scores[1][1]+this.scores[1][2];
        String catches0 = "";
        String catches1 = "";
        if(this.catches[0][0]) catches0+="*";
        if(this.catches[1][0]) catches1+="*";
        if(this.catches[0][1]) catches0+="^";
        if(this.catches[1][1]) catches1+="^";
        if(this.catches[0][2]) catches0+="!";
        if(this.catches[1][2]) catches1+="!";
        return Long.toString(score0)+catches0+" : "+Long.toString(score1)+catches1;
    }
    public String getDateTimeStr()
    {
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy 'at' hh:mm a z", this.locale);
        return format.format(this.datetime.getTime());
    }


}
