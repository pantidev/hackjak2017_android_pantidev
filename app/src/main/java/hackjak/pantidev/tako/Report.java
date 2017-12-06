package hackjak.pantidev.tako;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kosalgeek.android.photoutil.PhotoLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.Map;

/**
 * Created by bsupriadi on 12/6/2017.
 */

public class Report extends AppCompatActivity {
    TextView tvLokasi;
    EditText txtPesan, txtPelapor;
    ImageView ivCamera;
    LinearLayout llReportImage;
    Spinner spnEvent;
    String id, photoPath;
    
    long length;
    File file;

    CustomCamera cameraPhoto;
    final int CAMERA_REQUEST = 2;

    ArrayList<String> photo = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        tvLokasi = findViewById(R.id.tvNama);
        txtPelapor = findViewById(R.id.txtPelapor);
        txtPesan = findViewById(R.id.txtPesan);
        ivCamera = findViewById(R.id.ivCamera);

        llReportImage = findViewById(R.id.llReportImage);
        spnEvent = findViewById(R.id.spnEvent);

        //@SuppressLint("ResourceType")
        //ArrayAdapter<String>adapter = new ArrayAdapter<String>(Report.this,android.R.layout.simple_dropdown_item_1line,R.array.Event);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Event,android.R.layout.simple_dropdown_item_1line);
        spnEvent.setAdapter(adapter);

        cameraPhoto = new CustomCamera(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            tvLokasi.setText(bundle.getString("nama"));
            id = bundle.getString("id");

            ivCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (llReportImage.getChildCount()>0){
                        Toast.makeText(getApplicationContext(), "Photo already taken, please insert or delete in preview first", Toast.LENGTH_SHORT).show();
                    }else {
                        try {
                            Intent in = cameraPhoto.takePhotoIntent();
                            startActivityForResult(in, CAMERA_REQUEST);
                            cameraPhoto.addToGallery();
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(),
                                    "Something Wrong while taking photos", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        else
        {
            finishAffinity();
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
             if (requestCode == CAMERA_REQUEST) {
                photoPath = cameraPhoto.getPhotoPath();
                MediaScannerConnection.scanFile(Report.this,new String[]{photoPath},new String[]{"image/jpg"},null);
                Log.d("PATH",photoPath);

                file = new File(photoPath); //take a file for check length (name)
                length = file.length(); //check and get length file

                try {
                    Bitmap bitmap = PhotoLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                    Bitmap Scaled = Bitmap.createScaledBitmap(bitmap, 300, 250, false);
                    ImageView imageView = new ImageView(getApplicationContext());
                    ImageView clearPhoto = new ImageView(getApplicationContext());

                    LinearLayout.LayoutParams layoutParams =
                            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);

                    final LinearLayout llCamera = new LinearLayout(getApplicationContext());
                    llCamera.setOrientation(LinearLayout.HORIZONTAL);
                    llCamera.setLayoutParams(layoutParams);
                    imageView.setRotation(90);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.setPadding(0, 0, 0, 10);
                    imageView.setAdjustViewBounds(true);
                    imageView.setImageBitmap(Scaled);

                    LinearLayout.LayoutParams clearParams =
                            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                    clearPhoto.setLayoutParams(clearParams);
                    clearPhoto.setBackgroundResource(android.R.drawable.ic_menu_close_clear_cancel);

                    llCamera.addView(imageView);
                    llCamera.addView(clearPhoto);
                    llReportImage.addView(llCamera);

                    final LinearLayout llCam2 = llCamera;
                    ivCamera.setVisibility(View.GONE);
                    clearPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ivCamera.setVisibility(View.VISIBLE);
                            llReportImage.removeView(llCam2);
                            photoPath = "";

                        }
                    });

                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Error while loading image", Toast.LENGTH_SHORT).show();
                }
            }/*else if (requestCode == GALLERY_REQUEST) {
                 galleryPhoto.setPhotoUri(data.getData());
                 photoPath = galleryPhoto.getPath();

                 file = new File(photoPath); //take a file for check length
                 length = file.length(); //check and get length file

                 try {
                     Bitmap bitmap = PhotoLoader.init().from(photoPath).requestSize(512, 512).getBitmap();

                     ImageView imageView = new ImageView(getApplicationContext());
                     ImageView clearPhoto = new ImageView(getApplicationContext());

                     LinearLayout.LayoutParams layoutParams =
                             new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                     LinearLayout.LayoutParams.WRAP_CONTENT);

                     final LinearLayout llGallery = new LinearLayout(getApplicationContext());
                     llGallery.setLayoutParams(layoutParams);

                     imageView.setLayoutParams(layoutParams);
                     imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                     imageView.setPadding(10, 10, 10, 10);
                     imageView.setAdjustViewBounds(true);
                     imageView.setImageBitmap(bitmap);

                    *//*LinearLayout.LayoutParams clearParams =
                            new LinearLayout.LayoutParams(100,
                                    100);
                    clearParams.gravity = Gravity.RIGHT;*//*
                     clearPhoto.setLayoutParams(layoutParams);
                     clearPhoto.setBackgroundResource(android.R.drawable.ic_menu_close_clear_cancel);
                     llGallery.addView(clearPhoto);
                     llGallery.addView(imageView);
                     llGallery.setTag(String.valueOf(mapPhoto.size()-1));
                     llReportImage.addView(llGallery);


                     clearPhoto.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             llReportImage.removeView(llGallery);
                             photoPath = "";

                         }
                     });



                 } catch (FileNotFoundException e) {
                     Toast.makeText(getApplicationContext(), "Error while loading image", Toast.LENGTH_SHORT).show();
                 }
             
            }*/

        }

    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.approve_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    */

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.approve:
                if (spnEvent.getSelectedItem().equals("Pilih Tema Pelaporan")){
                    Toast.makeText(this, "Tolong Pilih Tema Pelaporan", Toast.LENGTH_SHORT).show();
                }else if (llReportImage.getChildCount()==0){
                    Toast.makeText(this, "Tolong Berikan 1 Foto TKP ", Toast.LENGTH_SHORT).show();
                }else if(txtPesan.getText().toString().trim().isEmpty()){
                    Toast.makeText(this, "Tolong Isi Pesan / Keterangan Kejadian", Toast.LENGTH_SHORT).show();
                }else if(txtPelapor.getText().toString().trim().isEmpty()){
                    Toast.makeText(this, "Tolong Ketik Nama Anda Pada Kolom Pelapor", Toast.LENGTH_SHORT).show();
                }else{

                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    */

    public void insert(){
        StringRequest req_rptra = new StringRequest(Request.Method.POST,
                "http://awseb-e-e-awsebloa-19aedqm1ecvzp-1894315445.ap-southeast-1.elb.amazonaws.com/api/lapor"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(Report.this, "Please Check Your Connection", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(Report.this, error+"\nPlease Contact pantidev2017@gmail.com", Toast.LENGTH_LONG).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("IdLokasi",id);
                params.put("Tentang",spnEvent.getSelectedItem().toString());
                params.put("Pesan", txtPesan.getText().toString().trim());
                params.put("Pengadu", txtPelapor.getText().toString());
                return params;
            }
        };
        MySingleton.getInstance(Report.this).addToRequestQueue(req_rptra);

    }

    public void submitReport(View view) {
        if (spnEvent.getSelectedItem().equals("Pilih Tema Pelaporan")){
            Toast.makeText(this, "Tolong Pilih Tema Pelaporan", Toast.LENGTH_SHORT).show();
        }else if (llReportImage.getChildCount()==0){
            Toast.makeText(this, "Tolong Berikan 1 Foto TKP ", Toast.LENGTH_SHORT).show();
        }else if(txtPesan.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Tolong Isi Pesan / Keterangan Kejadian", Toast.LENGTH_SHORT).show();
        }else if(txtPelapor.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Tolong Ketik Nama Anda Pada Kolom Pelapor", Toast.LENGTH_SHORT).show();
        }else{

        }
    }
}
