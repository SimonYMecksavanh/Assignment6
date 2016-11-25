package starter.ca.qc.johnabbott.cs.cs616.starter.notes.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by 1473031 on 2016-09-27.
 */
public class NoteTable extends Table<Note> {
    /**
     * ISO 8601 standard date format.
     */
    private static final SimpleDateFormat isoISO8601 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss");

    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_BODY = "body";
    private static final String COLUMN_HAS_REMINDER = "hasReminder";
    private static final String COLUMN_REMINDER = "reminder";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_CREATED = "created";

    /**
     * Create a ContactTable with the DB handler.
     *
     * @param dbh
     */
    public NoteTable(SQLiteOpenHelper dbh) {
        super(dbh, "note");

        //Create table structure
        addColumn(new Column(COLUMN_TITLE, "TEXT").unique().notNull());
        addColumn(new Column(COLUMN_BODY, "TEXT"));
        addColumn(new Column(COLUMN_HAS_REMINDER, "INTEGER"));
        addColumn(new Column(COLUMN_REMINDER, "TEXT"));
        addColumn(new Column(COLUMN_CATEGORY, "INTEGER").notNull());
        addColumn(new Column(COLUMN_CREATED, "TEXT").notNull());


    }

    @Override
    public ContentValues toContentValues(Note element) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, element.getTitle());
        values.put(COLUMN_BODY, element.getBody());
        values.put(COLUMN_HAS_REMINDER, element.isHasReminder());
        values.put(COLUMN_REMINDER, element.getReminder() != null ? isoISO8601.format(element.getReminder()) : null);
        values.put(COLUMN_CATEGORY, element.getCategory());
        values.put(COLUMN_CREATED, element.getCreated() != null ? isoISO8601.format(element.getCreated()) : null);

        return values;
    }


    @Override
    public Note fromCursor(Cursor cursor) throws DatabaseException {
        Note note = new Note(cursor.getLong(0));

        // get name and phone number
        note.setTitle(cursor.getString(1));
        note.setBody(cursor.getString(2));

        note.setHasReminder(cursor.getInt(3) > 0);

        if (!cursor.isNull(4)) {
            try {
                note.setReminder(isoISO8601.parse(cursor.getString(4)));
            } catch (ParseException e) {
                throw new DatabaseException(e);
            }
        }

        if (!cursor.isNull(5))
            note.setCategory(cursor.getInt(5));

        if (!cursor.isNull(6)) {
            try {
                note.setCreated(isoISO8601.parse(cursor.getString(6)));
            } catch (ParseException e) {
                throw new DatabaseException(e);
            }
        }


        return note;
    }


    @Override
    public String getId(Note element) {
        return String.valueOf(element.getId());
    }

    @Override
    public boolean hasInitialData() {
        return true;
    }

    @Override
    public void initialize(SQLiteDatabase database) {
        for (Note note : NoteData.getData())
            database.insertOrThrow(getName(), null, toContentValues(note));
    }

    @Override
    public Long create(Note element) throws DatabaseException {
        long id = super.create(element);
        element.setId(id);
        return id;
    }
}