package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBAccess {
    
    DBContract dbc = new DBContract();
    
    private Connection con = null;
    private Statement state = null;
    private final String songStatement = "INSERT INTO "+DBContract.TABLE_SONGS+
                    "("+DBContract.COL_S_TITLE+","+DBContract.COL_S_ARTIST+","+DBContract.COL_S_ALBUM+","
                            +DBContract.COL_S_GENRE+","+DBContract.COL_S_PLAYS+","+DBContract.COL_S_RATING
                                    +") VALUES(?,?,?,?,?,?)";
    private final String userStatement = "INSERT INTO "+DBContract.TABLE_USERS+
                    "("+DBContract.COL_U_USERNAME+","+DBContract.COL_U_PASS+","
                       +DBContract.COL_U_LIB+") VALUES(?,?,?)";
    private final String libStatement = "UPDATE "+DBContract.TABLE_USERS+" SET "+DBContract.COL_U_LIB+
                                        " = ? , "+DBContract.COL_U_LIB_SIZE+" = ? WHERE username = ?";
    private ResultSet result = null;
    
    private String url = dbc.DBURL;
    private String user = dbc.DBUSER;
    private String password = dbc.DBPASS;
    
    public ArrayList<Song> getAllSongs(){
        ArrayList<Song> songs = new ArrayList<>();
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            ResultSet result = state.executeQuery("SELECT * FROM songs ORDER BY id ASC");
            while (result.next()) {
                songs.add(new Song(
                            result.getString("title"),
                            result.getString("artist"),
                            result.getString("album"),
                            result.getString("genre"),
                            result.getInt("plays"),
                            result.getInt("rating"),
                            Integer.toString(result.getInt("id"))
                ));
            }
            
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return songs;
    }

    public ArrayList<Song> getSongsByGenre(String genre, Song song){
        ArrayList<Song> songs = new ArrayList<>();
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            ResultSet result = state.executeQuery("SELECT * FROM songs WHERE genre = \""+
                                                    genre + "\" ORDER BY plays DESC");
            while (result.next()) {
                if(!result.getString("title").equals(song.getTitle())){
                    songs.add(new Song(
                            result.getString("title"),
                            result.getString("artist"),
                            result.getString("album"),
                            result.getString("genre"),
                            result.getInt("plays"),
                            result.getInt("rating"),
                            Integer.toString(result.getInt("id"))
                    ));
                }
            }
            
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return songs;
    }
    
    public ArrayList<Song> getSongsByArtist(String artist){
        ArrayList<Song> songs = new ArrayList<>();
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            ResultSet result = state.executeQuery("SELECT * FROM songs WHERE artist = \""+
                                                    artist + "\" ORDER BY album ASC");
            while (result.next()) {
                songs.add(new Song(
                        result.getString("title"),
                        result.getString("artist"),
                        result.getString("album"),
                        result.getString("genre"),
                        result.getInt("plays"),
                        result.getInt("rating"),
                        Integer.toString(result.getInt("id"))
                ));
            }
            
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return songs;
    }
    
    public Song getSongByID(int id){
        Song song = null;
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            ResultSet result = state.executeQuery("SELECT * FROM songs WHERE id = "+id);
            result.next();
            song = new Song(
                            result.getString("title"),
                            result.getString("artist"),
                            result.getString("album"),
                            result.getString("genre"),
                            result.getInt("plays"),
                            result.getInt("rating"),
                            Integer.toString(result.getInt("id")));
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (NullPointerException ex){
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            System.out.println("SQL Error: Null Pointer Exeception");
        }finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return song;
    }
    
    public ArrayList<String> getGenresByArtists(String artist){
        ArrayList<String> genres = new ArrayList<>();
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            ResultSet result = state.executeQuery("SELECT genre FROM songs WHERE artist = \""+
                                                    artist+"\" ORDER BY plays DESC");
            while (result.next()) {
                if(!genres.contains(result.getString("genre"))){
                    genres.add(result.getString("genre"));
                }
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return genres;
    }
    
    public void updateSongRating(Song song, int rating){
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            state.executeUpdate("UPDATE songs SET rating=\""+rating+"\" WHERE title=\""
                            +song.getTitle()+"\" AND artist=\""+song.getArtist()+"\""); 
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public boolean addSongPlay(String songID){
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            ResultSet result = state.executeQuery("SELECT plays FROM songs WHERE id = "+songID);
            result.next();
            int plays = result.getInt("plays") + 1;
            state = con.createStatement();
            state.executeUpdate("UPDATE songs SET plays=\""+plays+"\" WHERE id="+songID); 
            return true;
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
    }
    
    public void updateSongGenre(Song song, String genre){
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            state.executeUpdate("UPDATE songs SET genre=\""+genre+"\" WHERE title=\""
                            +song.getTitle()+"\" AND artist=\""+song.getArtist()+"\""); 
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public int checkExists(String title, String artist){
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            ResultSet result = state.executeQuery("SELECT title,artist,id FROM songs WHERE title = \""+title+
                                                    "\" AND artist = \""+artist+"\" ORDER BY plays DESC");
            while (result.next()) {
                if(result.getString(DBContract.COL_S_TITLE).equals(title) &&
                        result.getString(DBContract.COL_S_ARTIST).equals(artist)){
                    return result.getInt(DBContract.COL_S_ID);
                }
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return -404;
    }
    
    public void insertSong(Song song){
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            PreparedStatement statement = con.prepareStatement(songStatement);
            statement.setString(1, song.getTitle());
            statement.setString(2, song.getArtist());
            statement.setString(3, song.getAlbum());
            statement.setString(4, song.getGenre());
            statement.setInt(5, song.getPlays());
            statement.setInt(6, song.getRating());
            statement.executeUpdate();
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void insertSongs(ArrayList<Song> songList){
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            PreparedStatement statement = con.prepareStatement(songStatement);
            for(Song song : songList){
                statement.setString(1, song.getTitle());
                statement.setString(2, song.getArtist());
                statement.setString(3, song.getAlbum());
                statement.setString(4, song.getGenre());
                statement.setInt(5, song.getPlays());
                statement.setInt(6, song.getRating());
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public boolean addUser(String username, String pass){
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            PreparedStatement statement = con.prepareStatement(userStatement);
            statement.setString(1, username);
            statement.setString(2, pass);
            statement.setString(3, DBContract.EMPTY_LIBRARY);
            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            String msg = ex.getMessage();
            if(msg.matches("(Duplicate entry[\\s\\S]+)")){
                return false;
            }
            lgr.log(Level.SEVERE, msg, ex);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    
    public boolean setUserLibrary(String username, String library, int size){
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            PreparedStatement statement = con.prepareStatement(libStatement);
            statement.setString(1, library);
            statement.setInt(2, size);
            statement.setString(3, username);
            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    
    public User getUser(String username, String pass){
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            ResultSet result = state.executeQuery("SELECT username, password, librarySize FROM users WHERE username = '"+username+"'");
            result.next();
            User theUser = new User(
                    result.getString("username"),
                    result.getString("password"),
                    result.getInt("librarySize"));
            if(theUser.getPassword().equals(pass)){
                return theUser;
            } else return new User("invalid","password",-403);
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            String msg = ex.getMessage();
            if(msg.matches("[\\s\\S]*(empty result set)[\\s\\S]*")){
                return new User("invalid","username",-402);
            }
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new User("invalid","unknown error",-500);
    }
    
    public String getUsersLibrary(String username){
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            ResultSet result = state.executeQuery("SELECT library FROM users WHERE username = '"+username+"'");
            result.next();
            return result.getString("library");
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return DBContract.EMPTY_LIBRARY;
    }

    public void version() {
        try {
            
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            result = state.executeQuery("SELECT VERSION()");

            if (result.next()) {
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBAccess.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (state != null) {
                    state.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
