package starter.ca.qc.johnabbott.cs.cs616.starter.notes.model;

/**
 * Created by ian on 2016-09-05.
 */
public class Column {

    private String name;
    private String type;
    private boolean notNull;
    private boolean unique;
    private boolean primaryKey;
    private boolean autoincrement;

    public Column(String name, String type) {
        this.name = name;
        this.type = type;

        // attributes
        notNull = false;
        unique = false;
        primaryKey = false;
        autoincrement = false;
    }

    /* Getters and setters
    *
    *   - setters return "this" to allow cascading setters.
    * */

    public String getName() {
        return name;
    }

    public Column setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public Column setType(String type) {
        this.type = type;
        return this;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public Column notNull() {
        this.notNull = true;
        return this;
    }

    public boolean isUnique() {
        return unique;
    }

    public Column unique() {
        this.unique = true;
        return this;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public Column primaryKey() {
        this.primaryKey = true;
        return this;
    }

    public boolean isAutoincrement() {
        return autoincrement;
    }

    public Column autoincrement() {
        this.autoincrement = true;
        return this;
    }

    /**
     * Convert the column to SQL CREATE TABLE syntax
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ").append(type);
        if(primaryKey)
            sb.append(" PRIMARY KEY");
        if(notNull)
            sb.append(" NOT NULL");
        if(unique)
            sb.append(" UNIQUE");
        if(autoincrement)
            sb.append(" AUTOINCREMENT");
        return sb.toString();
    }
}
