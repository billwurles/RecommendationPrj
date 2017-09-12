package burles.brookes.ac.musicrec.Utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Comparable, Parcelable{
    private final String title;
    private final String artist;
    private final String album;
    private final String genre;
    private final int plays;
    private int rating;
    private final String id;

    public Song(String title, String artist, String album, String genre, int plays, int rating, String id) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.plays = plays;
        this.rating = rating;
        this.id = id;
    }

    protected Song(Parcel in) {
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        genre = in.readString();
        plays = in.readInt();
        rating = in.readInt();
        id = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

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

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString(){
        return title;
    }

    public String debug(){
        return artist + ", " + title + ", " + album + ", "+ genre+ ", " + plays + ", "+rating + ", "+id;
    }

    @Override
    public int compareTo(Object comp) {
        int compareRating = ((Song)comp).getRating();
        return compareRating-this.rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeString(album);
        parcel.writeString(genre);
        parcel.writeInt(plays);
        parcel.writeInt(rating);
        parcel.writeString(id);
    }
}
