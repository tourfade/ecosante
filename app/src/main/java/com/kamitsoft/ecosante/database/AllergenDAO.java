package com.kamitsoft.ecosante.database;



import com.kamitsoft.ecosante.model.Allergen;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface AllergenDAO {



    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insert(Allergen... allergens);

    @Delete
    void delete(Allergen... allergens);

    @Update(onConflict= OnConflictStrategy.REPLACE)
    int update(Allergen... allergens);

    @Query("DELETE FROM allergen")
    void deleteAll();

    @Query("SELECT * FROM allergen WHERE name like:key1 OR description like:key1")
    List<Allergen> finAll(String key1);
}
