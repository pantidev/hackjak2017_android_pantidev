package hackjak.pantidev.tako;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Map extends FragmentActivity implements LocationListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;

    TextView tvNama, tvAlamat, tvLuas, tvResmi, tvEmail, tvRepLingkungan, tvRepKejahatan,tvLaktasi;
    Button btnReport;
    SwitchCompat swTraffic;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private LocationManager locationManager;
    Location location;

    String URL = "http://ppid.jakarta.go.id/json?url=http://data.jakarta.go.id/dataset/a96e0288-90d8-48b1-9c3a-9db11372e59f/resource/a6ffd51f-279d-49d6-9f33-eed327ea4753/download/RPTRA-Peresmian-sampai-dengan-Maret-2017.csv";
    String TAG = this.getClass().getSimpleName();
    int index = -1;
    String ruang_laktasi;

    Bitmap rptra_bitmap, tawuran_bitmap;
    BitmapDescriptor rptra_icon, tawuran_icon;

    ArrayList<Rptra_Setter_Getter> rptraAr = new ArrayList<>();
    ArrayList<String>tempRPTRA = new ArrayList<>();
    ArrayList<String>lingkungan = new ArrayList<>();
    ArrayList<String>kejahatan = new ArrayList<>();

    private static final long INTERVAL = 1000 * 20;
    private static final long FASTEST_INTERVAL = 1000 * 10;

    private ProgressDialog pDialog;


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        swTraffic = findViewById(R.id.swTraffic);

        //Using Google Location Service to get Current Latitude & Longitude
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        createLocationRequest();

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Menunggu...");
        pDialog.setCancelable(false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        MapsInitializer.initialize(this);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d("LOCATION", String.valueOf(location));
        if (location == null){
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        BitmapDrawable bitmap_rptra = (BitmapDrawable) getResources().getDrawable(R.drawable.tako_marker);
        Bitmap rptra_bit = bitmap_rptra.getBitmap();
        rptra_bitmap = Bitmap.createScaledBitmap(rptra_bit, 70, 100, false);
        rptra_icon = BitmapDescriptorFactory.fromBitmap(rptra_bitmap);

        BitmapDrawable bitmap_tawuran = (BitmapDrawable) getResources().getDrawable(R.drawable.war_icon);
        Bitmap tawuran_bit = bitmap_tawuran.getBitmap();
        tawuran_bitmap = Bitmap.createScaledBitmap(tawuran_bit, 70, 100, false);
        tawuran_icon = BitmapDescriptorFactory.fromBitmap(tawuran_bitmap);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-6.203698, 106.840425), 11.0f));

        swTraffic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    mMap.setTrafficEnabled(true);
                    swTraffic.setText("Traffic On   ");
                }else{
                    mMap.setTrafficEnabled(false);
                    swTraffic.setText("Traffic Off   ");
                }
            }
        });

        //get taman
        showpDialog();
        StringRequest req_rptra = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    hidepDialog();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String id = obj.getString("id");
                        if (!id.equalsIgnoreCase("129")) {
                            String latitude = obj.getString("latitude_lokasi");
                            String longitude = obj.getString("longitude_lokasi");
                            if (!latitude.equalsIgnoreCase("")||!longitude.equalsIgnoreCase("")) {
                                Log.d("id", id);
                                String nama_rptra = obj.getString("nama_rptra");
                                String alamat = obj.getString("alamat");
                                String kelurahan = obj.getString("kelurahan");
                                String kecamatan = obj.getString("kecamatan");
                                String kab_kota = obj.getString("kab_kota");
                                String luas = obj.getString("luas");
                                String waktu_peresmiaan = obj.getString("waktu_peresmiaan");
                                String alamat_email = obj.getString("alamat_email");
                                double latitude_lokasi = Double.parseDouble(latitude);
                                double longitude_lokasi = Double.parseDouble(longitude);
                                String address = alamat+" "+kelurahan+" "+kecamatan+" "+kab_kota;

                                rptraAr.add(new Rptra_Setter_Getter(id,nama_rptra,address,luas,waktu_peresmiaan,alamat_email));
                                tempRPTRA.add(id);
                                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude_lokasi, longitude_lokasi)).title(id).icon(rptra_icon));
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(Map.this, "Coba Cek Koneksi Anda", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(Map.this, error+"\nTolong kirim email : pantidev2017@gmail.com", Toast.LENGTH_LONG).show();
                }
            }
        });
        MySingleton.getInstance(Map.this).addToRequestQueue(req_rptra);
        req_rptra.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        //Get tawuran
        StringRequest tawuran = new StringRequest(Request.Method.GET,
                "http://awseb-e-e-awsebloa-19aedqm1ecvzp-1894315445.ap-southeast-1.elb.amazonaws.com/api/tawuran",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("TAWURAN",response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                String id = obj.getString("id");
                                String lokasi = obj.getString("lokasi");
                                String unsur = obj.getString("unsur");
                                Double latitude = Double.valueOf(obj.getString("latitude"));
                                Double longitude = Double.valueOf(obj.getString("longitude"));


                                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Tawuran").icon(tawuran_icon));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(Map.this, "Coba Cek Koneksi Anda", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(Map.this, error+"\nTolong kirim email : pantidev2017@gmail.com", Toast.LENGTH_LONG).show();
                }
            }
        });
        MySingleton.getInstance(Map.this).addToRequestQueue(tawuran);
        tawuran.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (!marker.getTitle().equalsIgnoreCase("Tawuran")){

                    index = tempRPTRA.indexOf(marker.getTitle());
                    final String Markid = rptraAr.get(index).getId();
                    Log.d("MARKERID",Markid);
                    Log.d("MARKERID",String.valueOf(Integer.parseInt(Markid)+1));


                    lingkungan.clear();
                    kejahatan.clear();
                    showpDialog();
                    StringRequest dashboard = new StringRequest(Request.Method.GET,
                            "http://awseb-e-e-awsebloa-19aedqm1ecvzp-1894315445.ap-southeast-1.elb.amazonaws.com/api/lapor/"+Markid,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    hidepDialog();
                                    if (!response.equalsIgnoreCase("[]")) {
                                        try {
                                            JSONArray jsonArray = new JSONArray(response);
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject obj = jsonArray.getJSONObject(i);

                                                String Tentang = obj.getString("Tentang");
                                                if (Tentang.equalsIgnoreCase("Lingkungan")) {
                                                    lingkungan.add(Tentang);
                                                } else {
                                                    kejahatan.add(Tentang);
                                                }
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    //Ruang laktasi

                                    StringRequest laktasi = new StringRequest(Request.Method.GET,
                                            "http://awseb-e-e-awsebloa-19aedqm1ecvzp-1894315445.ap-southeast-1.elb.amazonaws.com/api/rl/"+String.valueOf(Integer.parseInt(Markid)+1),
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject obj = new JSONObject(response);

                                                            ruang_laktasi = obj.getString("ketersediaan_ruang_laktasi").replace("Ya","Ada");
                                                            Log.d("RUANG",ruang_laktasi);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }


                                                    final Dialog markerDialog;
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                        markerDialog = new Dialog(Map.this, android.R.style.Theme_Material_Dialog_Alert);

                                                    } else {
                                                        markerDialog = new Dialog(Map.this);
                                                    }

                                                    markerDialog.setTitle("Detail RPTRA");
                                                    markerDialog.getWindow().setBackgroundDrawableResource(R.drawable.box_ash);
                                                    markerDialog.setContentView(R.layout.detail_marker);

                                                    tvNama = markerDialog.findViewById(R.id.tvNama);
                                                    tvAlamat = markerDialog.findViewById(R.id.tvAlamat);
                                                    tvLuas = markerDialog.findViewById(R.id.tvLuas);
                                                    tvResmi = markerDialog.findViewById(R.id.tvResmi);
                                                    tvEmail = markerDialog.findViewById(R.id.tvEmail);
                                                    btnReport = markerDialog.findViewById(R.id.btnReport);
                                                    tvRepLingkungan = markerDialog.findViewById(R.id.tvRepLingkungan);
                                                    tvRepKejahatan = markerDialog.findViewById(R.id.tvRepKejahatan);
                                                    tvLaktasi = markerDialog.findViewById(R.id.tvLaktasi);

                                                    tvNama.setText(rptraAr.get(index).getNama());
                                                    tvAlamat.setText(rptraAr.get(index).getAddress());
                                                    tvLuas.setText(rptraAr.get(index).getLuas()+"m2");
                                                    tvResmi.setText(rptraAr.get(index).getWaktuperesmian());
                                                    tvEmail.setText(rptraAr.get(index).getEmail());
                                                    tvRepLingkungan.setText(String.valueOf(lingkungan.size()));
                                                    tvRepKejahatan.setText(String.valueOf(kejahatan.size()));
                                                    tvLaktasi.setText(ruang_laktasi);

                                                    btnReport.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent report = new Intent(Map.this,Report.class);
                                                            report.putExtra("id",rptraAr.get(index).getId());
                                                            report.putExtra("nama",rptraAr.get(index).getNama());
                                                            startActivity(report);
                                                        }
                                                    });

                                                    markerDialog.show();
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                                Toast.makeText(Map.this, "Coba Cek Koneksi Anda", Toast.LENGTH_LONG).show();
                                            }else{
                                                Toast.makeText(Map.this, error+"\nTolong kirim email : pantidev2017@gmail.com", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                    MySingleton.getInstance(Map.this).addToRequestQueue(laktasi);
                                      laktasi.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


                                }

                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hidepDialog();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(Map.this, "Please Check Your Connection", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(Map.this, error+"\nPlease Contact pantidev2017@gmail.com", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    MySingleton.getInstance(Map.this).addToRequestQueue(dashboard);
                    dashboard.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


                }
                    return true;
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(location == null){
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location locationUpd) {
        location = locationUpd;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        Log.d(TAG, "Location u" +
                "pdate started ..............: ");
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }else{
                Map.this.finish();
            }


        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan 2 Kali Untuk Keluar", Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);
    }
}
