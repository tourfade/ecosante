package com.kamitsoft.ecosante.database;



import com.kamitsoft.ecosante.model.DistrictInfo;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface DistrictDAO {

    @Query("SELECT * FROM districtinfo")
    LiveData<List<DistrictInfo>> all();

    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insert(DistrictInfo... districtInfos);

    @Delete
    void delete(DistrictInfo... districtInfos);

    @Update(onConflict= OnConflictStrategy.REPLACE)
    int update(DistrictInfo... districtInfos);

    @Query("DELETE FROM districtinfo")
    void deleteAll();

    @Query("SELECT * FROM districtinfo WHERE name like:key1")
    List<DistrictInfo> finDistricts(String key1);


    @Query("SELECT * FROM districtinfo WHERE needUpdate >= 1")
    LiveData<List<DistrictInfo>> dirty();
}
