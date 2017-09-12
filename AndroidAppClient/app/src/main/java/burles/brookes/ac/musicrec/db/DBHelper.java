package burles.brookes.ac.musicrec.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String COMMA_SEP = ", ";
    public static final int DATABASE_VERSION = 14;
    public static final String DATABASE_NAME = "database.db";
    public static final String CREATE_SONGS_ENTRY =
            "CREATE TABLE "+ DBContract.TABLE_NAME_SONGS + " (" +
                    DBContract._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    DBContract.COL_NAME_SONG_TITLE + " TEXT NOT NULL"+ COMMA_SEP +
                    DBContract.COL_NAME_SONG_ARTIST + " TEXT NOT NULL"+ COMMA_SEP +
                    DBContract.COL_NAME_SONG_ALBUM + " TEXT"+ COMMA_SEP +
                    DBContract.COL_NAME_SONG_GENRE + " TEXT"+ COMMA_SEP +
                    DBContract.COL_NAME_SONG_PLAYS + " INTEGER NOT NULL DEFAULT 0"+ COMMA_SEP +
                    DBContract.COL_NAME_SONG_RATING + " INTEGER NOT NULL DEFAULT 0"+ COMMA_SEP +
                    DBContract.COL_NAME_SONG_ID + " TEXT DEFAULT NULL)";
    public static final String CREATE_USER_ENTRY =
            "CREATE TABLE "+ DBContract.TABLE_NAME_USER + " (" +
                    DBContract.COL_NAME_USER_NAME + " TEXT NOT NULL"+ COMMA_SEP +
                    DBContract.COL_NAME_USER_PASS + " TEXT NOT NULL"+ COMMA_SEP +
                    DBContract.COL_NAME_USER_NUM + " INTEGER NOT NULL DEFAULT 0)";


    private static final String DELETE_SONGS = "DROP TABLE IF EXISTS " + DBContract.TABLE_NAME_SONGS;
    private static final String DELETE_USER = "DROP TABLE IF EXISTS " + DBContract.TABLE_NAME_USER;

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d(DBContract.DBTag, "creating sqlite \n\n" + CREATE_SONGS_ENTRY);
        db.execSQL(CREATE_SONGS_ENTRY);
        db.execSQL(CREATE_USER_ENTRY);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(DELETE_SONGS);
        db.execSQL(DELETE_USER);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }
}
