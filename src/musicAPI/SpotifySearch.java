/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicAPI;

import database.GenreMap;
import database.Song;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Will
 */
public class SpotifySearch {
    
    public ArrayList<Song> getAllSongsFromArtist(String id, String genre){
        ArrayList<Song> albumList = new ArrayList<>();
            try {
                URL url = new URL(APIContract.ARTISTS + id + APIContract.ALBUMS);

                URLConnection conn = url.openConnection();
                conn.addRequestProperty("client_id", APIContract.CLIENT_ID);
                conn.addRequestProperty("response_type", "code");
                
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder builder = new StringBuilder();
                while ((inputLine = in.readLine()) != null) builder.append(inputLine);
                in.close();

                JSONObject json = (JSONObject) new JSONParser().parse(builder.toString());

                long num = (long) json.get("total");
                if (num == 0) {
                    return null;
                }

                JSONArray items = (JSONArray) json.get("items");
                for (int i = 0; i < items.size(); i++) {
                    JSONObject item = (JSONObject) items.get(i);
                    JSONObject artist = (JSONObject) ((JSONArray) item.get("artists")).get(0);
                    Song album = new Song(item.get("name").toString(), artist.get("name").toString(),
                            "albumsearched", genre, -1, -1, (String) item.get("id").toString());
                    albumList.add(album);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException ex) {
            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<Thread> threads = new ArrayList<>();
        final ArrayList<Song> newSongList = new ArrayList<>();
        for(final Song album : albumList){
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    newSongList.addAll(getAllSongsFromAlbum(album.getId(), album.getArtist(), album.getTitle(), album.getGenre(), null));
                }
            });
            thread.start();
            threads.add(thread);
        }
        for(Thread thread : threads){
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return newSongList;
    }
    
    public ArrayList<Song> getAllSongsFromAlbum(String id, String artist, String album, String genre, GenreMap map){
        ArrayList<Song> songList = new ArrayList<>();
        try{
            URL url = new URL(APIContract.BASE_URL + APIContract.ALBUMS + id + APIContract.TRACKS);

            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder builder = new StringBuilder();
            while ((inputLine = in.readLine()) != null) builder.append(inputLine);
            in.close();

            JSONObject json = (JSONObject) new JSONParser().parse(builder.toString());

            long num = (long) json.get("total");
            if (num == 0) {
                return null;
            }
            
            
            JSONArray items = (JSONArray) json.get("items");
            if(artist == null){
                JSONObject artistObj = (JSONObject) ((JSONArray) ((JSONObject) items.get(0)).get("artists")).get(0);
                artist = artistObj.get("name").toString();
                url = new URL(artistObj.get("href").toString());
                conn = url.openConnection();
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                builder = new StringBuilder();
                while ((inputLine = in.readLine()) != null) builder.append(inputLine);
                in.close();
                json = (JSONObject) new JSONParser().parse(builder.toString());
                JSONArray genres = (JSONArray) json.get("genres");
                boolean valid = false;
                for(int i = 0; i < genres.size(); i++){
                    genre = genres.get(i).toString();
                    if(map.exists(genre)){
                        valid = true;
                        break;
                    }
                }
                if(!valid){
                    genre = genres.get(0).toString();
                }
            }
            
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = (JSONObject) items.get(i);
                
                Song newSong = new Song(item.get("name").toString(), artist,
                        album, genre, 0, -1, item.get("id").toString());
                songList.add(newSong);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException ex) {
            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return songList;
    }
    
    public String getGenre(String artistID, GenreMap map){
        String genre = null;
        try {
            URL url = new URL(APIContract.ARTISTS + artistID);
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) builder.append(inputLine);
            in.close();
            JSONObject json = (JSONObject) new JSONParser().parse(builder.toString());
            JSONArray genres = (JSONArray) json.get("genres");
            boolean valid = false;
            for(int i = 0; i < genres.size(); i++){
                genre = genres.get(i).toString();
                if(map.exists(genre)){
                    valid = true;
                    break;
                }
            }
            if(!valid){
                genre = genres.get(0).toString();
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return genre;
    }
    
    public SearchReturn getPopularity(Song song){
        try {
            String search = "track:"+URLEncoder.encode(song.getTitle(), "UTF-8");
            if(song.getArtist() != ""){
                search += "+artist:" + URLEncoder.encode(song.getArtist(), "UTF-8");
            }
            URL url = new URL(APIContract.SEARCH + search + APIContract.S_TRACK);
            
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                                        conn.getInputStream()));
            String inputLine;
            StringBuilder builder = new StringBuilder();
            while ((inputLine = in.readLine()) != null) builder.append(inputLine);
            in.close();
            
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(builder.toString());
            JSONObject results = (JSONObject) json.get("tracks");
            long numResults = (long) results.get("total");

            long num = (long) results.get("total");
            if(num == 0){
                return new SearchReturn(null, false);
            }
            
            JSONArray items = (JSONArray) results.get("items");
            JSONObject item = (JSONObject) items.get(0);
            
            song.setRating(Integer.valueOf(Long.toString((long) item.get("popularity"))));
            
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new SearchReturn(song, true);
    }
    
//    public InitSong search(InitSong song){
//        try {
//            String search = "track:"+URLEncoder.encode(song.getTitle(), "UTF-8");
//            if(song.getArtist() != ""){
//                search += "+artist:" + URLEncoder.encode(song.getArtist(), "UTF-8");
//            }
//            if(song.getAlbum() != ""){
//                search += "+album:" + URLEncoder.encode(song.getAlbum(), "UTF-8");
//            }
//            
//            url = new URL(APIContract.SEARCH + search + APIContract.S_TRACK);
////            System.out.println(url.toString());
//            
//            URLConnection conn = url.openConnection();
////            conn.addRequestProperty("client_id", APIContract.CLIENT_ID);
////            conn.addRequestProperty("client_secret", APIContract.CLIENT_SECRET);
//            BufferedReader in = new BufferedReader(new InputStreamReader(
//                                        conn.getInputStream()));
//            String inputLine;
//            StringBuilder builder = new StringBuilder();
//            while ((inputLine = in.readLine()) != null) builder.append(inputLine);
//            //System.out.println(builder.toString());
//            in.close();
//           
//            JSONParser parser = new JSONParser();
//            JSONObject json = (JSONObject) parser.parse(builder.toString());
//            JSONObject results = (JSONObject) json.get("tracks");
//            long numResults = (long) results.get("total");
////            System.out.println(numResults + " entries found");
//            
//            JSONArray items = (JSONArray) results.get("items");
//            JSONObject item = (JSONObject) items.get(0);
//            String title = (String) item.get("name");
//            JSONObject jAlbum = (JSONObject) item.get("album");
//            String album = (String) jAlbum.get("name");
//            JSONObject jArist = (JSONObject) item.get("artists");
//            String artist = (String) jAlbum.get("name");
//            String id = (String) item.get("id");
//            
////            Song searchedSong = new Song(title, artist, album)
//            
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ParseException ex) {
//            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return song;
//    }
}
