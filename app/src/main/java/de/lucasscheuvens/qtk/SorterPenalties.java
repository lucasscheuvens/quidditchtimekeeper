package de.lucasscheuvens.qtk;

import java.util.Comparator;

/**
 * Created by lucas on 18.11.2016.
 */

public class SorterPenalties implements Comparator<Penalty>
{
    @Override
    public int compare(Penalty p1, Penalty p2)
    {
        long timeRemaining1 = p1.getTimeRemaining();
        long timeRemaining2 = p2.getTimeRemaining();
        if(timeRemaining1 == 0 && timeRemaining2 == 0)
        {
            long timeEntered1 = p1.getTotalTime();
            long timeEntered2 = p2.getTotalTime();
            if(timeEntered1 == timeEntered2)
            {
                long systemTimeModified1 = p1.getTimestamp();
                long systemTimeModified2 = p2.getTimestamp();
                if(systemTimeModified1 == systemTimeModified2) return 0;
                else if(systemTimeModified1 > systemTimeModified2) return -1;
                else if(systemTimeModified1 < systemTimeModified2) return 1;
            }
            else if(timeEntered1 > timeEntered2) return -1;
            else if(timeEntered1 < timeEntered2) return 1;
        }
        else if (timeRemaining1 == 0 && timeRemaining2 > 0)
        {
            return 1;
        }
        else if (timeRemaining1 > 0 && timeRemaining2 == 0)
        {
            return -1;
        }
        else if(timeRemaining1 > 0 && timeRemaining2 > 0)
        {
            if(timeRemaining1 == timeRemaining2){
                if(p1.getReduced() && p2.getReduced()) return 0;
                else if(p1.getReduced() && !p2.getReduced()) return 1;
                else if(!p1.getReduced() && p2.getReduced()) return -1;
                else if(!p1.getReduced() && !p2.getReduced()) return 0;
            }
            else if(timeRemaining1 > timeRemaining2) return 1;
            else if(timeRemaining1 < timeRemaining2) return -1;
        }
        return 0;
    }
}
