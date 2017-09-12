package burles.brookes.ac.musicrec;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.ArrayList;

import burles.brookes.ac.musicrec.Utils.User;
import burles.brookes.ac.musicrec.db.LibraryDAO;
import burles.brookes.ac.musicrec.Utils.Song;

public class LibraryActivity extends AppCompatActivity {

    User user;
    private Toolbar toolbar;
    private ArrayAdapter<String> artistAdapter;
    private ListView artistView;
    private ArrayList<String> artistList;


    private LibraryDAO db = new LibraryDAO();
    private boolean activeIO = false;

    private final String APITag = LoginActivity.Tag+"API";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        user = db.getUser(getBaseContext());
        setTitle(user.getUsername()+"'s Library");

        artistList = new ArrayList<>();
        artistView = (ListView) findViewById(R.id.artist_list);
        artistAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, artistList);
        artistView.setAdapter(artistAdapter);

        getArtistsFromDBTask dbTask = new getArtistsFromDBTask();
        dbTask.execute();

        artistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(APITag, "the artist selected was: "+artistList.get(position));

                ArrayList<String> albums = db.getAlbumFromArtist(getBaseContext(), artistList.get(position));

                ArtistFragment artistFragment = new ArtistFragment();
                Bundle args = new Bundle();
                args.putString("artist",artistList.get(position));
                args.putStringArrayList("albums",albums);
                artistFragment.setArguments(args);
                artistFragment.show(getFragmentManager(), "album_fragment");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.library_menu, menu);
        invalidateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_songs :
                startActivity(new Intent(getBaseContext(), AddToLibraryActivity.class));
                return true;
            case R.id.action_change_user :
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.addFlags(LoginActivity.NO_AUTOMATIC_LOGIN);
                startActivity(intent);
                return true;
            default: return false;
        }
    }

    @Override
    public void onBackPressed() {
    }

    public class getArtistsFromDBTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            activeIO = true;
            Log.d(APITag,"OnPost getArtistsDB: retrieving artists from db");
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {

            ArrayList<String> artists = db.getAllArtists(getBaseContext());


            return artists;
        }

        @Override
        protected void onPostExecute(ArrayList<String> artists) {
            if(artists.size()==0){
                artistAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.empty_library_layout, artists);
                artistView.setAdapter(artistAdapter);
                artists.add("\n");
                artists.add("\nYou have no songs in your library. " +
                        "\nTo add songs press the music note button at the top " +
                        "or press the users button to log in as a different user\n");
                artists.add("\n");
                artistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                }});
            } else {
                artistList.addAll(artists);
            }

            artistAdapter.notifyDataSetChanged();
            Log.d(APITag,"OnPost getArtistsDB: "+artistAdapter.getCount()+" artists in db");
            activeIO = false;
            super.onPostExecute(artists);
        }
    }

    public class getSongsFromArtistTask extends AsyncTask<String, Void, ArrayList<Song>> {
        String statusMessage = "null";

        @Override
        protected ArrayList<Song> doInBackground(String... strings) {
            ArrayList<Song> songList = new ArrayList<>();

            HttpURLConnection connection = null;

            try {
                URL url = new URL("http://burles.ddns.net/artist/"+strings[0]);
                connection = (HttpURLConnection) url.openConnection();

                InputStream stream = connection.getInputStream();
                Log.d(APITag,"\n\ngetSongsFromArtistTask is Running: "+url.toString());
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                statusMessage = "Connection Successful";
                String line;
                StringBuilder builder = new StringBuilder();
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
                JSONObject json = new JSONObject(builder.toString());
                Log.d(APITag, "getArt: There are "+json.getInt("numResult")+" songs retreived");
                JSONArray songArray = json.getJSONArray("songs");

                for(int i = 0; i < songArray.length(); i++){
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
                    songList.add(newSong);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
            }
            return songList;
        }

        @Override
        protected void onPostExecute(ArrayList<Song> songs){

            Log.d(APITag,"OnPost getArtistsDB: "+activeIO);
            while(activeIO){
                try {
                    Log.d(APITag,"OnPost getAllSongs: waiting...");
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            artistAdapter.clear();
            artistAdapter.notifyDataSetChanged();
            Log.d(APITag,"onPost getArt running with "+songs.size() +" songs");
        }
    }


//    public class getAllSongsTask extends AsyncTask<Void,Boolean,ArrayList<Song>> {
//        String statusMessage = "null";
//
//        @Override
//        protected ArrayList<Song> doInBackground(Void... voids) {
//            ArrayList<Song> songList = new ArrayList<>();
//
//            HttpURLConnection connection = null;
//
//            try {
//                URL url = new URL("http://burles.ddns.net/getall");
//                connection = (HttpURLConnection) url.openConnection();
//
//                InputStream stream = connection.getInputStream();
//                Log.d(APITag,"\n\ngetAllSongsTask is Running "+url);
//                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
//                statusMessage = "Connection Successful";
//                String line;
//                StringBuilder builder = new StringBuilder();
//                while((line = reader.readLine()) != null){
//                    builder.append(line);
//                }
//                Log.d(APITag,"Returned: "+builder.toString());
//                JSONObject json = new JSONObject(builder.toString());
//                int numResult = json.getInt("numResult");
//                Log.d(APITag, "getAll: There are "+numResult+" songs retreived");
//                if(numResult != db.getNumOfSongs(getBaseContext())) {
//                    JSONArray songArray = json.getJSONArray("songs");
//                    for (int i = 0; i < songArray.length(); i++) {
//                        JSONObject song = songArray.getJSONObject(i);
//                        Song newSong = new Song(
//                                song.getString("title"),
//                                song.getString("artist"),
//                                song.getString("album"),
//                                song.getString("genre"),
//                                song.getInt("plays"),
//                                song.getInt("rating"),
//                                song.getString("id")
//                        );
//                        songList.add(newSong);
//                    }
//                    db.addSongs(getBaseContext(), songList);
//                }
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } finally {
//                if (connection != null){
//                    connection.disconnect();
//                }
//            }
//            return songList;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<Song> songs){
////            songList.clear();
////            songList.addAll(songs);
////            songAdapter.notifyDataSetChanged();
//            Log.d(APITag,"onPost AllSongs running with "+songList.size() +" songs");
//        }
//
//    }
}
