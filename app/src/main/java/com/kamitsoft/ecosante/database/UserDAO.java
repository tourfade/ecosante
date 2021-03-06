package com.kamitsoft.ecosante.database;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.kamitsoft.ecosante.model.CounterItem;
import com.kamitsoft.ecosante.model.PhysNursPat;
import com.kamitsoft.ecosante.model.SubConsumerInfo;
import com.kamitsoft.ecosante.model.SubInstanceInfo;
import com.kamitsoft.ecosante.model.UserAccountInfo;
import com.kamitsoft.ecosante.model.UserInfo;

import java.util.List;


@Dao
public interface UserDAO {

    @Query("SELECT * FROM userinfo ")
    LiveData<List<UserInfo>> allUsers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserInfo... user);

    @Delete
    void delete(UserInfo... user);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(UserInfo... user);

    @Query("DELETE FROM userinfo")
    void deleteAll();

    @Query("SELECT * FROM userinfo WHERE userType =:userType")
    LiveData<List<UserInfo>> getUsers(int userType);

    @Query("SELECT * FROM userinfo WHERE lastName like:key OR firstName like:key OR middleName like:key OR speciality like:key")
    List<UserInfo> findUsers(String key);

    @Query("SELECT * FROM userinfo WHERE userType =:userType LIMIT 1")
    UserInfo getUser(int userType);



    @Query("SELECT phy.* FROM userinfo phy " +
            " LEFT JOIN physnurspat nur ON(phy.uuid = nur.physicianUuid)" +
            " WHERE nur.nurseUuid =:nurseUuid")
    UserInfo getNurseSupervisor(String nurseUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PhysNursPat... params);

    @Query("SELECT * FROM physnurspat WHERE nurseUuid =:uuid LIMIT 1")
    PhysNursPat getNursePnp(String uuid);

    @Query("SELECT * FROM physnurspat")
    LiveData<List<PhysNursPat>> getAllPnpLinks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void connect(UserAccountInfo... params);

    @Query("DELETE FROM  useraccountinfo")
    void disconnect();

    @Query( " SELECT ui.* FROM userinfo ui " +
            " JOIN useraccountinfo ua ON (ui.uuid = ua.userUuid)" +
            " LIMIT 1")
    LiveData<UserInfo> getConnectedUser();

    @Query("SELECT * FROM useraccountinfo LIMIT 1")
    LiveData<UserAccountInfo> getLiveConnectedAccount();


    @Query("SELECT * FROM userinfo ")
    List<UserInfo> getAllUsers();

    @Query("UPDATE  userinfo SET status =:status WHERE uuid=:uuid")
    void changeStatus(String uuid, int status);


    @Query("SELECT * FROM  subconsumerinfo LIMIT 1")
    LiveData<SubConsumerInfo> getConsumerInfo();

    @Query("SELECT * FROM  subinstanceinfo ORDER BY created DESC LIMIT 1")
    LiveData<SubInstanceInfo> getSubInstance();

    @Query("SELECT ui.* FROM userinfo ui  JOIN useraccountinfo ua USING (username)  LIMIT 1")
    UserInfo getConnected();

    @Query("SELECT * FROM userinfo WHERE needUpdate >= 1")
    LiveData<List<UserInfo>> dirty();

    @Query("SELECT uuid, null AS supervisor, null AS monitor, accountId , null AS patientUuid, districtUuid, userType FROM userinfo WHERE  deleted = 0")
    LiveData<List<CounterItem>> countUsers();

    @Query("SELECT monitor, supervisor, accountId, userUuid AS uuid, patientUuid, districtUuid, 0 as userType FROM encounterinfo WHERE deleted = 0")
    LiveData<List<CounterItem>> countVisits();

    @Query("SELECT monitor, null as supervisor, accountId, uuid,  null as patientUuid, districtUuid , 0 as userType FROM patientinfo WHERE deleted = 0")
    LiveData<List<CounterItem>> countPatients();

    @Query("SELECT  null as monitor, null as supervisor, accountID AS accountId, " +
            " null as uuid, null as patientUuid, uuid as districtUuid, 0 as userType " +
            " FROM districtinfo WHERE deleted = 0")
    LiveData<List<CounterItem>> countDistricts();


    @Query("SELECT null as monitor, null as supervisor,  accountId, uuid, null as patientUuid, null as districtUuid, 0 as userType FROM appointmentinfo WHERE deleted = 0")
    LiveData<List<CounterItem>> countAppts();

    @Query("SELECT * FROM useraccountinfo LIMIT 1")
    UserAccountInfo getAccount();
}
