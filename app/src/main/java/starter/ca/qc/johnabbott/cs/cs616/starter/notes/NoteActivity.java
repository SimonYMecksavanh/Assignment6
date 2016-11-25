package starter.ca.qc.johnabbott.cs.cs616.starter.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import starter.ca.qc.johnabbott.cs.cs616.starter.notes.model.DatabaseException;
import starter.ca.qc.johnabbott.cs.cs616.starter.notes.model.DatabaseHandler;
import starter.ca.qc.johnabbott.cs.cs616.starter.notes.model.Note;

public class NoteActivity extends AppCompatActivity {

    public static class params {
        public static final String ID = "ID";

        // make params non-instantiable
        private params() {}
    }

    private NoteFragment noteFragment;
    private boolean inEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noteFragment = (NoteFragment) getSupportFragmentManager().findFragmentById(R.id.note_Fragment);

        // check the launching intent to determine the mode of use
        Intent intent = getIntent();

        // have this activity been launched for editing
        if(intent.hasExtra(params.ID)) {

            // set the fragment to edit mode
            inEditMode = true;

            // get note from DB and load it
            long id = intent.getLongExtra(params.ID, -1);
            Note note = null;
            try {
                DatabaseHandler dbh = new DatabaseHandler(this);
                note = dbh.getNoteTable().read(id);
            }
            catch (DatabaseException e) {
                e.printStackTrace();
            }

            // load the fragment
            noteFragment.loadNote(note);
        }
        else
            inEditMode = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_save) {

            // edit or update the note as needed.
            DatabaseHandler dbh = new DatabaseHandler(this);
            try {
                if(inEditMode)
                    dbh.getNoteTable().update(noteFragment.getNote());
                else
                    dbh.getNoteTable().create(noteFragment.getNote());

                // return success
                setResult(RESULT_OK);
                finish();

            } catch (DatabaseException e) {
                e.printStackTrace();
            }

        }

        return super.onOptionsItemSelected(item);
    }
}
