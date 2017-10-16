package de.lucasscheuvens.qtk;

import java.io.Serializable;

/**
 * Created by Lucas on 25.02.2017.
 */

public class Player implements Serializable
{
    private String name;
    private String number;

    public Player(String name, String number)
    {
        this.name = name;
        this.number = number;
    }

    public void setName(String name) {this.name = name;}
    public String getName() {return this.name;}
    public void setNumber(String number) {this.number = number;}
    public String getNumber() {return this.number;}
}
