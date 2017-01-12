/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicAPI;

import itunesxml.Song;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.net.www.http.HttpClient;

/**
 *
 * @author Will
 */
public class DiscogsSearch {
    
    Song song;
    URL url;
    
    
    public DiscogsSearch(Song song){
        this.song = song;
    }
    
    public Song search(){
        try {
            String urlstr = song.getTitle() + " " + song.getArtist() + " " + song.getAlbum();
            url = new URL(APIContract.BASE_URL + APIContract.SEARCH +
                            URLEncoder.encode(urlstr, "UTF-8") + APIContract.AUTH);
            System.out.println(url.toString());
            
            HttpClient client = new DefaultHttpClient();
            HttpGet request =  new HttpGet("http://api.discogs.com/artist/" + name);
            request.addHeader("Accept-Encoding" , "gzip");
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String jsonObject = EntityUtils.toString(entity); 
            
            
            
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            String input;
            while((input = reader.readLine()) != null){
                System.out.println(input);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(DiscogsSearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DiscogsSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return song;
    }
    
}
