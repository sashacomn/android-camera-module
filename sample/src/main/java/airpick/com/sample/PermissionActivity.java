package airpick.com.sample;

import android.support.v7.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public abstract class PermissionActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST = 101;
    protected String requestedPermission;
    public boolean isPermissionEnabled(String[] permissions) {
        boolean isEnabled = true;
        for(String permission : permissions) {
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                isEnabled = false;
            }
        }

        return isEnabled;
    }

    public void requestPermission(String[] permissions) {
        ActivityCompat.requestPermissions(this,
                permissions,
                MY_PERMISSIONS_REQUEST);
    }

    public abstract void onPermissionsAllowed();

    public abstract void onPermissionsDenied();

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0) {
                    boolean permissionGranted = true;
                    for (int res : grantResults) {
                        if (res != PackageManager.PERMISSION_GRANTED) {
                            permissionGranted = false;
                        }
                    }

                    if(permissionGranted){
                        onPermissionsAllowed();
                    } else {
                        onPermissionsDenied();
                    }
                } else {
                    onPermissionsDenied();
                }
                return;
            }
        }
    }
}
