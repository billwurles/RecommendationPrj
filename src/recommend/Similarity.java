/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recommend;

import database.DBAccess;
import database.Genre;
import database.GenreMap;
import database.Song;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Will
 */
public class Similarity {
    GenreMap map;
    DBAccess db;
    Popularity pop;
    ArrayList<String> common = new ArrayList<>(Arrays.asList("Rock","Rap","Pop","Electronic"));

    public Similarity(GenreMap map) {
        this.map = map;
        this.db = new DBAccess();
        this.pop = new Popularity(map, this);
    }
    
    public ArrayList<Song> recommendSongs(Song song, int scope){
        Genre start = new Genre(song.getGenre());
        start.setWeight(0);
        ArrayList<Genre> relations = map.getLowestGenres(start, new ArrayList<Genre>());
        ArrayList<Genre> usedGenres = new ArrayList<>();
        ArrayList<Genre> visited = new ArrayList<>();
        visited.add(start);
        ArrayList<Song> recommended = db.getSongsByGenre(song.getGenre(), song);
        int x = 0;
        for(Genre g : relations){
            if(x > 2){
                break;
            } else {
                if(!common.contains(g)){
                    recommended.addAll(db.getSongsByGenre(g.getName(), song));
                    recommended = pop.rateByPopularity(recommended);
                    for(Song s : recommended){
                        s.setRating(g.simMod(s.getRating()));
                    }
                    usedGenres.add(g);
                    visited.add(g);
                    x++;
                }
            }
        }
        
//        recommended = pop.rateByPopularity(recommended);
        int genreNum = 0;
        boolean complete = false;
        Collections.sort(recommended);
        
        while(!complete){
            if(recommended.size() < 20){
                ArrayList<Song> newSimilarSongs = new ArrayList<>();
                if(genreNum == relations.size()){
                    for(Genre genre : relations){
                        if(!visited.contains(genre)){
                            relations = map.getLowestGenres(genre, relations);
                            visited.add(genre);
                            break;
                        }
                    }
                }
                for(Genre g : relations){
                    if(!usedGenres.contains(g)){
                        newSimilarSongs = db.getSongsByGenre(g.getName(), song);
                        newSimilarSongs = pop.rateByPopularity(newSimilarSongs);
                        for(Song s : recommended){
                            s.setRating(g.simMod(s.getRating()));
                        }
                        recommended.addAll(newSimilarSongs);
                        Collections.sort(recommended);
                        usedGenres.add(g);
                        genreNum++;
                        break;
                    }
                }
            } else {
                ArrayList<Song> popular = new ArrayList<>();
                for(Song s : recommended){
                    if(s.getRating() > 85){
                        popular.add(s);
                    }
                }
                recommended = popular;
                if(recommended.size() == 20){
                    complete = true;
                } else if(recommended.size() > 20) {
                    List<Song> tempList = recommended.subList(0, 20);
                    recommended = new ArrayList<>();
                    recommended.addAll(tempList);
                    complete = true;
                }
            }
        }
        return recommended;
    }
}
