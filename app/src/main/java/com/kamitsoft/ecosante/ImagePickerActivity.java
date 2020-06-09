package com.kamitsoft.ecosante;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.kamitsoft.ecosante.client.HasFinishedSelection;
import com.kamitsoft.ecosante.client.patient.PatientProfileView;
import com.kamitsoft.ecosante.model.viewmodels.FilesViewModel;
import com.kamitsoft.ecosante.services.UnsyncFile;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.sql.Timestamp;
import java.util.UUID;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import pub.devrel.easypermissions.EasyPermissions;




public class ImagePickerActivity extends AppCompatActivity {
    public LocFoundCallBack callback;

    public interface LocFoundCallBack{
        void onLocated(Location location);
    }
    private DiskCache cache ;
    private HasFinishedSelection hasFinishedSelection;
    protected boolean square;
    private View view;
    protected String mimeType;
    protected String bucket;
    protected FilesViewModel fileModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cache = new DiskCache(this);
        bucket = BuildConfig.AVATAR_BUCKET;
        fileModel = ViewModelProviders.of(this).get(FilesViewModel.class);

    }

    public void setPlaceholder(int placeholder) {
        this.placeholder = placeholder;
    }



    public static final int LOCATION_PICKER = 1222;
    public static final int FILE_CHOOSER = 1221;
    protected String avatar;
    private ImageView imageView;
    @DrawableRes private int placeholder;

    public void openImageCropper() {



        if(EasyPermissions.hasPermissions(this, Utils.PIC_SELECTOR_PERMS)) {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAutoZoomEnabled(true)
                    .start(this);



        }else{
            EasyPermissions.requestPermissions(this,
                    getString(R.string.need_following_permission),
                    504, Utils.PIC_SELECTOR_PERMS);
        }
    }

    private void openFileSelector() {
        if(EasyPermissions.hasPermissions(this, Utils.PIC_SELECTOR_PERMS)) {
            Intent fileintent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            fileintent.addCategory(Intent.CATEGORY_OPENABLE);
            fileintent.putExtra(Intent.EXTRA_MIME_TYPES, Utils.MIME_TYPES);
            fileintent.setType("*/*");
            fileintent = Intent.createChooser(fileintent, getString(R.string.choose_file));
            try {
                startActivityForResult(fileintent, ImagePickerActivity.FILE_CHOOSER);
            } catch (ActivityNotFoundException e) {
                Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
            }
        }else{
            EasyPermissions.requestPermissions(this,
                    getString(R.string.need_following_permission),
                    504, Utils.PIC_SELECTOR_PERMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 504){
            openImageCropper();
        }
    }

    public void pick(View v){

        if(v instanceof  ImageView)
            imageView = (ImageView) v;
        else {
            view = v;
        }
        openImageCropper();
    }

    public void pickFile(View v){
        if(v instanceof  ImageView)
            imageView = (ImageView) v;
        else {
            view = v;
        }
        openFileSelector();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                (new Saver()).execute(result);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == FILE_CHOOSER && resultCode == RESULT_OK) {

            avatar = UUID.randomUUID().toString() + "." + ext(data.getData());
            cache.put(avatar, data.getData(), () -> {
                mimeType = getContentResolver().getType(data.getData());
                if (hasFinishedSelection != null) {
                    hasFinishedSelection.onSelectionFinished(avatar);
                }
                imageView.setImageResource(Utils.getPicture(mimeType));
                avatar = null;
            });
        }
        if(requestCode == LOCATION_PICKER) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locateUser(callback);
            }
        }

    }


    public void setSelectionFinishedListener(HasFinishedSelection sf) {
        this.hasFinishedSelection =sf;
    }

    public void setSquare(boolean b) {
        square = b;
    }

    public String getMimeType() {
        return mimeType;
    }

    private String deExt(String strWithExt){
        int i = strWithExt.lastIndexOf('.');
        return i > 0 ? strWithExt.substring(0,i):strWithExt;
    }
    private  String ext(String FilePath){
        int i = FilePath.lastIndexOf('.');
        return i > 0 ? FilePath.substring(i+1):"ext";
    }
    public  String ext(Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();

            extension = mime.getExtensionFromMimeType(getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    public void locateUser(LocFoundCallBack lf) {
        this.callback = lf;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PICKER);
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager
                .getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null && lf !=null) {
            lf.onLocated(location);
        }
    }



    class Saver extends   AsyncTask<CropImage.ActivityResult, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(CropImage.ActivityResult... result) {
            return  Utils.getBitmap(result[0].getUri().getPath());
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(null == bitmap){
                if(hasFinishedSelection!=null){
                    hasFinishedSelection.onSelectionFinished(null);
                };
                return;
            }
            avatar =  UUID.randomUUID().toString();
            cache.put(avatar,bitmap, ()-> {
                mimeType = "image/png";
                if(!square) {
                    Utils.load(getApplicationContext(),
                            bucket,
                            avatar,
                            imageView,
                            R.drawable.broken_mage,
                            placeholder);
                }else {
                    Utils.loadSquare(getApplicationContext(),
                            bucket,
                            avatar,
                            imageView,
                            R.drawable.broken_mage,
                            placeholder);
                }
                    if(hasFinishedSelection!=null){
                        hasFinishedSelection.onSelectionFinished(avatar);
                    }
                avatar = null;});
        }
    }


    public void syncAvatar(String newuuid , String olduuid, int type){
        if(newuuid !=null && !newuuid.equals(olduuid)){
            if(olduuid !=null)
                fileModel.remove(olduuid);
            UnsyncFile file = new UnsyncFile();
            file.setTries(0);
            file.setFkey(newuuid);
            file.setType(type);
            fileModel.insert(file);
        }
    }

}
