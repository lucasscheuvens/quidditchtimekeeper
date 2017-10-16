package de.lucasscheuvens.qtk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 23.01.2017.
 */

public class DBHelper extends SQLiteOpenHelper implements Serializable
{
    private SQLiteDatabase db_write;
    private SQLiteDatabase db_read;

    public static final String DATABASE_NAME = "timekeeper_db";
    public static final String GAMES_TABLE_NAME = "games";
    public static final String GAMES_COLUMN_ID = "id";
    public static final String GAMES_SERIALIZABLE_NAME = "serializable_data";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
        db_write = this.getWritableDatabase();
        db_read = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE "+GAMES_TABLE_NAME+" ("+GAMES_COLUMN_ID+" INTEGER PRIMARY KEY, "+GAMES_SERIALIZABLE_NAME+" BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+GAMES_TABLE_NAME);
        onCreate(db);
    }

    public long insertGame (Game game)
    {
        ContentValues contentValues = new ContentValues();
        byte[] data = SerializerClass.serializeObject(game);
        contentValues.put(GAMES_SERIALIZABLE_NAME, data);
        return db_write.insert(GAMES_TABLE_NAME, null, contentValues);
    }

    public Game getGame(Long id)
    {
        Cursor res = db_read.rawQuery("SELECT * FROM "+GAMES_TABLE_NAME+" WHERE "+GAMES_COLUMN_ID+"="+id+"", null);
        Game game = (Game) SerializerClass.deserializeObject(res.getBlob(res.getColumnIndex(GAMES_SERIALIZABLE_NAME)));
        res.close();
        return game;
    }

    public int numberOfRows()
    {
        return (int) DatabaseUtils.queryNumEntries(db_read, GAMES_TABLE_NAME);
    }

    public boolean updateGame(Long id, Game game) {
        ContentValues contentValues = new ContentValues();
        byte[] data = SerializerClass.serializeObject(game);
        contentValues.put(GAMES_SERIALIZABLE_NAME, data);
        db_write.update(GAMES_TABLE_NAME, contentValues, GAMES_COLUMN_ID+" = ? ", new String[] { Long.toString(id) } );
        return true;
    }

    public Integer deleteGame(Long id) {
        return db_write.delete(GAMES_TABLE_NAME, GAMES_COLUMN_ID+" = ? ", new String[] { Long.toString(id) });
    }

    public void deleteAllGames()
    {
        db_write.execSQL("DELETE FROM "+GAMES_TABLE_NAME);
    }

    public List<Game> getAllGames()
    {
        List<Game> all_games_from_db = new ArrayList<>();

        Cursor res =  db_read.rawQuery("SELECT * FROM "+GAMES_TABLE_NAME, null);
        res.moveToFirst();
        System.out.println(res);

        System.out.println("YEPPEH");
        while(!res.isAfterLast())
        {
            all_games_from_db.add((Game)SerializerClass.deserializeObject(res.getBlob(res.getColumnIndex(GAMES_SERIALIZABLE_NAME))));
            res.moveToNext();
        }
        res.close();
        return all_games_from_db;
    }
}