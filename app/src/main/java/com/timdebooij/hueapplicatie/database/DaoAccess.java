package com.timdebooij.hueapplicatie.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.timdebooij.hueapplicatie.models.ColorScheme;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    void insertColorScheme(ColorScheme scheme);

    @Query("SELECT  * FROM ColorScheme")
    List<ColorScheme> getAllSchemes();

    @Delete
    void deleteColorScheme(ColorScheme scheme);
}
