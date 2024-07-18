package com.jingyuan.capstone.Utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.jingyuan.capstone.Controller.HomeActivity;
import com.jingyuan.capstone.R;

public class NotificationHelper {
    public static void createNotificationChannel(Context context, String channelId, String channelName, String channelDesc) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.avatar)
                    .setChannelId(channelId)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            manager.createNotificationChannel(builder.build());
        }
    }

    public static void displayNotification(Context context, String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, HomeActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.avatar)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            manager.notify(1, builder.build());
        }
    }

    public static void updateAppIconBadge(Context context, int count) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground);
            if (drawable instanceof BitmapDrawable) {
                ((BitmapDrawable) drawable).getBitmap().setHasAlpha(true);
                context.getPackageManager().setApplicationBadge(context.getApplicationInfo(), count);
            }
        }
    }

    public static void clearAppIconBadge(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getPackageManager().clearApplicationBadge(context.getApplicationInfo());
        }
    }
}
