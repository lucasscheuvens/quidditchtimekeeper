package de.lucasscheuvens.qtk;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class PenaltyListAdapter extends RecyclerView.Adapter<PenaltyListAdapter.PenaltyListViewHolder>
{
    private LayoutInflater inflater;
    private Context context;
    List<Penalty> data = Collections.emptyList();

    private final OnItemClickListener listener;
    private final OnItemLongClickListener longListener;

    public PenaltyListAdapter(Context context, List<Penalty> data, OnItemClickListener listener, OnItemLongClickListener longListener)
    {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
        this.listener = listener;
        this.longListener = longListener;
    }

    @Override
    public PenaltyListViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.list_item_penalties, parent, false);
        PenaltyListViewHolder holder = new PenaltyListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PenaltyListViewHolder holder, int position)
    {
        holder.bind(position, data.get(position), listener, longListener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class PenaltyListViewHolder extends RecyclerView.ViewHolder
    {
        ImageView card_iv;
        TextView playerNumber_tv;
        TextView playerName_tv;
        TextView penaltyTimeEntered_tv;
        TextView penaltyCountdown_tv;

        public PenaltyListViewHolder(View itemView)
        {
            super(itemView);
            playerNumber_tv = (TextView) itemView.findViewById(R.id.playerNumber);
            playerName_tv = (TextView) itemView.findViewById(R.id.playerName);
            penaltyTimeEntered_tv = (TextView) itemView.findViewById(R.id.penalty_time_entered);
            penaltyCountdown_tv = (TextView) itemView.findViewById(R.id.penalty_countdown);
            card_iv = (ImageView) itemView.findViewById(R.id.card);
        }

        public void bind(final int position, final Penalty penalty, final OnItemClickListener listener, final OnItemLongClickListener longListener)
        {
            //countdown
            /*if(penalty.showRipple())
            {
                penalty.rippleShown();
                if(countdown!=null)
                {
                    RippleDrawable rd = (RippleDrawable) countdown.getBackground();
                    rd.setState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled });
                    rd.setState(new int[] {  });
                }
            }*/
            card_iv.setImageResource(penalty.getCardRes());
            playerNumber_tv.setBackground(ContextCompat.getDrawable(context, penalty.getTeamJersey()));
            playerNumber_tv.setText(penalty.getPlayerNumber());
            playerName_tv.setText(penalty.getPlayerName());
            penaltyTimeEntered_tv.setText(penalty.getTotalTimeStr());
            penaltyCountdown_tv.setText(penalty.getTimeRemainingString());
            if(penalty.getTimeRemaining()==0) penaltyCountdown_tv.setTextSize(18);
            else penaltyCountdown_tv.setTextSize(22);//this should be @dimen/textAppearanceLarge

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(position, penalty);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    longListener.onItemLongClick(position, penalty);
                    return true;
                }
            });
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position, Penalty penalty);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(int position, Penalty penalty);
    }

}

/*
public class OverviewListAdapter extends ArrayAdapter<Game>
{
    private final Activity context;
    private List<Game> games;

    public OverviewListAdapter(Activity context, List<Game> games)
    {
        super(context, R.layout.list_item_overview, games);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.games=games;
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        View rowView;
        if(view == null)
        {
            LayoutInflater inflater=context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_overview, parent, false);
        }
        else rowView = view;
        this.updateView(rowView, games.get(position));
        return rowView;
    };

    public void updateView(View view, Game game)
    {
        TextView boolval = (TextView) view.findViewById(R.id.boolval);
        TextView intval = (TextView) view.findViewById(R.id.intval);
        TextView stringval = (TextView) view.findViewById(R.id.stringval);
        TextView timeval = (TextView) view.findViewById(R.id.timeval);

        boolval.setText(Boolean.toString(game.getTest1()));
        intval.setText(Integer.toString(game.getTest2()));
        stringval.setText(game.getTest3());
        timeval.setText(game.getLastUpdateTimeString());
    }
}
 */