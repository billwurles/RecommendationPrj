/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

/**
 *
 * @author Will
 */
public class DBContract {
    
    public static final String DBURL = "jdbc:mysql://192.168.0.37:3306/RecSys";
    public static final String DBUSER = "control";
    public static final String DBPASS = "willb1";
    
    public static final String TABLE_SONGS = "songs";
    public static final String TABLE_USERS = "users";
    
    public static final String COL_S_ID = "id";
    public static final String COL_S_TITLE = "title";
    public static final String COL_S_ARTIST = "artist";
    public static final String COL_S_ALBUM = "album";
    public static final String COL_S_GENRE = "genre";
    public static final String COL_S_PLAYS = "plays";
    public static final String COL_S_RATING = "rating";
    
}
