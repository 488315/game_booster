package com.kai.gamebooster.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "games")
public class GameEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "package_name")
    private String packageName;

    @ColumnInfo(name = "label")
    private String label;

    public GameEntity(@NonNull String packageName, String label) {
        this.packageName = packageName;
        this.label = label;
    }

    @NonNull
    public String getPackageName() {
        return packageName;
    }

    public String getLabel() {
        return label;
    }
}
