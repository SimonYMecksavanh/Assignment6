package starter.ca.qc.johnabbott.cs.cs616.starter.notes;

import android.app.Application;

/**
 * Created by 1473031 on 2016-11-22.
 */
public class NoteApplication extends Application {
    public static String HOST = "10.0.2.2";
    public static int PORT = 9999;
    public static String PREFIX = "http://" + HOST + ":" + String.valueOf(PORT);
    @Override
    public void onCreate() {
        super.onCreate();
    }
}