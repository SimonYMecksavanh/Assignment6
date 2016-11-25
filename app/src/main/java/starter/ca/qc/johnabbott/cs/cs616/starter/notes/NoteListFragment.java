package starter.ca.qc.johnabbott.cs.cs616.starter.notes;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import starter.ca.qc.johnabbott.cs.cs616.starter.notes.model.DatabaseException;
import starter.ca.qc.johnabbott.cs.cs616.starter.notes.model.DatabaseHandler;
import starter.ca.qc.johnabbott.cs.cs616.starter.notes.model.Note;
//import starter.ca.qc.johnabbott.cs.cs616.starter.notes.server.Note;
import starter.ca.qc.johnabbott.cs.cs616.starter.notes.server.AsyncHttpRequest;
import starter.ca.qc.johnabbott.cs.cs616.starter.notes.server.HttpProgress;
import starter.ca.qc.johnabbott.cs.cs616.starter.notes.server.HttpResponse;

/**
 * A placeholder fragment containing a simple view.
 */
public class NoteListFragment extends Fragment {

    //private int i;
    public interface OnNoteChosen{
        void onNoteChosen(Note note);
    }

    private ListView notes;
    private Spinner spinner;
    private ArrayAdapter<Note> adapter;
    private OnNoteChosen listener;
    private String url;

    public NoteListFragment() {
    }

    public void setOnNoteChosenListener(OnNoteChosen listener) {
        this.listener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_note_list, container, false);


        //Get the ListView
        notes = (ListView) root.findViewById(R.id.notes_ListView);
        spinner = (Spinner) root.findViewById(R.id.sort_Spinner);

        refreshNotes();

        //Populate spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource( this.getContext(),
                R.array.sort_choices, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(spinnerAdapter);


        //Create and initialize the adapter
        adapter = new NoteDataAdapter(this.getContext());
        notes.setAdapter(adapter);

        //Get the data from the database
        DatabaseHandler dbh = new DatabaseHandler(getContext());
        final List<Note> data;
        try{
            //Add the data to the list
            data = dbh.getNoteTable().readAll();
            AsyncHttpRequest task = new AsyncHttpRequest(url, AsyncHttpRequest.Method.GET);
            task.setOnResponseListener(new AsyncHttpRequest.OnResponse() {
                @Override
                public void onResult(HttpResponse response) {
                    if(response.getStatus() == 200){
                        //List<Note> data = Arrays.asList(Note.parseArray(response.getBody()));
                    }
                }

                @Override
                public void onProgress(HttpProgress progress) {

                }

                @Override
                public void onError(Exception e) {

                }
            });
           // task.execute();
            adapter.addAll(data);

            //Set up the spinner for sorting
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(final AdapterView<?> parent, View view, final int position, long id) {

                    String selectedItem = (String) parent.getSelectedItem();
                    //Sort notes by their title Z -> A
                    if (selectedItem.equals("Title")) {
                        Collections.sort(data, new Comparator<Note>() {
                            @Override
                            public int compare(Note o1, Note o2) {
                                return o2.getTitle().compareTo(o1.getTitle());

                            }
                        });
                    }

                    //Group the notes bases on  their category
                    if (selectedItem.equals("Category")) {
                        Collections.sort(data, new Comparator<Note>() {
                            @Override
                            public int compare(Note o1, Note o2) {

                                return o1.getCategory() - o2.getCategory();

                            }
                        });

                    }

                    //Sort the note by their creation date present -> pass
                    if (selectedItem.equals("Creation Date")) {
                        Collections.sort(data, new Comparator<Note>() {
                            @Override
                            public int compare(Note o1, Note o2) {

                                return o2.getCreated().compareTo(o1.getCreated());
                            }
                        });

                    }

                    //NullPointer Exception


                if (selectedItem.equals("Reminder")) {
                    Collections.sort(data, new Comparator<Note>() {
                        @Override
                        public int compare(Note o1, Note o2) {

                            if (o1.isHasReminder() == true && o2.isHasReminder() == false)
                                return -1;
                            else if (o2.isHasReminder() == true && o1.isHasReminder() == false)
                                return 1;
                            else
                                return o2.getReminder().compareTo(o1.getReminder());



                        }
                    });

                }

                    adapter.clear();
                    adapter.addAll(data);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

            });

        } catch (DatabaseException e) {
            e.printStackTrace();
        }


        //Display data for the selected item in a Toast
        notes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // Toast.makeText(getContext(), adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();

              //  if (listener != null)
               //     listener.onNoteChosen(adapter.getItem(position).getId());
               // Intent intent = new Intent(getContext(), NoteActivity.class);
                //intent.putExtra("ID", adapter.getItem(position).getId());
               // startActivityForResult(intent, 123);

            }
        });

        return root;
    }


    //Adapter for the notes
    private class NoteDataAdapter extends ArrayAdapter<Note> {

        public NoteDataAdapter(Context context) {
            super(context, -1);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //inflate or reuse previously inflated UI
            View root;
            if (convertView != null)
                root = convertView;
            else {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                root = inflater.inflate(R.layout.list_item_note, parent, false);
            }

            //Get the current note
            Note note = getItem(position);

            //Retrieve the objects from the view
            ImageView category = (ImageView) root.findViewById(R.id.category_ImageView);
            TextView title = (TextView) root.findViewById(R.id.title_TextView);
            TextView body = (TextView) root.findViewById(R.id.body_TextView);
            ImageView reminder = (ImageView) root.findViewById(R.id.reminder_ImageView);

            category.setBackgroundColor(note.getCategory());
            title.setText(note.getTitle());
            body.setText(note.getBody());


            if(note.getReminder() != null)
                reminder.setImageResource(R.drawable.alarm_check);
            else if (note.getReminder() == null)
                reminder.setImageResource(R.drawable.alarm_off);

            return root;
        }

        //Get the id of the current note
        @Override
        public long getItemId(int position) {
            return getItem(position).getId(); //Use Note IDs }
        }
    }

    public void refreshNotes() {

        ArrayAdapter<Note> noteAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        DatabaseHandler dbh = new DatabaseHandler(getContext());
        final List<Note> data;
        try {
            data = dbh.getNoteTable().readAll();
            noteAdapter.addAll(data);
            notes.setAdapter(noteAdapter);

            notes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    //Toast.makeText(getContext(), data.get(i).toString(), Toast.LENGTH_LONG).show();

                    // an Event!
                    if(listener != null)
                        listener.onNoteChosen(data.get(i));

                }
            });

        } catch (DatabaseException e) {
            e.printStackTrace();
        }


    }

    public void setUrl (String url){
        this.url = url;
    }
}
