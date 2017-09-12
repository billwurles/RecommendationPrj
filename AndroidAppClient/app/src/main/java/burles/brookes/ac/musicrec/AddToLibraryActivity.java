package burles.brookes.ac.musicrec;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import burles.brookes.ac.musicrec.Utils.Song;
import burles.brookes.ac.musicrec.Utils.SongAdapter;
import burles.brookes.ac.musicrec.Utils.User;
import burles.brookes.ac.musicrec.db.LibraryDAO;

public class AddToLibraryActivity extends AppCompatActivity {

    private Spinner spinnerChoice;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private LibraryDAO db = new LibraryDAO();

    private EditText editSong;
    private EditText editArtist;
    private EditText editAlbum;
    private Button buttonSearch;

    private ListView searchList;
    private ArrayList<Song> songList;
    private SongAdapter songAdapter;

    public static final int RETURN_FROM_ADD_TO_LIB_FLAG = 201;

    private static final String AddTag = LoginActivity.Tag+"Add";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_library);

        spinnerChoice = (Spinner) findViewById(R.id.search_spinner);
        editSong = (EditText) findViewById(R.id.edit_title);
        editArtist = (EditText) findViewById(R.id.edit_artist);
        editAlbum = (EditText) findViewById(R.id.edit_album);
        buttonSearch = (Button) findViewById(R.id.button_search);
        searchList = (ListView) findViewById(R.id.search_list);
        songList = new ArrayList<>();
        songAdapter = new SongAdapter(getBaseContext(), R.layout.song_list_layout, songList);

        spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.search_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChoice.setAdapter(spinnerAdapter);

        spinnerChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editArtist.setHintTextColor(Color.GRAY);
                editAlbum.setHintTextColor(Color.GRAY);
                editSong.setHintTextColor(Color.GRAY);
                songList.clear();
                songAdapter.notifyDataSetChanged();
                switch (i){
                    case 0: // Search for song
                        buttonSearch.setText(R.string.button_search_song);
                        editSong.setEnabled(true);
                        editSong.setHint(R.string.search_title);
                        editArtist.setEnabled(true);
                        editArtist.setHint(R.string.search_artist_opt);
                        editAlbum.setEnabled(true);
                        editAlbum.setHint(R.string.search_album_opt);
                        break;
                    case 1: // Search for artist
                        buttonSearch.setText(R.string.button_search_artist);
                        editSong.setEnabled(false);
                        editSong.setHint(" ");
                        editArtist.setEnabled(true);
                        editArtist.setHint(R.string.search_artist);
                        editAlbum.setEnabled(false);
                        editAlbum.setHint(" ");
                        break;
                    case 2: // Search for album
                        buttonSearch.setText(R.string.button_search_album);
                        editSong.setEnabled(false);
                        editSong.setHint(" ");
                        editArtist.setEnabled(true);
                        editArtist.setHint(R.string.search_artist_opt);
                        editAlbum.setEnabled(true);
                        editAlbum.setHint(R.string.search_album);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spotifySearchTask task = new spotifySearchTask();
                String track = editSong.getText().toString();
                String artist = editArtist.getText().toString();
                String album = editAlbum.getText().toString();
                switch(spinnerChoice.getSelectedItemPosition()){
                    case 0:{ // Search for a song
                        if(track.equals("")){
                            Toast.makeText(getBaseContext(),"You must enter the name of the track",Toast.LENGTH_LONG).show();
                            editSong.setHintTextColor(Color.RED);
                        } else {
                            if(artist.equals("")){
                                artist = null;
                            }
                            if(album.equals("")){
                                album = null;
                            }
                            task.execute(artist,album,track);
                        }
                        break;
                    }
                    case 1:{ // Search for an artist
                        if(artist.equals("")){
                            Toast.makeText(getBaseContext(),"You must enter the artist's name",Toast.LENGTH_LONG).show();
                            editArtist.setHintTextColor(Color.RED);
                        } else {
                            task.execute(artist,null,null);
                        }
                        break;
                    }
                    case 2:{ // Search for an album
                        if(album.equals("")){
                            Toast.makeText(getBaseContext(),"You must enter the name of the album",Toast.LENGTH_LONG).show();
                            editAlbum.setHintTextColor(Color.RED);
                        } else {
                            if(artist.equals("")){
                                artist = null;
                            }
                            task.execute(artist,album,null);
                        }
                        break;
                    }
                }

            }
        });
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Song songToAdd = songList.get(i);
                int addType = spinnerChoice.getSelectedItemPosition();
                String[] types = {getString(R.string.track),getString(R.string.artist),getString(R.string.album)};
                AlertDialog alert = new AlertDialog.Builder(AddToLibraryActivity.this)
                        .setTitle(getString(R.string.are_you_sure)+" "+types[addType]+" "+songToAdd.getTitle())
                        .setNegativeButton(getString(R.string.button_dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton(getString(R.string.button_dialog_add) + " " + types[addType], new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new addSongsToDatabaseTask().execute(songToAdd);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });
    }

    public class addSongsToDatabaseTask extends AsyncTask<Song, String, ArrayList<Song>> {
        @Override
        protected void onProgressUpdate(String... values) {
            setTitle(getString(R.string.adding_lib_update) + values[0]);
        }

        @Override
        protected ArrayList<Song> doInBackground(Song... songs) {
            String theUrl = null;
            Song theSearch = songs[0];
            publishProgress(theSearch.getTitle());
            ArrayList<Song> updatedLibrary = new ArrayList<>();
            try {
                User user = db.getUser(getBaseContext());
                if (theSearch.getArtist().equals("artistsearch")) {
                    theUrl = "http://burles.ddns.net/user/libadd?stype=artist?username="+user.getUsername()+"?password="+user.getPassword()
                            +"?id="+theSearch.getId()+"?genre="+theSearch.getGenre()+"?apikey="+LoginActivity.key;
                } else if (theSearch.getArtist().equals("albumsearch")) {
                    theUrl = "http://burles.ddns.net/user/libadd?stype=album?username="+user.getUsername()+"?password="+user.getPassword()
                            +"?id="+theSearch.getId()+"?album="+theSearch.getTitle()+"?apikey="+LoginActivity.key;
                } else if (theSearch.getGenre().equals("tracksearch")) {
                    theUrl = "http://burles.ddns.net/user/libadd?stype=track?username="+user.getUsername()+"?password="+user.getPassword()
                            +"?id="+theSearch.getId()+"?name="+theSearch.getTitle()+"?artist="+theSearch.getArtist()
                            + "?album="+theSearch.getAlbum()+"?pop="+theSearch.getRating()+"?apikey="+LoginActivity.key;
                }
                URL url = new URL(theUrl.replace(" ","%20"));
                Log.d(AddTag, "\n\naddSongsToDB is Running "+", " + url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                publishProgress(getString(R.string.connected_server));
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                Log.d(AddTag, "addSongsToDB: Returned: " + builder.toString());
                publishProgress(getString(R.string.refreshing_data));
                JSONObject json = new JSONObject(builder.toString());
                int numResult = json.getInt("numResult");
                publishProgress(numResult + getString(R.string.num_songs_retrieved));
                Log.d(AddTag, "addSongsToDB: There are " + numResult + " songs retreived");
                JSONArray songArray = json.getJSONArray("songs");
                for (int i = 0; i < songArray.length(); i++) {
                    JSONObject song = songArray.getJSONObject(i);
                    Song newSong = new Song(
                            song.getString("title"),
                            song.getString("artist"),
                            song.getString("album"),
                            song.getString("genre"),
                            song.getInt("plays"),
                            song.getInt("rating"),
                            song.getString("id")
                    );
                    updatedLibrary.add(newSong);
                }
                db.addSongs(getBaseContext(), updatedLibrary);
                publishProgress(numResult + getString(R.string.database_refreshed));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return updatedLibrary;
        }

        @Override
        protected void onPostExecute(ArrayList<Song> songs) {
            Intent intent = new Intent(getBaseContext(), LibraryActivity.class);
            startActivity(intent);
        }
    }

    public class spotifySearchTask extends AsyncTask<String,String,ArrayList<Song>> {

        final String BASE_URL = "https://api.spotify.com/v1/search?q=";
        final String[] S_TYPE = {"&type=artist","&type=album","&type=track"};

        int searchType = 0;

        final String progressTxt = "Song Search: ";

        @Override
        protected void onProgressUpdate(String... values) {
            setTitle(progressTxt+values[0]);
        }

        @Override
        protected ArrayList<Song> doInBackground(String... strings) {
            ArrayList<Song> songList = new ArrayList<>();
            String artist = strings[0];
            String album = null;
            String title = null;
            if(strings[1] != null){
                album = strings[1];
                searchType = 1;
            }
            if(strings[2] != null){
                searchType = 2;
                title = strings[2];
            }
            StringBuilder search = new StringBuilder();
            try {

                if(artist != null){
                    search.append("+artist:").append(URLEncoder.encode(artist, "UTF-8"));
                }
                if(album != null){
                    search.append("+album:").append(URLEncoder.encode(album, "UTF-8"));
                }
                if(title != null){
                    search.append("+track:").append(URLEncoder.encode(title, "UTF-8"));
                }

                URL url = new URL(BASE_URL + search + S_TYPE[searchType]);
                Log.d(AddTag, "SpotifySrchTask running: "+url.toString());

                URLConnection conn = url.openConnection();
                publishProgress("Connection Made");
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder builder = new StringBuilder();
                while ((inputLine = in.readLine()) != null) builder.append(inputLine);
                publishProgress("Got Result");
                Log.d(AddTag, "SpotifySrchTask got result: "+builder.toString());
                in.close();

                JSONObject json = new JSONObject(builder.toString());
                JSONObject results = null;
                switch (searchType){
                    case 0: results = json.getJSONObject("artists");
                        break;
                    case 1: results = json.getJSONObject("albums");
                        break;
                    case 2: results = json.getJSONObject("tracks");
                        break;
                }
                int num = results.getInt("total");
                publishProgress("Got Result ("+num+")");

                if(num == 0){
                    return null;
                }

                JSONArray items = results.getJSONArray("items");
                publishProgress("Retrieving " + num + " results");
                for(int i = 0; i < items.length(); i++) {
                    switch (searchType) {
                        case 0: { // artist search
                            JSONObject item = items.getJSONObject(i); // returns name of artist, genre and artist id
                            JSONArray genres = item.getJSONArray("genres");
                            songList.add(new Song(item.getString("name"),"artistsearch","artistsearch",
                                    (String) genres.get(0),-1,-1,item.getString("id")));
                            Log.d(AddTag, "SpotifySrchTask adding result: "+songList.get(i));
                            break;
                        }
                        case 1: { // album search
                            JSONObject item = items.getJSONObject(i); // returns name of album, and album id
                            songList.add(new Song(item.getString("name"),"albumsearch","albumsearch",
                                    "albumsearch",-1,-1,item.getString("id")));
                            Log.d(AddTag, "SpotifySrchTask adding result: "+songList.get(i));
                            break;
                        }
                        case 2: { // track search
                            JSONObject item = items.getJSONObject(i); // returns name of  track, artist, album, popularity score and id of artist
                            JSONObject artistObj = item.getJSONArray("artists").getJSONObject(0);
                            JSONObject albumObj = item.getJSONObject("album");

                            songList.add(new Song(item.getString("name"),artistObj.getString("name"),
                                    albumObj.getString("name"), "tracksearch",
                                    0,item.getInt("popularity"), artistObj.getString("id")));
                            Log.d(AddTag, "SpotifySrchTask adding result: "+songList.get(i).debug());
                            break;
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return songList;
        }

        @Override
        protected void onPostExecute(ArrayList<Song> songs) {
            setTitle("Search Complete");
            if(songs == null){
                searchType = -1;
            }
            switch (searchType){
                case -1:{
                    setTitle("Search Failed");
                    Toast.makeText(getBaseContext(),"Search Failed, check terms and try again",Toast.LENGTH_LONG).show();
                    break;
                }
                default: { // album search
                    songList.clear();
                    songList.addAll(songs);
                    songAdapter = new SongAdapter(getBaseContext(), R.layout.song_list_small_layout, songList);
                    searchList.setAdapter(songAdapter);
                    songAdapter.notifyDataSetChanged();
                    break;
                }
                case 2: { // track search
                    songList.clear();
                    songList.addAll(songs);
                    SongAdapter songAdapter = new SongAdapter(getBaseContext(), R.layout.song_list_layout, songList);
                    searchList.setAdapter(songAdapter);
                    songAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }
}
