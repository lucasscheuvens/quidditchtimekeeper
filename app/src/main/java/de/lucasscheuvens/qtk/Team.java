package de.lucasscheuvens.qtk;

import java.io.Serializable;

/**
 * Created by Lucas on 25.02.2017.
 */

public class Team implements Serializable
{
    private int position; // either "0" or "1"
    private String name;
    private String jerseyColor;

    public Team(int position)
    {
        this.position = position;
        this.name = null;
        this.jerseyColor = "transparent";
    }

    public int getPosition() {return this.position;}
    public void setName(String name) {this.name = name;}
    public String getName() {return this.name;}
    public void setJerseyColor(String jerseyColor) {this.jerseyColor = jerseyColor;}
    public String getJerseyColor() {return this.jerseyColor;}
    public int getJerseyColorRes() {return Admin.getJerseyRes(this.jerseyColor);}
    public String getNotNullName() // returns "Team A" and "Team B" instead of null
    {
        if(this.name == null)
        {
            if(this.position == 0) return "Team A";
            else if(this.position == 1) return "Team B";
        }
        return this.name;
    }
}
