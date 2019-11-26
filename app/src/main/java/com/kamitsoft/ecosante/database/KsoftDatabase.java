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
import com.kamitsoft.ecosante.constant.Gender;
import com.kamitsoft.ecosante.constant.MaritalStatus;
import com.kamitsoft.ecosante.constant.TitleType;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.database.converters.DiseaseDataTypeConverter;
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
import com.kamitsoft.ecosante.model.PhysicianNurses;
import com.kamitsoft.ecosante.model.Speciality;
import com.kamitsoft.ecosante.model.SummaryInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.services.UnsyncFile;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.Executors;


@Database(entities = {Allergen.class,
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
                    PhysicianNurses.class,
                    UserInfo.class,
                    EncounterInfo.class,
                    SummaryInfo.class,
                    LabInfo.class},
        version = 2,exportSchema = false)
@TypeConverters({TimestampTypeConverter.class, DiseaseDataTypeConverter.class})
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
                                        PhysicianNurses.class,
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
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(1977,11,06);
            UserInfo doctor = new UserInfo();
            doctor.setUserID(1);
            doctor.setFirstName("Souleymane");
            doctor.setLastName("Diop");
            doctor.setTitle(TitleType.DOCTOR.value);
            doctor.setDob(new Timestamp(calendar.getTimeInMillis()));
            doctor.setPob("Dakar");
            doctor.setUuid("0942db6b-8bb1-4cbe-9268-0e1de9433e20");
            doctor.setUserType(UserType.PHYSIST.type);
            helper.userDAO().insert(doctor);

            UserInfo admin = new UserInfo();
            admin.setUserID(2);
            admin.setFirstName("Ousmane");
            admin.setLastName("CAMARA");
            admin.setTitle(TitleType.MISTER.value);
            admin.setDob(new Timestamp(calendar.getTimeInMillis()));
            admin.setPob("Kaedi");
            admin.setUuid("1042db6b-8bb1-4cbe-9268-0e1de9433e20");
            admin.setUserType(UserType.ADMIN.type);
            helper.userDAO().insert(admin);

            UserInfo nurse = new UserInfo();
            nurse.setUserID(3);
            nurse.setFirstName("Aicha");
            nurse.setLastName("FALL");
            nurse.setTitle(TitleType.MISS.value);
            nurse.setDob(new Timestamp(calendar.getTimeInMillis()));
            nurse.setPob("Kaedi");
            nurse.setUuid("1042db6b-8bb1-4cbe-9268-0e1de9433e2X");
            nurse.setUserType(UserType.NURSE.type);
            helper.userDAO().insert(nurse);


            PatientInfo pi = new PatientInfo();
            pi.setFirstName("Fadel");
            pi.setLastName("TOURE");
            pi.setDob(new Timestamp(System.currentTimeMillis()));
            pi.setMiddleName("");
            pi.setMobile("+221778351734");
            pi.setPob("Kaedi");
            pi.setSex(Gender.MALE.sex);
            pi.setOccupation("Informaticien");
            pi.setMaritalStatus(MaritalStatus.MARRIED.status);
            pi.setPatientID(2);
            pi.setUserName("ftoure");

            helper.patientDAO().insert(pi);

            pi = new PatientInfo();
            pi.setFirstName("Oumar");
            pi.setLastName("TALL");
            pi.setDob(new Timestamp(System.currentTimeMillis()));
            pi.setMiddleName("");
            pi.setMobile("+221788311734");
            pi.setPob("Podor");
            pi.setSex(Gender.MALE.sex);
            pi.setOccupation("Mécanicien");
            pi.setMaritalStatus(MaritalStatus.WIDOW.status);
            pi.setPatientID(3);
            pi.setUserName("tallom");

            helper.patientDAO().insert(pi);

            pi = new PatientInfo();
            pi.setFirstName("Aissata");
            pi.setLastName("DIOP");
            pi.setDob(new Timestamp(System.currentTimeMillis()));
            pi.setMiddleName("");
            pi.setMobile("+221776311734");
            pi.setPob("Dakar");
            pi.setSex(Gender.FEMALE.sex);
            pi.setOccupation("Assistante");
            pi.setMaritalStatus(MaritalStatus.DIVORCED.status);
            pi.setPatientID(3);
            pi.setUserName("diopa");

            helper.patientDAO().insert(pi);



        }catch (Exception e){
            e.printStackTrace();
        }
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