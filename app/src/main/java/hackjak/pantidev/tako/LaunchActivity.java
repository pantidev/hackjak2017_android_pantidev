package hackjak.pantidev.tako;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class LaunchActivity extends AppCompatActivity {

    static final int LOCATION = 1;
    static final int WRITE_EXTERNAL = 2;
    static final int CAMERA = 3;
    int PERMISSION_ALL = 0;
    int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        permission_cek();
        finish();
    }

    public void permission_cek(){
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};

        //if (!hasPermissions(this, PERMISSIONS)) {
        //    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);

        for(String permission : PERMISSIONS) {
            if (!hasPermissions(this, permission))
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_ALL);
        }

        if(counter==3){
            Intent map = new Intent(this,Map.class);
            startActivity(map);
        }
        else
        {
            permission_cek();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            //for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            //}
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    counter++;
                    //Toast.makeText(LoginActivity.this, "Permission Accept!", Toast.LENGTH_SHORT).show();

                } else {
                    //Toast.makeText(Start.this, "For ", Toast.LENGTH_SHORT).show();

                }

            case WRITE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(Start.this, "Permission Accept!", Toast.LENGTH_SHORT).show();
                    counter++;

                } else {
                    //Toast.makeText(Start.this, "Permission Denied!", Toast.LENGTH_SHORT).show();

                }

            case CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(LoginActivity.this, "Permission Accept!", Toast.LENGTH_SHORT).show();
                    counter++;

                } else {
                    //Toast.makeText(LoginActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();

                }


        }
    }

    /*
    public void cekOverlay(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Please Turn on This Permission", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }else{
                Intent map = new Intent(this,Map.class);
                startActivity(map);
            }
        }else{
            Intent map = new Intent(this,Map.class);
            startActivity(map);
        }
    }
    */
}
