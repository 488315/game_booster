package com.kai.gamebooster;

import android.accessibilityservice.AccessibilityService;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.kai.gamebooster.db.GameDB;
import com.kai.gamebooster.db.GameEntity;

import java.util.Arrays;
import java.util.List;

public class GameLaunchAccessibilityService extends AccessibilityService {
    private static final String TAG = "GameLaunchAccessibilityService";
    private UsageStatsManager usageStatsManager;
    private GameDB gameDB;

    private static final List<String> SYSTEM_PACKAGE_WHITELIST = Arrays.asList(
            "com.android.launcher3",
            "com.android.settings",
            "com.android.systemui",
            "com.google.android.gms"
    );

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "GameLaunchAccessibilityService started");
        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        gameDB = new GameDB(this);

        new Thread(gameDB::scanAndStoreGames).start();  // Initial scan to populate the database
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packageName = event.getPackageName() != null ? event.getPackageName().toString() : "";

            // Check if the package is in the system package whitelist
            if (SYSTEM_PACKAGE_WHITELIST.contains(packageName)) {
                Log.d(TAG, "Skipping non-game package: " + packageName);
                return;
            }

            Log.d(TAG, "Window state changed, package: " + packageName);

            gameDB.getAllGamesAsync(games -> {
                boolean isGame = false;
                for (GameEntity game : games) {
                    if (game.getPackageName().equals(packageName)) {
                        Log.d(TAG, "isGameApp: " + packageName + " found in GameDB as a game");
                        isGame = true;
                        break;
                    }
                }

                // Start or continue the GameBoosterService if the package is identified as a game
                if (isGame) {
                    Log.d(TAG, "Starting or resuming GameBoosterService for package: " + packageName);
                    Intent gameBoosterIntent = new Intent(this, GameBoosterService.class);
                    startForegroundService(gameBoosterIntent);
                } else if (!SYSTEM_PACKAGE_WHITELIST.contains(packageName)) {
                    // Stop the service only if a non-whitelisted, non-game package is detected
                    Log.d(TAG, "Stopping GameBoosterService, package is not a game or whitelisted system app: " + packageName);
                    Intent gameBoosterIntent = new Intent(this, GameBoosterService.class);
                    stopService(gameBoosterIntent);
                }
            });
        }
    }

    @Override
    public void onInterrupt() {
        // No additional handling needed for onInterrupt
    }
}
