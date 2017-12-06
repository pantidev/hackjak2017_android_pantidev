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
        setContentView(R.layout.start);
        /*
        //cekOverlay();
        permission_cek();

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

        /*Handler wait = new Handler();
        wait.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent map = new Intent(Start.this,Map.class);
                startActivity(map);
            }
        },2000);*/

    }


}
