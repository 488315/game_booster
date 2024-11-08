package com.kai.gamebooster;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class GameBoosterNotificationService extends Service {
    private static final String CHANNEL_ID = "game_booster_channel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotification();
        return START_NOT_STICKY;
    }

    private void sendNotification() {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.sports_esports_24px) // Replace with your icon
                .setContentTitle("Game Booster Active")
                .setContentText("Game Booster is optimizing your game experience.")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        CharSequence name = "Game Booster";
        String description = "Notifications for Game Booster";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // We don’t need binding here
    }
}
