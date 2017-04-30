package app.hacareem.com.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Numair Qadir on 04/30/2017.
 */

public class AppToast {

    public static void show(Context ctx, String message) {
        Toast.makeText(ctx, "" + message, Toast.LENGTH_SHORT).show();
    }
}