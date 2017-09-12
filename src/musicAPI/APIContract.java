/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicAPI;

/**
 *
 * @author Will
 */
public class APIContract {
//    // --------- napster
//    private static final String BASE_URL = "http://api.napster.com/v2.1/";
//    
//    public static final String API_KEY = "MWU1MmFmMmUtMDI3Yi00MjZmLWJkZTUtOWRkMzk0MjVlYmUx";
//    public static final String API_SECRET = "MjM0YzVlNzYtOWMyZC00NDRjLThkMWEtNTlkZWE4YTY3ZmUz";
//    
//    public static final String SEARCH = BASE_URL + "search" + "?q=";
//    public static final String GENRE = BASE_URL + "genres/";
//    public static final String S_TRACK = "&type=track";
    
    
    
//     --------- spotify
    public static final String BASE_URL = "https://api.spotify.com/v1";
    
    public static final String CLIENT_ID = "824c54a9eed3479e9cc35e63b553efd0";
    public static final String CLIENT_SECRET = "dd6433d261d1458291e1c85236fdea6f";
    
    public static final String SEARCH = BASE_URL + "/search?q=";
    public static final String ARTISTS = BASE_URL + "/artists/";
    public static final String ALBUMS = "/albums/";
    public static final String TRACKS = "/tracks";
    public static final String S_TRACK = "&type=track";
    
    
    // --------- discogs
//    private static final String DISCOGS_USER_TOKEN = "oUnYKlqLfKNJnPqESvCzBqSzOFRlhiukfdDYhYPW";
//    public static final String CONSUMER_KEY = "YkWdxhfMdgTEYJIkxykr";
//    public static final String CONSUMER_SECRET = "EUxeXEKkPLUOotPfBssxEsBZOuSKvqdy";
//    public static final String USER_AGENT = "14038690RecSys";
//    
//    private static final String BASE_URL = "https://api.discogs.com/";
//    public static final String AUTH = "&key="+CONSUMER_KEY+"&secret="+CONSUMER_SECRET;
//    
//    public static final String SEARCH = BASE_URL + "database/search?q=";
//    public static final String MASTERS = BASE_URL + "masters/";
    
}
