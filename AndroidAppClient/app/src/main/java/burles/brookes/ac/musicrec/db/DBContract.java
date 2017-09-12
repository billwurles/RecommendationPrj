package burles.brookes.ac.musicrec.db;

import android.provider.BaseColumns;

import burles.brookes.ac.musicrec.LoginActivity;

public class DBContract implements BaseColumns {
    public static final int COL_INDEX_ID = 0;

    public static final String TABLE_NAME_SONGS = "songs";
    public static final String TABLE_NAME_USER = "user";

    public static final String COL_NAME_SONG_TITLE = "title";
    public static final String COL_NAME_SONG_ARTIST = "artist";
    public static final String COL_NAME_SONG_ALBUM = "album";
    public static final String COL_NAME_SONG_GENRE = "genre";
    public static final String COL_NAME_SONG_PLAYS = "plays";
    public static final String COL_NAME_SONG_RATING = "rating";
    public static final String COL_NAME_SONG_ID = "id";

    public static final int COL_INDEX_SONG_TITLE = 1;
    public static final int COL_INDEX_SONG_ARTIST = 2;
    public static final int COL_INDEX_SONG_ALBUM = 3;
    public static final int COL_INDEX_SONG_GENRE = 4;
    public static final int COL_INDEX_SONG_PLAYS = 5;
    public static final int COL_INDEX_SONG_RATING = 6;
    public static final int COL_INDEX_SONG_ID = 7;

    public static final String COL_NAME_USER_NAME = "username";
    public static final String COL_NAME_USER_PASS = "password";
    public static final String COL_NAME_USER_NUM = "libraryNum";

    public static final int COL_INDEX_USER_NAME = 0;
    public static final int COL_INDEX_USER_PASS = 1;
    public static final int COL_INDEX_USER_NUM = 2;

    public static final String DBTag = LoginActivity.Tag+"DB";
}
