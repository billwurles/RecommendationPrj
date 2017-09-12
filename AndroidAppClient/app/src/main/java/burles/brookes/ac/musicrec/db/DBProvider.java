package burles.brookes.ac.musicrec.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DBProvider extends ContentProvider {
    public static final String AUTHORITY = "burles.brookes.ac.musicrec";
    public static final Uri SONGS_URI = Uri.parse("content://" + AUTHORITY + "/songs");
    public static final Uri USER_URI = Uri.parse("content://" + AUTHORITY + "/user");
    public static final String SONGS_PATH = DBContract.TABLE_NAME_SONGS;
    public static final String USER_PATH = DBContract.TABLE_NAME_USER;
    private static final int SONGS = 0;
    private static final int USER = 1;
    private static final int SONGS_ID = 2;


    private DBHelper helper;
    private UriMatcher urImatcher;

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        helper = new DBHelper(getContext());
        urImatcher = new UriMatcher(UriMatcher.NO_MATCH);
        urImatcher.addURI(AUTHORITY,SONGS_PATH, SONGS);
        urImatcher.addURI(AUTHORITY,USER_PATH, USER);
        urImatcher.addURI(AUTHORITY,SONGS_PATH + "/#",SONGS_ID);
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowsDeleted = 0;
        int uriType = urImatcher.match(uri);
        String newSelection;
        switch (uriType) {
            case SONGS:
                rowsDeleted = db.delete(DBContract.TABLE_NAME_SONGS,
                        selection, selectionArgs);
                break;
            case USER:
                rowsDeleted = db.delete(DBContract.TABLE_NAME_USER,
                        selection, selectionArgs);
                break;
            case SONGS_ID:
                newSelection = appendToSelection(uri, selection);
                rowsDeleted = db.delete(DBContract.TABLE_NAME_SONGS,
                        newSelection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unrecognised uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getReadableDatabase();
        int uriType = urImatcher.match(uri);
        Uri resultUri = null;
        long rowId;
        switch (uriType){
            case SONGS:
                rowId = db.insert(DBContract.TABLE_NAME_SONGS, null, values);
                resultUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(resultUri, null);
                break;
            case USER:
                rowId = db.insert(DBContract.TABLE_NAME_USER, null, values);
                resultUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(resultUri, null);
                break;
            case SONGS_ID:
                rowId = db.insert(DBContract.TABLE_NAME_SONGS, null, values);
                resultUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(resultUri, null);
                break;
            default: throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return resultUri;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        int uriType = urImatcher.match(uri);
        switch (uriType) {
            case SONGS:
                builder.setTables(DBContract.TABLE_NAME_SONGS);
                break;
            case USER:
                builder.setTables(DBContract.TABLE_NAME_USER);
                break;
            case SONGS_ID:
                builder.setTables(DBContract.TABLE_NAME_SONGS);
                builder.appendWhere(DBContract._ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unrecognised URI");
        }
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = builder.query(db, projection, selection, selectionArgs,null,null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowsUpdated = 0;
        int uriType = urImatcher.match(uri);
        String newSelection;
        switch (uriType) {
            case SONGS:
                rowsUpdated = db.update(DBContract.TABLE_NAME_SONGS, values, selection, selectionArgs);
                break;
            case USER:
                rowsUpdated = db.update(DBContract.TABLE_NAME_USER, values, selection, selectionArgs);
                break;
            case SONGS_ID:
                newSelection = appendToSelection(uri, selection);
                rowsUpdated = db.update(DBContract.TABLE_NAME_SONGS, values, newSelection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unrecognised uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }

    private String appendToSelection(Uri uri, String selection){
        String id = uri.getLastPathSegment();
        StringBuilder newSelection = new StringBuilder(DBContract._ID + "=" + id);
        if(selection != null && !selection.isEmpty()){
            newSelection.append(" AND "+selection);
        }
        return newSelection.toString();
    }
}
