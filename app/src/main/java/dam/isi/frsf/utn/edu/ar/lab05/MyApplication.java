package dam.isi.frsf.utn.edu.ar.lab05;

import android.app.Application;
import android.content.Context;

/**
 * Created by Agustin on 10/31/2016.
 */

public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
