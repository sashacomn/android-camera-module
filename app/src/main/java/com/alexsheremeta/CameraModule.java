package com.alexsheremeta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.alexsheremeta.view.CameraActivity;

public class CameraModule {
    public static final int CAMERA_PHOTO = 0;
    public static final int CAMERA_VIDEO = 1;
    public static final String MODE = "mode";
    public static final int REQ_CAMERA_PICTURE = 0;
    public static final int REQ_CAMERA_VIDEO = 1;

    public static void openCamera(int mode, Activity activity) {
        Intent intent = new Intent(activity, CameraActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(MODE, mode);
        intent.putExtras(bundle);
        switch (mode) {
            case CAMERA_PHOTO:
                activity.startActivityForResult(intent, REQ_CAMERA_PICTURE);
                break;
            case CAMERA_VIDEO:
                activity.startActivityForResult(intent, REQ_CAMERA_VIDEO);
                break;
        }
    }

    public static void handleResult(int requestCode, Intent data, CameraResultHandlerCallback cameraResultHandlerCallback) {
        switch (requestCode) {
            case REQ_CAMERA_PICTURE:
                String url = data.getExtras().getString(CameraActivity.IMAGE_URL);
                if (url != null) {
                    cameraResultHandlerCallback.onImageTaken(url);
                }
                break;
            case REQ_CAMERA_VIDEO:
                String videoUrl = data.getExtras().getString(CameraActivity.VIDEO_URL);
                if (videoUrl != null) {
                    cameraResultHandlerCallback.onVideoTaken(videoUrl);
                }
                break;
        }
    }

    public interface CameraResultHandlerCallback {
        void onImageTaken(String url);

        void onVideoTaken(String url);
    }
}
