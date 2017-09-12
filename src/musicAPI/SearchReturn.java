/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicAPI;

import database.Song;

/**
 *
 * @author Will
 */
public class SearchReturn {
    private final Song song;
    private final boolean success;

    public SearchReturn(Song song, boolean success) {
        this.song = song;
        this.success = success;
    }

    public Song getSong() {
        return song;
    }

    public boolean wasSuccess() {
        return success;
    }
    
    
}
