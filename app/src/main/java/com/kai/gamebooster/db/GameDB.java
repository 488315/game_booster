package com.kai.gamebooster.db;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameDB {
    private static final String TAG = "GameDB";
    private final GameDao gameDao;
    private final PackageManager packageManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public GameDB(Context context) {
        GameDatabase database = GameDatabase.getInstance(context);
        this.gameDao = database.gameDao();
        this.packageManager = context.getPackageManager();
    }

    public interface GameQueryCallback {
        void onResult(List<GameEntity> games);
    }

    /**
     * Scans installed apps, identifies games, and stores them in the database.
     */
    public void scanAndStoreGames() {
        executor.execute(() -> {
            List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

            for (PackageInfo packageInfo : packages) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;

                // Skip system apps and check if categorized as a game
                if (!isSystemApp(appInfo) && isGameApp(appInfo)) {
                    GameEntity game = new GameEntity(packageInfo.packageName, packageManager.getApplicationLabel(appInfo).toString());
                    insertGame(game);  // Use the new insertGame method
                    Log.d(TAG, "Game added to DB: " + appInfo.packageName);
                }
            }
        });
    }

    /**
     * Retrieves all games asynchronously and returns them via a callback.
     */
    public void getAllGamesAsync(GameQueryCallback callback) {
        executor.execute(() -> {
            List<GameEntity> games = gameDao.getAllGames();
            callback.onResult(games);
        });
    }

    /**
     * Clears all games from the database asynchronously.
     */
    public void clearGameDatabase() {
        executor.execute(gameDao::deleteAllGames);
    }

    /**
     * Inserts a single game entity into the database asynchronously.
     * @param game The GameEntity to insert.
     */
    public void insertGame(GameEntity game) {
        executor.execute(() -> {
            gameDao.insertGame(game);
            Log.d(TAG, "Inserted game into DB: " + game.getPackageName());
        });
    }

    /**
     * Checks if an application is categorized as a game.
     * @param appInfo The ApplicationInfo object to check.
     * @return True if categorized as a game, false otherwise.
     */
    private boolean isGameApp(ApplicationInfo appInfo) {
        return appInfo.category == ApplicationInfo.CATEGORY_GAME;
    }

    /**
     * Checks if an application is a system app.
     * @param appInfo The ApplicationInfo object to check.
     * @return True if it is a system app, false otherwise.
     */
    private boolean isSystemApp(ApplicationInfo appInfo) {
        return (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}
