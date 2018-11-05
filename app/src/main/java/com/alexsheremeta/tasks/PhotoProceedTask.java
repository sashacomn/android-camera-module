package com.alexsheremeta.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;

import com.alexsheremeta.Utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoProceedTask extends AsyncTask<Void, Void, Void> {
    private byte[] imageBytes;
    private int orientation = 90;
    private OnImageTakenListener onImageTakenListener;

    public PhotoProceedTask(byte[] arr, int orientation) {
        this.imageBytes = arr;
        this.orientation = orientation;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        File pictureFile = FileUtils.getOutputMediaFile(FileUtils.MEDIA_TYPE_IMAGE);
        try {

            FileOutputStream fos = new FileOutputStream(pictureFile);

            Bitmap realImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            realImage = rotate(realImage, orientation);

            boolean bo = realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.close();

            if (onImageTakenListener != null) {
                onImageTakenListener.onPhotoTaken(pictureFile.getAbsolutePath());
            }
            Log.d("Info", bo + "");

        } catch (FileNotFoundException e) {
            Log.d("Info", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("TAG", "Error accessing file: " + e.getMessage());
        }
        return null;
    }

    public void setOnImageTakenListener(OnImageTakenListener onImageTakenListener) {
        this.onImageTakenListener = onImageTakenListener;
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();

        mtx.setRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }
}
