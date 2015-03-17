package ca.josephroque.bowlingcompanion.database;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ca.josephroque.bowlingcompanion.database.Contract.*;

/**
 * Created by josephroque on 15-03-12.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.database
 * in project Bowling Companion
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    /** Tag for the database to be used in log output */
    private static final String TAG = "DBHelper";
    /** Name of the database */
    private static final String DATABASE_NAME = "bowlingdata";
    /** Version of the database, incremented with changes */
    private static final int DATABASE_VERSION = 1;

    /** Instance of the database */
    private SQLiteDatabase mDatabase;

    /** Singleton instance of the DatabaseHelper */
    private static DatabaseHelper sDatabaseHelperInstance = null;

    /**
     * Returns a singleton instance of DatabaseHelper
     *
     * @param context the current activity
     * @return static instance of DatabaseHelper
     */
    public static DatabaseHelper getInstance(Context context)
    {
        if (sDatabaseHelperInstance == null)
        {
            sDatabaseHelperInstance = new DatabaseHelper(context);
        }
        return sDatabaseHelperInstance;
    }

    /**
     * Private constructor for singleton access
     *
     * @param context the current activity
     */
    private DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        mDatabase = db;

        /*
         * Defines tables for the database, creating columns and constraints
         */

        mDatabase.execSQL("CREATE TABLE "
                + BowlerEntry.TABLE_NAME + "("
                + BowlerEntry._ID + " INTEGER PRIMARY KEY, "
                + BowlerEntry.COLUMN_BOWLER_NAME + " TEXT NOT NULL COLLATE NOCASE, "
                + BowlerEntry.COLUMN_DATE_MODIFIED + " TEXT NOT NULL"
                + ");");
        mDatabase.execSQL("CREATE TABLE "
                + LeagueEntry.TABLE_NAME + "("
                + LeagueEntry._ID + " INTEGER PRIMARY KEY, "
                + LeagueEntry.COLUMN_LEAGUE_NAME + " TEXT NOT NULL COLLATE NOCASE, "
                + LeagueEntry.COLUMN_NUMBER_OF_GAMES + " INTEGER NOT NULL, "
                + LeagueEntry.COLUMN_DATE_MODIFIED + " TEXT NOT NULL, "
                + LeagueEntry.COLUMN_IS_EVENT + " INTEGER NOT NULL DEFAULT 0, "
                + LeagueEntry.COLUMN_BOWLER_ID + " INTEGER NOT NULL"
                        + " REFERENCES " + BowlerEntry.TABLE_NAME
                        + " ON UPDATE CASCADE ON DELETE CASCADE, "
                + "CHECK (" + LeagueEntry.COLUMN_NUMBER_OF_GAMES + " > 0 AND " + LeagueEntry.COLUMN_NUMBER_OF_GAMES + " <= 20), "
                + "CHECK (" + LeagueEntry.COLUMN_IS_EVENT + " = 0 OR " + LeagueEntry.COLUMN_IS_EVENT + " = 1)"
                + ");");
        mDatabase.execSQL("CREATE TABLE "
                + SeriesEntry.TABLE_NAME + " ("
                + SeriesEntry._ID + " INTEGER PRIMARY KEY, "
                + SeriesEntry.COLUMN_SERIES_DATE + " TEXT NOT NULL, "
                + SeriesEntry.COLUMN_LEAGUE_ID + " INTEGER NOT NULL"
                        + " REFERENCES " + LeagueEntry.TABLE_NAME
                        + " ON UPDATE CASCADE ON DELETE CASCADE"
                + ");");
        mDatabase.execSQL("CREATE TABLE "
                + GameEntry.TABLE_NAME + " ("
                + GameEntry._ID + " INTEGER PRIMARY KEY, "
                + GameEntry.COLUMN_GAME_NUMBER + " INTEGER NOT NULL, "
                + GameEntry.COLUMN_SCORE + " INTEGER NOT NULL DEFAULT 0, "
                + GameEntry.COLUMN_IS_MANUAL + " INTEGER NOT NULL DEFAULT 0, "
                + GameEntry.COLUMN_IS_LOCKED + " INTEGER NOT NULL DEFAULT 0, "
                + GameEntry.COLUMN_SERIES_ID + " INTEGER NOT NULL"
                        + " REFERENCES " + SeriesEntry.TABLE_NAME
                        + " ON UPDATE CASCADE ON DELETE CASCADE, "
                + "CHECK (" + GameEntry.COLUMN_GAME_NUMBER + " >= 1 AND " + GameEntry.COLUMN_GAME_NUMBER + " <= 20), "
                + "CHECK (" + GameEntry.COLUMN_IS_LOCKED + " = 0 OR " + GameEntry.COLUMN_IS_LOCKED + " = 1), "
                + "CHECK (" + GameEntry.COLUMN_IS_MANUAL + " = 0 OR " + GameEntry.COLUMN_IS_MANUAL + " = 1), "
                + "CHECK (" + GameEntry.COLUMN_SCORE + " >= 0 OR " + GameEntry.COLUMN_SCORE + " <= 450)"
                + ");");
        mDatabase.execSQL("CREATE TABLE "
                + FrameEntry.TABLE_NAME + " ("
                + FrameEntry._ID + " INTEGER PRIMARY KEY, "
                + FrameEntry.COLUMN_FRAME_NUMBER + " INTEGER NOT NULL, "
                + FrameEntry.COLUMN_IS_ACCESSED + " INTEGER NOT NULL DEFAULT 0, "
                + FrameEntry.COLUMN_PIN_STATE[0] + " TEXT NOT NULL DEFAULT '00000', "
                + FrameEntry.COLUMN_PIN_STATE[1] + " TEXT NOT NULL DEFAULT '00000', "
                + FrameEntry.COLUMN_PIN_STATE[2] + " TEXT NOT NULL DEFAULT '00000', "
                + FrameEntry.COLUMN_FOULS + " TEXT NOT NULL DEFAULT '0', "
                + FrameEntry.COLUMN_GAME_ID + " INTEGER NOT NULL"
                        + " REFERENCES " + GameEntry.TABLE_NAME
                        + " ON UPDATE CASCADE ON DELETE CASCADE, "
                + "CHECK (" + FrameEntry.COLUMN_FRAME_NUMBER + " >= 1 AND " + FrameEntry.COLUMN_FRAME_NUMBER + " <= 10), "
                + "CHECK (" + FrameEntry.COLUMN_IS_ACCESSED + " = 0 OR " + FrameEntry.COLUMN_IS_ACCESSED + " = 1)"
                + ");");
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
        if (!db.isReadOnly())
        {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        /**
         * If an older version of the database exists, all the tables and data are dropped
         * and the table is recreated.
         *
         * In future version, if database is updated then tables should be altered,
         * not dropped
         */
        Log.w(TAG, "Upgrading database from version" + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + FrameEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GameEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SeriesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LeagueEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BowlerEntry.TABLE_NAME);
        onCreate(db);
    }

    /**
     * Displays a dialog to the user to delete data in the database
     *
     * @param context context instance to create dialog
     * @param deleter interface which should be overridden to call relevant data deletion method
     * @param name identifier for data to be deleted
     */
    public static void deleteData(final Context context, final DataDeleter deleter, final String name)
    {
        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(context);
        deleteBuilder.setMessage("WARNING: This action cannot be undone! Delete all data for " + name + "?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        deleter.execute();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * Provides a method which can be overridden to delete specific data
     * if the deleteData method is successful (user selects 'Delete')
     */
    public static interface DataDeleter
    {
        /**
         * Must be overriden to provide access to a relevant method which
         * should be used to delete data from the database
         */
        public void execute();
    }
}