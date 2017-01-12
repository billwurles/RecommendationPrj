/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itunesxml;

import database.DBAccess;
import database.DBContract;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import musicAPI.DiscogsSearch;

/**
 *
 * @author Will
 */
public class RecommendationSystem {

    public static FileInputStream openStream(String fileName) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        System.out.println("File input stream created");
        return fis;
    }

    public static void main(String args[]) {
        DBAccess v = new DBAccess();
        

        FileInputStream fis = null;
        ArrayList<Song> songList = new ArrayList<>();

        String titleRegExp = "(\\s)*<key>Name</key><string>.*</string>";
        String artistRegExp = "(\\s)*<key>Artist</key><string>.*</string>";
        String albumRegExp = "(\\s)*<key>Album</key><string>.*</string>";
        String genreRegExp = "(\\s)*<key>Genre</key><string>.*</string>";
        String playsRegExp = "(\\s)*<key>Play Count</key><integer>[0-9]*</integer>";
        String ratingsRegExp = "(\\s)*<key>Rating</key><integer>[0-9]*</integer>";
        String songEndRegExp = "(\\s)*</dict>";


        
        String fileName = "minilib.xml";
//        String fileName = "mash.xml";
            
//        String fileName = "library.xml";

        System.out.println("Starting with file name = " + fileName);

        // attempt to get file input stream 
        try {
            fis = openStream(fileName);
            Scanner scan = new Scanner(fis);
            String read;
            String title = "";
            String artist = "";
            String album = "";
            String genre = "";
            int plays = 0;
            int rating = 0;
            while(scan.hasNext()){
                read = scan.nextLine();
                if(read.matches(titleRegExp)){
                    title = xmlParse(read, 0);
                } else if(read.matches(artistRegExp)){
                    artist = xmlParse(read, 0);
                } else if(read.matches(albumRegExp)){
                    album = xmlParse(read, 0);
                } else if(read.matches(genreRegExp)){
                    genre = xmlParse(read, 0);
                } else if(read.matches(playsRegExp)){
                    plays = Integer.parseInt(xmlParse(read, 1));
                } else if(read.matches(ratingsRegExp)){
                    rating = Integer.parseInt(xmlParse(read, 1));
                } else if(read.matches(songEndRegExp)){
                    if(!title.equals("")){
                        songList.add(new Song(
                                    title, artist, album,
                                    genre, plays, rating));
                        title = "";
                        artist = "";
                        album = "";
                        genre = "";
                        plays = 0;
                        rating = 0;
                    }
                }
            }
//            for(Song song : songList){
//                System.out.println(song);
//            }
        }
        catch (FileNotFoundException ex){
          System.out.println("Whoops: FileNotFoundException caught: no such file");
        }
        catch (NullPointerException ex){
            System.out.println("Error: NullPointerException caught: null file");
        }
        catch (IOException ex) {
            Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        DiscogsSearch s = new DiscogsSearch(songList.get(40));
        s.search();

//        try {
            //v.insertSongs(songList);
//        } catch (SQLException ex) {
//            Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }
  
  public static String xmlParse(String input, int flag){
      String type;
      if(flag == 0) type = "string>";
      else type = "integer>";
      String parsed = input.split("</key>")[1];
      parsed = parsed.split("<" + type)[1];
      parsed = parsed.split("</" + type)[0];
      return parsed;
  }

    
}
