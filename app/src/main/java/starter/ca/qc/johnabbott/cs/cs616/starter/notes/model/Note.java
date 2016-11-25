package starter.ca.qc.johnabbott.cs.cs616.starter.notes.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Simple class to represent a note.
 * @author Ian Clement (ian.clement@johnabbott.qc.ca)
 */
public class Note implements Serializable {

    private long id;
    private String title;
    private String body;
    private int category;
    private boolean hasReminder;
    private Date reminder;
    private Date created;

    public Note() {
        id = -1;
    }

    public Note(long id) {
        this.id = id;
    }

    public Note(String title, String body, int category, boolean hasReminder, Date reminder, Date created) {
        this();
        this.body = body;
        this.category = category;
        this.hasReminder = hasReminder;
        this.reminder = reminder;
        this.title = title;
        this.created = created;
    }

    public Note setId(long id) {
        this.id = id;
        return this;
    }

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public Note setBody(String body) {
        this.body = body;
        return this;
    }

    public int getCategory() {
        return category;
    }

    public Note setCategory(int category) {
        this.category = category;
        return this;
    }

    public boolean isHasReminder() {
        return hasReminder;
    }

    public Note setHasReminder(boolean hasReminder) {
        this.hasReminder = hasReminder;
        return this;
    }

    public Date getReminder() {
        return reminder;
    }

    public Note setReminder(Date reminder) {
        this.reminder = reminder;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Note setTitle(String title) {
        this.title = title;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public Note setCreated(Date created) {
        this.created = created;
        return this;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", category=" + category +
                ", hasReminder=" + hasReminder +
                ", reminder=" + reminder +
                ", created=" + created +
                '}';
    }
}
