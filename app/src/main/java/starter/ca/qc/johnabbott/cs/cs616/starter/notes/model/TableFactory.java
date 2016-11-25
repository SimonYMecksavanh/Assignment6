package starter.ca.qc.johnabbott.cs.cs616.starter.notes.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ian on 2016-10-04.
 */
public class TableFactory<T> {


    public static <T> TableFactory<T> makeFactory(SQLiteOpenHelper dbh, Class<T> model) {
        return new TableFactory<>(dbh, model);
    }

    private static SimpleDateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss");;

    private SQLiteOpenHelper dbh;
    private Class<T> model;
    private List<T> seedData;

    private TableFactory(SQLiteOpenHelper dbh, Class<T> model) {
        this.dbh = dbh;
        this.model = model;
    }

    public TableFactory<T> setSeedData(List<T> seedData) {
        this.seedData = seedData;
        return this;
    }

    public Table<T> getTable() {

        final String tableName = stripPackageName(model.getName()).toLowerCase();

        Table<T> table = new Table<T>(dbh, tableName) {

            @Override
            public T fromCursor(Cursor cursor) throws DatabaseException {

                T element = null;
                try {
                    element = model.newInstance();
                } catch (InstantiationException e) {
                    throw new DatabaseException(e);
                } catch (IllegalAccessException e) {
                    throw new DatabaseException(e);
                }

                // get order to extract from cursor
                String[] columnNames = getSelectAll();

                for (Method method : getSetters(model)) {

                    String methodName = stripPackageName(method.getName());

                    // determine column name
                    String columnName;
                    if (methodName.equals("setId"))
                        columnName = "_id";
                    else {
                        methodName = methodName.replaceAll("^set", "");
                        // lowercase field name
                        char first = methodName.charAt(0);
                        columnName = Character.toLowerCase(first) + methodName.substring(1);
                    }

                    int pos = -1;
                    for (int i = 0; i < columnNames.length && pos < 0; i++)
                        if (columnNames[i].equals(columnName))
                            pos = i;

                    // setter does not correspond to a column skip
                    if (pos < 0)
                        continue;

                    // TODO: NOT NULL constraints, for now skip NULL columns
                    if(cursor.isNull(pos))
                        continue;

                    Class<?>[] parameters = method.getParameterTypes();
                    if (parameters.length != 1)
                        throw new DatabaseException("Error: setter " + method.toString() + " takes more than one argument...");

                    try {
                        switch (parameters[0].getName()) {
                            case "java.lang.Integer":
                            case "int":
                                method.invoke(element, cursor.getInt(pos));
                                break;

                            case "java.lang.Long":
                            case "long":
                                method.invoke(element, cursor.getLong(pos));
                                break;

                            case "java.lang.Boolean":
                            case "boolean":
                                method.invoke(element, cursor.getInt(pos) == 1);
                                break;

                            case "java.lang.Double":
                            case "double":
                                method.invoke(element, cursor.getDouble(pos));
                                break;

                            case "java.lang.Float":
                            case "float":
                                method.invoke(element, cursor.getFloat(pos));
                                break;

                            case "java.lang.String":
                                method.invoke(element, cursor.getString(pos));
                                break;

                            case "java.util.Date":
                                method.invoke(element, iso8601.parse(cursor.getString(pos)));
                                break;

                            default:
                                continue;

                        }
                    } catch (InvocationTargetException e) {
                        throw new DatabaseException(e);
                    } catch (IllegalAccessException e) {
                        throw new DatabaseException(e);
                    } catch (ParseException e) {
                        throw new DatabaseException(e);
                    }
                }

                return element;
            }

            @Override
            public ContentValues toContentValues(T element) {
                ContentValues contentValues = new ContentValues();
                for(Method method : getGetters(model)) {
                    String methodName = stripPackageName(method.getName());

                    // determine column name
                    String columnName;
                    if (methodName.equals("getId"))
                        columnName = "_id";
                    else {
                        methodName = methodName.replaceAll("^get","").replaceAll("^is", "");
                        // lowercase field name
                        char first = methodName.charAt(0);
                        columnName = Character.toLowerCase(first) + methodName.substring(1);
                    }

                    Object value = null;
                    try {
                        value = method.invoke(element);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    // TODO: NOT NULL constraints

                    // check ID is set (use -1 as not set)
                    if(columnName.equals("_id") && (Long) value == -1l)
                        continue;

                    switch(method.getReturnType().getName()) {
                        case "java.lang.Integer":
                        case "int":
                            contentValues.put(columnName, (Integer) value);
                            break;

                        case "java.lang.Long":
                        case "long":
                            contentValues.put(columnName, (Long) value);
                            break;

                        case "java.lang.Boolean":
                        case "boolean":
                            if(value == null)  // see above
                                contentValues.put(columnName, (Boolean) null);
                            else
                                contentValues.put(columnName, (Boolean) value ? 1 : 0);
                            break;

                        case "java.lang.Double":
                        case "double":
                            contentValues.put(columnName, (Double) value);
                            break;

                        case "java.lang.Float":
                        case "float":
                            contentValues.put(columnName, (Float) value);
                            break;

                        case "java.lang.String":
                            contentValues.put(columnName, (String) value);
                            break;

                        case "java.util.Date":
                            if(value == null) // see above
                                contentValues.put(columnName, (String) null);
                            else
                                contentValues.put(columnName, iso8601.format((Date) value));
                            break;

                        default:
                            continue;

                    }

                }
                return contentValues;
            }


            @Override
            public boolean hasInitialData() {
                return seedData != null;
            }

            @Override
            public void initialize(SQLiteDatabase database) {
                if(seedData != null) {
                    for(T element : seedData) {
                        ContentValues values = toContentValues(element);
                        database.insertOrThrow(tableName, null, values);
                    }
                }
            }

            @Override
            public String getId(T element) {
                try {
                    Method method = model.getMethod("getId");
                    return String.valueOf((Long) method.invoke(element));
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return null; // should not occur
            }

        };


        for(Method method : getGetters(model)) {

            String methodName = stripPackageName(method.getName());
            if(methodName.equals("getId"))
                continue;

            methodName = methodName.replaceAll("^get","").replaceAll("^is", "");

            // lowercase field name
            char first = methodName.charAt(0);
            methodName = Character.toLowerCase(first) + methodName.substring(1);

            // determine the sqlite datatype. Unsupported data-types are skipped.
            String dataType;
            switch(method.getReturnType().getName()) {
                case "java.lang.Integer":
                case "int":
                case "java.lang.Long":
                case "long":
                case "java.lang.Boolean":
                case "boolean":
                    dataType = "INTEGER";
                    break;

                case "java.lang.Double":
                case "double":
                case "java.lang.Float":
                case "float":
                    dataType = "REAL";
                    break;

                case "java.lang.String":
                case "java.util.Date":
                    dataType = "TEXT";
                    break;

                default:
                    continue;

            }

            // add column.
            table.addColumn(new Column(methodName, dataType));
        }

        return table;
    }

    /**
     * Removes the package name prefix from a Java class.
     * @param name
     * @return
     */
    private static String stripPackageName(String name) {
        return name.replaceFirst("^.*\\.", "");
    }

    /**
     * Get all the getters of a specific class.
     * @param model
     * @param <T>
     * @return
     */
    private static <T> List<Method> getGetters(Class<T> model) {
        List<Method> getters = new LinkedList<>();
        for(Method method : model.getMethods()) {
            String methodName = stripPackageName(method.getName());

            if (methodName.startsWith("get") || methodName.startsWith("is"))
                getters.add(method);
        }
        return getters;
    }

    /**
     * Get all the setters of a specific class.
     * @param model
     * @param <T>
     * @return
     */
    private static <T> List<Method> getSetters(Class<T> model) {
        List<Method> getters = new LinkedList<>();
        for(Method method : model.getMethods()) {
            String methodName = stripPackageName(method.getName());

            if (methodName.startsWith("set"))
                getters.add(method);
        }
        return getters;
    }
}

