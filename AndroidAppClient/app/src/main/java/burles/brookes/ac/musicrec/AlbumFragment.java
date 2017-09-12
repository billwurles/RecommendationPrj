package burles.brookes.ac.musicrec;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import burles.brookes.ac.musicrec.Utils.Song;
import burles.brookes.ac.musicrec.Utils.SongAdapter;


public class AlbumFragment extends DialogFragment {
    ArrayList<Song> songList;
    String album;

    ListView songView;
    SongAdapter songAdapter;

    String FragTag = LoginActivity.Tag+"Frag";

    @Override
    public void setArguments(Bundle args) {
        album = args.getString("album");
        songList = (ArrayList<Song>) args.get("songs");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container);
        getDialog().setTitle(album);

        songView = (ListView) view.findViewById(R.id.album_list);
        songAdapter = new SongAdapter(getActivity().getBaseContext(), R.layout.song_list_small_layout, songList);
        songView.setAdapter(songAdapter);

        Log.d(FragTag, "ArtistFragment is going : "+songList.size()+" albums ");

        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = songList.get(position);
                Log.d(FragTag,"Activity: Get Recommendation for "+song);
                Intent intent = new Intent(getActivity().getBaseContext(), SongActivity.class);
                intent.putExtra("song",song);
                startActivity(intent);
            }
        });
        return view;
    }
}
