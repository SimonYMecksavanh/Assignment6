package starter.ca.qc.johnabbott.cs.cs616.starter.notes.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * Client-side representation of a Note from the NoteServer "note" repository
 * @author Ian Clement (ian.clement@johnabbott.qc.a)
 */
public class Note {

    /* Server's date format */
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /* ===========================================================================================
       Inner classes (for Gson)
       ------------------------------------------------------------------------------------------ */

    /* inner class representing the JSON object substructure for a Note */
    private static class Links {

        private static class Href {
            public String href;
        }

        private Href self;      // used by Gson to parse JSON subobject
        private Href createdBy; // same
    }

    /* inner class representing the JSON object structure that contains an array of Notes */
    private static class NoteArray {

        private static class Embedded {
            private Note[] note;
        }

        private Embedded _embedded; // used by Gson to parse JSON subobject
    }

    /* ===========================================================================================
       Static methods
       ------------------------------------------------------------------------------------------ */

    /**
     * Parse the given JSON text into a Note.
     * @param json the JSON representation of a Note
     * @return the Note corresponding to the JSON representation.
     */
    public static Note parse(String json) {

        // create a Gson parser/formatter
        Gson gson = new GsonBuilder()
                        .setDateFormat(DATE_FORMAT)
                        .create();

        // parse the note using Gson
        Note note = gson.fromJson(json, Note.class);

        // extract the URLs from the subobject
        note.setUrl(note._links.self.href);
        note.setCreatedBy(note._links.createdBy.href);

        return note;
    }

    /**
     * Parse the given JSON text into a Note[]
     * @param json the JSON representation.
     * @return the Note[] corresponding to the JSON representation
     */
    public static Note[] parseArray(String json) {

        // create a Gson parser/formatter
        Gson gson = new GsonBuilder()
                        .setDateFormat(DATE_FORMAT)
                        .create();

        // parse the JSON representation into a NoteArray
        NoteArray notes = gson.fromJson(json, NoteArray.class);

        // extract the URL from each Note subobject
        for(Note note : notes._embedded.note)
            note.setUrl(note._links.self.href);
        return notes._embedded.note;
    }

    /* ===========================================================================================
       Fields
        - The @Exposed field distinction is used in JSON formatting only
       ------------------------------------------------------------------------------------------ */

    @Expose
    private String title;
    @Expose
    private String body;
    @Expose
    private int category;
    @Expose
    private Date reminder;
    @Expose
    private Date created;
    @Expose
    private String createdBy;

    private String url;
    private boolean hasReminder;
    private Links _links; // used by Gson to parse JSON subobject

    /* ===========================================================================================
       Constructors
       ------------------------------------------------------------------------------------------ */

    /**
     * Create a Note
     */
    public Note() {
    }

    /**
     * Create a Note
     * @param title
     * @param body
     * @param category
     * @param hasReminder
     * @param reminder
     * @param created
     */
    public Note(String title, String body, int category, boolean hasReminder, Date reminder, Date created) {
        this.body = body;
        this.category = category;
        this.hasReminder = hasReminder;
        this.reminder = reminder;
        this.title = title;
        this.created = created;
    }

    /* ===========================================================================================
       Methods
       ------------------------------------------------------------------------------------------ */

    /* getters and setters */

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public boolean isHasReminder() {
        return hasReminder;
    }

    public void setHasReminder(boolean hasReminder) {
        this.hasReminder = hasReminder;
    }

    public Date getReminder() {
        return reminder;
    }

    public void setReminder(Date reminder) {
        this.reminder = reminder;
        hasReminder = this.reminder != null;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Format the current Note as its JSON reprentation
     * @return the JSON representation of the Note
     */
    public String format() {
        // create a Gson parser/formatter
        // formats only exposed fields.
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls()
                .setDateFormat(DATE_FORMAT)
                .create();

        // convert to JSON using Gson formatter
        return gson.toJson(this);
    }

}
