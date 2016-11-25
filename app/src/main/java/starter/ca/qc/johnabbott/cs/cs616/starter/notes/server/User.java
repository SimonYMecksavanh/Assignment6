package starter.ca.qc.johnabbott.cs.cs616.starter.notes.server;

/**
 * Created by 1473031 on 2016-11-22.
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Client-side representation of a User from the NoteServer "note" repository
 * @author Ian Clement (ian.clement@johnabbott.qc.a)
 */
public class User {

     /* ===========================================================================================
       Inner classes (for Gson)
       ------------------------------------------------------------------------------------------ */

    /* inner class representing the JSON object substructure for a User */
    private static class Links {

        private static class Href {
            public String href;
        }

        private Href self;
    }

    /* inner class representing the JSON object structure that contains an array of Users */
    private static class UserArray {

        private static class Embedded {
            private User[] user;
        }

        private Embedded _embedded;
    }

    /* ===========================================================================================
       Static methods
       ------------------------------------------------------------------------------------------ */

    /**
     * Parse the given JSON text into a User.
     * @param json the JSON representation of a User
     * @return the User corresponding to the JSON representation.
     */
    public static User parse(String json) {

        // create a Gson parser/formatter
        Gson gson = new GsonBuilder()
                .create();

        // parse the note using Gson
        User user = gson.fromJson(json, User.class);

        // extract the URL from the subobject
        user.setUrl(user._links.self.href);

        return user;
    }

    /**
     * Parse the given JSON text into a User[]
     * @param json the JSON representation.
     * @return the User[] corresponding to the JSON representation
     */
    public static User[] parseArray(String json) {

        // create a Gson parser/formatter
        Gson gson = new GsonBuilder()
                .create();

        // parse the JSON representation into a UserArray
        UserArray users = gson.fromJson(json, UserArray.class);

        // extract the URL from each User subobject
        for(User user : users._embedded.user)
            user.setUrl(user._links.self.href);

        return users._embedded.user;
    }

    /* ===========================================================================================
       Fields
        - The @Exposed field distinction is used in JSON formatting only
       ------------------------------------------------------------------------------------------ */

    @Expose
    private String name;
    @Expose
    private String password;
    @Expose
    private String email;

    private String url;
    private Links _links;  // used by Gson to parse JSON subobject
    private List<Note> created;

    /* ===========================================================================================
       Constructors
       ------------------------------------------------------------------------------------------ */

    /**
     * Create a User
     */
    public User() {
    }

    /**
     * Create a User
     * @param name
     * @param password
     * @param email
     */
    public User(String name, String password, String email) {
        this.name = name;
        this.password = hashPassword(password);
        this.email = email;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Note> getCreated() {
        return created;
    }

    public void setCreated(List<Note> created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "User{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    /**
     * Checks the provided password against the User's password. Uses SHA-1 hashing.
     * @param password The password to check.
     * @return True if the passwords are the same.
     */
    public boolean isPassword(String password) {
        return hashPassword(password).equals(this.password);
    }

    // Used to hash passwords.
    private String hashPassword(String password) {

        // http://stackoverflow.com/questions/3103652/hash-string-via-sha-256-in-java
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            return String.format("%040x", new BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e1) {
            // should not happen
        }
        return null;
    }

    /**
     * Format the current User as its JSON reprentation
     * @return the JSON representation of the User
     */
    public String format() {
        // create a Gson parser/formatter
        // formats only exposed fields.
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls()
                .create();

        // convert to JSON using Gson formatter
        return gson.toJson(this);
    }
}
