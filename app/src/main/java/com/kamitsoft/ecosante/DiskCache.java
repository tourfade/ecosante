package com.kamitsoft.ecosante;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.kamitsoft.ecosante.model.DocumentInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tourfade on 18-03-20.
 */

public class DiskCache {



    class CacheImageWriter extends AsyncTask<String, Void, Void>{
         Completion complete;
         private final Bitmap picture;

         public CacheImageWriter(Bitmap picture, Completion complete){
             this.picture = picture;
             this.complete = complete;
         }
         @Override
         protected Void doInBackground(String... keys) {
            FileOutputStream out = null;
            try {
                File f = getFile(keys[0]);
                if(f.exists()){
                    f.delete();
                }
                out = new FileOutputStream(getFile(keys[0]));

                picture.compress(Bitmap.CompressFormat.PNG, 100, out);

            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
         @Override
         protected void onPostExecute(Void aVoid) {
            if(complete != null){
                complete.complete();
            }
         }
      }

     class CacheFileWriter extends   AsyncTask<String, Void, Void>{
         private final Completion complete;
         private final Uri filePath;

         public CacheFileWriter(Uri filePath, Completion complete){
             this.filePath = filePath;
             this.complete = complete;
         }

         @Override
         protected Void doInBackground(String... keys) {
            FileOutputStream out = null;
            InputStream in = null;
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(filePath);
                File f = getFile(keys[0]);
                if(f.exists()){
                    f.delete();
                }
                //in = new FileInputStream(src);
                out = new FileOutputStream(getFile(keys[0]));
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }


            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

         @Override
         protected void onPostExecute(Void aVoid) {
            if(complete != null){
                complete.complete();
            }
         }
     }

    public interface Completion{ void complete();}
    private final File cacheDir;
    private Context context;
    public DiskCache(Context context){
        this.context = context;
        cacheDir =  Glide.getPhotoCacheDir(context);
    }
    public synchronized void put(final String key, final Bitmap picture){
        put(key, picture, null);
    }
    public synchronized void put(final String key, final Bitmap picture, final Completion completion){

        if (picture == null){
            return;
        }
        new CacheImageWriter(picture,completion).execute(key);

    }
    public synchronized void put(String key, Uri filePath, final Completion completion){
        if (filePath == null){
            return;
        }
        new CacheFileWriter(filePath,completion).execute(key);

    }
    public byte[] getBytes(String key){
        if (key == null || key.isEmpty() ){
            return null;
        }
        try {
            File temp = new File(cacheDir.getPath() + "/" + key);
            byte[] fileBytes = new byte[(int) temp.length()];
            try(FileInputStream inputStream = new FileInputStream(temp))
            {
                inputStream.read(fileBytes);
            }
            return fileBytes;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public synchronized void remove(final String key){
        try {
            new AsyncTask<Void, Void, Void>(){

                @Override
                protected Void doInBackground(Void... voids) {

                File temp = new File(cacheDir.getPath() + "/" + key);
                if(temp.exists()){
                    temp.delete();
                }
                return null;
                }
            }.execute();
        }catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
    public Bitmap get(String key){
        if (key == null || key.isEmpty() ){
            return null;
        }
        return  BitmapFactory.decodeFile(cacheDir.getPath() + "/" + key);
    }
    public File getFile(String key) {
        return new File(cacheDir.getPath() + "/" + key);
    }

    public String getFilePath(String key) {
        return cacheDir.getPath() + "/" + key;
    }
}
