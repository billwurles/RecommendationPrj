package main;

import database.DBAccess;
import database.Genre;
import database.GenreMap;
import database.Song;
import database.User;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import musicAPI.SpotifySearch;
import recommend.Similarity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Will
 */
public class RecommendationSystem {
    
    public static DBAccess db = new DBAccess();
    public static void main(String[] args) throws IOException {
//        args = new String[6];
//        args[0] = "/Users/Will/Documents/Dissertation/System/RecommendationSystem/xml/";
//        args[1] = "7";
//        args[2] = "stype=track?username=theone?password=I+JYGIba3nCseoDGHIzY4g=?id=7dGJo4pcD2V6oG8kP0tJRR?name=Lose%20Yourself?artist=Eminem?album=SHADYXV?pop=49";
//        args[2] = "stype=track?username=will?password=I+JYGIba3nCseoDGHIzY4g==?id=0epOFNiUfyON9EYx7Tpr6V?name=Someday?artist=The%20Strokes?album=Is%20This%20It?pop=67";
//        args[2] = "username=will?password=I+JYGIba3nCseoDGHIzY4g==?id=2296";
//        args[2] = "username=testab?password=I+JYGIba3nCseoDGHIzY4g==";
//        args[2] = "will/track/1uNFoZAHBGtllmzznpCI3s?name=Baby?artist=Justin";
////        args[2] = "will/572";
        GenreMap map;
        if(args.length > 0){
            PrintWriter consoleOut = new PrintWriter(new java.io.OutputStreamWriter(System.out, StandardCharsets.UTF_8));
            args[2] = args[2].replace("%20", " ");
            int option = Integer.parseInt(args[1]);
            switch(option){
                case 0:{ // get all songs in db
                    ArrayList<Song> songs = db.getAllSongs();
                    JSONObject json = toJSONSongArray(songs);
                    consoleOut.println(json.toJSONString());
                    break;
                }
                case 1:{ // get all songs from a genre
                    String genre = args[2];
                    ArrayList<Song> songs = db.getSongsByGenre(genre, new Song("","","","",0,0,""));
                    JSONObject json = toJSONSongArray(songs);
                    consoleOut.println(json.toJSONString());
                    break;
                }
                case 2: { // recommend from specific song
                    map = new GenreMap(args[0]);
                    int id = Integer.parseInt(args[2]);
                    Song song = db.getSongByID(id);
                    Similarity sim = new Similarity(map);
                    ArrayList<Song> recommended = sim.recommendSongs(song, 10);
                    JSONObject json = toJSONSongArray(recommended);
                    consoleOut.println(json.toJSONString());
                    break;
                }
                case 3: { // get songs from a specific artist
                    String artist = args[2];
                    ArrayList<Song> songs = db.getSongsByArtist(artist);
                    JSONObject json = toJSONSongArray(songs);
                    consoleOut.println(json.toJSONString());
                    break;
                }
                case 4: { // add a user to the db
                    String[] split = args[2].split("=", 2);
                    split = split[1].split("\\?password=");
                    String username = split[0];
                    String pass = decrypt(split[1]);
                    if(db.addUser(username, pass)){
                        consoleOut.println("Response 200: user "+username+" added");
                    } else {
                        consoleOut.println("Error 1062: duplicate username: "+username);
                    }
                    break;
                }
                case 5: { // get a user from the db
                    String[] split = args[2].split("=", 2);
                    split = split[1].split("\\?password=");
                    String username = split[0];
                    String pass = decrypt(split[1]);
                    User user = db.getUser(username, pass);
                    if(user.getLibSize() == -402){
                        consoleOut.println("{\"server-response\":-402,\"libsize\":0,\"username\":\"invaliduser\"}");
                    } else if(user.getLibSize() == -403){
                        consoleOut.println("{\"server-response\":-403,\"libsize\":0,\"username\":\"invalidpass\"}");
                    } else if (user.getLibSize() == -500) {
                        consoleOut.println("{\"server-response\":-500,\"libsize\":0,\"username\":\"internalerror\"}");
                    } else {
                        JSONObject userObject = userToJSON(user);
                        consoleOut.println(userObject.toString());
                    }
                    break;
                }
                case 6: { // get a specific users library
                    String[] split = args[2].split("=", 2);
                    split = split[1].split("\\?password=");
                    String username = split[0];
                    String pass = decrypt(split[1]);
                    User user = db.getUser(username, pass);
                    if(user.getLibSize() == -402){
                        consoleOut.println("{\"server-response\":-402,\"libsize\":0,\"username\":\"invaliduser\"}");
                    } else if(user.getLibSize() == -403){
                        consoleOut.println("{\"server-response\":-403,\"libsize\":0,\"username\":\"invalidpass\"}");
                    } else {
                        consoleOut.println(db.getUsersLibrary(username));
                    }
                    break;
                }
                case 7: { // add songs to the library - globally and for a user
                    String[] split = args[2].split("stype="); // before the / is search type, after is id of search item then genre
                    split = split[1].split("\\?username=");
                    String searchType = split[0]; // stype=    recommended   ?username=     testaa?password=I+JYGIba3nCseoDGHIzY4g==?id=216
                    split = split[1].split("\\?password=");
                    final String user = split[0];
                    split = split[1].split("\\?id=");
                    String pass = decrypt(split[0]);
                    User userCheck = db.getUser(user, pass);
                    if(userCheck.getLibSize() == -402){
                        consoleOut.println("{\"server-response\":-402,\"libsize\":0,\"username\":\"invaliduser\"}");
                    } else if(userCheck.getLibSize() == -403){
                        consoleOut.println("{\"server-response\":-403,\"libsize\":0,\"username\":\"invalidpass\"}");
                    } else {
                        final ArrayList<Song> userLib = new ArrayList<>();
                        Thread getLibThread = new Thread(new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    String libJSON = db.getUsersLibrary(user);
                                    JSONParser parse = new JSONParser();
                                    JSONArray songs = (JSONArray) ((JSONObject) parse.parse(libJSON)).get("songs");
                                    for(int i = 0; i < songs.size(); i++){
                                        JSONObject song = (JSONObject) songs.get(i);
                                        Song newSong = new Song(
                                                song.get("title").toString(),
                                                song.get("artist").toString(),
                                                song.get("album").toString(),
                                                song.get("genre").toString(),
                                                Integer.parseInt(song.get("plays").toString()),
                                                Integer.parseInt(song.get("rating").toString()),
                                                song.get("id").toString()
                                        );
                                        userLib.add(newSong);
                                    }
                                } catch (ParseException ex) {
                                    Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });
                        getLibThread.start();
                        SpotifySearch search = new SpotifySearch();
                        ArrayList<Song> songList = new ArrayList<>();
                        boolean cont = false;
                        switch(searchType){
                            case "artist":{ // add all songs from an artist
                                split = split[1].split("\\?genre="); // splits apart the id and genre
                                String id = split[0];
                                String genre = split[1];
                                songList = search.getAllSongsFromArtist(id, genre);
                                cont = true;
                                break;
                            }
                            case "album":{ // add all songs from an album
                                map = new GenreMap(args[0]);
                                split = split[1].split("\\?album="); // splits off the id
                                String id = split[0];
                                String album = split[1];
                                songList = search.getAllSongsFromAlbum(id, null, album, null, map);
                                cont = true;
                                break;
                            }
                            case "track":{ // add a specific song
                                map = new GenreMap(args[0]);
                                split = split[1].split("\\?name="); // name of the track
                                String id = split[0];
                                split = split[1].split("\\?artist="); // name of the artist
                                String track = split[0];
                                split = split[1].split("\\?album="); // name of the album
                                String artist = split[0];
                                split = split[1].split("\\?pop="); // popularity score of the song
                                String album = split[0];
                                int pop = Integer.parseInt(split[1]);
                                String genre = search.getGenre(id, map);
                                songList.add(new Song(
                                        track, artist, album,
                                        genre, 0, pop, id));
                                cont = true;
                                break;
                            }
                            case "recommended":{
                                try {
                                    getLibThread.join();
                                    int songID = Integer.parseInt(split[1]);
                                    songList.add(db.getSongByID(songID));
                                    cont = true;
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                        try {
                            if(cont){
                                getLibThread.join();
                                for(Song song : songList){
                                    int pos;
                                    if((pos = db.checkExists(song.getTitle(), song.getArtist())) != -404){
                                        song = db.getSongByID(pos);
                                        song.resetPlays();
                                    } else {
                                        db.insertSong(song);
                                    }
                                    if(!userLib.contains(song)){
                                        pos = db.checkExists(song.getTitle(), song.getArtist());
                                        song.updateId(pos);
                                        userLib.add(song);
                                    }
                                }
                                JSONObject songsObject = toJSONSongArray(userLib);
                                db.setUserLibrary(user, songsObject.toString(), userLib.size());
                                consoleOut.println(db.getUsersLibrary(user));
                            } else consoleOut.println("User Search Error 500: Internal Server Error");
                        } catch (InterruptedException ex) {
                            Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                }
                case 8:{ // add a play for a song on the global database and the users own library
                    try { //username=    testaa   ?password=   I+JYGIba3nCseoDGHIzY4g=?id=2824
                        String[] split = args[2].split("=", 2);
                        split = split[1].split("\\?password=");
                        String user = split[0];
                        split = split[1].split("\\?id=");
                        String pass = decrypt(split[0]);
                        String songID = split[1];
                        User userCheck = db.getUser(user, pass);
                        if(userCheck.getLibSize() == -402){
                            consoleOut.println("{\"server-response\":-402,\"libsize\":0,\"username\":\"invaliduser\"}");
                        } else if(userCheck.getLibSize() == -403){
                            consoleOut.println("{\"server-response\":-403,\"libsize\":0,\"username\":\"invalidpass\"}");
                        } else {
                            JSONParser parser = new JSONParser();
                            JSONObject userLib = (JSONObject) parser.parse(db.getUsersLibrary(user));
                            JSONArray songs = (JSONArray) userLib.get("songs");
                            int libSize = Integer.parseInt(userLib.get("numResult").toString());
                            db.addSongPlay(songID);
                            for(int i = 0; i < songs.size(); i++){
                                JSONObject song = (JSONObject) songs.get(i);
                                if(song.get("id").toString().equals(songID)){
                                    int plays = Integer.parseInt(song.get("plays").toString());
                                    plays++;
                                    song.replace("plays", plays);
                                }
                            }
                            if(db.setUserLibrary(user, userLib.toString(), libSize)) consoleOut.println("201 Success");
                            else consoleOut.println("501 Internal Server Error");
                        }
                        } catch (ParseException ex) {
                            Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    break;
                }
            }
            consoleOut.flush();
        }
    }
    public static String decrypt(String value){
        String decrypted = null;
        try {
            SecretKeySpec spec = new SecretKeySpec("j34F0iS3gh12Vxz7".getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, spec, cipher.getParameters());
            byte[] decoded = Base64.getDecoder().decode(value);
            byte[] decryptBytes = cipher.doFinal(decoded);
            decrypted = new String(decryptBytes);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return decrypted;
    }
    public static JSONObject userToJSON(User user){
        JSONObject newUser = new JSONObject();
        newUser.put("username", user.getUsername());
        newUser.put("libsize", user.getLibSize());
        newUser.put("server-response",201);
        return newUser;
    }
    public static JSONObject toJSONObject(Song song){
        JSONObject newSong = new JSONObject();
                newSong.put("title", song.getTitle());
                newSong.put("artist", song.getArtist());
                newSong.put("album", song.getAlbum());
                newSong.put("genre", song.getGenre());
                newSong.put("plays", song.getPlays());
                newSong.put("rating", song.getRating());
                newSong.put("id", song.getId());
        return newSong;
    }
    public static JSONObject toJSONSongArray(ArrayList<Song> songList){
        JSONObject json = new JSONObject();
        json.put("numResult", songList.size());
        JSONArray songArray = new JSONArray();
        for(Song song : songList){
            songArray.add(toJSONObject(song));
        }
        json.put("songs", songArray);
        return json;
    }
}
