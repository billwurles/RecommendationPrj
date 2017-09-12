package burles.brookes.ac.musicrec;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Collections;

import burles.brookes.ac.musicrec.Utils.Song;
import burles.brookes.ac.musicrec.Utils.SongAdapter;
import burles.brookes.ac.musicrec.Utils.User;
import burles.brookes.ac.musicrec.db.LibraryDAO;


public class SongActivity extends AppCompatActivity {


    private Song song;
    private LibraryDAO db = new LibraryDAO();

    private TextView textTitle;
    private TextView textArtist;
    private TextView textAlbum;
    private TextView textGenre;
    private TextView textPlays;
    private Button buttonGetRecs;
    private Button buttonPlay;

    private ArrayList<Song> recList;
    private ListView recView;
    private SongAdapter recAdapter;
    public static final String key = "QE7IplGIJjNoPvhniRR4Q";

    private static final String RecTag = LoginActivity.Tag+"RecTask";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        Intent intent = getIntent();
        song = intent.getParcelableExtra("song");
        setTitle(song.getTitle());

        textTitle = (TextView) findViewById(R.id.text_title);
        textArtist = (TextView) findViewById(R.id.text_artist);
        textAlbum = (TextView) findViewById(R.id.text_album);
        textGenre = (TextView) findViewById(R.id.text_genre);
        textPlays = (TextView) findViewById(R.id.text_plays);
        buttonGetRecs = (Button) findViewById(R.id.button_recommend);
        buttonPlay = (Button) findViewById(R.id.button_play);

        textTitle.setText(song.getTitle());
        textArtist.setText(song.getArtist());
        textAlbum.setText(song.getAlbum());
        textGenre.setText(song.getGenre());
        textPlays.setText(song.getPlays() + " plays");

        recList = new ArrayList<>();
        recAdapter = new SongAdapter(getBaseContext(), R.layout.song_list_layout, recList);
        recView = (ListView) findViewById(R.id.recommendation_list);
        recView.setAdapter(recAdapter);

        buttonGetRecs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRecommendationsTask task = new getRecommendationsTask();
                recList.clear();
                recAdapter.notifyDataSetChanged();
                task.execute(song);
            }
        });
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new addPlayTask().execute(song);
            }
        });
        recView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Song songToAdd = recList.get(i);
                AlertDialog alert = new AlertDialog.Builder(SongActivity.this)
                        .setTitle(getString(R.string.are_you_sure)+" "+getString(R.string.track)+" "+songToAdd.getTitle())
                        .setNegativeButton(getString(R.string.button_dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton(getString(R.string.button_dialog_add) + " " + getString(R.string.track), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new addRecommendationToLibraryTask().execute(songToAdd);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getBaseContext(), LibraryActivity.class));
    }

    public class addRecommendationToLibraryTask extends AsyncTask<Song, String, Song> {
        @Override
        protected void onProgressUpdate(String... values) {
            setTitle("Adding to library... " + values[0]);
        }

        @Override
        protected Song doInBackground(Song... songs) {
            URL url = null;
            Song theSearch = songs[0];
            publishProgress(theSearch.getTitle());
            ArrayList<Song> updatedLibrary = new ArrayList<>();
            User user = db.getUser(getBaseContext());
            try {
                url = new URL("http://burles.ddns.net/user/libadd?stype=recommended?username="+user.getUsername()
                             +"?password="+user.getPassword()+"?id="+theSearch.getId()+"?apikey="+LoginActivity.key);

                Log.d(RecTag, "\n\naddRecToLib is Running " + url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                publishProgress("Connected to server");
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                Log.d(RecTag, "addRecToLib: Returned: " + builder.toString());
                publishProgress("Added, refreshing local data");
                JSONObject json = new JSONObject(builder.toString());
                int numResult = json.getInt("numResult");
                publishProgress(numResult + " songs retrieved");
                Log.d(RecTag, "addRecToLib: There are " + numResult + " songs retreived");
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
                publishProgress(numResult + " database refreshed");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return songs[0];
        }

        @Override
        protected void onPostExecute(Song song) {
            setTitle("Recommendation Added");
            recList.remove(song);
            recAdapter.notifyDataSetChanged();
            Toast.makeText(getBaseContext(),song.getTitle()+ " Added to Library",Toast.LENGTH_SHORT).show();
        }
    }

    private class addPlayTask extends AsyncTask<Song, Void, Void> {

        @Override
        protected Void doInBackground(Song... songs) {
            HttpURLConnection connection = null;
            User user = db.getUser(getBaseContext());
            try {
                URL url = new URL("http://burles.ddns.net/user/play?username="+user.getUsername()+"?password="+user.getPassword()
                    +"?id="+songs[0].getId()+"?apikey="+LoginActivity.key);
                connection = (HttpURLConnection) url.openConnection();

                InputStream stream = connection.getInputStream();
                Log.d(RecTag, "addPlayTask is adding play to "+songs[0]+ "  - " + url.toString());
                db.addPlayToSong(getBaseContext(), songs[0].getId());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            song = db.getSpecificSong(getBaseContext(), song.getId());
            textPlays.setText(song.getPlays() +  " plays");
        }
    }

    private class getRecommendationsTask extends AsyncTask<Song,Void,ArrayList<Song>> {
        String statusMessage = "null";

        @Override
        protected ArrayList<Song> doInBackground(Song... songs) {
            ArrayList<Song> songList = new ArrayList<>();

            HttpURLConnection connection = null;

            try {
                URL url = new URL("http://burles.ddns.net/get/recommend?="+songs[0].getId()+"?apikey="+LoginActivity.key);
                connection = (HttpURLConnection) url.openConnection();
                Log.d(RecTag,"getRecommendationsTask is Running: "+url.toString());
                InputStream stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                statusMessage = "Connection Successful";
                String line;
                StringBuilder builder = new StringBuilder();
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
                JSONObject json = new JSONObject(builder.toString());
                Log.d(RecTag, "getRecommendationsTask: There are "+json.getInt("numResult")+" songs retreived");
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
        protected void onPostExecute(ArrayList<Song> songs) {
            recList.addAll(songs);
            Collections.sort(recList);
            recAdapter.notifyDataSetChanged();
            super.onPostExecute(songs);
        }
    }

}
