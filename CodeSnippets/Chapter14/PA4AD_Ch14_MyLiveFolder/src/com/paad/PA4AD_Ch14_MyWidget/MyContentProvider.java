package com.paad.PA4AD_Ch14_MyWidget;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.LiveFolders;


public class MyContentProvider extends ContentProvider
{
    
    // LiverFolders公开的授权
    public static final Uri LIVE_FOLDER_URI=Uri.parse("content://com.paad.appwidgets/livefolder");
    public static final String KEY_ID="KEY_ID";
    public static final String KEY_NAME="KEY_NAME";
    public static final String KEY_DESCRIPTION="KEY_DESCRIPTION";
    public static final String KEY_IMAGE="KEY_IMAGE";
    private static final HashMap<String, String> LIVE_FOLDER_PROJECTION;
    static{
        // 应用一个投影，将现有列名映射到LiveFolders所需的列名
        LIVE_FOLDER_PROJECTION=new HashMap<String, String>();
        LIVE_FOLDER_PROJECTION.put(LiveFolders._ID, KEY_ID + " AS " + LiveFolders._ID);
        LIVE_FOLDER_PROJECTION.put(LiveFolders.NAME, KEY_NAME + " AS " + LiveFolders.NAME);
        LIVE_FOLDER_PROJECTION.put(LiveFolders.DESCRIPTION, KEY_DESCRIPTION + " AS " + LiveFolders.DESCRIPTION);
        LIVE_FOLDER_PROJECTION.put(LiveFolders.ICON_BITMAP, KEY_IMAGE + " AS " + LiveFolders.ICON_BITMAP);
    }
    private static final int LIVE_FOLDER=4;
    public static UriMatcher URI_MATCHER;
    static{
        URI_MATCHER=new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI("com.paad.provider.MyLiveFolder", "live_folder", LIVE_FOLDER);
    }
    
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();
        switch(URI_MATCHER.match(uri)){
            case LIVE_FOLDER:
                qb.setTables("table");
                qb.setProjectionMap(LIVE_FOLDER_PROJECTION);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Cursor c=qb.query(null, projection, selection, selectionArgs, null, null, null);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
    
    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        return 0;
    }
    
    @Override
    public String getType(Uri arg0) {
        return null;
    }
    
    @Override
    public Uri insert(Uri arg0, ContentValues arg1) {
        return null;
    }
    
    @Override
    public boolean onCreate() {
        return false;
    }
    
    @Override
    public int update(Uri uri, ContentValues contentValues, String arg2, String[] arg3) {
        return 0;
    }
}
