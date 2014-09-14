package com.t3coode.togg.activities;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class ErrorHandler {

    public static void handle(Context ctx, Exception ex) {
        Toast t = Toast.makeText(ctx, ex.getMessage(), Toast.LENGTH_SHORT);
        t.show();
    }

    public static void handle(Activity activity, Exception ex) {
        if (!activity.isFinishing()) {
            handle((Context) activity, ex);
        }
    }
}
