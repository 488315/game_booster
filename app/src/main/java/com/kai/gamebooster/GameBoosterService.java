package com.kai.gamebooster;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

public class GameBoosterService extends Service {

    private static final String TAG = "GameBoosterService";
    private static final String CHANNEL_ID = "game_booster_channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "GameBoosterService onStartCommand called");

        createNotificationChannel();
        postForegroundNotification();



        Log.d(TAG, "GameBoosterService running game optimization logic");
        // Request the main activity to set the display to max refresh rate
        Log.d(TAG, "onStartCommand: Requesting max refresh rate");
        requestMaxRefreshRate();
        return START_STICKY;
    }

    private void requestMaxRefreshRate() {
        Intent intent = new Intent("com.kai.gamebooster.SET_MAX_REFRESH_RATE");
        sendBroadcast(intent);
    }

    private boolean hasUsageStatsPermission() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // Log to confirm that the app is not flagged as a system app
                Log.d(TAG, "App is not a system app, continuing with normal flow");
            }
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to check if app has usage stats permission", e);
            return false;
        }
    }

    private boolean isSystemApp() {
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), 0);
            return (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error checking if app is a system app", e);
            return false;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Game Booster",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Notifications for Game Booster Service");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel created");
        }
    }

    private void postForegroundNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.sports_esports_24px)  // Make sure this icon resource exists
                .setContentTitle("Game Booster Running")
                .setContentText("Optimizing performance for your game.")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true) // Make it a persistent notification for foreground service
                .setAutoCancel(false); // Prevent accidental dismissal

        Notification notification = builder.build();

        // Start service in the foreground with the notification
        startForeground(NOTIFICATION_ID, notification);
        Log.d(TAG, "Foreground notification posted");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // Log service destruction
        Log.d(TAG, "GameBoosterService is being destroyed, stopping foreground service");
        stopForeground(true);
        super.onDestroy();
    }
}
