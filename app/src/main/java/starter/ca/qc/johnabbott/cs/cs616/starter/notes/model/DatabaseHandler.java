package starter.ca.qc.johnabbott.cs.cs616.starter.notes.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by ian on 15-08-26.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    /**
     * Filename to store the local database (on device).
     */
    private static final String DATABASE_FILE_NAME = "notes.db";

    /**
     * Update this field for every structural change to the database.
     */
    private static final int DATABASE_VERSION = 1;


    // TODO: maybe these could be replaced with a map of tables...

    /*  DatabaseHandler Tables */
    private Table<Note> noteTable;


    /**
     * Construct a new database handler.
     * @param context The application context.
     */
    public DatabaseHandler(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);

        noteTable = TableFactory.makeFactory(this, Note.class)
                                .setSeedData(NoteData.getData())
                                .getTable();

        Log.d("DEBUG", noteTable.getCreateTableStatement());
        Log.d("DEBUG", noteTable.getDropTableStatement());
    }

    /**
     * Get the Category table.
     * @return The Category table.
     */
    /*public CategoryTable getCategoryTable() {
        return categoryTable;
    }*/

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(noteTable.getCreateTableStatement());

        if(noteTable.hasInitialData()) {
            noteTable.initialize(database);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w("DEBUG", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        //database.execSQL("DROP TABLE IF EXISTS " + categoryTable.getTableName());
        database.execSQL(noteTable.getDropTableStatement());

        onCreate(database);
    }

    public Table<Note> getNoteTable() {
        return noteTable;
    }


}
