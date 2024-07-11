package com.jingyuan.capstone.Utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.jingyuan.capstone.Controller.HomeActivity;
import com.jingyuan.capstone.R;

public class NotificationHelper {
    public static void displayNotification(Context context, String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, HomeActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.avatar)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.notify(1, builder.build());
    }
}
