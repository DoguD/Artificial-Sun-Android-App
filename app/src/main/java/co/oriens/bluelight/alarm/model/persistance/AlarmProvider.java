package co.oriens.bluelight.alarm.model.persistance;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import co.github.androidutils.logger.LogcatLogWriter;
import co.github.androidutils.logger.Logger;
import co.oriens.bluelight.BuildConfig;

public class AlarmProvider extends ContentProvider {
    private AlarmDatabaseHelper mOpenHelper;

    private Logger log;

    private static final int ALARMS = 1;
    private static final int ALARMS_ID = 2;
    private static final UriMatcher sURLMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURLMatcher.addURI(BuildConfig.APPLICATION_ID + ".model", "alarm", ALARMS);
        sURLMatcher.addURI(BuildConfig.APPLICATION_ID + ".model", "alarm/#", ALARMS_ID);
    }

    public AlarmProvider() {
    }

    @Override
    public boolean onCreate() {
        log = new Logger();
        log.addLogWriter(LogcatLogWriter.getInstance());
        mOpenHelper = new AlarmDatabaseHelper(getContext(), log);
        return true;
    }

    @Override
    public Cursor query(Uri url, String[] projectionIn, String selection, String[] selectionArgs, String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Generate the body of the query
        int match = sURLMatcher.match(url);
        switch (match) {
        case ALARMS:
            qb.setTables("alarms");
            break;
        case ALARMS_ID:
            qb.setTables("alarms");
            qb.appendWhere("_id=");
            qb.appendWhere(url.getPathSegments().get(1));
            break;
        default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret;
        try {
            ret = qb.query(db, projectionIn, selection, selectionArgs, null, null, sort);
        } catch (SQLException e) {
            log.e("query failed because of " + e.getMessage() + ", recreating DB");
            db.execSQL("DROP TABLE IF EXISTS alarms");
            // I know this is not nice to call onCreate() by ourselves :-)
            mOpenHelper.onCreate(db);
            ret = qb.query(db, projectionIn, selection, selectionArgs, null, null, sort);
        }
        if (ret == null) {
            log.e("AlarmsManager.query: failed");
        } else {
            ret.setNotificationUri(getContext().getContentResolver(), url);
        }

        return ret;
    }

    @Override
    public String getType(Uri url) {
        int match = sURLMatcher.match(url);
        switch (match) {
        case ALARMS:
            return "vnd.android.cursor.dir/alarms";
        case ALARMS_ID:
            return "vnd.android.cursor.item/alarms";
        default:
            throw new IllegalArgumentException("Unknown URL");
        }
    }

    @Override
    public int update(Uri url, ContentValues values, String where, String[] whereArgs) {
        int count;
        long rowId = 0;
        int match = sURLMatcher.match(url);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (match) {
        case ALARMS_ID: {
            String segment = url.getPathSegments().get(1);
            rowId = Long.parseLong(segment);
            count = db.update("alarms", values, "_id=" + rowId, null);
            break;
        }
        default: {
            throw new UnsupportedOperationException("Cannot update URL: " + url);
        }
        }
        log.d("*** notifyChange() rowId: " + rowId + " url " + url);
        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }

    @Override
    public Uri insert(Uri url, ContentValues initialValues) {
        if (sURLMatcher.match(url) != ALARMS) throw new IllegalArgumentException("Cannot insert into URL: " + url);

        Uri newUrl = mOpenHelper.commonInsert(initialValues);
        getContext().getContentResolver().notifyChange(newUrl, null);
        return newUrl;
    }

    @Override
    public int delete(Uri url, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sURLMatcher.match(url)) {
        case ALARMS:
            count = db.delete("alarms", where, whereArgs);
            break;
        case ALARMS_ID:
            String segment = url.getPathSegments().get(1);
            if (TextUtils.isEmpty(where)) {
                where = "_id=" + segment;
            } else {
                where = "_id=" + segment + " AND (" + where + ")";
            }
            count = db.delete("alarms", where, whereArgs);
            break;
        default:
            throw new IllegalArgumentException("Cannot delete from URL: " + url);
        }

        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }
}
