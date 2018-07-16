package sg.edu.rp.c347.p09_gettingmylocations;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MainActivity extends AppCompatActivity {

    String folderLocation;
    FusedLocationProviderClient client;
    TextView tv;
    Button btnStart, btnStop,btnCheck;
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnCheck = (Button)findViewById(R.id.btnCheck);
        tv=(TextView)findViewById(R.id.tv);

        client = LocationServices.getFusedLocationProviderClient(this);

        if(checkPermission()==true){
            folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09";
            File folder = new File(folderLocation);
            if (folder.exists() == false) {
                boolean result = folder.mkdir();
                if (result == true) {
                    Log.d("File Read/Write", "Folder created");
                } else {
                    Log.d("File Read/Write", "Folder fail");
                }
            }else{
                Toast.makeText(MainActivity.this, "File already created", Toast.LENGTH_SHORT).show();

            }
        }else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

        }



        if(checkPermission() == true){
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                         msg = "Last known location when this Activity Started \n Latitude: "+location.getLatitude()+" \nLongitude : "+location.getLongitude();
                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT ).show();
                    }else{
                         msg = "No Last Known Location found";
                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT ).show();
                    }
                    tv.setText(msg);
                }
            });
        }else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }




        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkPermission() == true){
                    Intent i = new Intent(MainActivity.this, sg.edu.rp.c347.p09_gettingmylocations.MyService.class);
                    startService(i);

                }else{
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, sg.edu.rp.c347.p09_gettingmylocations.MyService.class);
                stopService(i);
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09";
                File targetFile = new File(folderLocation, "data.txt");
                if(targetFile.exists()== true){
                    String data="";
                    try{
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);
                        String line = br.readLine();
                        while(line !=null){
                            data += line+"\n";
                            line = br.readLine();
                        }

                        br.close();
                        reader.close();
                    }catch(Exception e){
                        Toast.makeText(MainActivity.this, "Failed to Read!",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                    Log.d("content",data);
                }
            }
        });




    }
    private boolean checkPermission() {
        int permissionCheck_Read = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck_Write = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED|| permissionCheck_Read == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Write == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }



}
