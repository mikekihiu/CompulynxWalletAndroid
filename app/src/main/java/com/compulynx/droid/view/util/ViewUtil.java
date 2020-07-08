package com.compulynx.droid.view.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class ViewUtil {

    public static void showSnackbar(View anchor, String message, boolean isShortDuration) {
        Snackbar.make(anchor, message, isShortDuration ? Snackbar.LENGTH_SHORT : Snackbar.LENGTH_LONG).show();
    }

    public static void showToast(Context context, String text, boolean isShort) {
        Toast.makeText(context, text, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    public static void showAlert(Activity activity, String  message) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .show();
    }

    public static void toggleProgress(ProgressBar progressBar, View... views) {
        boolean isLoading = progressBar.getVisibility() == View.VISIBLE;
        progressBar.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        for (View view : views)
            view.setEnabled(isLoading);
    }

    public static boolean isBlank(TextInputEditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }
}
