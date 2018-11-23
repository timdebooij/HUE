package com.timdebooij.hueapplicatie.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.timdebooij.hueapplicatie.database.DaoAccess;
import com.timdebooij.hueapplicatie.models.ColorScheme;

@Database(entities = {ColorScheme.class}, version = 1, exportSchema = false)
public abstract class DatabaseColorScheme extends RoomDatabase {
    public abstract DaoAccess daoAccess();
}
