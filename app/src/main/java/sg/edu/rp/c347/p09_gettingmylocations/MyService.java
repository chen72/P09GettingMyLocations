package sg.edu.rp.c347.p09_gettingmylocations;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileWriter;

public class MyService extends Service {

    FusedLocationProviderClient client;
    LocationCallback mLocationCallback;
    String folderLocation;


    public MyService() {
    }

    boolean started;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        client = LocationServices.getFusedLocationProviderClient(this);
        Log.d("Service", "Service created");


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location data = locationResult.getLastLocation();
                    double lat = data.getLatitude();
                    double lng = data.getLongitude();
                    String msg = "New Location detected lat: " + lat + " Lng : " + lng;
                    Toast.makeText(MyService.this, msg, Toast.LENGTH_SHORT).show();






                }
            }

            ;
        };

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (started == false) {
            started = true;
            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setSmallestDisplacement(100);

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        Location data = locationResult.getLastLocation();
                        double lat = data.getLatitude();
                        double lng = data.getLongitude();
                        String msg = "New Location detected lat: " + lat + " Lng : " + lng;
                        Log.i("info ", lat + " " + lng);
                        Toast.makeText(MyService.this, msg, Toast.LENGTH_SHORT).show();


                        try {
                            folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09";
                            File targetFile = new File(folderLocation, "data.txt");
                            FileWriter writer = new FileWriter(targetFile, true);
                            writer.write(lat+","+lng);
                            writer.flush();
                            writer.close();
                            Toast.makeText(MyService.this, "Successful write!",
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(MyService.this, "Failed to write!",
                                    Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }

                ;
            };

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);





            Log.d("Service", "Service started");
        } else {
            Log.d("Service", "Service is still running");
        }
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        Log.d("Service", "Service exited");

        client.removeLocationUpdates(mLocationCallback);
        super.onDestroy();
    }


}
