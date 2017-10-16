package de.lucasscheuvens.qtk;

import java.io.Serializable;

/**
 * Created by lucas on 18.11.2016.
 */

public class ScoreEvent implements Serializable
{
    private String penalty_id;
    private int team_scored;
    private long old_score;
    private long new_score;
    private long game_time;
    private String dir;

    public ScoreEvent(int team_scored, long old_score, long new_score, long game_time, String penalty_id)
    {
        this.penalty_id = penalty_id;
        this.team_scored = team_scored;
        this.old_score = old_score;
        this.new_score = new_score;
        this.game_time = game_time;
        if(old_score < new_score) this.dir = "up";
        else this.dir = "down";
    }

    public String getPenaltyId()
    {
        return this.penalty_id;
    }
    public boolean getIfIncrease()
    {
        return (new_score > old_score);
    }

    public int getTeam()
    {
        return this.team_scored;
    }

    public long getOldScore()
    {
        return this.old_score;
    }
    public long getNewScore()
    {
        return this.new_score;
    }
    public long getGameTime()
    {
        return this.old_score;
    }
}
