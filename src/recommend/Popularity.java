/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recommend;

import database.DBAccess;
import database.GenreMap;
import database.Song;
import java.util.ArrayList;
import musicAPI.SearchReturn;
import musicAPI.SpotifySearch;

public class Popularity {
    GenreMap map;
    SpotifySearch srch;
    Similarity sim;
    
    public Popularity(GenreMap map, Similarity sim){
        this.map = map;
        this.sim = sim;
        this.srch = new SpotifySearch();
    }
    
    public ArrayList<Song> rateByPopularity(ArrayList<Song> songs){
        int avg = 0;
        int cnt = 0;
        for(Song song : songs){
            avg+=song.getPlays();
            cnt++;
        }
        for(Song song : songs){
            if(song.getRating() == -1){
                SearchReturn returned = srch.getPopularity(song);
                if(!returned.wasSuccess()){
                    song.setRating(song.getPlays());
                    continue;
                } else {
                    DBAccess db = new DBAccess();
                    db.updateSongRating(song, returned.getSong().getRating());
                }
            }
            song.setRating(song.getRating()+(song.getRating()/2));
            int plays = song.getPlays();
            int bonus = 0;
            if(plays >= (avg + 30)){
                bonus += 30;
            } else if (plays >= avg){
                bonus += (plays - avg);
            }
            if(plays <= 10){
                song.setRating(song.getRating() + plays);
                continue;
            } else if(plays > 10 && plays <= 25){
                song.setRating(song.getRating() + (plays * 2));
            } else if(plays > 25 && plays <= 85){
                song.setRating(song.getRating() + (plays + (plays / 2)));
            } else if(plays > 100){
                song.setRating(song.getRating() + plays / 2);
            } else {
                song.setRating(song.getRating() + plays);
            }
        }
        return songs;
    }
    
}
