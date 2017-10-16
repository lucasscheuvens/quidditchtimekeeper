package de.lucasscheuvens.qtk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.lang.StrictMath.abs;

import android.support.design.widget.FloatingActionButton;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.StrictMath.abs;

public class GameActivity extends AppCompatActivity implements java.io.Serializable
{
    private Game game;
    private DBHelper db;
    private Context context;
    private TextView clock_vals[];
    private CustomScrollView scoreViews[][];        // first index = team; second index = term (regular, first overtime, second overtime)
    private TextView teamname_tvs[];
    private ImageView teamjersey_ivs[];
    //---------------------------------------
    //----- TOOLBAR -------------------------
    //---------------------------------------
    private Toolbar toolbar;
    private TextView toolbarTitle;
    //---------------------------------------
    //----- STOPWATCH -----------------------
    //---------------------------------------
    private long previousRemaining; // first overtime sound help variable
    private List<Boolean> firstOvertimeSoundPlayed;
    private boolean[] seekerReleaseSoundPlayed;
    //private long[] firstOvertimeSoundPlayedArrayReminderTimes;
    //private Stopwatch sw_second_overtime;
    //private TextView sw_val;
    //private TextView timer_first_overtime_val;
    //private TextView sw_second_overtime_val;
    //private String currently_active_time; //"reg_time", "first_overtime", "second_overtime"
    //---------------------------------------
    //----- PLAY/PAUSE ANIMATION ------------
    //---------------------------------------
    private ImageView playPause[];
    private AnimatedVectorDrawable playToPause;
    private AnimatedVectorDrawable pauseToPlay;
    private boolean play = true;
    //---------------------------------------
    //----- GENERAL RUNTIME VARIABLES -------
    //---------------------------------------
    /*long pointsTeam0_regTime;
    long pointsTeam1_regTime;
    long pointsTeam0_firstOvertime;
    long pointsTeam1_firstOvertime;
    long pointsTeam0_secondOvertime;
    long pointsTeam1_secondOvertime;*/
    boolean scoreBoardDisabled[];
    FloatingActionButton[][][] snitchFABs;      // 0 --> team, 1 --> term, 2 --> good/no good (good being 0, no good being 1)
    View[][][] scoreArrows;                     // 0 --> team, 1 --> term, 2 --> up/down (up being 0, down being 1)
    View penaltyEdit;
    View[] clockWrappers;
    View[] scoreWrappers;
    /*String jerseyColorTeam0;
    String jerseyColorTeam1;
    String teamName0;
    String teamName1;*/
    /*boolean team0ChangeFirstTime;
    boolean team1ChangeFirstTime;
    boolean snitchCaughtTeam0;
    boolean snitchCaughtTeam1;*/
    /*long timeToGetSnitchReady; // in seconds, 990=16,5*60
    long timeToReleaseSnitch; // in seconds, 1020=17*60
    long timeToGetSeekersReady; // in seconds, 1050=17,5*60
    long timeToShowSnitches; // in seconds, 1080=18*60
    long timeToReleaseSeekers; // in seconds, 1080=18*60*/
    int seekerFloorCountdownSeconds;
    boolean getSnitchReadyAlertShown;
    boolean getReleaseSnitchNotificationShown;
    boolean getSeekersReadyAlertShown;
    boolean getReleaseSeekersNotificationShown;
    //private boolean[] seekerReleaseSoundPlayedArray;
    //private long firstOvertimeReminderTimeInAdvance; // in ms // how much in advance should a "shout time out now" be called?
    private long previousTimeSeekerSound; // help variable, needed
    private MediaPlayer player_bip;
    private MediaPlayer player_beeep;
    boolean snitchesShown;
    private MediaPlayer alert1;
    //---------------------------------------
    //----- SCORE SCROLL --------------------
    //---------------------------------------
    /*CustomScrollView scoreTeam0_regTime;
    CustomScrollView scoreTeam1_regTime;
    CustomScrollView scoreTeam0_firstOvertime;
    CustomScrollView scoreTeam1_firstOvertime;
    CustomScrollView scoreTeam0_secondOvertime;
    CustomScrollView scoreTeam1_secondOvertime;*/
    //---------------------------------------
    //----- CHANGE PENALTY VARs--------------
    //---------------------------------------
    Penalty currently_active_penalty;
    int penalty_currently_active_pos;
    int penalty_selected_team;
    String penalty_player_number;
    String penalty_player_name;
    long penalty_time_delta;
    String penalty_color;
    //---------------------------------------
    //----- CHANGE TIME VARs ----------------
    //---------------------------------------
    AlertDialog timeChangeGlobalAlertDialog;
    TextView timeChangeGlobalTextView;
    long changeGlobalTime; // in secs
    //---------------------------------------
    //----- RELEASE PLAYER ON OPPONENT SCORE
    //---------------------------------------
    List<ScoreEvent> score_events;
    //---------------------------------------
    //----- FABs ----------------------------
    //---------------------------------------
    FloatingActionButton fabBlue;
    FloatingActionButton fabYellow;
    FloatingActionButton fabRed;

    //View penalty_slider;
    long maxlong = 9223372036854775807L;

    Menu menu = null;

    //---------------------------------------
    //----- PENALTY LIST --------------------
    //---------------------------------------
    private RecyclerView penaltyList;
    private PenaltyListAdapter penaltyListAdapter;
    private PenaltyListAdapter.OnItemClickListener penaltyClickListener;
    private PenaltyListAdapter.OnItemLongClickListener penaltyLongClickListener;
    //---------------------------------------
    //----- PLAYER NUMBER KEYBOARD ----------
    //---------------------------------------
    EditText focusedPlayerNumberEditText;
    View numberKeyboard;
    int maxPlayerNumberSize;

    /**********************************************/
    /***** CREATOR ********************************/
    /**********************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        context = GameActivity.this;
        db = new DBHelper(context);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Screen must not turn off
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Screen can turn off again
        //---------------------------------------
        //----- DATA FROM PREVIOUS ACTIVITY -----
        //---------------------------------------
        Intent intent = getIntent();
        game = (Game) intent.getSerializableExtra("game");
        //---------------------------------------
        //----- TOOLBAR -------------------------
        //---------------------------------------
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_logo_white);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        updateToolbarText();
        //---------------------------------------
        //----- STOPWATCH UI --------------------
        //---------------------------------------
        clock_vals = new TextView[3];
        clock_vals[0] = (TextView) findViewById(R.id.stopwatch);
        clock_vals[1] = (TextView) findViewById(R.id.timer_first_overtime);
        clock_vals[2] = (TextView) findViewById(R.id.stopwatch_second_overtime);
        //---------------------------------------
        //----- PLAY/PAUSE ANIMATION ------------
        //---------------------------------------
        playPause = new ImageView[3];
        playPause[0] = (ImageView) findViewById(R.id.play_pause);
        playPause[1] = (ImageView) findViewById(R.id.play_pause_first_overtime);
        playPause[2] = (ImageView) findViewById(R.id.play_pause_second_overtime);
        playToPause = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_play_to_pause);
        pauseToPlay = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_pause_to_play);
        //---------------------------------------
        //----- SLIDER INITIALIZATION ----------- --> this initialization slides down every menu which is slid up on runtime
        //---------------------------------------
        //initializeSliders();
        //---------------------------------------
        //----- SCORE SCROLL -------------------- --> this disables manual scrolling on the score scrolls
        //---------------------------------------
        scoreViews = new CustomScrollView[2][3];
        scoreViews[0][0] = (CustomScrollView) findViewById(R.id.team0_reg_time_scorefield);
        scoreViews[0][0].setEnableScrolling(false);
        scoreViews[1][0] = (CustomScrollView) findViewById(R.id.team1_reg_time_scorefield);
        scoreViews[1][0].setEnableScrolling(false);
        scoreViews[0][1] = (CustomScrollView) findViewById(R.id.team0_first_overtime_scorefield);
        scoreViews[0][1].setEnableScrolling(false);
        scoreViews[1][1] = (CustomScrollView) findViewById(R.id.team1_first_overtime_scorefield);
        scoreViews[1][1].setEnableScrolling(false);
        scoreViews[0][2] = (CustomScrollView) findViewById(R.id.team0_second_overtime_scorefield);
        scoreViews[0][2].setEnableScrolling(false);
        scoreViews[1][2] = (CustomScrollView) findViewById(R.id.team1_second_overtime_scorefield);
        scoreViews[1][2].setEnableScrolling(false);

        scoreBoardDisabled = new boolean[3];

        snitchFABs = new FloatingActionButton[2][3][2];
        snitchFABs[0][0][0] = (FloatingActionButton) findViewById(R.id.snitchcatchteam0_reg_time);
        snitchFABs[0][0][1] = (FloatingActionButton) findViewById(R.id.snitchcatchnogoodteam0_reg_time);
        snitchFABs[0][1][0] = (FloatingActionButton) findViewById(R.id.snitchcatchteam0_first_overtime);
        snitchFABs[0][1][1] = (FloatingActionButton) findViewById(R.id.snitchcatchnogoodteam0_first_overtime);
        snitchFABs[0][2][0] = (FloatingActionButton) findViewById(R.id.snitchcatchteam0_second_overtime);
        snitchFABs[0][2][1] = (FloatingActionButton) findViewById(R.id.snitchcatchnogoodteam0_second_overtime);
        snitchFABs[1][0][0] = (FloatingActionButton) findViewById(R.id.snitchcatchteam1_reg_time);
        snitchFABs[1][0][1] = (FloatingActionButton) findViewById(R.id.snitchcatchnogoodteam1_reg_time);
        snitchFABs[1][1][0] = (FloatingActionButton) findViewById(R.id.snitchcatchteam1_first_overtime);
        snitchFABs[1][1][1] = (FloatingActionButton) findViewById(R.id.snitchcatchnogoodteam1_first_overtime);
        snitchFABs[1][2][0] = (FloatingActionButton) findViewById(R.id.snitchcatchteam1_second_overtime);
        snitchFABs[1][2][1] = (FloatingActionButton) findViewById(R.id.snitchcatchnogoodteam1_second_overtime);

        scoreArrows = new View[2][3][2];
        scoreArrows[0][0][0] = findViewById(R.id.team0_score_up_reg_time);
        scoreArrows[0][0][1] = findViewById(R.id.team0_score_down_reg_time);
        scoreArrows[0][1][0] = findViewById(R.id.team0_score_up_first_overtime);
        scoreArrows[0][1][1] = findViewById(R.id.team0_score_down_first_overtime);
        scoreArrows[0][2][0] = findViewById(R.id.team0_score_up_second_overtime);
        scoreArrows[0][2][1] = findViewById(R.id.team0_score_down_second_overtime);
        scoreArrows[1][0][0] = findViewById(R.id.team1_score_up_reg_time);
        scoreArrows[1][0][1] = findViewById(R.id.team1_score_down_reg_time);
        scoreArrows[1][1][0] = findViewById(R.id.team1_score_up_first_overtime);
        scoreArrows[1][1][1] = findViewById(R.id.team1_score_down_first_overtime);
        scoreArrows[1][2][0] = findViewById(R.id.team1_score_up_second_overtime);
        scoreArrows[1][2][1] = findViewById(R.id.team1_score_down_second_overtime);

        teamname_tvs = new TextView[2];
        teamname_tvs[0] = (TextView) findViewById(R.id.teamname0);
        teamname_tvs[1] = (TextView) findViewById(R.id.teamname1);

        teamjersey_ivs = new ImageView[2];
        teamjersey_ivs[0] = (ImageView) findViewById(R.id.jerseyteam0);
        teamjersey_ivs[1] = (ImageView) findViewById(R.id.jerseyteam1);

        penaltyEdit = findViewById(R.id.penalty_edit);

        clockWrappers = new RelativeLayout[3];
        clockWrappers[0] = findViewById(R.id.stopwatch_reg_time_wrapper);
        clockWrappers[1] = findViewById(R.id.stopwatch_first_overtime_wrapper);
        clockWrappers[2] = findViewById(R.id.stopwatch_second_overtime_wrapper);

        scoreWrappers = new RelativeLayout[3];
        scoreWrappers[0] = findViewById(R.id.score_reg_time_wrapper);
        scoreWrappers[1] = findViewById(R.id.score_first_overtime_wrapper);
        scoreWrappers[2] = findViewById(R.id.score_second_overtime_wrapper);

        fabBlue = (FloatingActionButton) findViewById(R.id.bluePenaltyFAB);
        fabYellow = (FloatingActionButton) findViewById(R.id.yellowPenaltyFAB);
        fabRed = (FloatingActionButton) findViewById(R.id.redPenaltyFAB);
        //---------------------------------------
        //----- HIDE KEYBOARD ON OUTSIDE CLICK---
        //---------------------------------------
        //
        //---------------------------------------
        //----- FIRST OVERTIME REMINDERS --------
        //---------------------------------------
        firstOvertimeSoundPlayed = new ArrayList<>();
        for(int i=0;i<game.firstOvertimeReminderTimes.size();i++){firstOvertimeSoundPlayed.add(false);}
        //---------------------------------------
        //----- SEEKER RELEASE SOUNDS -----------
        //---------------------------------------
        seekerFloorCountdownSeconds = 5; // if 5, then countdown sound at 5, 4, 3, 2, 1, 0
        seekerReleaseSoundPlayed = new boolean[seekerFloorCountdownSeconds+1];
        //---------------------------------------
        //----- SOUND DEFINITIONS ---------------
        //---------------------------------------
        alert1 = MediaPlayer.create(context, R.raw.alert1);
        alert1.setVolume((float) 1.0, (float) 1.0);
        player_bip = MediaPlayer.create(context, R.raw.bip);
        player_bip.setVolume((float) 1.0, (float) 1.0);
        player_beeep = MediaPlayer.create(context, R.raw.beeep);
        player_beeep.setVolume((float) 1.0, (float) 1.0);
        //---------------------------------------
        //----- CREATE PENALTY LIST -------------
        //---------------------------------------
        penaltyList = (RecyclerView) findViewById(R.id.list_penalties);
        Penalty penalty = new Penalty("bla", "yellow", game.teams[0], 0, game.activeTime);
        game.penalties.add(penalty);
        penalty = new Penalty("bla", "blue", game.teams[1], 30, game.activeTime);
        game.penalties.add(penalty);
        penalty = new Penalty("bla", "red", game.teams[0], 60, game.activeTime);
        game.penalties.add(penalty);
        setUpPenaltyList();
        //penalty_slider = findViewById(R.id.penalty);

        //penaltyListAdapter = new PenaltyListAdapter(this, this.penalties);
        //list = (ListView) findViewById(R.id.list);
        //System.out.println(list);
        //list.setAdapter(penaltyListAdapter);
        /*this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Penalty penalty = this.penalties.get(position);
                setPenaltyChangeActivityStatus(position, penalty);
                this.penalty_selected_team = this.currently_active_penalty.getPlayerTeam();
                this.penalty_player_number = this.currently_active_penalty.getPlayerNumber();
                this.penalty_player_name = this.currently_active_penalty.getPlayerName();
                this.penalty_time_delta = 0;
                this.penalty_color = this.currently_active_penalty.getColor();
                // MODIFY INFORMATION IN CHANGE VIEW
                //View slider = penalty.getChangeViewLayout();
                //View slider = findViewById(R.id.penalty);
                // set colors for shirts
                ImageView imgViewTeam0 = (ImageView) penalty_slider.findViewById(R.id.penalty_jersey_team0);
                imgViewTeam0.setImageResource(teamToCurrentColorRes(0, "jersey_drawable_id"));
                ImageView imgViewTeam1 = (ImageView) penalty_slider.findViewById(R.id.penalty_jersey_team1);
                imgViewTeam1.setImageResource(teamToCurrentColorRes(1, "jersey_drawable_id"));
                // select shirt (if applicable)
                penalty_slider.findViewById(R.id.penalty_jersey_team0_tick).setVisibility(View.GONE);
                penalty_slider.findViewById(R.id.penalty_jersey_team1_tick).setVisibility(View.GONE);
                if(penalty_selected_team != -1)
                {
                    if(penalty_selected_team == 0) penalty_slider.findViewById(R.id.penalty_jersey_team0_tick).setVisibility(View.VISIBLE);
                    else if(penalty_selected_team == 1) penalty_slider.findViewById(R.id.penalty_jersey_team1_tick).setVisibility(View.VISIBLE);
                }
                // select card
                penalty_slider.findViewById(R.id.penalty_bluecardtick).setVisibility(View.GONE);
                penalty_slider.findViewById(R.id.penalty_yellowcardtick).setVisibility(View.GONE);
                penalty_slider.findViewById(R.id.penalty_redcardtick).setVisibility(View.GONE);
                penalty_slider.findViewById(R.id.penalty_yellowredcardtick).setVisibility(View.GONE);
                if(penalty_color.equals("blue"))
                {
                    penalty_slider.findViewById(R.id.penalty_bluecardtick).setVisibility(View.VISIBLE);
                }
                else if(penalty_color.equals("yellow"))
                {
                    penalty_slider.findViewById(R.id.penalty_yellowcardtick).setVisibility(View.VISIBLE);
                }
                else if(penalty_color.equals("red"))
                {
                    penalty_slider.findViewById(R.id.penalty_redcardtick).setVisibility(View.VISIBLE);
                }
                else if(penalty_color.equals("yellowred"))
                {
                    penalty_slider.findViewById(R.id.penalty_yellowredcardtick).setVisibility(View.VISIBLE);
                }
                // set player number
                EditText player_number_et = (EditText) penalty_slider.findViewById(R.id.penalty_player_number_content_et);
                player_number_et.setText(penalty_player_number);
                // set player number
                EditText player_name_et = (EditText) penalty_slider.findViewById(R.id.penalty_player_name_content_et);
                player_name_et.setText(penalty_player_name);
                // update penaltfy countdown
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateChangePenaltyCountdown();
                    }
                });
                // make appear
                performSlide(penalty_slider, "left");
            }
        });
        // --------------------------------------------------------------
        // ------ SLIDE OUT FIRST OT AND SECOND OT STOPWATCHES ----------
        // --------------------------------------------------------------
        View sw_ot1_wrapper = findViewById(R.id.stopwatch_first_overtime_wrapper);
        stopwatchSlide(sw_ot1_wrapper, "rightout", 0);
        View sw_ot2_wrapper = findViewById(R.id.stopwatch_second_overtime_wrapper);
        stopwatchSlide(sw_ot2_wrapper, "rightout", 0);
        // --------------------------------------------------------------
        // ------ SLIDE OUT FIRST OT AND SECOND OT SCORES ---------------
        // --------------------------------------------------------------
        View score_ot1_wrapper = findViewById(R.id.score_first_overtime_wrapper);
        stopwatchSlide(score_ot1_wrapper, "rightout", 0);
        View score_ot2_wrapper = findViewById(R.id.score_second_overtime_wrapper);
        stopwatchSlide(score_ot2_wrapper, "rightout", 0);
        //---------------------------------------
        //----- PLAYER NUMBER KEYBOARD ----------
        //---------------------------------------
        numberKeyboard = findViewById(R.id.player_number_keyboard);
        // -------------------------------------------
        // ----- PLAYER NUMBER KEYBOARD --------------
        // -------------------------------------------
        EditText playerNumberEditText= (EditText) findViewById(R.id.penalty_player_number_content_et);
        playerNumberEditText.setInputType(InputType.TYPE_NULL); // prevent keyboard (at least, that's the plan)
        playerNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(hasFocus)
                {
                    hideSoftKeyboardIfVisible();
                    focusedPlayerNumberEditText = (EditText) v;
                    numberKeyboard.setVisibility(View.VISIBLE);
                    numberKeyboard.bringToFront();
                    //Toast.makeText(getApplicationContext(), "got the focus", Toast.LENGTH_LONG).show();
                }
                else
                {
                    focusedPlayerNumberEditText = null;
                    numberKeyboard.setVisibility(View.GONE);
                    //Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });
        //---------------------------------------
        //----- DONATION POPUP ------------------
        //---------------------------------------
        if(forFree)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("This app is for free.\nIts development is not.");
            builder.setMessage("Please consider donating to the developer if you like the app.");

            builder.setPositiveButton("Donate", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://schwatogo.de/timekeeper/donate.php"));
                    startActivity(browserIntent);

                } });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                } });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }*/
        //---------------------------------------
        //----- TIMEOUTS ------------------------
        //---------------------------------------
        if(game.timeOutsTaken[0]) findViewById(R.id.timeOutParentTeam0).setVisibility(View.GONE);
        if(game.timeOutsTaken[1]) findViewById(R.id.timeOutParentTeam1).setVisibility(View.GONE);
        //---------------------------------------
        //----- SETUP UI ------------------------
        //---------------------------------------
        scoreViews[0][0].post(new Runnable() {@Override public void run() {setupUI();}});// can only be called after layout has been laid out
        //---------------------------------------
        //----- INTERVAL START ------------------
        //---------------------------------------
        startInterval();
    }
    @Override
    protected void onPause()
    {
        super.onPause();
    }
    //---------------------------------------
    //----- TOOLBAR -------------------------
    //---------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu_local) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu_local);
        menu = menu_local;
        System.out.println("onCreateOptionsMenu CALLED");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        System.out.println("onOptionsItemSelected CALLED");
        int id = item.getItemId();
        System.out.println(id);
        System.out.println(R.id.action_first_overtime);
        if(id == R.id.action_first_overtime)
        {
            System.out.println("ENTERING IF CLAUSE");
            showFirstOvertimeInstructions();
        }
        else if(id == R.id.action_second_overtime)
        {
            System.out.println("ENTERING IF CLAUSE");
            showSecondOvertimeInstructions();
        }
        else if(id == R.id.action_donate)
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://lucasscheuvens.de/timekeeper/donate.php"));
            startActivity(browserIntent);
        }
        else if(id == R.id.action_resort_penalties)
        {
            resortPenalties();
            penaltyDataSetHasChanged();
            View w = findViewById(R.id.notify_penaltiesresorted);
            fadeInOut(w);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed()
    {
        if(game.clocks[game.activeTime].isRunning())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Exiting the game stops all watches. Proceed?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    pauseWatches();
                    dialog.dismiss();
                    onBackPressed();
                } });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                } });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else
        {
            db.updateGame(game.getDBId(), game);
            Intent intent = new Intent();
            intent.putExtra("game", game);
            setResult(RESULT_OK, intent);
            finish();
        }
        /*if(currently_active_penalty != null)
        {
            //apply penalty number and name changes (all other changes have been made already)
            EditText player_number = (EditText) penalty_slider.findViewById(R.id.penalty_player_number_content_et);
            EditText player_name = (EditText) penalty_slider.findViewById(R.id.penalty_player_name_content_et);
            currently_active_penalty.setPlayerNumber(player_number.getText().toString());
            currently_active_penalty.setPlayerName(player_name.getText().toString());
            // reset currently_active_penalty
            resetPenaltyChangeActivityStatus();
            // update penalty set (list)
            penaltyDataSetHasChanged();
            slideDownPenaltyChangeView(null);
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Would you really like to exit and reset the app?");
            builder.setMessage("All data entered will be lost.");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    super.onBackPressed();
                    dialog.dismiss();

                } });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                } });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }*/
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK)
            {
                Team team = (Team) intent.getSerializableExtra("team");
                game.teams[team.getPosition()] = team;
                setTeamNameUI(game.teams[team.getPosition()]);
                setJerseyColorUI(game.teams[team.getPosition()]);
            }
        }
    }
    //---------------------------------------
    //----- TOOLBAR END ---------------------
    //---------------------------------------

    // This function gives back not only the min value but all values that are within 100ms of the min value
    public List<Integer> getMinIndizes(List<Long> arrayList)
    {
        if(arrayList.size() > 0)
        {
            long delta_time = 100; // in ms
            long minTime = Collections.min(arrayList);
            if(minTime!=maxlong)//if minTime == maxint there are no reducible penalties
            {
                List<Integer> minInd = new ArrayList<>();
                for(int i=0;i<arrayList.size();i++)
                {
                    if(arrayList.get(i)<=minTime+delta_time) minInd.add(i);
                }
                return minInd;
            }
        }
        return new ArrayList<>();
    }

    //---------------------------------------
    //----- HIDE KEYBOARD ON CLICK OUSTIDE --
    //---------------------------------------

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus() != null)
        {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
    public void setupUI()
    {
        teamname_tvs[0].setText(game.teams[0].getNotNullName());
        teamname_tvs[1].setText(game.teams[1].getNotNullName());
        teamjersey_ivs[0].setImageResource(game.teams[0].getJerseyColorRes());
        teamjersey_ivs[1].setImageResource(game.teams[1].getJerseyColorRes());
        updateClockUI();
        updateTermUI();
        updateScoresUI();
        /*// Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }*/
    }

    //---------------------------------------
    //----- STOPWATCH -----------------------
    //---------------------------------------

    public void toggleWatches()
    {
        if(game.clocks[game.activeTime].isRunning())
        {
            arrowsChangeStopwatchSetVisible();
            game.clocks[game.activeTime].pause();
            pauseAllPenaltyTimers();
            outputAllTimerValues();
            showFABs();
            //ll.setVisibility(View.VISIBLE);
        }
        else
        {
            arrowsChangeStopwatchSetGone();
            game.clocks[game.activeTime].start();
            startAllPenaltyTimers();
            outputAllTimerValues();
            hideFABs();
            //ll.setVisibility(View.GONE);
        }
    }

    /*public void toggleWatchesRegTime()
    {
        //LinearLayout ll = (LinearLayout) findViewById(R.id.addPenaltiesWrapper);
        if(sw.isRunning())
        {
            arrowsChangeStopwatchSetVisible();
            sw.pause();
            pauseAllPenaltyTimers();
            outputAllTimerValues();
            showFABs();
            //ll.setVisibility(View.VISIBLE);
        }
        else
        {
            arrowsChangeStopwatchSetGone();
            sw.start();
            startAllPenaltyTimers();
            outputAllTimerValues();
            hideFABs();
            //ll.setVisibility(View.GONE);
        }
    }
    public void toggleWatchesFirstOvertime()
    {
        //LinearLayout ll = (LinearLayout) findViewById(R.id.addPenaltiesWrapper);
        //System.out.println(timer_first_overtime.getTimeRemainingMinSecString());
        //System.out.println(timer_first_overtime.isRunning());
        if(timer_first_overtime.isRunning())
        {
            arrowsChangeStopwatchSetVisible();
            timer_first_overtime.pause();
            pauseAllPenaltyTimers();
            outputAllTimerValues();
            showFABs();
            //ll.setVisibility(View.VISIBLE);
        }
        else
        {
            arrowsChangeStopwatchSetGone();
            timer_first_overtime.start();
            startAllPenaltyTimers();
            outputAllTimerValues();
            hideFABs();
            //ll.setVisibility(View.GONE);
        }
    }
    public void toggleWatchesSecondOvertime()
    {
        //LinearLayout ll = (LinearLayout) findViewById(R.id.addPenaltiesWrapper);
        if(sw_second_overtime.isRunning())
        {
            arrowsChangeStopwatchSetVisible();
            sw_second_overtime.pause();
            pauseAllPenaltyTimers();
            outputAllTimerValues();
            showFABs();
            //ll.setVisibility(View.VISIBLE);
        }
        else
        {
            arrowsChangeStopwatchSetGone();
            sw_second_overtime.start();
            startAllPenaltyTimers();
            outputAllTimerValues();
            hideFABs();
            //ll.setVisibility(View.GONE);
        }
    }*/
    public void arrowsChangeStopwatchSetVisible()
    {
        View w = findViewById(R.id.all_stopwatches_wrapper);
        w.findViewById(R.id.regtime_arrow_right).setVisibility(View.VISIBLE);
        w.findViewById(R.id.first_overtime_arrow_left).setVisibility(View.VISIBLE);
        w.findViewById(R.id.first_overtime_arrow_right).setVisibility(View.VISIBLE);
        w.findViewById(R.id.second_overtime_arrow_left).setVisibility(View.VISIBLE);
    }
    public void arrowsChangeStopwatchSetGone()
    {
        View w = findViewById(R.id.all_stopwatches_wrapper);
        w.findViewById(R.id.regtime_arrow_right).setVisibility(View.GONE);
        w.findViewById(R.id.first_overtime_arrow_left).setVisibility(View.GONE);
        w.findViewById(R.id.first_overtime_arrow_right).setVisibility(View.GONE);
        w.findViewById(R.id.second_overtime_arrow_left).setVisibility(View.GONE);
    }
    public void resetWatches(View v) {game.clocks[game.activeTime].reset();}
    public long getCurrentTimestamp(){
        return System.currentTimeMillis()/1000;
    }
    public void startInterval()
    {
        new Timer().scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                playSeekerReleaseCountdownSoundIfTime(game.clocks[game.activeTime].getElapsedTime());
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //long startTime = System.nanoTime();
                        penaltyDataSetHasChanged();
                        //long midTime = System.nanoTime();
                        //sysout(midTime-startTime);
                        if(game.activeTime == 1) if(game.clocks[game.activeTime].getTimeRemaining() == 0) pauseWatches();
                        updateClockUI();
                        updateChangePenaltyCountdown(); // updates alle watches in change penalty views (where you can change a penalty)
                        showSnitchesIfTimeReady();
                        showGetSnitchReadyAlert();
                        showFirstOvertimeShoutAlerts();
                        //long endTime = System.nanoTime();
                        //sysout(endTime-startTime);
                        //sysout("OVER");
                    }
                });
            }
        },0,100);
    }

    /*public void updatePenaltyListTimerValues()
    {
        final int firstListItemPosition = list.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + list.getChildCount() - 1;
        //sysout("HIHIHI");
        //sysout(firstListItemPosition);
        //sysout(lastListItemPosition);
        int counter = 0;
        for(int i=firstListItemPosition;i<=lastListItemPosition;i++)
        {
            System.out.println("YEPPP");
            try
            {
                TextView tv = (TextView) list.getChildAt(counter).findViewById(R.id.penalty_countdown);
                penaltyListAdapter.updateTimer(tv, penalties.get(i));
            }
            catch(NullPointerException|IndexOutOfBoundsException e)
            {
                // Do nothing
                //list.getChildAt(firstListItemPosition).setBackgroundColor(0xFF00FF00);
                //System.out.println(penalties.get(i).getTimeRemainingString());
                //System.out.println("NullpointerException triggered at position "+Integer.toString(i));
                sysout("Exception thrown.");
            }
            counter++;
        }
    }*/




    public void pauseWatches() // pauses all watches; does nothing if watches already paused
    {
        if(game.clocks[game.activeTime].isRunning()) animatePlayPause(null);
    }
    public void startWatches() // starts all watches; does nothing if watches already started
    {
        if(!game.clocks[game.activeTime].isRunning()) animatePlayPause(null);
    }
    public void updateChangePenaltyCountdown()
    {
        if(currently_active_penalty != null) // change penalty view ist aktiv
        {
            TextView tv = (TextView) penaltyEdit.findViewById(R.id.penalty_time_remaining);
            tv.setText(currently_active_penalty.getTimeRemainingString("00:00"));
        }
    }

    //---------------------------------------
    //----- PLAY/PAUSE ANIMATION ------------
    //---------------------------------------

    public void animatePlayPause(View w)
    {
        if(game.clocks[game.activeTime].getMode().equals("timer") && !game.clocks[game.activeTime].isRunning() && game.clocks[game.activeTime].getTimeRemaining()==0)
        {
            //nicht starten
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("You can't start the timer because it is already 00:00.");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                } });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else
        {
            toggleWatches();
            AnimatedVectorDrawable drawable = play ? playToPause : pauseToPlay;
            playPause[game.activeTime].setImageDrawable(drawable);
            drawable.start();
            play = !play;
        }
    }

    //---------------------------------------
    //----- SCORE SCROLL --------------------
    //---------------------------------------

    public void team0ScoreUp(View w) {teamScore(0, "up", true);}
    public void team0ScoreUp(boolean reduce_potential) {teamScore(0, "up", reduce_potential);}
    public void team0ScoreDown(View w) {teamScore(0, "down", true);}
    public void team0ScoreDown(boolean reduce_potential) {teamScore(0, "down", reduce_potential);}
    public void team1ScoreUp(View w) {teamScore(1, "up", true);}
    public void team1ScoreUp(boolean reduce_potential) {teamScore(1, "up", reduce_potential);}
    public void team1ScoreDown(View w) {teamScore(1, "down", true);}
    public void team1ScoreDown(boolean reduce_potential)
    {
        teamScore(1, "down", reduce_potential);
    }
    public long teamScore(final int team_pos, final String dir, boolean reduce_potential)
    {
        final long old_score = game.scores[team_pos][game.activeTime];
        long working_new_score = dir.equals("up") ? old_score+10 : old_score-10;
        if(working_new_score<0) working_new_score=0;
        else if(working_new_score>990) working_new_score=990;
        final long new_score = working_new_score;

        game.scores[team_pos][game.activeTime] = new_score;

        // -----------------------------------
        // ---- PLAYER RELEASE LOGIC ---------
        // -----------------------------------
        boolean snitchWasCaught = game.catches[0][game.activeTime] || game.catches[0][game.activeTime];

        /*if(reduce_potential && !(snitchWasCaught && (game.scores[0][game.activeTime] != game.scores[1][game.activeTime])) && !(game.activeTime == 2)) // dialog should not show when game has ended because of snitch catch
        {
            if (old_score < new_score) // the team has scored
            {
                // Find the penalties of the opposing team that have the least amount of time left
                List<Long> times = new ArrayList<>();
                for (int i = 0; i < penalties.size(); i++)
                {
                    Penalty penalty = penalties.get(i);
                    if (penalty.getReducible() && (penalty.getPlayerTeamPosition() == (1 - team_pos)) && penalty.getTimeRemaining() > 0)
                    {
                        times.add(penalty.getTimeRemaining());
                        //System.out.println("If team 2 pressed, should be 0: "+penalty.getPlayerTeam());
                    }
                    else
                    {
                        times.add(maxlong);//maxint is the highest available int
                    }
                }
                //System.out.println(times);
                final List<Integer> minInd = getMinIndizes(times); // minInd stores the indizes of penalties of the opposing team that have the least amount of time left. Often, this is only one penalty
                //System.out.println(minInd);
                //System.out.println(penalties);
                //System.out.println(times);
                //System.out.println(minInd.size());

                if (minInd.size() > 0) {
                    //Define the content of each field of the dialog that appears and asks the user which penalty should be reduced
                    final int[] cards = new int[minInd.size()];
                    final int[] jerseys = new int[minInd.size()];
                    final String[] numbers = new String[minInd.size()];
                    final String[] names = new String[minInd.size()];
                    for (int i = 0; i < minInd.size(); i++) {
                        cards[i] = penalties.get(minInd.get(i)).getDrawableId();
                        jerseys[i] = penalties.get(minInd.get(i)).getJerseyColorDrawableId();
                        numbers[i] = penalties.get(minInd.get(i)).getPlayerNumber();
                        names[i] = penalties.get(minInd.get(i)).getPlayerName();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Select player to release");

                    ListAdapter adapter = new ArrayAdapter<String>(
                            getApplicationContext(), R.layout.list_item_dialog, names) {

                        ViewHolder holder;

                        class ViewHolder {
                            ImageView card;
                            TextView playerName;
                            TextView playerNumber;
                        }

                        public View getView(int position, View convertView,
                                            ViewGroup parent) {
                            final LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                                    .getSystemService(
                                            Context.LAYOUT_INFLATER_SERVICE);

                            if (convertView == null) {
                                convertView = inflater.inflate(
                                        R.layout.list_item_dialog, null);

                                holder = new ViewHolder();
                                holder.card = (ImageView) convertView
                                        .findViewById(R.id.icon);
                                holder.playerName = (TextView) convertView
                                        .findViewById(R.id.playerName);
                                holder.playerNumber = (TextView) convertView
                                        .findViewById(R.id.playerNumber);
                                convertView.setTag(holder);
                            } else {
                                // view already defined, retrieve view holder
                                holder = (ViewHolder) convertView.getTag();
                            }

                            holder.playerNumber.setText(numbers[position]);
                            holder.playerNumber.setTextSize(Admin.returnTShirtTextSize(numbers[position]));
                            //System.out.println(penalties.get(minInd.get(0)).getPlayerTeam());
                            //holder.playerNumber.setTextColor(ContextCompat.getColor(getApplicationContext(), getTextColorForegroundOfTeamJersey(0)));
                            holder.playerNumber.setBackground(ContextCompat.getDrawable(context, jerseys[position]));
                            holder.playerName.setText(names[position]);
                            holder.card.setImageDrawable(ContextCompat.getDrawable(context, cards[position]));

                            return convertView;
                        }
                    };

                    builder.setAdapter(adapter,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    //Toast.makeText(getApplicationContext(), "You selected: " + names[which], Toast.LENGTH_LONG).show();
                                    penalties.get(minInd.get(which)).setReduced();
                                    score_events.add(new ScoreEvent(team, old_score, new_score, score_time, penalties.get(minInd.get(which)).getId()));
                                    //Log.d("SCORE EVENT", "Attached penalty ID: "+penalties.get(minInd.get(which)).getId());
                                    resortPenalties();
                                    penaltyDataSetHasChanged();
                                    dialog.dismiss();
                                }
                            });
                    builder.setNegativeButton("Don't release anyone", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            score_events.add(new ScoreEvent(team, old_score, new_score, sw.getElapsedTime(), ""));
                            //Log.d("SCORE EVENT", "Attached penalty ID: none");
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    //ListView listView = alertDialog.getListView();
                    //listView.setDivider(new ColorDrawable(Color.GRAY)); // set color
                    //listView.setDividerHeight(1); // set height
                    alertDialog.show();


                } else {
                    score_events.add(new ScoreEvent(team, old_score, new_score, sw.getElapsedTime(), ""));
                }
                //if at least one player of the opposing team is in the penalty box for a reducible penalty, the player with the least amount of time left on the clock may be released.
            } else if (old_score > new_score) {
                // iterate through score_events to find the "up" score event that is linked to this "down" score
                boolean penaltyReduced = false;
                for (int i = score_events.size() - 1; i >= 0; i--)//iterate backwards to find the latest item
                {
                    if (score_events.get(i).getIfIncrease() && score_events.get(i).getTeam() == team) {
                        if (score_events.get(i).getOldScore() == new_score) {
                            String penalty_id = score_events.get(i).getPenaltyId();
                            for (Penalty penalty : penalties) {
                                if (penalty.getId().equals(penalty_id)) {
                                    penalty.setUnreduced();
                                    penaltyReduced = true;
                                    score_events.add(new ScoreEvent(team, old_score, new_score, score_time, penalty.getId()));
                                    Log.d("SCORE EVENT", "Attached penalty ID (unrelease): "+penalty.getId());
                                    break;
                                }
                            }
                            resortPenalties();
                            break;
                            //if a penalty was set inactive due to this event, set it to active again
                        }
                    }
                }
                if(!penaltyReduced)
                {
                    score_events.add(new ScoreEvent(team, old_score, new_score, score_time, ""));
                    Log.d("SCORE EVENT", "Attached penalty ID: none");
                }
            }
        }
        else
        {
            score_events.add(new ScoreEvent(team, old_score, new_score, score_time, ""));
            Log.d("SCORE EVENT", "Attached penalty ID: none");
        }*/

        /////////////////////////////////////////////////////////////////////////
        //// IN SECOND OVERTIME SHOW "TEAM BLA HAS WON" AFTER SNITCH CATCH //////
        /////////////////////////////////////////////////////////////////////////
        /*if(mode.equals("second_overtime") && dir.equals("up") && reduce_potential)
        {
            pauseWatches();

            boolean[] arrowHide;
            if(team == 0) arrowHide = new boolean[]{true, false, true, true};
            else if(team == 1) arrowHide = new boolean[]{true, true, true, false};
            else arrowHide = new boolean[]{false, false, false, false};
            hideCertainScoreArrowsSecondOvertime(arrowHide);
            hideSnitchesSecondOvertime();

            String teamname = getTeamName(team);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Team '"+teamname+"' has won!");

            // Add cancel button
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                } });

            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();
        }
        else if(mode.equals("second_overtime") && dir.equals("down") && reduce_potential)
        {
            showScoreArrowsSecondOvertime();
            showSnitchesSecondOvertime();
        }*/

        //System.out.println("SCORED");
        //System.out.println("childHeight="+childHeight);
        //System.out.println("currentSelected="+Integer.toString(currentSelected));
        //System.out.println("nextSelected="+Integer.toString(nextSelected));

        scrollToScore(team_pos, game.activeTime, new_score);
        return new_score;
    }
    public int getChildHeight(CustomScrollView scrollView)
    {
        LinearLayout ll = (LinearLayout) scrollView.getChildAt(0);
        return ll.getHeight()/ll.getChildCount();
    }

    //-----------------------------------------------------
    //------- SLIDE PERFORMANCE  --------------------------
    //-----------------------------------------------------

    public void showPenaltyEdit()
    {
        penaltyEdit.setVisibility(View.VISIBLE);
        hideFABsContainer();
    }
    public void hidePenaltyEdit()
    {
        penaltyEdit.setVisibility(View.GONE);
        showFABsContainer();
    }
    /*public void performSlide(View view, String dir)
    {
        Runnable r = new Runnable() {public void run() {}};
        performSlide(view, dir, 200, r);
    }
    public void performSlide(final View view, String dir, int speed)
    {
        Runnable r = new Runnable() {public void run() {}};
        performSlide(view, dir, speed, r);
    }*/
    /*public boolean initializeSliders()
    {
        RelativeLayout parent = (RelativeLayout) findViewById(R.id.restInfo);
        for(int index = 0; index < parent.getChildCount(); ++index)
        {
            View nextChild = parent.getChildAt(index);
            try
            {
                if (nextChild.getTag().equals("bottomslider"))
                {
                    //System.out.println("WAS HERE");
                    performSlide(nextChild, "down", 0);
                }
            } catch(Exception e)
            {
                // do nothing
            }
        }
        return true;
    }*/

    //-----------------------------------------------------
    //------- STOPWATCH SLIDE PERFORMANCE -----------------
    //-----------------------------------------------------

    public void stopwatchSlide(View view, String dir)
    {
        Runnable r = new Runnable() {public void run() {}};
        stopwatchSlide(view, dir, 200, r);
    }
    public void stopwatchSlide(final View view, String dir, int speed)
    {
        Runnable r = new Runnable() {public void run() {}};
        stopwatchSlide(view, dir, speed, r);
    }
    public void stopwatchSlide(final View view, String dir, int speed, final Runnable r)
    {
        final Animation a;
        switch (dir)
        {
            case "leftin":
                a = AnimationUtils.loadAnimation(context, R.anim.sw_slide_leftin);
                break;
            case "leftout":
                a = AnimationUtils.loadAnimation(context, R.anim.sw_slide_leftout);
                break;
            case "rightin":
                a = AnimationUtils.loadAnimation(context, R.anim.sw_slide_rightin);
                break;
            case "rightout":
                a = AnimationUtils.loadAnimation(context, R.anim.sw_slide_rightout);
                break;
            default:
                a = AnimationUtils.loadAnimation(context, R.anim.sw_slide_leftin);
                break;
        }
        a.setDuration(speed);
        if(dir.equals("leftin") || dir.equals("rightin")) view.setVisibility(View.VISIBLE);
        if(dir.equals("leftout") || dir.equals("rightout"))
        {
            a.setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation arg0){}
                @Override
                public void onAnimationRepeat(Animation arg0){}
                @Override
                public void onAnimationEnd(Animation arg0)
                {
                    view.setVisibility(View.GONE);
                    a.setAnimationListener(null); // onAnimationEnd feuerte sonst zweimal
                    view.clearAnimation(); // Animation wird sonst nicht gezeigt
                    r.run();
                }
            });
        }
        view.startAnimation(a);
    }
    public void regTimeToFirstOvertime(View w) {switchTerm(0, 1, true);}
    public void firstOvertimeToRegTime(View w) {switchTerm(1, 0, true);}
    public void firstOvertimeToSecondOvertime(View w) {switchTerm(1, 2, true);}
    public void secondOvertimeToFirstOvertime(View w) {switchTerm(2, 1, true);}
    public void switchTerm(int from, int to, boolean animate)
    {
        game.activeTime = to;
        String dir_in;
        String dir_out;
        if(to>from){dir_in = "leftin";dir_out = "leftout";}else{dir_in = "rightin";dir_out = "rightout";}
        if(animate)
        {
            stopwatchSlide(clockWrappers[from], dir_out);
            stopwatchSlide(scoreWrappers[from], dir_out);
            stopwatchSlide(clockWrappers[to], dir_in);
            stopwatchSlide(scoreWrappers[to], dir_in);
            final View view = findViewById(R.id.notify_firstovertime);
            if(animate)
            {
                Runnable runnable;
                if(from==0 && to==1) runnable = new Runnable() {@Override public void run() {showFirstOvertimeInstructions();}};
                else if(from==1 && to==2) runnable = new Runnable() {@Override public void run() {showSecondOvertimeInstructions();}};
                else runnable = new Runnable() {@Override public void run() {}};
                fadeInOut(view, runnable);
            }
        }
        else
        {
            stopwatchSlide(clockWrappers[from], dir_out, 0);
            stopwatchSlide(scoreWrappers[from], dir_out, 0);
            stopwatchSlide(clockWrappers[to], dir_in, 0);
            stopwatchSlide(scoreWrappers[to], dir_in, 0);
        }
        updateToolbarText();
    }


    public void fadeInOut(final View w)
    {
        fadeInOut(w, 1000, new Runnable() {
            @Override
            public void run() {}
        });
    }
    public void fadeInOut(final View w, final int delay_ms)
    {
        fadeInOut(w, delay_ms, new Runnable() {
            @Override
            public void run() {}
        });
    }
    public void fadeInOut(final View w, final Runnable runnable)
    {
        fadeInOut(w, 1000, runnable);
    }
    public void fadeInOut(final View w, final int delay_ms, final Runnable runnable)
    {
        w.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                w.setVisibility(View.GONE);
                runnable.run();
            }
        }, delay_ms);
    }
    public void showFirstOvertimeInstructions()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("First overtime instructions");
        builder.setMessage("The timekeeper takes an important role in the first overtime period. Your job in detail is:\n\n\u2022 Shout out the remaining time at every minute mark.\n\u2022 Shout out remaining time at 00:30 mark.\n" +
                "\u2022 Shout out remaining time at 00:15 mark.\n" +
                "\u2022 Shout out the last ten seconds as a countdown.\n" +
                "\u2022 Be ready to stop game time immediately upon referee's stoppage of play.\n" +
                "\u2022 Be ready to stop game time immediately upon referee's signal of advantage.\n\nIf you are uncertain about the meaning of any of the above, please contact the head referee now.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            } });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void showSecondOvertimeInstructions()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Second overtime instructions");
        builder.setMessage("In second overtime the watch counts up again (as opposed to first overtime).\n\nGame time doesn't need to be announced.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            } });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //---------------------------------------
    //----- CHANGE TEAM INFORMATION ---------
    //---------------------------------------

    //----- SLIDE UP/DOWN TEAM0 MENU --------
    public void changeTeam0Info(View w) {changeTeamInfo(0);}
    public void changeTeam1Info(View w) {changeTeamInfo(1);}
    public void changeTeamInfo(int team_pos)
    {
        Intent intent = new Intent(context, ChangeTeamInfoActivity.class);
        intent.putExtra("team", game.teams[team_pos]);
        startActivityForResult(intent, 1);
    }
    public void setTeamNameUI(Team team) {teamname_tvs[team.getPosition()].setText(team.getNotNullName());}
    public void setJerseyColorUI(Team team)
    {
        if(!team.getJerseyColor().equals("transparent"))
        {
            int rsc = Admin.teamToCurrentColorRes(team, "jersey_drawable_id");
            teamjersey_ivs[team.getPosition()].setImageResource(rsc);
        }
    }
    /*public boolean resetJerseyColor(int team) // hides all jerseys of team "team"
    {
        int jerseyParentId = getResources().getIdentifier("jerseyteam" + Integer.toString(team) + "parent", "id", getPackageName());
        FrameLayout jerseyParent = (FrameLayout) findViewById(jerseyParentId);
        for(int index = 0; index < jerseyParent.getChildCount(); ++index)
        {
            View nextChild = jerseyParent.getChildAt(index);
            try
            {
                nextChild.setVisibility(View.INVISIBLE);
            } catch(Exception e)
            {
                // do nothing
            }
        }
        return true;
    }*/
    /*public boolean resetAllPickedColorsRecursively(ViewGroup parent) // help function of resetAllPickedColors()
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
    }*/
    /*public boolean resetAllPickedColors(int team) // hides all ticks in color selection of team 0
    {
        if(team == 0)
        {
            ViewGroup parent = (ViewGroup) findViewById(R.id.selectJerseyColorTeam0);
            resetAllPickedColorsRecursively(parent);
        }
        else if(team == 1)
        {
            ViewGroup parent = (ViewGroup) findViewById(R.id.selectJerseyColorTeam1);
            resetAllPickedColorsRecursively(parent);
        }
        return true;
    }*/
    /*public void swapNamesTeam0(View w)
    {
        swapNames(0);
    }
    public void swapNamesTeam1(View w)
    {
        swapNames(1);
    }
    public void swapNames(int team)
    {
        TextView origin_header;
        EditText origin_content;
        TextView addNames_header;
        EditText addNames_content;
        if(team==0)
        {
            origin_header = (TextView) findViewById(R.id.originTeam0_header);
            origin_content = (EditText) findViewById(R.id.originTeam0);
            addNames_header = (TextView) findViewById(R.id.additionalNameTeam0_header);
            addNames_content = (EditText) findViewById(R.id.additionalNameTeam0);
        }
        else
        {
            origin_header = (TextView) findViewById(R.id.originTeam1_header);
            origin_content = (EditText) findViewById(R.id.originTeam1);
            addNames_header = (TextView) findViewById(R.id.additionalNameTeam1_header);
            addNames_content = (EditText) findViewById(R.id.additionalNameTeam1);
        }

        boolean nameSwap;
        if(team==0) nameSwap = nameSwapTeam0;
        else nameSwap = nameSwapTeam1;

        RelativeLayout.LayoutParams params_origin_header = (RelativeLayout.LayoutParams) origin_header.getLayoutParams();
        RelativeLayout.LayoutParams params_origin_content = (RelativeLayout.LayoutParams) origin_content.getLayoutParams();
        RelativeLayout.LayoutParams params_addNames_header = (RelativeLayout.LayoutParams) addNames_header.getLayoutParams();
        RelativeLayout.LayoutParams params_addNames_content = (RelativeLayout.LayoutParams) addNames_content.getLayoutParams();

        if(!nameSwap)
        {

            params_origin_header.removeRule(RelativeLayout.ALIGN_PARENT_START);
            params_origin_header.addRule(RelativeLayout.ALIGN_PARENT_END);
            params_origin_content.removeRule(RelativeLayout.ALIGN_PARENT_START);
            params_origin_content.addRule(RelativeLayout.ALIGN_PARENT_END);
            params_addNames_header.removeRule(RelativeLayout.ALIGN_PARENT_END);
            params_addNames_header.addRule(RelativeLayout.ALIGN_PARENT_START);
            params_addNames_content.removeRule(RelativeLayout.ALIGN_PARENT_END);
            params_addNames_content.addRule(RelativeLayout.ALIGN_PARENT_START);
            addNames_content.setGravity(Gravity.START);
            origin_content.setGravity(Gravity.END);
        }
        else
        {
            params_origin_header.removeRule(RelativeLayout.ALIGN_PARENT_END);
            params_origin_header.addRule(RelativeLayout.ALIGN_PARENT_START);
            params_origin_content.removeRule(RelativeLayout.ALIGN_PARENT_END);
            params_origin_content.addRule(RelativeLayout.ALIGN_PARENT_START);
            params_addNames_header.removeRule(RelativeLayout.ALIGN_PARENT_START);
            params_addNames_header.addRule(RelativeLayout.ALIGN_PARENT_END);
            params_addNames_content.removeRule(RelativeLayout.ALIGN_PARENT_START);
            params_addNames_content.addRule(RelativeLayout.ALIGN_PARENT_END);
            addNames_content.setGravity(Gravity.END);
            origin_content.setGravity(Gravity.START);
        }
        origin_header.setLayoutParams(params_origin_header);
        origin_content.setLayoutParams(params_origin_content);
        addNames_header.setLayoutParams(params_addNames_header);
        addNames_content.setLayoutParams(params_addNames_content);

        if(team==0) nameSwapTeam0 = !nameSwapTeam0;
        else nameSwapTeam1 = !nameSwapTeam1;
    }*/
    /*public void selectColorTeam0(View w) // saves the selected color of team 0 in a variable
    {
        String color = (String) w.getTag();
        selectColor(0, color);
    }
    public void selectColorTeam1(View w) // saves the selected color of team 1 in a variable
    {
        String color = (String) w.getTag();
        selectColor(1, color);
    }
    public void selectColor(int team, String color) // help function of selectColorTeam0 and selectColorTeam1
    {
        resetAllPickedColors(team);
        int tickId = getResources().getIdentifier("team" + Integer.toString(team) + "pick" + color + "tick", "id", getPackageName());
        View tick = findViewById(tickId);
        tick.setVisibility(View.VISIBLE);

        if(team == 0) jerseyColorTeam0 = color;
        else if(team == 1) jerseyColorTeam1 = color;
    }*/

    //---------------------------------------
    //----- SNITCH --------------------------
    //---------------------------------------

    //----- SHOW SNITCHES AFTER 18 MIN ------
    public void showSnitchesIfTimeReady()
    {
        if(game.activeTime == 0)
        {
            if(!snitchesShown)
            {
                if(game.clocks[game.activeTime].getElapsedTimeSecs() >= game.timeToShowSnitches)
                {
                    View parent0 = findViewById(R.id.snitchCatchParentTeam0_reg_time);
                    View parent1 = findViewById(R.id.snitchCatchParentTeam1_reg_time);
                    parent0.setVisibility(View.VISIBLE);
                    parent1.setVisibility(View.VISIBLE);

                    findViewById(R.id.timeOutParentTeam0).setVisibility(View.GONE);
                    findViewById(R.id.timeOutParentTeam1).setVisibility(View.GONE);

                    snitchesShown = true;
                }
            }
        }
    }
    public void showGetSnitchReadyAlert()
    {
        if(game.activeTime == 0)
        {
            if (!getSnitchReadyAlertShown) {
                if (game.clocks[game.activeTime].getElapsedTime() >= game.timeToGetSnitchReady * 1000) {
                    getSnitchReadyAlertShown = true;
                    if (game.clocks[game.activeTime].getElapsedTime() < (game.timeToReleaseSnitch + 1) * 1000) {
                        alert1.start();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Get the snitch ready!");
                        builder.setMessage("Snitch will be released at " + Admin.getMinSecStringFromSec(game.timeToReleaseSnitch) + "!");

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }
            if (!getSeekersReadyAlertShown) {
                if (game.clocks[game.activeTime].getElapsedTime() >= game.timeToGetSeekersReady * 1000) {
                    getSeekersReadyAlertShown = true;
                    if (game.clocks[game.activeTime].getElapsedTime() < (game.timeToReleaseSeekers + 1) * 1000) {
                        alert1.start();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Get the seekers ready!");
                        builder.setMessage("Seekers will be released at " + Admin.getMinSecStringFromSec(game.timeToReleaseSeekers) + "!");

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }
            if (!getReleaseSnitchNotificationShown) {
                if (game.clocks[game.activeTime].getElapsedTime() >= game.timeToReleaseSnitch * 1000) {
                    getReleaseSnitchNotificationShown = true;
                    if (game.clocks[game.activeTime].getElapsedTime() < (game.timeToReleaseSnitch + 1) * 1000) {
                        alert1.start();
                        final View view = findViewById(R.id.notify_release_snitch);
                        fadeInOut(view, 2500);
                    }
                }
            }
            if (!getReleaseSeekersNotificationShown) {
                if (game.clocks[game.activeTime].getElapsedTime() >= game.timeToReleaseSeekers * 1000) {
                    getReleaseSeekersNotificationShown = true;
                    if (game.clocks[game.activeTime].getElapsedTime() < (game.timeToReleaseSeekers + 1) * 1000) {
                        alert1.start();
                        final View view = findViewById(R.id.notify_release_seekers);
                        fadeInOut(view, 2500);
                    }
                }
            }
        }
    }
    public void showFirstOvertimeShoutAlerts()
    {
        if(game.activeTime == 1)
        {
            long remaining = game.clocks[game.activeTime].getTimeRemaining();
            for(int ii=0; ii<game.firstOvertimeReminderTimes.size();ii++)
            {
                boolean timeBoundaryPassed = remaining<=game.firstOvertimeReminderTimes.get(ii) && remaining>(game.firstOvertimeReminderTimes.get(ii)-1000); // second argument prevents execution on large gap (i.e., when time jumps)
                boolean checkedAlready = previousRemaining <= game.firstOvertimeReminderTimes.get(ii);
                if(timeBoundaryPassed && !checkedAlready)
                {
                    if(!firstOvertimeSoundPlayed.get(ii))
                    {
                        firstOvertimeSoundPlayed.set(ii, true);
                        alert1.start();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    if(game.firstOvertimeReminderInstructionTitles.get(ii) != null) builder.setTitle(game.firstOvertimeReminderInstructionTitles.get(ii));
                    if(game.firstOvertimeReminderInstructionMessages.get(ii) != null) builder.setMessage(game.firstOvertimeReminderInstructionMessages.get(ii));

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
            previousRemaining = game.clocks[game.activeTime].getTimeRemaining();
        }
    }
    public void setFirstOvertimeSoundPlayedArray()
    {
        if(game.activeTime == 1)
        {
            long remaining = game.clocks[game.activeTime].getTimeRemaining();
            for(int i=0;i<firstOvertimeSoundPlayed.size();i++)
            {
                if(remaining>game.firstOvertimeReminderTimes.get(i)) firstOvertimeSoundPlayed.set(i, false);
            }
            previousRemaining = remaining;
        }
    }
    public void setRegTimeSnitchReminder()
    {
        if(game.activeTime == 0)
        {
            if(game.clocks[game.activeTime].getElapsedTimeSecs() < game.timeToGetSeekersReady) getSeekersReadyAlertShown = false;
            if(game.clocks[game.activeTime].getElapsedTimeSecs() < game.timeToGetSnitchReady) getSnitchReadyAlertShown = false;
            if(game.clocks[game.activeTime].getElapsedTimeSecs() < game.timeToReleaseSeekers) getReleaseSeekersNotificationShown = false;
            if(game.clocks[game.activeTime].getElapsedTimeSecs() < game.timeToReleaseSnitch) getReleaseSnitchNotificationShown = false;
            if(game.clocks[game.activeTime].getElapsedTimeSecs() < game.timeToShowSnitches)
            {
                View parent0 = findViewById(R.id.snitchCatchParentTeam0_reg_time);
                View parent1 = findViewById(R.id.snitchCatchParentTeam1_reg_time);
                parent0.setVisibility(View.GONE);
                parent1.setVisibility(View.GONE);
                if(!game.timeOutsTaken[0]) findViewById(R.id.timeOutParentTeam0).setVisibility(View.VISIBLE);
                if(!game.timeOutsTaken[1]) findViewById(R.id.timeOutParentTeam0).setVisibility(View.VISIBLE);
                snitchesShown = false;
            }
            // Seeker release countdown sounds
            long currentTimeSeekerSound = game.clocks[game.activeTime].getElapsedTime();
            long timeToReleaseSeekers = game.timeToReleaseSeekers*1000;
            for(int i=0;i<=seekerFloorCountdownSeconds;i++)
            {
                if(currentTimeSeekerSound < timeToReleaseSeekers-i*1000) seekerReleaseSoundPlayed[i] = false;
            }
            previousTimeSeekerSound = currentTimeSeekerSound;
        }
    }
    public void playSeekerReleaseCountdownSoundIfTime(long currentTime)
    {
        if(game.activeTime == 0)
        {
            long timeToReleaseSeekersTimesThousand = game.timeToReleaseSeekers*1000;
            for(int i=0;i<seekerFloorCountdownSeconds+1;i++)
            {
                boolean timeBoundaryPassed = currentTime<timeToReleaseSeekersTimesThousand-(i-1)*1000 && currentTime>=timeToReleaseSeekersTimesThousand-i*1000; // second argument prevents execution on large gap (i.e., when time jumps)
                boolean checkedAlready = previousTimeSeekerSound >= timeToReleaseSeekersTimesThousand-i*1000;
                if(timeBoundaryPassed && !checkedAlready)
                {
                    if(!seekerReleaseSoundPlayed[i])
                    {
                        seekerReleaseSoundPlayed[i] = true;
                        showStopwatchRipple();
                        if(i==0) player_beeep.start();
                        else player_bip.start();
                    }
                }
            }
            previousTimeSeekerSound = currentTime;
        }
    }
    public void showStopwatchRipple()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                View stopwatchView = findViewById(R.id.stopwatch);
                if(stopwatchView!=null)
                {
                    RippleDrawable rd = (RippleDrawable) stopwatchView.getBackground();
                    rd.setState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled });
                    rd.setState(new int[] {  });
                }
            }
        });
    }

    //----- SELECT/DESELECT SNITCH CATCH ----
    public void snitchCatchTeam0(View w) {snitchCatch(0, game.activeTime);}
    public void snitchCatchTeam1(View w) {snitchCatch(1, game.activeTime);}
    public void snitchCatchNoGoodTeam0(View w) {snitchCatchNoGood(0, game.activeTime);}
    public void timeOutTeam0(View w) {timeOut(0);}
    public void timeOutTeam1(View w) {timeOut(1);}

    public int currentTimeOutTeam = -1;
    public TextView timeoutTimeLeftTextView;
    public Timer timeoutPeriodicUpdate;
    public FloatingActionButton timeoutPlaySymbol;

    public void timeOut(int team)
    {
        currentTimeOutTeam = team;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Timeout "+game.teams[team].getNotNullName());
        builder.setMessage("Pressing the play button uses the timeout of the team. This cannot be undone.");

        LayoutInflater inflater = getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.timeout, null);
        timeoutTimeLeftTextView = (TextView) inflatedView.findViewById(R.id.timeoutLeftTextView);
        timeoutPlaySymbol = (FloatingActionButton) inflatedView.findViewById(R.id.timeout_play_pause);
        builder.setView(inflatedView);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(timeoutPeriodicUpdate!=null)
                {
                    timeoutPeriodicUpdate.cancel();
                    timeoutPeriodicUpdate = null;
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    public void timeoutPlay(View w)
    {
        // PAUSE THE CURRENT STOPWATCH
        System.out.println(game.timeOuts[currentTimeOutTeam]);
        timeoutPlaySymbol.setEnabled(false);
        timeoutPlaySymbol.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lightlight_grey)));
        timeoutPlaySymbol.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_play_timeout_grey));
        game.timeOuts[currentTimeOutTeam].start();
        game.timeOutsTaken[currentTimeOutTeam] = true;

        if(currentTimeOutTeam == 0) findViewById(R.id.timeOutParentTeam0).setVisibility(View.GONE);
        else if(currentTimeOutTeam == 1) findViewById(R.id.timeOutParentTeam1).setVisibility(View.GONE);

        timeoutPeriodicUpdate = new Timer();
        timeoutPeriodicUpdate.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        String str = game.timeOuts[currentTimeOutTeam].getTimeRemainingMinSecString();
                        if(game.timeOuts[currentTimeOutTeam].getTimeRemaining() == 0)
                        {
                            timeoutPeriodicUpdate.cancel();
                            str = "resume";
                        };
                        timeoutTimeLeftTextView.setText(str);
                    }
                });
            }
        },0,100);
    }
    public void snitchCatchNoGoodTeam1(View w) {snitchCatchNoGood(1, game.activeTime);}
    public void snitchCatch(int team_pos, int term)
    {
        game.catches[team_pos][term] = true;

        pauseWatches();

        snitchUIAtCatch(team_pos, term);

        teamScore(team_pos, "up", false);
        teamScore(team_pos, "up", false);
        teamScore(team_pos, "up", true);

        hideScoreArrows();

        showDialogProceedToFirstOvertimeOnEqualScore();
        showDialogProceedToSecondOvertimeOnEqualScore();
    }
    public void snitchCatchNoGood(int team_pos, int term)
    {
        game.catches[team_pos][term] = false;

        snitchUIAtNoGood(team_pos, term);

        teamScore(team_pos, "down", true);
        teamScore(team_pos, "down", false);
        teamScore(team_pos, "down", false);

        showScoreArrows();
    }
    public void snitchUIAtCatch(int team_pos, int term)
    {
        snitchFABs[team_pos][term][0].setVisibility(View.INVISIBLE);
        snitchFABs[team_pos][term][1].setVisibility(View.VISIBLE);
        snitchFABs[1-team_pos][term][0].setVisibility(View.INVISIBLE);
        showStar(team_pos, term);
    }
    public void snitchUIAtNoGood(int team_pos, int term)
    {
        snitchFABs[team_pos][term][0].setVisibility(View.VISIBLE);
        snitchFABs[team_pos][term][1].setVisibility(View.INVISIBLE);
        snitchFABs[1-team_pos][term][0].setVisibility(View.VISIBLE);
        hideStar(team_pos, term);
    }

    //----- STAR SYMBOL ---------------------
    public boolean showStar(int team_pos, int term)// shows star symbol in score of team "team" in term "term"
    {
        showStarRecursively(scoreViews[team_pos][term]);
        return true;
    }
    public boolean showStar(int team_pos)// shows star symbol in score of team "team"
    {
        showStarRecursively(scoreViews[team_pos][game.activeTime]);
        return true;
    }
    public boolean hideStar(int team_pos, int term)// shows star symbol in score of team "team" in term "term"
    {
        hideStarRecursively(scoreViews[team_pos][term]);
        return true;
    }
    public boolean hideStar(int team_pos)// hides star symbol in score of team "team"
    {
        hideStarRecursively(scoreViews[team_pos][game.activeTime]);
        return true;
    }
    public boolean showStarRecursively(ViewGroup parent)// help function of showStar
    {
        if(parent != null)
        {
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                if (child instanceof ViewGroup) {
                    showStarRecursively((ViewGroup) child);
                } else if (child != null) {
                    try {
                        if (child.getTag().equals("star")) {
                            //System.out.println("WAS HERE");
                            child.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        // do nothing
                    }
                }

            }
        }
        return true;
    }
    public boolean hideStarRecursively(ViewGroup parent)// help function of hideStar
    {
        if(parent != null) {

            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                if (child instanceof ViewGroup) {
                    hideStarRecursively((ViewGroup) child);
                } else if (child != null) {
                    try {
                        if (child.getTag().equals("star")) {
                            //System.out.println("WAS HERE");
                            child.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        // do nothing
                    }
                }

            }
        }
        return true;
    }
    public void showDialogProceedToFirstOvertimeOnEqualScore()
    {
        if(game.activeTime == 0 && game.scores[0][0] == game.scores[1][0])
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Would you like to proceed to first overtime?");
            builder.setMessage("The snitch catch tied the game to\n\n"+Long.toString(game.scores[0][0])+" : "+Long.toString(game.scores[1][0])+".\n\nPlease wait with your answer until the referee has called the catch 'good'.");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    regTimeToFirstOvertime(null);

                } });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                } });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
    }
    public void showDialogProceedToSecondOvertimeOnEqualScore()
    {
        if(game.activeTime == 1 && game.scores[0][1] == game.scores[1][1])
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Would you like to proceed to second overtime?");
            builder.setMessage("The snitch catch tied the game to\n\n"+Long.toString(game.scores[0][1])+" : "+Long.toString(game.scores[1][1])+" in first overtime.\n\nPlease wait with your answer until the referee has called the catch 'good'.");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    firstOvertimeToSecondOvertime(null);

                } });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                } });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
    }
    //---------------------------------------
    //----- PENALTIES -----------------------
    //---------------------------------------
    public void createPenalty(View w)
    {
        String color = w.getTag().toString();
        String uniqueID = UUID.randomUUID().toString();
        long time = Admin.getTotalTimeElapsed(game.clocks);
        Penalty penalty = new Penalty(uniqueID, color, game.teams[0], time, game.activeTime);
        Log.e("SIZE BEFORE", Integer.toString(game.penalties.size()));
        game.penalties.add(penalty);
        int new_position = game.penalties.size()-1;
        penaltyListAdapter.notifyItemInserted(new_position);
        //resortPenalties();
        //penaltyListAdapter.notifyDataSetChanged();
        Log.e("SIZE AFTER", Integer.toString(game.penalties.size()));




        //penalty.setPlayerName("");
        //penalty.setPlayerNumber("");
        //penalty.setPlayerTeam(0);
        //System.out.println(penalty.getColor());
        //System.out.println(penalty.getTimeRemainingString());
        //System.out.println(penalty.getPlayerName());
        //System.out.println(penalty.getPlayerNumber());
        //System.out.println(penalty.getPlayerTeam());






        // define information to be displayed in dialog popup
        /*final int[] jerseys = new int[2];
        final String[] teamNames = new String[2];
        jerseys[0] = Admin.teamToCurrentColorRes(game.teams[0], "jersey_drawable_id");
        teamNames[0] = game.teams[0].getName();
        jerseys[1] = Admin.teamToCurrentColorRes(game.teams[1], "jersey_drawable_id");
        teamNames[1] = game.teams[1].getName();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select team of penalty");
        ListAdapter adapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.list_item_teamchoice, teamNames) {

            ViewHolder holder;

            class ViewHolder
            {
                ImageView jersey;
                TextView teamName;
            }

            public View getView(int position, View convertView, ViewGroup parent)
            {
                final LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (convertView == null)
                {
                    convertView = inflater.inflate(R.layout.list_item_teamchoice, null);

                    holder = new ViewHolder();
                    holder.jersey = (ImageView) convertView.findViewById(R.id.jersey);
                    holder.teamName = (TextView) convertView.findViewById(R.id.teamOrigin);
                    convertView.setTag(holder);
                }
                else
                {
                    // view already defined, retrieve view holder
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.jersey.setImageResource(jerseys[position]);
                holder.teamName.setText(teamNames[position]);

                return convertView;
            }
        };
        builder.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int team_position) {
                        //Toast.makeText(getApplicationContext(), "You selected: " + names[which], Toast.LENGTH_LONG).show();

                        //LayoutInflater L = getLayoutInflater();
                        //RelativeLayout RL = (RelativeLayout) findViewById(R.id.restInfo);
                        //View v = L.inflate(R.layout.penalty, RL, false);
                        //RL.addView(v);
                        //penalty.setChangeViewLayout(v);
                        penalty.setPlayerTeam(game.teams[team_position]);
                        //performSlide(v, "right", 0);
                        // -------------------------------------------
                        // ----- PLAYER NUMBER KEYBOARD --------------
                        // -------------------------------------------
                        EditText playerNumberEditText= (EditText) v.findViewById(R.id.player_number_content_et);
                        playerNumberEditText.setInputType(InputType.TYPE_NULL); // prevent keyboard (at least, that's the plan)
                        playerNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
                        {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus)
                            {
                                if(hasFocus)
                                {
                                    hideSoftKeyboardIfVisible();
                                    focusedPlayerNumberEditText = (EditText) v;
                                    numberKeyboard.setVisibility(View.VISIBLE);
                                    numberKeyboard.bringToFront();
                                    //Toast.makeText(getApplicationContext(), "got the focus", Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    focusedPlayerNumberEditText = null;
                                    numberKeyboard.setVisibility(View.GONE);
                                    //Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
                                }
                            }
                        });*/

                        // CHECK IF PLAYER NUMBER ENTERED ALREADY HAS YELLOW CARD AND CREATE DIALOG TO CONVERT TO YELLOWRED CARD
                        // THIS CODE DOES NOT WORK PROPERLY
                        /*final EditText txtEdit = (EditText) v.findViewById(R.id.player_number_content_et);
                        txtEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus)
                            {
                                if (!hasFocus) {
                                    String playerNumber = txtEdit.getText().toString();
                                    sysout(playerNumber);
                                    for(Penalty penalty_iteration : penalties)
                                    {
                                        System.out.println(penalty_iteration);
                                        System.out.println(penalty);
                                        if(penalty_iteration != penalty)
                                        {
                                            if (penalty_iteration.getColor().equals("yellow"))
                                            {
                                                sysout(penalty_iteration.getColor());
                                                System.out.println(penalty_iteration.getPlayerNumber());
                                                System.out.println(penalty_iteration.getPlayerTeam());
                                                if (penalty_iteration.getPlayerNumber().equals(playerNumber) && team == penalty_iteration.getPlayerTeam())
                                                {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                    builder.setTitle("Convert yellow card to yellow/red card?");
                                                    builder.setMessage("Note that this player already received a yellow card. Do you want to convert your entry to a yellow/red card?");

                                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            selectPenaltyColor("yellowred");
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    AlertDialog alertDialog = builder.create();
                                                    alertDialog.setCanceledOnTouchOutside(true);
                                                    alertDialog.show();
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });

                        //penalties.add(penalty);
                        //resortPenalties();
                        //penaltyDataSetHasChanged();

                        //dialog.dismiss();
                    }
                });
        // Add cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            } });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        //ListView listView = alertDialog.getListView();
        //listView.setDivider(new ColorDrawable(Color.GRAY)); // set color
        //listView.setDividerHeight(1); // set height
        alertDialog.show();*/
    }
    public void deletePenalty(View w)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to delete?");
        builder.setMessage("This cannot be undone.");


        // Add cancel button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            } });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                //Penalty penalty = penalties.get(penalty_currently_active_pos);
                //RelativeLayout RL = (RelativeLayout) findViewById(R.id.restInfo);
                //RL.removeView(penalty.getChangeViewLayout());
                game.penalties.remove(penalty_currently_active_pos);
                penaltyDataSetHasChanged();
                showFABsContainer();

            } });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    public void setPenaltyChangeActivityStatus(int pos, Penalty penalty)
    {
        penalty_currently_active_pos = pos;
        currently_active_penalty = penalty;
    }
    public void resetPenaltyChangeActivityStatus()
    {
        penalty_currently_active_pos = -1;
        currently_active_penalty = null;
    }

    public void pauseAllPenaltyTimers()
    {
        for(Penalty penalty: game.penalties)
        {
            penalty.pauseTimer();
        }
    }

    public void startAllPenaltyTimers()
    {
        for(Penalty penalty : game.penalties)
        {
            penalty.startTimer();
        }
    }

    public void outputAllTimerValues()
    {
        /*for(Penalty penalty: game.penalties)
        {
            //System.out.println(penalty.getTimeRemainingString());
        }*/
    }

    public void releaseOrUnreleasePlayer(View w)
    {
        Button b = (Button) w;
        if(currently_active_penalty.getReduced())
        {
            currently_active_penalty.setUnreduced();
            b.setText(R.string.release_player);
        }
        else
        {
            currently_active_penalty.setReduced();
            b.setText(R.string.unrelease_player);
        }
    }

    //-------------------------------------------------
    //----- CHANGE PENALTY FUNCTIONS ------------------
    //-------------------------------------------------
    /*public View getActivePenaltyView()
    {
        //Penalty penalty = penalties.get(penalty_currently_active);
        //View slider = penalty.getChangeViewLayout();
        //slider = penalty.getChangeViewLayout();
        //return slider;
    }*/
    public void selectPenaltyTeam(View w)
    {
        int selected_team = Integer.parseInt(w.getTag().toString());
        if(selected_team == 0)
        {
            penaltyEdit.findViewById(R.id.penalty_jersey_team1_tick).setVisibility(View.GONE);
            penaltyEdit.findViewById(R.id.penalty_jersey_team0_tick).setVisibility(View.VISIBLE);
        }
        else if(selected_team == 1)
        {
            penaltyEdit.findViewById(R.id.penalty_jersey_team0_tick).setVisibility(View.GONE);
            penaltyEdit.findViewById(R.id.penalty_jersey_team1_tick).setVisibility(View.VISIBLE);
        }
        penalty_selected_team = selected_team;
    }
    public void selectPenaltyColor(String selected_color)
    {
        //String old_color = currently_active_penalty.getColor();
        if(selected_color.equals("blue"))
        {
            penaltyEdit.findViewById(R.id.penalty_bluecardtick).setVisibility(View.VISIBLE);
            penaltyEdit.findViewById(R.id.penalty_yellowcardtick).setVisibility(View.GONE);
            penaltyEdit.findViewById(R.id.penalty_redcardtick).setVisibility(View.GONE);
            penaltyEdit.findViewById(R.id.penalty_yellowredcardtick).setVisibility(View.GONE);
        }
        else if(selected_color.equals("yellow"))
        {
            penaltyEdit.findViewById(R.id.penalty_bluecardtick).setVisibility(View.GONE);
            penaltyEdit.findViewById(R.id.penalty_yellowcardtick).setVisibility(View.VISIBLE);
            penaltyEdit.findViewById(R.id.penalty_redcardtick).setVisibility(View.GONE);
            penaltyEdit.findViewById(R.id.penalty_yellowredcardtick).setVisibility(View.GONE);
        }
        else if(selected_color.equals("red"))
        {
            penaltyEdit.findViewById(R.id.penalty_bluecardtick).setVisibility(View.GONE);
            penaltyEdit.findViewById(R.id.penalty_yellowcardtick).setVisibility(View.GONE);
            penaltyEdit.findViewById(R.id.penalty_redcardtick).setVisibility(View.VISIBLE);
            penaltyEdit.findViewById(R.id.penalty_yellowredcardtick).setVisibility(View.GONE);
        }
        else if(selected_color.equals("yellowred"))
        {
            penaltyEdit.findViewById(R.id.penalty_bluecardtick).setVisibility(View.GONE);
            penaltyEdit.findViewById(R.id.penalty_yellowcardtick).setVisibility(View.GONE);
            penaltyEdit.findViewById(R.id.penalty_redcardtick).setVisibility(View.GONE);
            penaltyEdit.findViewById(R.id.penalty_yellowredcardtick).setVisibility(View.VISIBLE);
        }
        penalty_color = selected_color;
        String new_color = penalty_color;
        currently_active_penalty.setColor(new_color);
        // update penalty countdown
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateChangePenaltyCountdown();
            }
        });
        /*if((old_color.equals("yellow") || old_color.equals("blue")) && (new_color.equals("yellow") || new_color.equals("blue"))) calculateTimeDelta("zero");
        else if((old_color.equals("yellow") || old_color.equals("blue")) && (new_color.equals("red") || new_color.equals("yellowred"))) calculateTimeDelta("plussixtysec");
        else if((old_color.equals("red") || old_color.equals("yellowred")) && (new_color.equals("yellow") || new_color.equals("blue"))) calculateTimeDelta("minussixtysec");
        else if((old_color.equals("red") || old_color.equals("yellowred")) && (new_color.equals("red") || new_color.equals("yellowred"))) calculateTimeDelta("zero");*/
    }
    public void selectPenaltyColor(View w)
    {
        String selected_color = w.getTag().toString();
        selectPenaltyColor(selected_color);
    }
    /*public void calculateTimeDelta(View w)
    {
        String selected_calculation = w.getTag().toString();
        calculateTimeDelta(selected_calculation);
    }*/
    /*public void calculateTimeDelta(String selected_calculation)
    {
        long card_change_penalty_time_delta = 0;
        long red_yellow_delta = currently_active_penalty.getRedPenalty()-currently_active_penalty.getYellowPenalty();
        if(selected_calculation.equals("zero")) card_change_penalty_time_delta = 0;
        else if(selected_calculation.equals("minussixtysec")) card_change_penalty_time_delta = -red_yellow_delta;
        else if(selected_calculation.equals("plussixtysec")) card_change_penalty_time_delta = red_yellow_delta;
        else if(selected_calculation.equals("minustensec")) penalty_time_delta -= 10;
        else if(selected_calculation.equals("plustensec")) penalty_time_delta += 10;
        else if(selected_calculation.equals("plusonesec")) penalty_time_delta += 1;
        //TextView tv = (TextView) penalty_slider.findViewById(R.id.penalty_delta_time);
        //String text = "";
        //System.out.println(card_change_penalty_time_delta);
        //System.out.println(penalty_time_delta);
        //System.out.println(selected_calculation);
        //if(card_change_penalty_time_delta + penalty_time_delta == 0) text = "";
        //else if(card_change_penalty_time_delta + penalty_time_delta > 0) text = "+"+Long.toString(card_change_penalty_time_delta+penalty_time_delta)+" secs";
        //else if(card_change_penalty_time_delta + penalty_time_delta < 0) text = Long.toString(card_change_penalty_time_delta+penalty_time_delta)+" secs";
        //sysout(card_change_penalty_time_delta + penalty_time_delta);
        //tv.setText(text);
    }*/

    /*public void applyPenaltyChanges(View w)
    {
        Penalty penalty = penalties.get(penalty_currently_active);

        EditText et_player_number = (EditText) penalty_slider.findViewById(R.id.penalty_player_number_content_et);
        penalty_player_number = et_player_number.getText().toString();
        EditText et_player_name = (EditText) penalty_slider.findViewById(R.id.penalty_player_name_content_et);
        penalty_player_name = et_player_name.getText().toString();

        penalty.setPlayerTeam(penalty_selected_team);
        penalty.setPlayerNumber(penalty_player_number);
        penalty.setPlayerName(penalty_player_name);
        //System.out.println(penalty_time_delta);
        penalty.applyTimeDelta(penalty_time_delta);
        penalty.setColor(penalty_color);

        penalty_currently_active = -1;

        resortPenalties();

        penaltyDataSetHasChanged();

        performSlide(penalty_slider,"right");
    }*/
    /*public void slideDownPenaltyChangeView(View w)
    {
        performSlide(penalty_slider,"right");
    }*/


    //-------------------------------------------------
    //----- CHANGE GLOBAL TIME ------------------------
    //-------------------------------------------------
    public void adjustTime(final View w)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Adjust time of play by");

        LayoutInflater inflater = getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.time_change, null);
        timeChangeGlobalTextView = (TextView) inflatedView.findViewById(R.id.timeChangeGlobal);
        builder.setView(inflatedView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                long realTimeChange_ms;
                if(w.getTag().toString().equals("reg_time"))
                {
                    realTimeChange_ms = game.clocks[0].setDeltaStartTime(changeGlobalTime*1000);
                    setRegTimeSnitchReminder();
                }
                else if(w.getTag().toString().equals("first_overtime"))
                {
                    realTimeChange_ms = game.clocks[1].setDeltaStartTime(changeGlobalTime*1000);
                    realTimeChange_ms = -realTimeChange_ms;
                    setFirstOvertimeSoundPlayedArray();
                }
                else if(w.getTag().toString().equals("second_overtime")) realTimeChange_ms = game.clocks[2].setDeltaStartTime(changeGlobalTime*1000);
                else realTimeChange_ms = 0;
                for(Penalty penalty : game.penalties)
                {
                    penalty.applyTimeDelta(-realTimeChange_ms);
                }
                changeGlobalTime = 0;
                timeChangeGlobalTextView = null;
                //OK_Code
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                changeGlobalTime = 0;
                timeChangeGlobalTextView = null;
                //OK_Code
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    public void plusTenSecs(View w)
    {
        changeGlobalTime += 10;
        setTimeChange();
    }
    public void plusOneSec(View w)
    {
        changeGlobalTime += 1;
        setTimeChange();
    }
    public void minusTenSecs(View w)
    {
        changeGlobalTime -= 10;
        setTimeChange();
    }
    public void minusOneSec(View w)
    {
        changeGlobalTime -= 1;
        setTimeChange();
    }
    public void setTimeChange()
    {
        String str;
        int signum = Long.signum(changeGlobalTime);
        if(signum == -1) str = "- ";
        else if(signum == 0) str = "± ";
        else str = "+ ";
        str += Long.toString(abs(changeGlobalTime))+" second";
        str += (abs(changeGlobalTime)!=1) ? "s" : "";
        timeChangeGlobalTextView.setText(str);
    }
    //-------------------------------------------------
    //----- PLAYER NUMBER KEYBOARD --------------------
    //-------------------------------------------------
    public static boolean isParsable(String input)
    {
        boolean parsable = true;
        try{
            Integer.parseInt(input);
        }catch(NumberFormatException e){
            parsable = false;
        }
        return parsable;
    }
    public void enter0(View w){enterPlayerNumber("0", "append");}
    public void enter1(View w){enterPlayerNumber("1", "append");}
    public void enter2(View w){enterPlayerNumber("2", "append");}
    public void enter3(View w){enterPlayerNumber("3", "append");}
    public void enter4(View w){enterPlayerNumber("4", "append");}
    public void enter5(View w){enterPlayerNumber("5", "append");}
    public void enter6(View w){enterPlayerNumber("6", "append");}
    public void enter7(View w){enterPlayerNumber("7", "append");}
    public void enter8(View w){enterPlayerNumber("8", "append");}
    public void enter9(View w){enterPlayerNumber("9", "append");}
    public void enterHashtag(View w){enterPlayerNumber("#", "replace");}
    public void enterPi(View w){enterPlayerNumber("\u03C0", "replace");}
    public void enterInf(View w){enterPlayerNumber("\u221e", "replace");}
    public void enterA(View w){enterPlayerNumber("A", "replace");}
    public void enterG(View w){enterPlayerNumber("G", "replace");}
    public void enterH(View w){enterPlayerNumber("H", "replace");}
    public void enterJ(View w){enterPlayerNumber("J", "replace");}
    public void enterK(View w){enterPlayerNumber("K", "replace");}
    public void enterN(View w){enterPlayerNumber("N", "replace");}
    public void enterP(View w){enterPlayerNumber("P", "replace");}
    public void enterR(View w){enterPlayerNumber("R", "replace");}
    public void enterW(View w){enterPlayerNumber("W", "replace");}
    public void enterX(View w){enterPlayerNumber("X", "replace");}
    public void enterY(View w){enterPlayerNumber("Y", "replace");}
    public void enterBackspace(View w){enterPlayerNumber("", "replace");}
    public void enterDone(View w){focusedPlayerNumberEditText.clearFocus();}

    public void enterPlayerNumber(String text, String mode)
    {
        if(focusedPlayerNumberEditText!=null)
        {
            String currentText = focusedPlayerNumberEditText.getText().toString();
            if(!isParsable(currentText)) mode = "replace";

            if(mode.equals("append"))
            {
                if(currentText.length()<maxPlayerNumberSize)
                {
                    String newText = currentText+text;
                    focusedPlayerNumberEditText.setText(newText);
                }
            }
            else if(mode.equals("replace"))
            {
                focusedPlayerNumberEditText.setText(text);
            }
        }
    }

    private void resortPenalties()
    {
        Collections.sort(game.penalties, new SorterPenalties());
    }

    /*private void changeAllPenaltyJerseyColors(Team team)
    {
        int team_position = team.getPosition();
        for(Penalty penalty : game.penalties)
        {
            if(penalty.getPlayerTeamPosition() == team_position)
            {
                penalty.setPlayerTeam(team);
            }
            //penalty.setCorrectJerseyTickInChangeView(team);
            //penalty.setCorrectJerseyTickInChangeView(1-team);
        }
    }*/


    public void penaltyDataSetHasChanged()
    {
        /*penaltyListAdapter.notifyDataSetChanged();
        resortPenalties();
        if(menu != null) {
            if (penaltyListAdapter.getCount() > 1)
                menu.findItem(R.id.action_resort_penalties).setVisible(true);
            else menu.findItem(R.id.action_resort_penalties).setVisible(false);
        }*/
    }

    public void hideSoftKeyboardIfVisible()
    {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void hideFABs()
    {
        fabBlue.hide();
        fabYellow.hide();
        fabRed.hide();
    }

    public void showFABs()
    {
        fabBlue.show();
        fabYellow.show();
        fabRed.show();
    }

    public void hideFABsContainer()
    {
        View w = findViewById(R.id.addPenaltiesWrapper);
        w.setVisibility(View.GONE);
    }

    public void showFABsContainer()
    {
        View w = findViewById(R.id.addPenaltiesWrapper);
        w.setVisibility(View.VISIBLE);
    }

    public void hideScoreArrows()
    {
        scoreArrows[0][game.activeTime][0].setVisibility(View.INVISIBLE);
        scoreArrows[0][game.activeTime][1].setVisibility(View.INVISIBLE);
        scoreArrows[1][game.activeTime][0].setVisibility(View.INVISIBLE);
        scoreArrows[1][game.activeTime][1].setVisibility(View.INVISIBLE);
    }
    public void showScoreArrows()
    {
        scoreArrows[0][game.activeTime][0].setVisibility(View.VISIBLE);
        scoreArrows[0][game.activeTime][1].setVisibility(View.VISIBLE);
        scoreArrows[1][game.activeTime][0].setVisibility(View.VISIBLE);
        scoreArrows[1][game.activeTime][1].setVisibility(View.VISIBLE);
    }
    public void hideCertainScoreArrowsSecondOvertime(boolean[] arrowHide)
    {
        if(arrowHide.length==4)
        {
            if(arrowHide[0]) findViewById(R.id.team0_score_up_second_overtime).setVisibility(View.INVISIBLE);
            if(arrowHide[1]) findViewById(R.id.team0_score_down_second_overtime).setVisibility(View.INVISIBLE);
            if(arrowHide[2]) findViewById(R.id.team1_score_up_second_overtime).setVisibility(View.INVISIBLE);
            if(arrowHide[3]) findViewById(R.id.team1_score_down_second_overtime).setVisibility(View.INVISIBLE);
        }
    }
    public void hideSnitchesSecondOvertime()
    {
        FloatingActionButton fab_catch = (FloatingActionButton) findViewById(R.id.snitchcatchteam0_second_overtime);
        FloatingActionButton fab_otherteam = (FloatingActionButton) findViewById(R.id.snitchcatchteam1_second_overtime);
        fab_catch.setVisibility(View.INVISIBLE);
        fab_otherteam.setVisibility(View.INVISIBLE);
    }
    public void showSnitchesSecondOvertime()
    {
        FloatingActionButton fab_catch = (FloatingActionButton) findViewById(R.id.snitchcatchteam0_second_overtime);
        FloatingActionButton fab_otherteam = (FloatingActionButton) findViewById(R.id.snitchcatchteam1_second_overtime);
        fab_catch.setVisibility(View.VISIBLE);
        fab_otherteam.setVisibility(View.VISIBLE);
    }
    public void updateToolbarText()
    {
        switch(game.activeTime)
        {
            case 0:
                toolbarTitle.setText("Regular Time");
                break;
            case 1:
                toolbarTitle.setText("First Overtime");
                break;
            case 2:
                toolbarTitle.setText("Second Overtime");
                break;
        }
    }
    public void updateClockUI()
    {
        for(int term=0;term<3;term++)
        {
            if(game.clocks[term].getMode().equals("timer")) clock_vals[term].setText(game.clocks[term].getTimeRemainingMinSecString());
            else if(game.clocks[term].getMode().equals("stopwatch")) clock_vals[term].setText(game.clocks[term].getMinSecString());
        }
    }
    public void updateTermUI()
    {
        if(game.activeTime == 1) switchTerm(0,1,false);
        else if(game.activeTime == 2) {switchTerm(0,1,false);switchTerm(1,2,false);}
    }
    public void updateScoresUI()
    {
        for(int team_pos=0;team_pos<2;team_pos++)
        {
            for(int term=0;term<3;term++)
            {
                scrollToScore(team_pos, term, game.scores[team_pos][term], false);
                if(game.catches[team_pos][term]) snitchUIAtCatch(team_pos, term);
            }
        }
    }
    public void scrollToScore(int team_pos, int term, long new_score){scrollToScore(team_pos, term, new_score, true);}
    public void scrollToScore(int team_pos, int term, long new_score, boolean animate)
    {
        long newScrollPos = new_score/10;
        if(newScrollPos<0) newScrollPos=0;
        else if(newScrollPos>99) newScrollPos=99;
        CustomScrollView scoreScrollView = scoreViews[team_pos][term];
        int childHeight = getChildHeight(scoreScrollView);
        if(animate) scoreScrollView.smoothScrollTo(0, (int) newScrollPos*childHeight);
        else scoreScrollView.scrollTo(0, (int) newScrollPos*childHeight);
    }

    /*public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
        sysout(firstListItemPosition);
        sysout(lastListItemPosition);
        if(lastListItemPosition < 0) return null;
        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }*/





























































    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////   RECYCLER VIEW (LIST) STUFF    ///////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    private void setUpPenaltyList()
    {
        penaltyClickListener = new PenaltyListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Penalty penalty) {
                Admin.sysout("OPEN PENALTY DIALOG");
            }
        };
        penaltyLongClickListener = new PenaltyListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, Penalty penalty) {
                Admin.sysout("LONG CLICK REGISTERED");
            }
        };
        penaltyList.setLayoutManager(new LinearLayoutManager(this));
        penaltyListAdapter = new PenaltyListAdapter(this, game.penalties, penaltyClickListener, penaltyLongClickListener);
        penaltyList.setAdapter(penaltyListAdapter);
        penaltyList.setHasFixedSize(true);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
    }
    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(ContextCompat.getColor(GameActivity.this, R.color.red));
                xMark = ContextCompat.getDrawable(GameActivity.this, R.drawable.ic_close_white);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) GameActivity.this.getResources().getDimension(R.dimen.clear_margin);
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                PenaltyListAdapter testAdapter = (PenaltyListAdapter)recyclerView.getAdapter();
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                PenaltyListAdapter adapter = (PenaltyListAdapter) penaltyList.getAdapter();
                game.penalties.remove(swipedPosition);
                penaltyListAdapter.notifyItemRemoved(swipedPosition);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(penaltyList);
    }
    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper() {
        penaltyList.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(ContextCompat.getColor(GameActivity.this, R.color.red));
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }
}
