/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import database.DBAccess;
import database.Genre;
import database.GenreMap;
import database.Song;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Will
 */
public class ITunesRead {
    private FileInputStream fis = null;
    private DBAccess db;
    private GenreMap map;
    
    private String titleRegExp = "(\\s)*<key>Name</key><string>.*</string>";
    private String artistRegExp = "(\\s)*<key>Artist</key><string>.*</string>";
    private String albumRegExp = "(\\s)*<key>Album</key><string>.*</string>";
    private String genreRegExp = "(\\s)*<key>Genre</key><string>.*</string>";
    private String playsRegExp = "(\\s)*<key>Play Count</key><integer>[0-9]*</integer>";
    private String songEndRegExp = "(\\s)*</dict>";
    
    public static FileInputStream openStream(String fileName) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        System.out.println("File input stream created");
        return fis;
    }

    public ITunesRead(GenreMap map) {
        this.db = new DBAccess();
        this.map = map;
    }
    
    public ArrayList<Song> addXMLToDB(String fileName) throws IOException{
        ArrayList<Song> songList = new ArrayList<>();
        ArrayList<Song> needsGenre = new ArrayList<>();
        ArrayList<String> newGenres = new ArrayList<>();
        try {
            fis = openStream(fileName);
            Scanner scan = new Scanner(fis);
            String read;
            String title = "";
            String artist = "";
            String album = "";
            String genre = "";
            int plays = 0;
            while(scan.hasNext()){
                read = scan.nextLine();
                read = read.replaceAll("\"", "'").replaceAll("&#38;", "&");
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
                } else if(read.matches(songEndRegExp)){
                    if(!title.equals("") && !artist.equals("")){
                        if(db.checkExists(title, artist) == -404){
                            if(!genre.equals("")){
                                if(!map.exists(genre)){
                                    if(!newGenres.contains(genre)){
                                        newGenres.add(genre);
                                    }
                                    Song newSong = new Song(title, artist, album,
                                                    genre, plays, -1, "noid");
                                    needsGenre.add(newSong);
                                } else {
                                    Song newSong = new Song(title, artist, album,
                                                    genre, plays, -1, "noid");
                                    songList.add(newSong);
                                    db.insertSong(newSong);
                                }
                                
                            } else {
                                Song newSong = new Song(title, artist, album,
                                                    "", plays, -1, "noid");
                                needsGenre.add(newSong);
                            }
                            
                        }
                        title = "";
                        artist = "";
                        album = "";
                        genre = "";
                        plays = 0;
                    }
                }
            }
            fis.close();
        } catch (FileNotFoundException ex){
          System.out.println("Whoops: FileNotFoundException caught: no such file");
        } catch (NullPointerException ex){
            System.out.println("Error: NullPointerException caught: null file");
        } catch (IOException ex) {
            Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
//        needsGenre = addGenres(needsGenre);
        ArrayList<String> discarded = map.addMultipleGenres(newGenres);
        ArrayList<Song> needsUpdate = new ArrayList<>();
        for(String discard : discarded){
            needsUpdate.addAll(db.getSongsByGenre(discard, new Song("","","",null,0,0,"")));
        }
        System.out.printf("There are %d genres over %d songs that need updating%n",discarded.size(),needsUpdate.size());
        
        modifyGenres(needsUpdate);
        return songList;
    }
    public void modifyGenres(ArrayList<Song> unmodified) throws IOException{
        HashMap<String, String> artistGenreMap = new HashMap<>();
        for(Song song : unmodified){
            String mappedGenre = artistGenreMap.get(song.getArtist());
            if(mappedGenre != null){
                db.updateSongGenre(song, mappedGenre);
                System.out.println("Updated "+song.getTitle()+" with genre "+mappedGenre);
                continue;
            }
            ArrayList<String> genres = db.getGenresByArtists(song.getArtist());
            System.out.println("\n"+song.getTitle()+" by: "+song.getArtist()+" "+song.getAlbum()
                    +"\nCurrent Genre is "+song.getGenre());
            if(genres.size() > 0){
                System.out.print(song.getArtist()+" is associated with these genres: \n");
                for(String s : genres){
                    System.out.print(s + "\t");
                }
                System.out.println("\n");
            }
            boolean valid = false;
            do {
                System.out.print("Type new genre: ");
                Scanner scan = new Scanner(System.in);
                String input = scan.nextLine();
                if(map.exists(input)){
                    db.updateSongGenre(song, input);
                    System.out.print("Do you want to associate this genre with all songs from "+song.getArtist()+
                                        "? Y/N: ");
                    String genre = input;
                    input = scan.nextLine();
                    if(input.equalsIgnoreCase("y")){
                        artistGenreMap.put(song.getArtist(), genre);
                    }
                    valid = true;
                } else if(input.equals("listallgenres")){
                    System.out.print("\n");
                    for(Genre genre : map.getGenres()){
                        System.out.print(genre.getName() + "\t");
                    }
                    System.out.println("\n");
                } else { System.out.print("You did not enter a valid genre in the DB \n"
                                        + "type 'listallgenres' to see a full list \n"
                                        + "or would you like to add this genre? Y/N: ");
                    String genre = input;
                    input = scan.nextLine();
                    if(input.equalsIgnoreCase("y")){
                        input = map.addNewGenre(genre);
                        map.loadMap();
                        if(input == null){
                            db.updateSongGenre(song, genre);
                            System.out.print("Do you want to associate this genre with all songs from "+song.getArtist()+
                                        "? Y/N: ");
                            input = scan.nextLine();
                            if(input.equalsIgnoreCase("y")){
                                artistGenreMap.put(song.getArtist(), genre);
                            }
                            valid = true;
                        } else System.out.println("You discarded the genre");
                    }
                }
            } while (!valid);
        }
    }
    public ArrayList<Song> addGenres(ArrayList<Song> needsGenre){
        ArrayList<Song> genred = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
        System.out.print("There are "+needsGenre.size()+" songs that need Genres\n"
                                + "Would you like to skip or add genres? S/A: ");
        String input = scan.next();
        if(input.equals("S")){
            return genred;
        }
        for(Song song : needsGenre){
            if(map.exists(song.getGenre())){
                genred.add(song);
            }
            System.out.println(song.getTitle()+", by "+song.getArtist()+" "+song.getAlbum()
            +"\nGenre: ");
            input = scan.next();
            Genre genre = map.getGenre(input);
            if(genre != null){
                genred.add(new Song(song.getTitle(), song.getArtist(), song.getAlbum(),
                                    input, song.getPlays(), song.getRating(), "noid"));
            }
        }
        return genred;
    }
//    public ArrayList<InitSong> read(String fileName){
//        try {
//            fis = openStream(fileName);
//            Scanner scan = new Scanner(fis);
//            String read;
//            String title = "";
//            String artist = "";
//            String album = "";
//            String genre = "";
//            int plays = 0;
//            while(scan.hasNext()){
//                read = scan.nextLine();
//                if(read.matches(titleRegExp)){
//                    title = xmlParse(read, 0);
//                } else if(read.matches(artistRegExp)){
//                    artist = xmlParse(read, 0);
//                } else if(read.matches(albumRegExp)){
//                    album = xmlParse(read, 0);
//                } else if(read.matches(genreRegExp)){
//                    genre = xmlParse(read, 0);
//                } else if(read.matches(playsRegExp)){
//                    plays = Integer.parseInt(xmlParse(read, 1));
//                } else if(read.matches(songEndRegExp)){
//                    if(!title.equals("")){
//                        songList.add(new InitSong(
//                                    title, artist, album,
//                                    genre, plays, -1));
//                        title = "";
//                        artist = "";
//                        album = "";
//                        genre = "";
//                        plays = 0;
//                    }
//                }
//            }
//            fis.close();
//        } catch (FileNotFoundException ex){
//          System.out.println("Whoops: FileNotFoundException caught: no such file");
//        } catch (NullPointerException ex){
//            System.out.println("Error: NullPointerException caught: null file");
//        } catch (IOException ex) {
//            Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return songList;
//    }
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
