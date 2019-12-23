package com.sykomaniac.sunwidget.prefs;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.sykomaniac.sunwidget.R;

import static com.sykomaniac.sunwidget.Constants.CONFIGURATION_PREFERENCE_FILE_NAME;

public class SharedPreferenceAPI extends ContentProvider {
    public static String PREFERENCE_AUTHORITY;
    public static Uri BASE_URI;
    private static final String INT_TYPE = "integer";
    private static final String LONG_TYPE = "long";
    private static final String FLOAT_TYPE = "float";
    private static final String BOOLEAN_TYPE = "boolean";
    private static final String STRING_TYPE = "string";
    private static final int MATCH_DATA = 0x010000;
    private static UriMatcher matcher;

    private static void init(Context context){
        PREFERENCE_AUTHORITY = context.getString(R.string.api_authority);

        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(PREFERENCE_AUTHORITY, "*/*", MATCH_DATA);

        BASE_URI = Uri.parse("content://" + PREFERENCE_AUTHORITY);
    }

    @Override
    public boolean onCreate() {
        if(matcher == null){
            init(getContext());
        }
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + PREFERENCE_AUTHORITY + ".item";
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //Provide a read only access to the content provider

        MatrixCursor cursor = null;
        if (matcher.match(uri) == MATCH_DATA) {
            final String key = uri.getPathSegments().get(0);
            final String type = uri.getPathSegments().get(1);
            cursor = new MatrixCursor(new String[]{key});
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(CONFIGURATION_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            if (!sharedPreferences.contains(key))
                return cursor;
            MatrixCursor.RowBuilder rowBuilder = cursor.newRow();
            Object object = null;
            if (STRING_TYPE.equals(type)) {
                object = sharedPreferences.getString(key, null);
            } else if (BOOLEAN_TYPE.equals(type)) {
                object = sharedPreferences.getBoolean(key, false) ? 1 : 0;
            } else if (LONG_TYPE.equals(type)) {
                object = sharedPreferences.getLong(key, 0l);
            } else if (INT_TYPE.equals(type)) {
                object = sharedPreferences.getInt(key, 0);
            } else if (FLOAT_TYPE.equals(type)) {
                object = sharedPreferences.getFloat(key, 0f);
            } else {
                throw new IllegalArgumentException("Unsupported type " + uri);
            }
            rowBuilder.add(object);
        } else {
            throw new IllegalArgumentException("Unsupported uri " + uri);
        }
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
