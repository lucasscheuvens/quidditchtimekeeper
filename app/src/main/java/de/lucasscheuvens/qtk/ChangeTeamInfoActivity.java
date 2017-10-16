package de.lucasscheuvens.qtk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class ChangeTeamInfoActivity extends AppCompatActivity {

    //---------------------------------------
    //----- TOOLBAR -------------------------
    //---------------------------------------
    private Toolbar toolbar;
    private TextView toolbarText;
    private EditText teamName_et;
    //---------------------------------------
    //----- FROM INTENT ---------------------
    //---------------------------------------
    Team team;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_team_info);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Screen must not turn off
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Screen can turn off again
        //---------------------------------------
        //----- TOOLBAR -------------------------
        //---------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_logo_white);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        toolbarText = (TextView) findViewById(R.id.toolbar_title);

        Intent intent = getIntent();
        team = (Team) intent.getSerializableExtra("team");

        setToolbarTeamName();

        teamName_et = (EditText) findViewById(R.id.teamname);
        if(team.getName() != null) teamName_et.setText(team.getName());
        teamName_et.addTextChangedListener(teamnameWatcher);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.putExtra("team", team);
        setResult(RESULT_OK, intent);
        finish();
        //super.onBackPressed();
    }

    private void setToolbarTeamName() {toolbarText.setText(team.getNotNullName());}

    private final TextWatcher teamnameWatcher = new TextWatcher()
    {
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
            //Nothing
        }

        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            team.setName(s.toString());
            setToolbarTeamName();
        }

        public void afterTextChanged(Editable s) {
            //Nothing
        }
    };

    public void selectColor(View w) // saves the selected color of team in a variable
    {
        String color = (String) w.getTag();
        selectColor(color);
    }
    public void selectColor(String color) // help function of selectColorTeam0 and selectColorTeam1
    {
        resetAllPickedColors();
        int tickId = getResources().getIdentifier("pick" + color + "tick", "id", getPackageName());
        View tick = findViewById(tickId);
        tick.setVisibility(View.VISIBLE);

        team.setJerseyColor(color);
    }
    public boolean resetAllPickedColors() // hides all ticks in color selection
    {
        ViewGroup parent = (ViewGroup) findViewById(R.id.jerseycolor);
        resetAllPickedColorsRecursively(parent);
        return true;
    }
    public boolean resetAllPickedColorsRecursively(ViewGroup parent) // help function of resetAllPickedColors()
    {
        if(parent != null)
        {
            for (int i = 0; i < parent.getChildCount(); i++)
            {
                View child = parent.getChildAt(i);
                if (child instanceof ViewGroup)
                {
                    resetAllPickedColorsRecursively((ViewGroup) child);
                } else if (child != null)
                {
                    try
                    {
                        if(child.getTag().equals("selectJerseyColor"))
                        {
                            //System.out.println("WAS HERE");
                            child.setVisibility(View.INVISIBLE);
                        }
                    }
                    catch (Exception e)
                    {
                        // do nothing
                    }
                }
            }
        }
        return true;
    }
}
