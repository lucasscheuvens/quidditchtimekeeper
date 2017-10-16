package de.lucasscheuvens.qtk;

/**
 * Created by Lucas on 25.02.2017.
 */

public class Admin
{
    public static int returnTShirtTextSize(String playerNumber)
    {
        if(playerNumber != null) {
            if (playerNumber.length() == 1) return 18;
            else if (playerNumber.length() == 2) return 14;
            else if (playerNumber.length() == 3) return 10;
            else return -1;
        }
        return 22;
    }
    public static int getTextColorForegroundOfTeamJersey(Team team) {return getColorForegroundOfTeamJersey(team, "text");}
    public static int getTickColorForegroundOfTeamJersey(Team team) {return getColorForegroundOfTeamJersey(team, "tick");}
    public static int getColorForegroundOfTeamJersey(Team team, String mode)
    {
        String jerseyColor = team.getJerseyColor();
        if(mode.equals("text"))
        {
            switch (jerseyColor)
            {
                default:
                    return R.color.black;
                case "black":
                    return R.color.white;
                case "blue":
                    return R.color.white;
                case "brown":
                    return R.color.white;
                case "cyan":
                    return R.color.black;
                case "darkblue":
                    return R.color.white;
                case "darkgreen":
                    return R.color.white;
                case "darkpurple":
                    return R.color.white;
                case "darkred":
                    return R.color.white;
                case "green":
                    return R.color.black;
                case "orange":
                    return R.color.black;
                case "petrol":
                    return R.color.white;
                case "pink":
                    return R.color.white;
                case "purple":
                    return R.color.white;
                case "red":
                    return R.color.white;
                case "white":
                    return R.color.black;
                case "yellow":
                    return R.color.black;
                case "transparent":
                    return R.color.black;
            }
        }
        else if(mode.equals("tick"))
        {
            switch (jerseyColor) {
                default:
                    return R.drawable.ic_tick_black;
                case "black":
                    return R.drawable.ic_tick_white;
                case "blue":
                    return R.drawable.ic_tick_white;
                case "brown":
                    return R.drawable.ic_tick_white;
                case "cyan":
                    return R.drawable.ic_tick_black;
                case "darkblue":
                    return R.drawable.ic_tick_white;
                case "darkgreen":
                    return R.drawable.ic_tick_white;
                case "darkpurple":
                    return R.drawable.ic_tick_white;
                case "darkred":
                    return R.drawable.ic_tick_white;
                case "green":
                    return R.drawable.ic_tick_black;
                case "orange":
                    return R.drawable.ic_tick_black;
                case "petrol":
                    return R.drawable.ic_tick_white;
                case "pink":
                    return R.drawable.ic_tick_white;
                case "purple":
                    return R.drawable.ic_tick_white;
                case "red":
                    return R.drawable.ic_tick_white;
                case "white":
                    return R.drawable.ic_tick_black;
                case "yellow":
                    return R.drawable.ic_tick_black;
                case "transparent":
                    return R.drawable.ic_tick_black;
            }
        }
        return -1;
    }
    //-------------------------------------------------
    //----- HELP FUNCTIONS ----------------------------
    //-------------------------------------------------
    public static int teamToCurrentColorRes(Team team, String mode)
    {
        String jerseyColor = team.getJerseyColor();
        if(!mode.equals("circle_drawable_id") && !mode.equals("jersey_drawable_id")) mode = "circle_drawable_id";
        switch (mode)
        {
            case "circle_drawable_id":
                return getCircleRes(jerseyColor);
            case "jersey_drawable_id":
                return getJerseyRes(jerseyColor);
            default:
                return 0;
        }
    }
    public static int getCircleRes(String jerseyColor)
    {
        switch (jerseyColor)
        {
            default:
                return 0;
            /*case "black":
                return R.drawable.ic_circleblack;
            case "blue":
                return R.drawable.ic_circleblue;
            case "brown":
                return R.drawable.ic_circlebrown;
            case "cyan":
                return R.drawable.ic_circlecyan;
            case "darkblue":
                return R.drawable.ic_circledarkblue;
            case "darkgreen":
                return R.drawable.ic_circledarkgreen;
            case "darkpurple":
                return R.drawable.ic_circledarkpurple;
            case "darkred":
                return R.drawable.ic_circledarkred;
            case "green":
                return R.drawable.ic_circlegreen;
            case "orange":
                return R.drawable.ic_circleorange;
            case "petrol":
                return R.drawable.ic_circlepetrol;
            case "pink":
                return R.drawable.ic_circlepink;
            case "purple":
                return R.drawable.ic_circlepurple;
            case "red":
                return R.drawable.ic_circlered;
            case "white":
                return R.drawable.ic_circlewhite;
            case "yellow":
                return R.drawable.ic_circleyellow;
            case "transparent":
                return R.drawable.ic_circletransparent;*/
        }
    }
    public static int getJerseyRes(String jerseyColor)
    {
        switch (jerseyColor)
        {
            default:
                return 0;
            case "black":
                return R.drawable.ic_shirt_black;
            case "blue":
                return R.drawable.ic_shirt_blue;
            case "brown":
                return R.drawable.ic_shirt_brown;
            case "cyan":
                return R.drawable.ic_shirt_cyan;
            case "darkblue":
                return R.drawable.ic_shirt_darkblue;
            case "darkgreen":
                return R.drawable.ic_shirt_darkgreen;
            case "darkpurple":
                return R.drawable.ic_shirt_darkpurple;
            case "darkred":
                return R.drawable.ic_shirt_darkred;
            case "green":
                return R.drawable.ic_shirt_green;
            case "orange":
                return R.drawable.ic_shirt_orange;
            case "petrol":
                return R.drawable.ic_shirt_petrol;
            case "pink":
                return R.drawable.ic_shirt_pink;
            case "purple":
                return R.drawable.ic_shirt_purple;
            case "red":
                return R.drawable.ic_shirt_red;
            case "white":
                return R.drawable.ic_shirt_white;
            case "yellow":
                return R.drawable.ic_shirt_yellow;
            case "transparent":
                return R.drawable.ic_shirt_transparent;
        }
    }
    public static String getMinSecStringFromSec(long secs)
    {
        long mins = secs/60;
        secs = secs%60;
        String secs_str = Long.toString(secs);
        String mins_str = Long.toString(mins);
        if(secs<10) secs_str = "0"+secs_str;
        if(mins<10) mins_str = "0"+mins_str;
        return mins_str+":"+secs_str;
    }
    public static String convertSecToMinSecString(long secs)
    {
        long mins = secs/60;
        secs = secs%60;
        String secs_str = Long.toString(secs);
        String mins_str = Long.toString(mins);
        if(secs<10) secs_str = "0"+secs_str;
        if(mins<10) mins_str = "0"+mins_str;
        return mins_str+":"+secs_str;
    }
    public static long getTotalTimeElapsed(GameClock[] gameclocks)
    {
        long timeElapsed = 0;
        for(GameClock gameclock : gameclocks)
        {
            timeElapsed+=gameclock.getElapsedTime();
        }
        return timeElapsed;
    }
    public static void sysout(String str)
    {
        System.out.println(str);
    }
    public static void sysout(long l)
    {
        System.out.println(l);
    }
    public static void sysout(int i)
    {
        System.out.println(i);
    }
}
