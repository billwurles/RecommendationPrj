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
    
    private static final String DISCOGS_USER_TOKEN = "oUnYKlqLfKNJnPqESvCzBqSzOFRlhiukfdDYhYPW";
    private static final String CONSUMER_KEY = "nWNpDxBlgvyuSQAddyZz";
    private static final String CONSUMER_SECRET = "nMKEfyrhhdVYgzrVsbwIgPBCVKUuykvX";
    
    public static final String BASE_URL = "https://api.discogs.com/";
    public static final String AUTH = "&key="+CONSUMER_KEY+"&secret="+CONSUMER_SECRET;
    
    public static final String SEARCH = "database/search?q=";
    
}
