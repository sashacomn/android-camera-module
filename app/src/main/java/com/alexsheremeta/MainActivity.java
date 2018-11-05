package com.alexsheremeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.alexsheremeta.view.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openPhotoClick(View v){
        CameraModule.openCamera(CameraModule.CAMERA_PHOTO, MainActivity.this);
    }

    public void openVideoClick(View v){
        CameraModule.openCamera(CameraModule.CAMERA_VIDEO, MainActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            CameraModule.handleResult(requestCode, data, new CameraModule.CameraResultHandlerCallback() {
                @Override
                public void onImageTaken(String url) {
                    Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onVideoTaken(String url) {
                    Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
