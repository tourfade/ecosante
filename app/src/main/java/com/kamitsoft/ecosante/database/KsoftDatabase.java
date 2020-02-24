package com.kamitsoft.ecosante.database;


import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.RawRes;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.database.converters.ArrayJsonTypeConverter;
import com.kamitsoft.ecosante.database.converters.DiseaseDataTypeConverter;
import com.kamitsoft.ecosante.database.converters.MonitorTypeConverter;
import com.kamitsoft.ecosante.database.converters.StatusTypeConverter;
import com.kamitsoft.ecosante.database.converters.SupervisorTypeConverter;
import com.kamitsoft.ecosante.database.converters.TimestampTypeConverter;
import com.kamitsoft.ecosante.model.Act;
import com.kamitsoft.ecosante.model.Allergen;
import com.kamitsoft.ecosante.model.Analysis;
import com.kamitsoft.ecosante.model.AppointmentInfo;
import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.Drug;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.EntitySync;
import com.kamitsoft.ecosante.model.LabInfo;
import com.kamitsoft.ecosante.model.MedicationInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.PhysNursPat;
import com.kamitsoft.ecosante.model.Speciality;
import com.kamitsoft.ecosante.model.SubConsumerInfo;
import com.kamitsoft.ecosante.model.SubInstanceInfo;
import com.kamitsoft.ecosante.model.SummaryInfo;
import com.kamitsoft.ecosante.model.UserAccountInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.services.UnsyncFile;

import java.io.InputStream;
import java.util.concurrent.Executors;


@Database(entities = {Allergen.class,
                    UserAccountInfo.class,
                    UnsyncFile.class,
                    AppointmentInfo.class,
                    EntitySync.class,
                    PatientInfo.class,
                    Act.class,
                    DocumentInfo.class,
                    Analysis.class,
                    Drug.class,
                    Speciality.class,
                    MedicationInfo.class,
                    PhysNursPat.class,
                    UserInfo.class,
                    EncounterInfo.class,
                    SummaryInfo.class,
                    LabInfo.class,
                    SubConsumerInfo.class,
                    SubInstanceInfo.class},
        version = 2,exportSchema = false)
@TypeConverters({TimestampTypeConverter.class,
                 DiseaseDataTypeConverter.class,
                 SupervisorTypeConverter.class,
                 MonitorTypeConverter.class,
                 StatusTypeConverter.class,
                 ArrayJsonTypeConverter.class})
public abstract class KsoftDatabase extends RoomDatabase {
    private static KsoftDatabase INSTANCE;

    public abstract UserDAO userDAO();
    public abstract AppointmentDAO appointmentDAO();
    public abstract EntityDAO entityDAO();
    public abstract EncounterDAO encounterDAO();
    public abstract SummaryDAO summaryDAO();
    public abstract PatientDAO patientDAO();
    public abstract DrugDAO drugDAO();
    public abstract SpecialityDAO specialityDAO();
    public abstract AnalysisDAO analysisDAO();
    public abstract ActDAO actDAO();
    public abstract UnsyncFileDAO fileDAO();
    public abstract AllergenDAO allergenDAO();

    public synchronized static KsoftDatabase getInstance(Context context) {
        if (INSTANCE == null) {
             INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    private static KsoftDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context, KsoftDatabase.class,"database")
                   .addCallback(new RoomDatabase.Callback() {
                       public void onCreate (SupportSQLiteDatabase db) {
                           super.onCreate(db);
                           Executors.newSingleThreadScheduledExecutor().execute(() -> iniSchema(db, INSTANCE,context));
                       }})
                   .allowMainThreadQueries()
                   .build();
    }



    private static void iniSchema(SupportSQLiteDatabase db, KsoftDatabase helper, Context context) {

        loadSQLRaw(db, context, R.raw.drug);
        loadSQLRaw(db, context, R.raw.specialities);
        loadSQLRaw(db, context, R.raw.labs);
        loadSQLRaw(db, context, R.raw.acts);
        loadSQLRaw(db, context, R.raw.allergens);
        Class[] entities = new Class[] {Allergen.class,
                                        AppointmentInfo.class,
                                        PatientInfo.class,
                                        Act.class,
                                        DocumentInfo.class,
                                        Analysis.class,
                                        Drug.class,
                                        Speciality.class,
                                        MedicationInfo.class,
                                        PhysNursPat.class,
                                        UserInfo.class,
                                        EncounterInfo.class,
                                        SummaryInfo.class,
                                        LabInfo.class};
        EntitySync[] data = new EntitySync[entities.length];

        int i = 0;
        for(Class entity:entities){
            data[i] = new EntitySync();
            data[i].setEntity(entity.getSimpleName().toLowerCase());
            data[i].setLastSynced(0);
            i++;
        }
        helper.entityDAO().insert(data);

    }

    private static void loadSQLRaw(SupportSQLiteDatabase helper, Context context, @RawRes int sqlRaw) {
       new AsyncTask<Void, Void, Void>(){

           @Override
           protected Void doInBackground(Void[] objects) {
               String sql;
               try {
                   InputStream is = context.getResources().openRawResource(sqlRaw);
                   int size = is.available();
                   byte[] buffer = new byte[size];
                   is.read(buffer);
                   is.close();
                   sql = new String(buffer, "UTF-8");
                   helper.execSQL(sql);
               } catch (Exception ex) {
                   ex.printStackTrace();
                   return null;
               }

               return null;
           }
       }.execute();

    }


}