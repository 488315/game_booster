package com.kai.gamebooster.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {GameEntity.class}, version = 1)
public abstract class GameDatabase extends RoomDatabase {

    private static volatile GameDatabase INSTANCE;

    public abstract GameDao gameDao();

    public static GameDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (GameDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    GameDatabase.class, "game_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
