package airpick.com.sample;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alexsheremeta.CameraModule;

public class MainActivity extends PermissionActivity {
    String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission(permissions);
    }

    private boolean checkPermissions(){
        if(!isPermissionEnabled(permissions)){
            requestPermission(permissions);
            return false;
        } else {
            return true;
        }
    }

    public void openPhotoClick(View v) {
        if(checkPermissions()) {
            CameraModule.openCamera(CameraModule.CAMERA_PHOTO, MainActivity.this);
        }
    }

    public void openVideoClick(View v) {
        if(checkPermissions()) {
            CameraModule.openCamera(CameraModule.CAMERA_VIDEO, MainActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
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

    @Override
    public void onPermissionsAllowed() {

    }

    @Override
    public void onPermissionsDenied() {

    }
}
