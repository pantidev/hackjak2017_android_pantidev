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

public class Start extends AppCompatActivity {

    static final int LOCATION = 1;
    static final int WRITE_EXTERNAL = 2;
    static final int CAMERA = 3;
    int PERMISSION_ALL = 0;
    Thread a = new Thread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //cekOverlay();
        permission_cek();
        //finish();

        /*
        a = new Thread(){
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000 * 2);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                permission_cek();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        a.start();
        */



    }

    public void permission_cek(){
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);

        }else{
            Intent map = new Intent(Start.this,Map.class);
            startActivity(map);
            finish();
            //a.interrupt();
            /*Handler wait = new Handler();
            wait.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //a.interrupt();
                }
            },2000);*/
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int counter = 0;
        for(int i : grantResults)
        {
            if(i==PackageManager.PERMISSION_GRANTED)
                counter++;
        }
        if(counter==3)
        {
            Intent map = new Intent(Start.this,Map.class);
            startActivity(map);
            finish();
        }
        else
        {
            permission_cek();
        }
    }

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


}
