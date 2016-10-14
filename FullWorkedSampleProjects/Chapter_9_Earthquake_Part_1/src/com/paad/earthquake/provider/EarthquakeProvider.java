package com.paad.earthquake.provider;

import java.util.HashMap;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


public class EarthquakeProvider extends ContentProvider
{
    
    EarthquakeDatabaseHelper dbHelper;
    // uri授权
    public static final Uri CONTENT_URI=Uri.parse("content://com.paad.earthquakeprovider/earthquakes");
    // 公开使用的常量
    public static final String KEY_ID="_id";
    public static final String KEY_DATE="date";
    public static final String KEY_DETAILS="details";
    public static final String KEY_SUMMARY="summary";
    public static final String KEY_LOCATION_LAT="latitude";
    public static final String KEY_LOCATION_LNG="longitude";
    public static final String KEY_MAGNITUDE="magnitude";
    public static final String KEY_LINK="link";
    private static final HashMap<String, String> SEARCH_PROJECTION_MAP;
    static{
        // 搜索建议的查询必须返回一组预定义的cursor
        // id表示唯一行id（必须）
        // SearchManager.SUGGEST_COLUMN_TEXT_1表示搜索结果的文本
        // SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID表示添加到可搜索描述文件xml的searchSuggestIntentData属性后面
        SEARCH_PROJECTION_MAP=new HashMap<String, String>();
        SEARCH_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_1, KEY_SUMMARY + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        SEARCH_PROJECTION_MAP.put("_id", KEY_ID + " AS " + "_id");
        SEARCH_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "_id as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
    }
    private static final int QUAKES=1;
    private static final int QUAKE_ID=2;
    private static final int SEARCH=3;
    private static final UriMatcher uriMatcher;
    static{
        uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.paad.earthquakeprovider", "earthquakes", QUAKES);
        uriMatcher.addURI("com.paad.earthquakeprovider", "earthquakes/#", QUAKE_ID);
        // 提供搜索建议
        uriMatcher.addURI("com.paad.earthquakeprovider", SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH);
        uriMatcher.addURI("com.paad.earthquakeprovider", SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);
        uriMatcher.addURI("com.paad.earthquakeprovider", SearchManager.SUGGEST_URI_PATH_SHORTCUT, SEARCH);
        uriMatcher.addURI("com.paad.earthquakeprovider", SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", SEARCH);
    }
    
    @Override
    public boolean onCreate() {
        Context context=getContext();
        dbHelper=new EarthquakeDatabaseHelper(context, EarthquakeDatabaseHelper.DATABASE_NAME, null, EarthquakeDatabaseHelper.DATABASE_VERSION);
        return true;
    }
    
    @Override
    public String getType(Uri uri) {
        switch(uriMatcher.match(uri)){
            case QUAKES:
                return "vnd.android.cursor.dir/vnd.paad.earthquake";
            case QUAKE_ID:
                return "vnd.android.cursor.item/vnd.paad.earthquake";
            case SEARCH:
                // 添加返回搜索建议的结果
                return SearchManager.SUGGEST_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();
        qb.setTables(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE);
        switch(uriMatcher.match(uri)){
            case QUAKE_ID:
                qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            case SEARCH:
                // 必须是一组预定义的cursor，cursor必须含有_id和SearchManager.SUGGEST_COLUMN_TEXT_1
                qb.appendWhere(KEY_SUMMARY + " LIKE \"%" + uri.getPathSegments().get(1) + "%\"");
                qb.setProjectionMap(SEARCH_PROJECTION_MAP);
                break;
            default:
                break;
        }
        String orderBy;
        if(TextUtils.isEmpty(sort)){
            orderBy=KEY_DATE;
        }
        else{
            orderBy=sort;
        }
        Cursor c=qb.query(database, projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);// 为这个uri注册观察者
        return c;
    }
    
    @Override
    public Uri insert(Uri _uri, ContentValues _initialValues) {
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        long rowID=database.insert(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, "quake", _initialValues);
        if(rowID > 0){
            Uri uri=ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(uri, null);// 通知这个uri的观察者数据更新
            return uri;
        }
        throw new SQLException("Failed to insert row into " + _uri);
    }
    
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        int count;
        switch(uriMatcher.match(uri)){
            case QUAKES:
                count=database.delete(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, where, whereArgs);
                break;
            case QUAKE_ID:
                String segment=uri.getPathSegments().get(1);
                count=database.delete(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, KEY_ID + "=" + segment + ( !TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    
    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        int count;
        switch(uriMatcher.match(uri)){
            case QUAKES:
                count=database.update(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, values, where, whereArgs);
                break;
            case QUAKE_ID:
                String segment=uri.getPathSegments().get(1);
                count=database.update(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, values, KEY_ID + "=" + segment + ( !TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);// 通知这个uri的观察者数据更新
        return count;
    }
    
    private static class EarthquakeDatabaseHelper extends SQLiteOpenHelper
    {
        
        private static final String TAG="EarthquakeProvider";
        private static final String DATABASE_NAME="earthquakes.db";
        private static final int DATABASE_VERSION=1;
        private static final String EARTHQUAKE_TABLE="earthquakes";
        private static final String DATABASE_CREATE="create table " + EARTHQUAKE_TABLE + " (" + KEY_ID + " integer primary key autoincrement, " + KEY_DATE + " INTEGER, " + KEY_DETAILS + " TEXT, " + KEY_SUMMARY + " TEXT, " + KEY_LOCATION_LAT + " FLOAT, " + KEY_LOCATION_LNG + " FLOAT, " + KEY_MAGNITUDE + " FLOAT, " + KEY_LINK + " TEXT);";
        // The underlying database
        private SQLiteDatabase earthquakeDB;
        
        public EarthquakeDatabaseHelper(Context context, String name, CursorFactory factory, int version){
            super(context, name, factory, version);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + EARTHQUAKE_TABLE);
            onCreate(db);
        }
    }
}