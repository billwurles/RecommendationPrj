package burles.brookes.ac.musicrec;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import burles.brookes.ac.musicrec.db.LibraryDAO;
import burles.brookes.ac.musicrec.Utils.Song;
import burles.brookes.ac.musicrec.Utils.User;


public class LoginActivity extends AppCompatActivity {

    private LibraryDAO db = new LibraryDAO();

    private TextView statusText;
    private EditText editUser;
    private EditText editPass;
    private Button buttonLogin;
    private Button buttonRegister;

    private loginUserTask loginTask = new loginUserTask();
    public static final String key = "QE7IplGIJjNoPvhniRR4Q";
    private boolean auto = false;

    public static final int NO_AUTOMATIC_LOGIN = 101;

    public static final String Tag = "RecSys";
    private static final String UsrTag = Tag+"Usr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusText = (TextView) findViewById(R.id.text_status);
        editUser = (EditText) findViewById(R.id.edit_username);
        editPass = (EditText) findViewById(R.id.edit_password);
        buttonLogin = (Button) findViewById(R.id.button_login);
        buttonRegister = (Button) findViewById(R.id.button_register);

        Intent intent = getIntent();
        int flag = intent.getFlags();
        if(flag == NO_AUTOMATIC_LOGIN){
            auto = false;
            User user = db.getUser(getBaseContext());
            editUser.setText(user.getUsername());
        } else if(db.hasUser(getBaseContext())){
            auto = true;
            buttonRegister.setEnabled(false);
            buttonLogin.setText("Cancel");
            User user = db.getUser(getBaseContext());
            editUser.setText(user.getUsername());
            buttonRegister.setEnabled(true);
            buttonLogin.setEnabled(true);
            buttonLogin.setText("Login");

            loginTask.execute(user.getUsername(),user.getPassword());
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!auto) {
                    buttonRegister.setEnabled(false);
                    buttonLogin.setEnabled(false);
                    statusText.setText("");
                    String username = editUser.getText().toString();
                    String password = editPass.getText().toString();

                    if (username.equals("")) {
                        statusText.setText("You must enter a username");
                        buttonRegister.setEnabled(true);
                        buttonLogin.setEnabled(true);
                    } else if (password.equals("")) {
                        statusText.setText("You must enter a password");
                        buttonRegister.setEnabled(true);
                        buttonLogin.setEnabled(true);
                    } else {
                        loginUserTask task = new loginUserTask();
                        task.execute(username, encrypt(password));
                    }
                } else {
                    statusText.setText("Cancelled Login");
                    buttonLogin.setText("Login");
                    loginTask.cancel(true);
                    buttonLogin.setEnabled(true);
                    buttonRegister.setEnabled(true);
                    auto = false;
                }
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonRegister.setEnabled(false);
                buttonLogin.setEnabled(false);
                statusText.setText("");
                String username = editUser.getText().toString();
                String password = encrypt(editPass.getText().toString());

                if(username.equals("")){
                    statusText.setText("You must enter a username");
                } else if(password.equals("")){
                    statusText.setText("You must enter a password");
                } else {
                    registerUserTask task = new registerUserTask();
                    task.execute(username,password);
                }
            }
        });
    }

    public class loginUserTask extends AsyncTask<String,String,User>{
        String updatePrefix = "Logging in as ";

        @Override
        protected void onProgressUpdate(String... values) {
            statusText.setText(values[0]);
        }

        @Override
        protected User doInBackground(String... strings) {
            HttpURLConnection connection = null;
            updatePrefix = updatePrefix + strings[0];
            publishProgress(updatePrefix+"...");

            try {
                URL url = new URL("http://burles.ddns.net/user/verify?username="+strings[0]
                                    +"?password="+strings[1]+"?apikey="+key);
                Log.d(UsrTag,"\n\nloginUserTask is Running "+url);
                connection = (HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                publishProgress(updatePrefix+" Connected to server");
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                StringBuilder builder = new StringBuilder();
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
                Log.d(UsrTag,"Returned: "+builder.toString());
                publishProgress(updatePrefix+" Got response");
                JSONObject json = new JSONObject(builder.toString());
                int response = json.getInt("server-response");
                Log.d(UsrTag,"Recieved response: "+response);
                if(response == 201) {
                    User newUser = new User(json.getString("username"),strings[1],json.getInt("libsize"));
                    Log.d(UsrTag,"Recieved response and running: "+newUser);
                    return newUser;
                } else return new User("   ","   ",response);

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
            return new User("   ","   ",-101);
        }

        @Override
        protected void onPostExecute(User newUser){
            int result = newUser.getLibSize();
            if(result == -402){
                Log.e(UsrTag,"onPost loginUser unsuccessful: HTTP 402: Username invalid");
                statusText.setText("The username given is incorrect");
                buttonRegister.setEnabled(true);
                buttonLogin.setEnabled(true);
                buttonLogin.setText("Login");
                auto = false;
            } else if(result == -403){
                Log.e(UsrTag,"onPost loginUser unsuccessful: HTTP 403: Password invalid");
                statusText.setText("The password given is incorrect");
                buttonRegister.setEnabled(true);
                buttonLogin.setEnabled(true);
                buttonLogin.setText("Login");
                auto = false;
            } else if(result == -500){
                statusText.setText("Internal Server Error, please try again");
                Log.e(UsrTag,"onPost loginUser unsuccessful: HTTP 500: Server Java Error");
                buttonRegister.setEnabled(true);
                buttonLogin.setEnabled(true);
                buttonLogin.setText("Login");
                auto = false;
            } else if(result == -101){
                statusText.setText("Unknown Android Error, please try again");
                Log.e(UsrTag,"onPost loginUser unsuccessful: Unknown Error: Returned val -101");
                buttonRegister.setEnabled(true);
                buttonLogin.setEnabled(true);
                buttonLogin.setText("Login");
                auto = false;
            } else {
                statusText.setText("Adding userinfo to Database");
                Log.d(UsrTag,"onPost loginUser success: "+newUser);
                if(db.hasUser(getBaseContext())){
                    if(db.getUser(getBaseContext()).getLibSize() != newUser.getLibSize()){

                    }
                }
                db.setUser(getBaseContext(), newUser);
                getUserSongsTask getSongsTask = new getUserSongsTask();
                getSongsTask.execute(newUser.getUsername(),newUser.getPassword());

            }
        }
    }

    public class getUserSongsTask extends AsyncTask<String,String,Void> {
        String updatePrefix = "Filling database... ";

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(String... strings) {
            ArrayList<Song> songList = new ArrayList<>();

            HttpURLConnection connection = null;

            try {
                URL url = new URL("http://burles.ddns.net/user/library?username="+strings[0]
                                    +"?password="+strings[1]+"?apikey="+key);
                Log.d(UsrTag,"\n\ngetUserSongsTask is Running "+url);
                connection = (HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                publishProgress(updatePrefix+" established connection");
                String line;
                StringBuilder builder = new StringBuilder();
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
                Log.d(UsrTag,"Returned: "+builder.toString());
                JSONObject json = new JSONObject(builder.toString());
                int numResult = json.getInt("numResult");
                publishProgress(updatePrefix+numResult+" songs retrieved");
                Log.d(UsrTag, "getAll: There are "+numResult+" songs retreived");
                if(numResult != db.getNumOfSongs(getBaseContext())) {
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
                        songList.add(newSong);
                    }
                    db.addSongs(getBaseContext(), songList);
                }
                publishProgress(updatePrefix+numResult+" database filled");

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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("TaskView","Activity: Starting Main Library Activity");
            Intent intent = new Intent(getBaseContext(), LibraryActivity.class);
            startActivity(intent);
            buttonRegister.setEnabled(true);
            buttonLogin.setEnabled(true);
            buttonLogin.setText("Login");
            auto = false;
        }
    }

    @Override
    public void onBackPressed() {
    }

    public class registerUserTask extends AsyncTask<String,String,Integer>{

        @Override
        protected void onPreExecute() {
            statusText.setText("Registering User...");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            statusText.setText(values[0]);
        }

        @Override
        protected Integer doInBackground(String... strings) {
            HttpURLConnection connection = null;

            try {
                String theUrl = "http://burles.ddns.net/user/add?username="+strings[0]
                        +"?password="+strings[1]+"?apikey="+key;
                theUrl = theUrl.replace("\\s","");
                URL url = new URL(theUrl);
                Log.d(UsrTag,"\n\nregisterUserTask is Running "+url);
                connection = (HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                publishProgress("Registering user... Connected to server");
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                StringBuilder builder = new StringBuilder();
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
                String result = builder.toString();
                Log.d(UsrTag,"Returned: "+result);
                publishProgress("Registering user... Got result");
                if(result.matches("[\\s\\S]*(Response 200)[\\s\\S]*")){
                    return 201;
                } else if(result.matches("[\\s\\S]*(Error 1062)[\\s\\S]*")){
                    return -1062;
                } else if(result.matches("[\\s\\S]*(Error 401)[\\s\\S]*")){
                    return -401;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
            }
            return -500;
        }

        @Override
        protected void onPostExecute(Integer result){
            if(result == 201){
                Log.d(UsrTag,"onPost registerUser successful result");
                statusText.setText("User Registered Successfully");
            } else if(result == -1062){
                Log.e(UsrTag,"onPost registerUser unsuccessful: MySQL 1062: User already exists");
                statusText.setText("This username is already in use");
            } else if(result == -401){
                statusText.setText("Server Communication Error");
                Log.e(UsrTag,"onPost registerUser unsuccessful: Server Error 401: Incorrect API key");
            } else if(result == -500){
                statusText.setText("Unknown Error, please try again");
                Log.e(UsrTag,"onPost registerUser unsuccessful: Unknown Error 500");
            }
            buttonRegister.setEnabled(true);
            buttonLogin.setEnabled(true);
            buttonLogin.setText("Login");
            auto = false;
        }
    }
    public static String encrypt(String value){
        try {
            SecretKeySpec spec = new SecretKeySpec("j34F0iS3gh12Vxz7".getBytes("UTF-8"), "AES");
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, spec);
            byte[] bytes = value.getBytes("UTF-8");
            byte[] encryptBytes = cipher.doFinal(bytes);
            String encrypted = Base64.encodeToString(encryptBytes, Base64.DEFAULT);
            return encrypted.substring(0,encrypted.length()-1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
