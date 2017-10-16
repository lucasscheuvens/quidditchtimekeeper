package de.lucasscheuvens.qtk;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class OverviewListAdapterOld extends ArrayAdapter<Game>
{
    private final Activity context;
    private List<Game> games;

    public OverviewListAdapterOld(Activity context, List<Game> games)
    {
        super(context, R.layout.list_item_overview, games);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.games=games;
    }

    /*@Override
    public int getCount() {
        return penalties.size();
    }

    @Override
    public Penalty getItem(int position) {
        return penalties.get(position);
    }*/

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
        ImageView team0jersey = (ImageView) view.findViewById(R.id.team0jersey);
        ImageView team1jersey = (ImageView) view.findViewById(R.id.team1jersey);
        TextView team0name = (TextView) view.findViewById(R.id.team0name);
        TextView team1name = (TextView) view.findViewById(R.id.team1name);
        TextView datetime = (TextView) view.findViewById(R.id.datetime);
        TextView score = (TextView) view.findViewById(R.id.score);
        // rest
        team0name.setText(game.teams[0].getName());
        team1name.setText(game.teams[1].getName());
        datetime.setText(game.getDateTimeStr());
        score.setText(game.getScoreStr());
        team0jersey.setImageResource(Admin.getJerseyRes(game.teams[0].getJerseyColor()));
        team1jersey.setImageResource(Admin.getJerseyRes(game.teams[1].getJerseyColor()));
    }
}