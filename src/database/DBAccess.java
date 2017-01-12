package database;

import itunesxml.Song;
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
    private String statement = "INSERT INTO "+DBContract.TABLE_SONGS+
                    "("+DBContract.COL_S_TITLE+","+DBContract.COL_S_ARTIST+","+DBContract.COL_S_ALBUM+","
                            +DBContract.COL_S_GENRE+","+DBContract.COL_S_PLAYS+","+DBContract.COL_S_RATING
                                    +") VALUES(?,?,?,?,?,?)";
    private PreparedStatement songStatement;
    private ResultSet result = null;
    
    private String url = dbc.DBURL;
    private String user = dbc.DBUSER;
    private String password = dbc.DBPASS;
    
    public void insertSongs(ArrayList<Song> songList) throws SQLException {
        try {
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            System.out.println(statement);
            songStatement = con.prepareStatement(statement);
            for(Song song : songList){
                System.out.println("SQL: Adding Song: " + song);
                songStatement.setString(1, song.getTitle());
                songStatement.setString(2, song.getArtist());
                songStatement.setString(3, song.getAlbum());
                songStatement.setString(4, song.getGenre());
                songStatement.setInt(5, song.getPlays());
                songStatement.setInt(6, song.getRating());
                songStatement.executeUpdate();
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
                
                Logger lgr = Logger.getLogger(DBAccess.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    public void version() {
        try {
            
            con = DriverManager.getConnection(url, user, password);
            state = con.createStatement();
            result = state.executeQuery("SELECT VERSION()");

            if (result.next()) {
                
                System.out.println(result.getString(1));
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
                
                Logger lgr = Logger.getLogger(DBAccess.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
}
