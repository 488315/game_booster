package com.kai.gamebooster.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGame(GameEntity game);

    @Query("SELECT * FROM games")
    List<GameEntity> getAllGames();

    @Query("DELETE FROM games")
    void deleteAllGames();
}
