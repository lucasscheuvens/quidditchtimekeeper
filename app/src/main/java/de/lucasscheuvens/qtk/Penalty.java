package de.lucasscheuvens.qtk;

import java.io.Serializable;

public class Penalty implements Serializable
{
    private String id;
    private long yellowTime = 60; // in s
    private long blueTime = 30; // in s
    private long redTime = 120; // in s
    private long yellowredTime = 120; // in s
    private PenaltyTimer timer;
    private String totalTimeStr;
    private long timestamp;
    private long totalTime;
    private int term; // 0-->'reg_time', 1-->'first_overtime', 2-->'second_overtime'
    private String cardColor;
    private Team team;
    private Player player;
    private String player_number;
    private int player_team_pos;
    private String player_name;
    private boolean reduced; // describes if penalty was reduced
    private boolean reducible; // beschreibt, ob timer durch ein Tor des Gegners aufgehoben werden kann

    public Penalty(String Id, String color, Team team, long time, int term)
    {
        this.id = Id;
        this.team = team;
        this.reduced = false;
        this.totalTime= time;
        this.player = new Player("Test Name", "23");
        this.totalTimeStr = "@"+Admin.convertSecToMinSecString(time);
        this.term = term;
        this.timer = new PenaltyTimer(0);
        this.setColor(color);
        this.updateTimestamp();
    }

    public void setColor(String color)
    {
        this.cardColor = color;
        switch(color)
        {
            case "yellow":
                this.timer.setTimerValue(this.yellowTime*1000);
                this.reducible = true;
                break;
            case "blue":
                this.timer.setTimerValue(this.blueTime*1000);
                this.reducible = true;
                break;
            case "red":
                this.timer.setTimerValue(this.redTime*1000);
                this.reducible = false;
                break;
            case "yellowred":
                this.timer.setTimerValue(this.yellowredTime*1000);
                this.reducible = false;
                break;
        }
        this.updateTimestamp();
    }
    public void setPlayer(Player player) {this.player = player;this.updateTimestamp();}
    public void setPlayerTeam(Team team) {this.team = team;this.updateTimestamp();}
    public String getId() {return this.id;}
    public String getPlayerName() {return this.player.getName();}
    public String getPlayerNumber() {return this.player.getNumber();}
    public int getTeamJersey() {return this.team.getJerseyColorRes();}
    public String getTimeRemainingString(String mode)
    {
        if(mode.equals("released")) {
            if (this.reduced) return "released";
            else if (this.timer.getTimeRemaining() == 0) return "released";
            return this.timer.getTimeRemainingMinSecString();
        }
        else if(mode.equals("00:00")) {
            if (this.reduced) return "released";
            else if (this.timer.getTimeRemaining() == 0) return "00:00";
            return this.timer.getTimeRemainingMinSecString();
        }
        return "ERROR in Penalty.java";
    }
    public String getTimeRemainingString() {return this.getTimeRemainingString("released");}
    public long getTimeRemaining() {if(this.reduced) return 0;return this.timer.getTimeRemaining();}
    public void startTimer() {this.timer.start();}
    public void pauseTimer() {this.timer.pause();}
    public void applyTimeDelta(long delta_time)
    {
        PenaltyTimer timer = this.timer;
        timer.setDeltaStartTime(delta_time);
        this.updateTimestamp();
    }
    public boolean showRipple()
    {
        PenaltyTimer timer = this.timer;
        return timer.showRipple();
    }
    public void rippleShown()
    {
        PenaltyTimer timer = this.timer;
        timer.rippleShown();
    }
    public long getTotalTime()
    {
        return this.totalTime;
    }
    public String getTotalTimeStr()
    {
        return this.totalTimeStr;
    }
    public int getTerm()
    {
        return this.term;
    }
    public int getCardRes()
    {
        switch(this.cardColor)
        {
            case "yellow":
                return R.drawable.ic_card_yellow;
            case "blue":
                return R.drawable.ic_card_blue;
            case "red":
                return R.drawable.ic_card_red;
            case "yellowred":
                return R.drawable.ic_card_yellowred;
            default: return -1;
        }
    }
    public boolean getReducible() {return this.reducible;}
    public void setReduced() {this.reduced = true;this.updateTimestamp();}
    public void setUnreduced() {this.reduced = false;this.updateTimestamp();}
    public boolean getReduced() {return this.reduced;}
    public long getYellowTime() {return yellowTime;}
    public long getBlueTime() {return blueTime;}
    public long getRedTime() {return redTime;}
    public long getYellowRedTime() {return yellowredTime;}
    public void updateTimestamp(){this.timestamp = System.currentTimeMillis();}
    public long getTimestamp(){return this.timestamp;}
}
