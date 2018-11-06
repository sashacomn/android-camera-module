package com.alexsheremeta.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static com.alexsheremeta.CameraModule.MODE;

public class CameraActivity extends AppCompatActivity {
    public static final String IMAGE_URL = "image";
    public static final String VIDEO_URL = "image";


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        int mode = getIntent().getExtras().getInt(MODE);

        CameraFragment cameraFragment = new CameraFragment();
        cameraFragment.setCurrentCameraMode(mode);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, cameraFragment).commit();
        }
        hideSystemUI();
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
