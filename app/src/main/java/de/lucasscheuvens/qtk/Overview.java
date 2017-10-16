package de.lucasscheuvens.qtk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 12.01.2017.
 */

public class Overview extends AppCompatActivity
{
    Context context;
    private Toolbar toolbar;
    private OverviewListAdapter overviewListAdapter;
    private OverviewListAdapter.OnItemClickListener clickListener;
    private OverviewListAdapter.OnItemLongClickListener longClickListener;
    List<Game> games = new ArrayList<>();
    private int game_position; // when user navigates to next activity, this value is set for Overview Activity. This way it is known, which position in games the game was.
    private RecyclerView list;
    private DBHelper db;
    private RelativeLayout loadingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Screen must not turn off
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Screen can turn off again
        context = this.getApplicationContext();
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        //---------------------------------------
        //----- INITIALIZE VALUES ---------------
        //---------------------------------------
        game_position = -1;
        //---------------------------------------
        //----- DATABASE STUFF ------------------
        //---------------------------------------
        db = new DBHelper(this);
        //SQLiteDatabase db = openOrCreateDatabase("timekeeper_db",MODE_PRIVATE,null);
        //db.execSQL("CREATE TABLE IF NOT EXISTS game(id INT, obj VARCHAR);");
        //db.execSQL("INSERT INTO game VALUES(1,'BLABLABLA');");
        //Cursor resultSet = db.rawQuery("Select * from game",null);
        //mydb.insertGame("das sind testdaten");
        //mydb.insertGame("noch mehr daten");
        //mydb.insertGame("Lucas!");
        //mydb.insertGame("Es klappt so wunderbar!");
        db.deleteAllGames();
        games = db.getAllGames();
        //System.out.println(resultSet);
        /*resultSet.moveToFirst();
        int id = resultSet.getInt(1);
        String obj = resultSet.getString(2);
        System.out.println(id);
        System.out.println(obj);*/
        //--------------------------------------
        //---- DATA BASE DEBUGGING -------------
        //--------------------------------------
        // ---> run app and navigate to chrome. Type chrome://inspect into address bar and select "inspect" of the respective item
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(context));
        Stetho.Initializer initializer = initializerBuilder.build();
        Stetho.initialize(initializer);
        //---------------------------------------
        //----- TOOLBAR -------------------------
        //---------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_logo_white);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //---------------------------------------
        //----- LIST ----------------------------
        //---------------------------------------
        Game game;
        long id;
        for(int i=0;i<5;i++)
        {
            game = new Game();
            id = db.insertGame(game);
            game.setDBId(id);
            games.add(game);
        }
        //---------------------------------------
        //----- LIST STUFF ----------------------
        //---------------------------------------
        list = (RecyclerView) findViewById(R.id.list_overview);
        System.out.println(findViewById(R.id.list_overview));
        System.out.println(list);
        setUpRecyclerView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        loadingPanel.setVisibility(View.GONE);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                Game game = (Game) intent.getSerializableExtra("game");
                games.set(game_position, game);
                System.out.println(game.teams[0].getName());
                overviewListAdapter.notifyItemChanged(game_position);
                game_position = -1;
            }
        }
    }

    public void onFABClick(View w)
    {
        Game game = new Game();
        games.add(game);
        long id = db.insertGame(game);
        game.setDBId(id);
        int new_position = games.size()-1;
        overviewListAdapter.notifyItemInserted(new_position);
        loadingPanel.setVisibility(View.VISIBLE);
        startGameActivity(new_position, game);
    }

    public void startGameActivity(int game_position_new, Game game)
    {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("game", game);
        game_position = game_position_new;
        loadingPanel.setVisibility(View.VISIBLE);
        startActivityForResult(intent, 1);
    }

    public void startGameInfoActivity(Game game)
    {
        Intent intent = new Intent(this, GameInfoActivity.class);
        intent.putExtra("game", game);
        loadingPanel.setVisibility(View.VISIBLE);
        startActivityForResult(intent, 2);
    }




































    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////   RECYCLER VIEW (LIST) STUFF    ///////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    private void setUpRecyclerView()
    {
        clickListener = new OverviewListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Game game) {
                startGameActivity(position, game);
            }
        };
        longClickListener = new OverviewListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, Game game) {
                startGameInfoActivity(game);
            }
        };
        list.setLayoutManager(new LinearLayoutManager(this));
        overviewListAdapter = new OverviewListAdapter(this, games, clickListener, longClickListener);
        list.setAdapter(overviewListAdapter);
        list.setHasFixedSize(true);
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
                background = new ColorDrawable(ContextCompat.getColor(Overview.this, R.color.red));
                xMark = ContextCompat.getDrawable(Overview.this, R.drawable.ic_close_white);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) Overview.this.getResources().getDimension(R.dimen.clear_margin);
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
                OverviewListAdapter testAdapter = (OverviewListAdapter)recyclerView.getAdapter();
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                OverviewListAdapter adapter = (OverviewListAdapter) list.getAdapter();
                db.deleteGame(games.get(swipedPosition).getDBId()); // delete from DB
                games.remove(swipedPosition);
                overviewListAdapter.notifyItemRemoved(swipedPosition);
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
        mItemTouchHelper.attachToRecyclerView(list);
    }
    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper() {
        list.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(ContextCompat.getColor(Overview.this, R.color.red));
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
