package de.lucasscheuvens.qtk;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class OverviewListAdapter extends RecyclerView.Adapter<OverviewListAdapter.OverviewListViewHolder>
{
    private LayoutInflater inflater;
    List<Game> data = Collections.emptyList();

    private final OnItemClickListener listener;
    private final OnItemLongClickListener longListener;

    public OverviewListAdapter(Context context, List<Game> data, OnItemClickListener listener, OnItemLongClickListener longListener)
    {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.listener = listener;
        this.longListener = longListener;
    }

    @Override
    public OverviewListViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.list_item_overview, parent, false);
        OverviewListViewHolder holder = new OverviewListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(OverviewListViewHolder holder, int position)
    {
        holder.bind(position, data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class OverviewListViewHolder extends RecyclerView.ViewHolder
    {
        TextView team0name_tv;
        TextView team1name_tv;
        TextView score_tv;
        TextView datetime_tv;
        ImageView team0jersey_iv;
        ImageView team1jersey_iv;

        public OverviewListViewHolder(View itemView) {
            super(itemView);
            team0name_tv =  (TextView) itemView.findViewById(R.id.team0name);
            team1name_tv =  (TextView) itemView.findViewById(R.id.team1name);
            score_tv =  (TextView) itemView.findViewById(R.id.score);
            datetime_tv =  (TextView) itemView.findViewById(R.id.datetime);
            team0jersey_iv =  (ImageView) itemView.findViewById(R.id.team0jersey);
            team1jersey_iv =  (ImageView) itemView.findViewById(R.id.team1jersey);
        }

        public void bind(final int position, final Game current_game, final OnItemClickListener listener)
        {
            this.team0name_tv.setText(current_game.teams[0].getNotNullName());
            this.team1name_tv.setText(current_game.teams[1].getNotNullName());
            this.score_tv.setText(current_game.getScoreStr());
            this.datetime_tv.setText(current_game.getDateTimeStr());
            this.team0jersey_iv.setImageResource(current_game.teams[0].getJerseyColorRes());
            this.team1jersey_iv.setImageResource(current_game.teams[1].getJerseyColorRes());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(position, current_game);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    longListener.onItemLongClick(position, current_game);
                    return true;
                }
            });
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position, Game game);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(int position, Game game);
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