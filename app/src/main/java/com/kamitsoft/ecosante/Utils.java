package com.kamitsoft.ecosante;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kamitsoft.ecosante.constant.TitleType;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.Drug;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.PhysicianInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.services.DateDeserializer;
import com.kamitsoft.ecosante.services.FirebaseChannels;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.AnyRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.FileProvider;

public class Utils {
    private static DateFormat df = DateFormat.getDateInstance();
    private static DecimalFormat fmt = new DecimalFormat("#.#");
    private static DecimalFormat fmt0 = new DecimalFormat("#");
    static final long DAY_MS = 24 * 3600000;
    private static final int PICTURE_WIDTH = 230;
    private static String[] MONTHES = new String[]{"janv.", "fevr.", "mars","avril", "mai", "juin", "juil.", "aout", "sept.", "octb.", "nov.", "dec."};

    public static String formatUser(Context context, UserInfo user) {
        return  formatName(context,
                    user.getFirstName(),
                    user.getLastName(),
                    user.getTitle());
    }
    public static String formatUser(Context context, PhysicianInfo user) {
        return  formatName(context,
                            user.firstName,
                            user.lastName,
                            user.title);
    }

    public static String formatPatient(Context context, PatientInfo patientInfo) {
        return patientInfo==null?"":formatName(context,
                patientInfo.getFirstName(),
                patientInfo.getLastName(),
                -1);
    }
    public static String formatName(Context context, String firstName, String lastName, int title) {
        String theTitle = TitleType.typeOf(title).getLocaleName(context);
        firstName = firstName == null ?"":firstName;
        lastName = lastName == null ?"":lastName;
        if(firstName.length() < 2){
            return  theTitle+" "+firstName.toUpperCase() +" "+lastName.toUpperCase();
        }
        if(title >0)
            return theTitle+" "+firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase()+ " "+lastName.toUpperCase();

        return firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase()+ " "+lastName.toUpperCase();

    }

    public static float floatFromEditText(EditText edit){

        try {
            return  Float.parseFloat(edit.getText()==null?"0.0":edit.getText().toString());
        }catch (Exception e){
            return  0;
        }
    }

    public static String format(float value) {
        return String.format((value == (int)value)?"%.0f":"%.1f",value);
    }
    public static double doubleFromEditText(EditText edit) {
        return  Double.parseDouble(edit==null
                || edit.getText()==null
                || edit.getText().toString().trim().length() <= 0? "0.0":edit.getText().toString());
    }
    public static String stringFromEditText(EditText edit) {
        return  edit==null
                || edit.getText()==null
                || edit.getText().toString().trim().length() <= 0? "":edit.getText().toString();
    }
    public static int intFromEditText(EditText edit) {
        return  Integer.parseInt(edit==null
                || edit.getText()==null
                || edit.getText().toString().trim().length() <= 0? "0":edit.getText().toString());
    }
    public static int intFromString(String text) {
        return  Integer.parseInt(text==null || text.trim().length() <= 0?"0":text);
    }

    public static String niceFormat(int value) {
        return value <= 0 ?"":String.valueOf(value);
    }
    public static String niceFormat(double value) {
        return value <= 0 ?"":String.valueOf(value);
    }

    public static String niceFormat0(double value) {
        return value <= 0 ?"":fmt0.format(value);
    }

    public static String niceFormat(String value) {
        return value ==null?"":value.trim();
    }


    public static String format(Date dob) {
        return dob==null?"":df.format(dob);
    }

    public static String formatAge(Timestamp dob) {
        if(dob == null ){
            return "0an";
        }

        double  patientAge = 1.0*((System.currentTimeMillis() / DAY_MS) - (dob.getTime() / DAY_MS)) / 365;


        return fmt.format(patientAge)+(patientAge<= 1?"an":"ans");
    }

    public static void loadSignature(Context context, String bucket, String keyuuid,  SignaturePad to) {
        DiskCache cache = new DiskCache(context);
        if(cache.getFile(keyuuid).exists()){
            Glide.with(context)
                    .asBitmap()
                    .load(cache.getFile(keyuuid)).
                    into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                to.setSignatureBitmap(resource);
                                to.setEnabled(false);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                to.clearView();
                                to.setEnabled(true);
                            }});

        }else {
            Glide.with(context)
                    .asBitmap()
                    .load(bucket+keyuuid)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            to.setSignatureBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            to.clearView();
                        }});
        }
    }

    public static void load(Context context, String bucket, String keyuuid,  ImageView to, @DrawableRes  int failed, @DrawableRes  int placeholder) {
        DiskCache cache = new DiskCache(context);
        if(cache.getFile(keyuuid).exists()){
            Glide.with(context)
                    .load(cache.getFile(keyuuid))
                    .error(failed)
                    .placeholder(placeholder)
                    .circleCrop()
                    .into(to);
        }else {
            Glide.with(context)
                    .load(bucket+keyuuid)
                    .error(failed)
                    .placeholder(placeholder)
                    .circleCrop()
                    .into(to);
        }
    }
    public static void loadSquare(Context context, String bucket, String keyuuid,
                                  ImageView to, @DrawableRes
                                          int failed, @DrawableRes
                                          int placeholder) {
        DiskCache cache = new DiskCache(context);
        if(cache.getFile(keyuuid).exists()){
            Glide.with(context)
                    .load(cache.getFile(keyuuid))
                    .error(failed)
                    .placeholder(placeholder)
                    .centerInside()
                    .into(to);
        }else {
            Glide.with(context)
                    .load(bucket+keyuuid)
                    .error(failed)
                    .placeholder(placeholder)
                    .centerInside()
                    .into(to);
        }
    }

    public static Uri getUri(Context context, String bucket, String keyuuid) {
        DiskCache cache = new DiskCache(context);
        if(cache.getFile(keyuuid).exists()){
            return FileProvider.getUriForFile(context,"com.kamitsoft.dmi.fileprovider",
                    cache.getFile(keyuuid));

        }else {

                return  Uri.parse(bucket+keyuuid);


        }
    }

    public static final String[] MIME_TYPES = { "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.ms-word",
            "application/vnd.ms-powerpoint",
            "application/vnd.ms-excel",
            "application/rtf",
            "image/jpeg",
            "image/png",
            "image/gif",
            "text/plain",
            "video/*",
            "application/zip",
            "application/x-rar-compressed"};

    public static final String[] IMG_MIME_TYPES = {
            "image/jpeg",
            "image/png",
            "image/gif"};



    public static Bitmap getBitmap(String path){
        return getBitmap(path, PICTURE_WIDTH);
    }

    public static Bitmap getBitmap(String path, int width){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opt);
        float r = 1.0f*opt.outHeight/opt.outWidth;
        opt.inSampleSize = calculateInSampleSize(opt, width,(int)(width *r));
        opt.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, opt);
    }
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    public static final String[] PIC_SELECTOR_PERMS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
    };

    public static boolean isNullOrEmpty(String txt) {
        return txt==null || txt.trim().length()<= 0;
    }

    public static String niceFormat(Drug drug) {
        boolean hasd = !isNullOrEmpty(drug.getDosage());
        boolean hasf = !isNullOrEmpty(drug.getForm());
        return isNullOrEmpty(drug.getReference())? niceFormat(drug.getDci()):niceFormat(drug.getReference())
                +((hasd || hasf)?" ("+niceFormat(drug.getForm())
                +(hasd && hasf?", ":"")
                +niceFormat(drug.getDosage())+")" :"");

    }


    public static boolean isPicutre(String mimeType) {
        for(String mim:IMG_MIME_TYPES){
            if(mim.equalsIgnoreCase(mimeType)){
                return true;
            }
        }
        return false;
    }

    public static @DrawableRes int getPicture(String mimeType) {
        if(mimeType == null){
            return  R.drawable.docs;
        }
        switch (mimeType){
            case "application/pdf":
                return R.drawable.pdf;
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
            case  "application/msword":
                return R.drawable.docx;

            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
            case "application/vnd.ms-powerpoint":
                return R.drawable.pptx;
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
            case  "application/vnd.ms-excel":
                return R.drawable.xlsx;
            case "application/rtf":
                return R.drawable.rtf;
            case "image/jpeg":
            case "image/png":
            case "image/gif":
                return R.drawable.broken_mage;

            case "text/plain":
                return R.drawable.txt;
            case "video/*":
                return R.drawable.video;
            case "application/zip":
                return R.drawable.zip;
            case  "application/x-rar-compressed":
                return R.drawable.rar;
            default:return R.drawable.docs;


        }

    }


    public static double ageOf(int[] date) {
        if(date == null || date.length != 3){
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        int age = cal.get(Calendar.YEAR)-date[0];
        int nbm = cal.get(Calendar.MONTH)-date[1];
        return age+(nbm/12.0);
    }

    public static String formattedAgeOf(int[] date){
        return formattedAgeOf(date, Calendar.getInstance());
    }
    public static String formattedAgeOf(int[] date,  Calendar cal) {
        if(date == null || date.length != 3){
            return "";
        }

        int age = cal.get(Calendar.YEAR)-date[0];
        String ageans="";
        if(age > 1 ){
            ageans = age+"ans";
        }else if(age == 1 ){
            ageans = age+"an";
        }

        int nbm = cal.get(Calendar.MONTH) - date[1];
        String month="";

        if(nbm > 0 ) {
            month = " "+nbm+"mois";
        }
        if(age == 0 && nbm == 0){
            month = "Ce mois-ci";
        }

        return ageans+month;
    }
    public static String formattedAgeOf(long ms) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);


        return  formattedAgeOf(new int[]{year, month, 0},Calendar.getInstance() );
    }


    public static int intFromSpiner(View  view) {
        return ((AppCompatSpinner)view).getSelectedItemPosition();
    }

    public static void initEditText(View view, String niceFormat) {
        ((EditText)view).setText(niceFormat);
    }

    public static void initSpiner(View view, int position) {
        ((Spinner)view).setSelection(position);
    }

    public static String stringFromSpiner(View view) {
        return ((Spinner)view).getSelectedItem().toString();
    }

    public static String stringFromArray(Context context, @AnyRes int anemia_forms, int index) {
        return  context.getResources()
                .getStringArray(anemia_forms)[index];
    }

    public static String formatTime(Calendar calendar) {
        int hh = calendar.get(Calendar.HOUR);
        int mn = calendar.get(Calendar.MINUTE);
        return(hh<=9?"0"+hh:hh)+":"+(mn<=9?"0"+mn:mn);
    }

    public static String formatDateWithDayOfWeek(Context context, Calendar calendar) {
        return context.getResources().getStringArray(R.array.days)[calendar.get(Calendar.DAY_OF_WEEK)-1]+" "
                +df.format(calendar.getTime());
    }

    public static void manageDatePicker(Context context, EditText date, Calendar calendar) {
        date.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(context, (vieww, year, month, dayOfMonth) -> {
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        date.setText(Utils.niceFormat(Utils.format(calendar.getTime())));
                    },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });
    }

    public static void manageDatePicker(Context context, Timestamp date, TextView tv) {

        manageDatePicker(context, date, (selDate)->{
            tv.setText(Utils.niceFormat(Utils.format(selDate)));
        });
    }

    public static void manageDatePicker(Context context, Timestamp date, OnDateSelectedListener selector) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date != null ? date.getTime():System.currentTimeMillis());
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (vie, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if(selector != null)
                selector.onDateSelected(calendar.getTime());


        },  calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    public static Gson  getGsonBuilder(){
            return new GsonBuilder() //"2019-06-06 21:25:52"
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .create();

    }

    public static String format(int[] dob) {
        return dob == null ? "":dob[2]+" "+MONTHES[dob[1]]+" "+dob[0];
    }

    public static int[] toArray(Calendar calendar) {
        return new int[]{ calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)};
    }
    public static void toCalendar(int[] date, Calendar calendar) {
        calendar.setTimeInMillis(0);
        calendar.set(Calendar.YEAR, date[0]);
        calendar.set(Calendar.MONTH, date[1]);
        calendar.set(Calendar.DAY_OF_MONTH, date[2]);
    }

    public static void subscribe(UserInfo userInfo) {

        unSubscribe(userInfo);
        int accountID = userInfo.getAccountId();
        FirebaseMessaging.getInstance()
                .subscribeToTopic(FirebaseChannels.ACCOUNT+accountID)
                .addOnCompleteListener(Utils::subScribeComplete);

        switch (UserType.typeOf(userInfo.getUserType())){
            case PHYSIST:
                Log.i("XXXXXXX->T1", new Gson().toJson(FirebaseChannels.PHYSISTS_OF+userInfo.getDistrictUuid()));
                Log.i("XXXXXXX->T2", new Gson().toJson(FirebaseChannels.PHYSISTS_OF+accountID));
                Log.i("XXXXXXX->T3", new Gson().toJson(FirebaseChannels.PHYSIST+userInfo.getUuid()));

                FirebaseMessaging.getInstance()
                        .subscribeToTopic(FirebaseChannels.PHYSISTS_OF+userInfo.getDistrictUuid())
                        .addOnCompleteListener(Utils::subScribeComplete);
                FirebaseMessaging.getInstance()
                        .subscribeToTopic(FirebaseChannels.PHYSISTS_OF+accountID)
                        .addOnCompleteListener(Utils::subScribeComplete);
                FirebaseMessaging.getInstance()
                        .subscribeToTopic(FirebaseChannels.PHYSIST+userInfo.getUuid())
                        .addOnCompleteListener(Utils::subScribeComplete);

                break;
            case NURSE:
                FirebaseMessaging.getInstance()
                        .subscribeToTopic(FirebaseChannels.NURSES_OF+userInfo.getDistrictUuid())
                        .addOnCompleteListener(Utils::subScribeComplete);
                FirebaseMessaging.getInstance()
                        .subscribeToTopic(FirebaseChannels.NURSES_OF+accountID)
                        .addOnCompleteListener(Utils::subScribeComplete);
                FirebaseMessaging.getInstance()
                        .subscribeToTopic(FirebaseChannels.NURSE+userInfo.getUuid())
                        .addOnCompleteListener(Utils::subScribeComplete);

                break;
            case ADMIN:
                FirebaseMessaging.getInstance()
                        .subscribeToTopic(FirebaseChannels.PHYSISTS_OF+accountID)
                        .addOnCompleteListener(Utils::subScribeComplete);
                FirebaseMessaging.getInstance()
                        .subscribeToTopic(FirebaseChannels.NURSES_OF+accountID)
                        .addOnCompleteListener(Utils::subScribeComplete);

                break;

        }

    }

    private static void subScribeComplete(Task<Void> voidTask) {
        if(!voidTask.isSuccessful()){
            Log.i("XXXXXXX", "Registration: ERRRROOOOOR");
        }else{
            Log.i("XXXXXXX", "Registration: SUCESSS "+voidTask.toString());
        }

    }

    public static void unSubscribe(UserInfo userInfo) {
        int accountID = userInfo.getAccountId();

        if(userInfo.getDistrictUuid() != null)
            FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseChannels.NURSES_OF+userInfo.getDistrictUuid());

        FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseChannels.NURSES_OF+accountID);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseChannels.PHYSISTS_OF+accountID);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseChannels.PHYSIST+userInfo.getUuid());
        FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseChannels.NURSE+userInfo.getUuid());

    }



    @FunctionalInterface
    public interface OnDateSelectedListener{
        void onDateSelected(Date date);
    }
}
