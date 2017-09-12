///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package musicAPI;
//
//import database.Song;
//import database.GenreAPI;
//import main.InitSong;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//
///**
// *
// * @author Will
// */
//public class NapsterSearch {
//    public ArrayList<GenreAPI> retrieveGenre(String genURL){
//        try {
//            URL url = new URL(genURL);
//            System.out.println(url.toString());
//            URLConnection conn = url.openConnection();
//            conn.setRequestProperty("apikey", APIContract.API_KEY);
//            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String inputLine;
//            StringBuilder builder = new StringBuilder();
//            while ((inputLine = in.readLine()) != null) builder.append(inputLine);
//            System.out.println(builder.toString());
//            in.close();
//            
//            JSONParser parser = new JSONParser();
//            JSONObject json = (JSONObject) parser.parse(builder.toString());
//            JSONArray genreArray = (JSONArray) json.get("genres");
//            
//            ArrayList<GenreAPI> genres = new ArrayList<GenreAPI>();
//            for(int i = 0; i < genreArray.size(); i++){
//                JSONObject item = (JSONObject) genreArray.get(i);
//                String gName = (String) item.get("name");
//                String gID = (String) item.get("id");
//                JSONObject links = (JSONObject) item.get("links");
//                String[] parentGenres;
//                if(links.containsKey("parentGenres")){
//                    JSONObject parents = (JSONObject) links.get("parentGenres");
//                    JSONArray parentIDs = (JSONArray) parents.get("ids");
//                    parentGenres = new String[parentIDs.size()];
//                    for(int r = 0; i < parentIDs.size(); i++){
//                        parentGenres[r] = (String) parentIDs.get(r);
//                    }
//                } else {
//                    parentGenres = new String[1];
//                    parentGenres[0] = "null";
//                }
//                String[] childGenres;
//                if(links.containsKey("childGenres")){
//                    JSONObject children = (JSONObject) links.get("childGenres");
//                    JSONArray childIDs = (JSONArray) children.get("ids");
//                    childGenres = new String[childIDs.size()];
//                    for(int r = 0; i < childIDs.size(); i++){
//                        childGenres[r] = (String) childIDs.get(r);
//                    }
//                } else {
//                    childGenres = new String[1];
//                    childGenres[0] = "null";
//                }
//                System.out.println("name: " + gName + "\nID: "+gID+ "\npar: "+parentGenres.length+ "\nchild: "+childGenres.length);
//                GenreAPI newGenre = new GenreAPI(gName, gID, parentGenres, childGenres);
//                genres.add(newGenre);
//            }
//            return genres;
//            
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ParseException ex) {
//            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        return null;
//    }
//    
//    public String roldetrieveGenre(ArrayList<String> genres){
//        try {
//            for(int i = 0; i < genres.size(); i++){
//                URL url = new URL(APIContract.GENRE + URLEncoder.encode(genres.get(i), "UTF-8") + APIContract.S_TRACK);
//                System.out.println(url.toString());
//
//                URLConnection conn = url.openConnection();
//                conn.setRequestProperty("apikey", APIContract.API_KEY);
//                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                String inputLine;
//                StringBuilder builder = new StringBuilder();
//                while ((inputLine = in.readLine()) != null) builder.append(inputLine);
//                System.out.println(builder.toString());
//                in.close();
//            }
//            
//            
//            
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
//        } //catch (ParseException ex) {
////            Logger.getLogger(EchonestSearch.class.getName()).log(Level.SEVERE, null, ex);
////        }
//        return "";
//    }
//    
//    public Song search(InitSong song){
//        try {
//            URL url = new URL(APIContract.SEARCH + URLEncoder.encode(song.getArtist()+"+"+song.getTitle(), "UTF-8") + APIContract.S_TRACK);
//            System.out.println(url.toString());
//            
//            URLConnection conn = url.openConnection();
//            conn.setRequestProperty("apikey", APIContract.API_KEY);
//            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String inputLine;
//            StringBuilder builder = new StringBuilder();
//            while ((inputLine = in.readLine()) != null) builder.append(inputLine);
//            System.out.println(builder.toString());
//            in.close();
//           
//            JSONParser parser = new JSONParser();
//            JSONObject json = (JSONObject) parser.parse(builder.toString());
//            JSONArray data = (JSONArray) json.get("data");
//            
//            JSONObject item = (JSONObject) data.get(0);
//            String title = (String) item.get("name");
//            String album = (String) item.get("albumName");
//            String artist = (String) item.get("artistName");
//            String id = (String) item.get("id");
//            JSONObject links = (JSONObject) item.get("links");
//            JSONObject genreList = (JSONObject) links.get("genres");
//            String genreURL = (String) genreList.get("href");
//            
//            ArrayList<GenreAPI> genres = retrieveGenre(genreURL);
//            assert(genres != null);
//            System.out.println("Track: " + title + "\nArtist: "+artist+ "\nAlbum: "+album+ "\nID: "+id);
//            
//            return new Song(title, artist, album, genres, song.getPlays(), song.getRating(), id);
//            
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ParseException ex) {
//            Logger.getLogger(SpotifySearch.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return new Song("searchfailed","searchfailed","searchfailed",null,0,0,"searchfailed");
//    }
//}
