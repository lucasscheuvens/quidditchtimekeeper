package de.lucasscheuvens.qtk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class GameInfoActivity extends AppCompatActivity {

    //---------------------------------------
    //----- FROM INTENT ---------------------
    //---------------------------------------
    Game game;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_info);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Screen must not turn off
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Screen can turn off again

        Intent intent = getIntent();
        game = (Game) intent.getSerializableExtra("game");

        TextView team0name = (TextView) findViewById(R.id.team0name);
        TextView team1name = (TextView) findViewById(R.id.team1name);

        team0name.setText(game.teams[0].getNotNullName());
        team1name.setText(game.teams[1].getNotNullName());
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
