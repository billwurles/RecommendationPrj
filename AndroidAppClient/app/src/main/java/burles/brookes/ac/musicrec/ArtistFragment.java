package burles.brookes.ac.musicrec;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import burles.brookes.ac.musicrec.db.LibraryDAO;
import burles.brookes.ac.musicrec.Utils.Song;


public class ArtistFragment extends DialogFragment{
    ArrayList<String> albumList;
    String artist;

    ListView albumView;
    ArrayAdapter<String> albumAdapter;

    String FragTag = LoginActivity.Tag+"Frag";

    private final static int VIEW_USERS_TASKS = 2;

    @Override
    public void setArguments(Bundle args) {
        artist = args.getString("artist");
        albumList = (ArrayList<String>) args.get("albums");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container);
        getDialog().setTitle(artist);

        albumView = (ListView) view.findViewById(R.id.album_list);
        albumAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, albumList);
        albumView.setAdapter(albumAdapter);

        Log.d(FragTag, "ArtistFragment is going : "+albumList.size()+" albums "+albumAdapter.getCount());

        albumView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LibraryDAO db = new LibraryDAO();
                String album = albumList.get(position);
                ArrayList<Song> songList = db.getSongsFromAlbum(getActivity().getBaseContext(), album);

                AlbumFragment albumFragment = new AlbumFragment();
                Bundle args = new Bundle();
                args.putString("album",album);
                args.putParcelableArrayList("songs",songList);
                albumFragment.setArguments(args);
                albumFragment.show(getFragmentManager(), "album_fragment");
            }
        });
        return view;
    }
}
