/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itunesxml;

/**
 *
 * @author Will
 */
public class Song {
    private String title;
    private String artist;
    private String album;
    private String genre;
    private int plays;
    private int rating;

    public Song(String title, String artist, String album, String genre, int plays, int rating) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.plays = plays;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public int getPlays() {
        return plays;
    }

    public int getRating() {
        return rating;
    }
    
    @Override
    public String toString(){
        return artist + ", " + title + ", " + album + ", " + plays;
    }
}
