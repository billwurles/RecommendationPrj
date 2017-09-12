package database;

public class Song implements Comparable{
    private final String title;
    private final String artist;
    private final String album;
    private final String genre;
    private int plays;
    private int rating;
    private String id;

    public Song(String title, String artist, String album, String genre, int plays, int rating, String id) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.plays = plays;
        this.rating = rating;
        this.id = id;
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
    
    public void resetPlays() {
        plays = 0;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    public String getId() {
        return id;
    }
    
    public void updateId(int id){
        this.id = Integer.toString(id);
    }
    
    @Override
    public String toString(){
        return artist + ", " + title + ", " + album + ", "+ genre+ ", " + plays + ", "+rating;
    }

    @Override
    public int compareTo(Object comp) {
        int compareRating = ((Song)comp).getRating();
        return compareRating-this.rating;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (obj instanceof Song){
            Song song = (Song) obj;
            if(song.title.equalsIgnoreCase(this.title) && song.artist.equalsIgnoreCase(this.artist)){
                return true;
            }
        }
        return equals;
    }
}
