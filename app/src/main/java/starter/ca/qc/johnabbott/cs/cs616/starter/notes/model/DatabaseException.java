package starter.ca.qc.johnabbott.cs.cs616.starter.notes.model;

import java.text.ParseException;

/**
 * Created by ian on 2016-09-05.
 */
public class DatabaseException extends Throwable {
    public DatabaseException(String s) {
        super(s);
    }

    public DatabaseException(Exception e) {
        super(e);
    }
}
