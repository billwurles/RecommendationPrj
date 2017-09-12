package burles.brookes.ac.musicrec.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import burles.brookes.ac.musicrec.R;

public class SongAdapter extends ArrayAdapter<Song> {

    private Context context;
    private int layout;
    private ArrayList<Song> songList;

    public SongAdapter(Context context, int resource, ArrayList<Song> songList) {
        super(context, resource, songList);
        this.context = context;
        this.layout = resource;
        this.songList = songList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(layout, parent, false);
        }
        TextView textTitle = (TextView) view.findViewById(R.id.text_list_title);
        TextView textPlays = (TextView) view.findViewById(R.id.text_list_plays);
        Song song = songList.get(position);
        if(layout == R.layout.song_list_layout) {
            TextView textArtist = (TextView) view.findViewById(R.id.text_list_artist);
            TextView textAlbum = (TextView) view.findViewById(R.id.text_list_album);
            TextView textGenre = (TextView) view.findViewById(R.id.text_list_genre);
            textArtist.setText(song.getArtist());
            textAlbum.setText(song.getAlbum());
            textGenre.setText(song.getGenre());
        }
        textTitle.setText(song.getTitle());
        textPlays.setText(String.valueOf(song.getPlays()));
        return view;
    }
}
