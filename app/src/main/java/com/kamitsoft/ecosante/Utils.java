package com.kamitsoft.ecosante;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kamitsoft.ecosante.constant.TitleType;
import com.kamitsoft.ecosante.model.Drug;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.PhysicianInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.services.DateDeserializer;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.AnyRes;
import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatSpinner;

public class Utils {
    private static DateFormat df = DateFormat.getDateInstance();
    private static DecimalFormat fmt = new DecimalFormat("#.#");
    static final long DAY_MS = 24 * 3600000;
    private static final int PICTURE_WIDTH = 230;

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

    public static void load(Context context, String keyuuid,  ImageView to, @DrawableRes  int failed, @DrawableRes  int placeholder) {
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
                    .load(avatarUrl(keyuuid))
                    .error(failed)
                    .placeholder(placeholder)
                    .circleCrop()
                    .into(to);
        }
    }
    public static void loadSquare(Context context, String keyuuid,  ImageView to, @DrawableRes  int failed, @DrawableRes  int placeholder) {
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
                    .load(avatarUrl(keyuuid))
                    .error(failed)
                    .placeholder(placeholder)
                    .centerInside()
                    .into(to);
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

    private static String avatarUrl(String keyuuid) {
        return null;
    }

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

    public static double ageOf(Timestamp dob) {
        if(dob == null ){
            return 0.0;
        }

        return ageOf(dob.getTime());
    }
    public static double ageOf(long ms) {

        return 1.0*((System.currentTimeMillis() / DAY_MS) - (ms / DAY_MS)) / 365;
    }

    public static String formattedAgeOf(Timestamp date) {

        return formattedAgeOf(date == null ? System.currentTimeMillis():date.getTime());
    }
    public static String formattedAgeOf(long ms) {
        double age = ageOf(ms);
        return fmt.format(ageOf(ms))+(age > 1? "ans":"an");
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

    public static void manageDataPicker(Context context, EditText date, Calendar calendar) {
        date.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (vie, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                date.setText(Utils.niceFormat(Utils.format(calendar.getTime())));

            },  calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });
    }

    public static void manageDataPicker(Context context, Timestamp date, TextView tv) {

        manageDataPicker(context, date, (selDate)->{
            tv.setText(Utils.niceFormat(Utils.format(selDate)));
        });
    }

    public static void manageDataPicker(Context context, Timestamp date, OnDateSelectedListener selector) {
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
                    .setDateFormat("yyyy-MM-dd HH:mm")
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .create();

    }

    @FunctionalInterface
    public interface OnDateSelectedListener{
        void onDateSelected(Date date);
    }
}
