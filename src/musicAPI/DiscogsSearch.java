///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package musicAPI;
//
//import itunesxml.Song;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.net.URLEncoder;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//import sun.net.www.http.HttpClient;
//
///**
// *
// * @author Will
// */
//public class DiscogsSearch {
//    
//    URL url;
//    
//    public Song search(Song song){
//        try {
//            String urlstr = song.getTitle() + " " + song.getArtist();
//            url = new URL(APIContract.SEARCH + URLEncoder.encode(urlstr, "UTF-8"));
//            System.out.println(url.toString());
//            
//            URLConnection conn = url.openConnection();
//            conn.addRequestProperty("consumerKey", APIContract.CONSUMER_KEY);
//            conn.addRequestProperty("consumerSecret", APIContract.CONSUMER_SECRET);
//            conn.addRequestProperty("User-Agent", APIContract.USER_AGENT);
//            BufferedReader in = new BufferedReader(new InputStreamReader(
//                                        conn.getInputStream()));
//            String inputLine;
//            StringBuilder builder = new StringBuilder();
//            while ((inputLine = in.readLine()) != null) builder.append(inputLine);
//            in.close();
//           
//            JSONParser parser = new JSONParser();
//            JSONObject json = (JSONObject) parser.parse(builder.toString());
//            JSONArray results = (JSONArray) json.get("results");
//            JSONObject result = (JSONObject) results.get(0);
//            Long id = (Long) result.get("id");
//            String idStr = Long.toString(id);
//            
//            url = new URL(APIContract.MASTERS + idStr);
//            System.out.println(url.toString());
//            
//            conn = url.openConnection();
//            conn.addRequestProperty("consumerKey", APIContract.CONSUMER_KEY);
//            conn.addRequestProperty("consumerSecret", APIContract.CONSUMER_SECRET);
//            conn.addRequestProperty("User-Agent", APIContract.USER_AGENT);
//            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            builder = new StringBuilder();
//            while ((inputLine = in.readLine()) != null) builder.append(inputLine);
//            json = (JSONObject) parser.parse(builder.toString());
//            JSONArray tracks = (JSONArray) json.get("tracklist");
//            for(Object track : tracks){
//                JSONObject jTrack = (JSONObject) track;
//            }
//            
//            in.close();
//            
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(DiscogsSearch.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(DiscogsSearch.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ParseException ex) {
//            Logger.getLogger(DiscogsSearch.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return song;
//    }
//    
//}
