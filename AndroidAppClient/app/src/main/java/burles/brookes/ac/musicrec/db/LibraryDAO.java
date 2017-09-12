package burles.brookes.ac.musicrec.db;

/**
 * Created by Will on 06/03/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import burles.brookes.ac.musicrec.Utils.Song;
import burles.brookes.ac.musicrec.Utils.User;

public class LibraryDAO {

    public void addSongs(Context context, ArrayList<Song> songsList){
        context.getContentResolver().delete(DBProvider.SONGS_URI,null,null);
        for(Song song : songsList){
            ContentValues values = new ContentValues();
            values.put(DBContract.COL_NAME_SONG_TITLE, song.getTitle());
            values.put(DBContract.COL_NAME_SONG_ARTIST, song.getArtist());
            if(song.getAlbum().equals("")){
                values.put(DBContract.COL_NAME_SONG_ALBUM, "Unknown Album");
            } else {
                values.put(DBContract.COL_NAME_SONG_ALBUM, song.getAlbum());
            }
            values.put(DBContract.COL_NAME_SONG_GENRE, song.getGenre());
            values.put(DBContract.COL_NAME_SONG_PLAYS, song.getPlays());
            values.put(DBContract.COL_NAME_SONG_RATING, song.getRating());
            values.put(DBContract.COL_NAME_SONG_ID, song.getId());
            context.getContentResolver().insert(DBProvider.SONGS_URI, values);
            Log.d(DBContract.DBTag, "Adding song :'"+song+"' to db");
        }
    }

    public ArrayList<Song> getAllSongs(Context context){
        ArrayList<Song> songs = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(DBProvider.SONGS_URI, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Song song = new Song(
                    cursor.getString(DBContract.COL_INDEX_SONG_TITLE),
                    cursor.getString(DBContract.COL_INDEX_SONG_ARTIST),
                    cursor.getString(DBContract.COL_INDEX_SONG_ALBUM),
                    cursor.getString(DBContract.COL_INDEX_SONG_GENRE),
                    cursor.getInt(DBContract.COL_INDEX_SONG_PLAYS),
                    cursor.getInt(DBContract.COL_INDEX_SONG_RATING),
                    cursor.getString(DBContract.COL_INDEX_SONG_ID)
            );
            songs.add(song);
            cursor.moveToNext();
            Log.d(DBContract.DBTag, "retrieving song :'"+song+"' from db");
        }
        return songs;
    }

    public ArrayList<String> getAllArtists(Context context){
        ArrayList<String> artists = new ArrayList<>();
        String[] projection = {DBContract.COL_NAME_SONG_ARTIST};
        String order = DBContract.COL_NAME_SONG_ARTIST+ " ASC";
        Cursor cursor = context.getContentResolver().query(DBProvider.SONGS_URI, projection, null, null, order, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String artist = cursor.getString(0);
            if(!artists.contains(artist)){
                artists.add(artist);
                Log.d(DBContract.DBTag, "retrieving artist :'"+artist+"' from db");
            }
            cursor.moveToNext();
        }
        return artists;
    }

    public ArrayList<String> getAlbumFromArtist(Context context, String artist){
        ArrayList<String> albums = new ArrayList<>();
        String[] projection = {DBContract.COL_NAME_SONG_ALBUM};
        String selection = DBContract.COL_NAME_SONG_ARTIST + " = \"" + artist+"\"";
        String order = DBContract.COL_NAME_SONG_ALBUM+ " ASC";
        Cursor cursor = context.getContentResolver().query(DBProvider.SONGS_URI, projection, selection, null, order, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String album = cursor.getString(0);
            if(!albums.contains(album)){
                albums.add(album);
                Log.d(DBContract.DBTag, "retrieving album :'"+album+"' from db");
            }
            cursor.moveToNext();
        }
        return albums;
    }

    public ArrayList<Song> getSongsFromArtist(Context context, String artist){
        ArrayList<Song> songs = new ArrayList<>();
        String selection = DBContract.COL_NAME_SONG_ARTIST + " = "+artist+"\"";
        String order = DBContract.COL_NAME_SONG_ALBUM + " ASC";
        Cursor cursor = context.getContentResolver().query(DBProvider.SONGS_URI, null, selection, null, order, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Song song = new Song(
                    cursor.getString(DBContract.COL_INDEX_SONG_TITLE),
                    cursor.getString(DBContract.COL_INDEX_SONG_ARTIST),
                    cursor.getString(DBContract.COL_INDEX_SONG_ALBUM),
                    cursor.getString(DBContract.COL_INDEX_SONG_GENRE),
                    cursor.getInt(DBContract.COL_INDEX_SONG_PLAYS),
                    cursor.getInt(DBContract.COL_INDEX_SONG_RATING),
                    cursor.getString(DBContract.COL_INDEX_SONG_ID)
            );
            Log.d(DBContract.DBTag, "retrieving song :'"+song+"' from db");
            songs.add(song);
            cursor.moveToNext();
        }
        return songs;
    }
    public ArrayList<Song> getSongsFromAlbum(Context context, String album){
        ArrayList<Song> songs = new ArrayList<>();
        String selection = DBContract.COL_NAME_SONG_ALBUM + " = \""+album+"\"";
        String order = DBContract.COL_NAME_SONG_TITLE+ " ASC";
        Cursor cursor = context.getContentResolver().query(DBProvider.SONGS_URI, null, selection, null, order, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Song song = new Song(
                    cursor.getString(DBContract.COL_INDEX_SONG_TITLE),
                    cursor.getString(DBContract.COL_INDEX_SONG_ARTIST),
                    cursor.getString(DBContract.COL_INDEX_SONG_ALBUM),
                    cursor.getString(DBContract.COL_INDEX_SONG_GENRE),
                    cursor.getInt(DBContract.COL_INDEX_SONG_PLAYS),
                    cursor.getInt(DBContract.COL_INDEX_SONG_RATING),
                    cursor.getString(DBContract.COL_INDEX_SONG_ID)
            );
            songs.add(song);
            cursor.moveToNext();
            Log.d(DBContract.DBTag, "retrieving song :'"+song+"' from db");
        }
        return songs;
    }
    public ArrayList<Song> getSongsFromGenre(Context context, String genre){
        ArrayList<Song> songs = new ArrayList<>();
        String selection = DBContract.COL_NAME_SONG_GENRE + " = \"" + genre + "\"";
        String order = DBContract.COL_NAME_SONG_ALBUM + " ASC";
        Cursor cursor = context.getContentResolver().query(DBProvider.SONGS_URI, null, selection, null, order, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Song song = new Song(
                    cursor.getString(DBContract.COL_INDEX_SONG_TITLE),
                    cursor.getString(DBContract.COL_INDEX_SONG_ARTIST),
                    cursor.getString(DBContract.COL_INDEX_SONG_ALBUM),
                    cursor.getString(DBContract.COL_INDEX_SONG_GENRE),
                    cursor.getInt(DBContract.COL_INDEX_SONG_PLAYS),
                    cursor.getInt(DBContract.COL_INDEX_SONG_RATING),
                    cursor.getString(DBContract.COL_INDEX_SONG_ID)
            );
            songs.add(song);
            cursor.moveToNext();
            Log.d(DBContract.DBTag, "retrieving song :'"+song+"' from db");
        }
        return songs;
    }

    public void addPlayToSong(Context context, String id){
        ArrayList<Song> songs = new ArrayList<>();
        String selection = DBContract.COL_NAME_SONG_ID + " = "+id;
        Cursor cursor = context.getContentResolver().query(DBProvider.SONGS_URI, null, selection, null, null, null);
        cursor.moveToFirst();
        int plays = cursor.getInt(DBContract.COL_INDEX_SONG_PLAYS);
        plays++;
        ContentValues values = new ContentValues();
        values.put(DBContract.COL_NAME_SONG_PLAYS, plays);
        context.getContentResolver().update(DBProvider.SONGS_URI, values, selection, null);
    }

    public Song getSpecificSong(Context context, String id){
        ArrayList<Song> songs = new ArrayList<>();
        String selection = DBContract.COL_NAME_SONG_ID + " = "+id;
        Cursor cursor = context.getContentResolver().query(DBProvider.SONGS_URI, null, selection, null, null, null);
        cursor.moveToFirst();
        return new Song(
                cursor.getString(DBContract.COL_INDEX_SONG_TITLE),
                cursor.getString(DBContract.COL_INDEX_SONG_ARTIST),
                cursor.getString(DBContract.COL_INDEX_SONG_ALBUM),
                cursor.getString(DBContract.COL_INDEX_SONG_GENRE),
                cursor.getInt(DBContract.COL_INDEX_SONG_PLAYS),
                cursor.getInt(DBContract.COL_INDEX_SONG_RATING),
                cursor.getString(DBContract.COL_INDEX_SONG_ID)
        );
    }

    public void setUser(Context context, User user){
        context.getContentResolver().delete(DBProvider.USER_URI,null,null);
        ContentValues values = new ContentValues();
        values.put(DBContract.COL_NAME_USER_NAME, user.getUsername());
        values.put(DBContract.COL_NAME_USER_PASS, user.getPassword());
        values.put(DBContract.COL_NAME_USER_NUM, user.getLibSize());

        context.getContentResolver().insert(DBProvider.USER_URI, values);
        Log.d(DBContract.DBTag, "Setting user to "+user.getUsername()+" with " +user.getLibSize()+" songs in lib");
    }

    public boolean hasUser(Context context){
        try {
            Cursor cursor = context.getContentResolver().query(DBProvider.USER_URI, null, null, null, null, null);
            cursor.moveToFirst();
            String username = cursor.getString(DBContract.COL_INDEX_USER_NAME);
            return true;
        } catch(android.database.CursorIndexOutOfBoundsException ex){
            return false;
        }
    }

    public User getUser(Context context){
        try {
            Cursor cursor = context.getContentResolver().query(DBProvider.USER_URI, null, null, null, null, null);
            cursor.moveToFirst();
            return new User(
                        cursor.getString(DBContract.COL_INDEX_USER_NAME),
                        cursor.getString(DBContract.COL_INDEX_USER_PASS),
                        cursor.getInt(DBContract.COL_INDEX_USER_NUM));
        } catch(android.database.CursorIndexOutOfBoundsException ex){
            Log.e(DBContract.DBTag,"attempted to query DB for a null user");
            ex.printStackTrace();
            return null;
        }
    }

    public int getNumOfSongs(Context context){
        DBHelper helper = new DBHelper(context);
        Cursor cursor = new DBHelper(context).getReadableDatabase().
                rawQuery("SELECT  * FROM " + DBContract.TABLE_NAME_SONGS, null);
        int count = cursor.getCount();
        Log.d(DBContract.DBTag, "There are "+count+" songs in the database");
        return count;
    }

}
