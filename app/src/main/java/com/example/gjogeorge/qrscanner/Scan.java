package com.example.gjogeorge.qrscanner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import javax.xml.transform.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;

public class Scan extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView zXingScannerView;
    private static final int REQUEST_CAMERA=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        zXingScannerView = new ZXingScannerView(this);

        setContentView(R.layout.activity_scan);
        Button btn = (Button) findViewById(R.id.btn1);
        btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                scan();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(Scan.this, "Permission is granted!", Toast.LENGTH_LONG).show();

            } else {
                //requestPermissions();
            }
        }
    }
    private boolean checkPermission(){

        return (ContextCompat.checkSelfPermission(Scan.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);

    }
    private void  requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{CAMERA},REQUEST_CAMERA);
    }


    public  void   onRequestPermissionResult(int requestCode, String permission[],int grantResults[])
    {
        switch (requestCode)
        {
            case REQUEST_CAMERA:
                if(grantResults.length>0)
                {
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted)
                    {
                        Toast.makeText(Scan.this, "Permission Granted",Toast.LENGTH_LONG).show();
                    }
                }else
                {
                    Toast.makeText(Scan.this, "Permission Denied",Toast.LENGTH_LONG).show();
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                    {
                        if(shouldShowRequestPermissionRationale(CAMERA))
                        {
                            displayAlertMessage("You need to allow access for both permission",
                                    new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                            }

                                        }
                                    }
                            );
                            return;
                        }
                    }
                }
                break;
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                if(zXingScannerView== null)
                {
                    zXingScannerView=new ZXingScannerView(this);
                    setContentView(zXingScannerView);
                }
                zXingScannerView.setResultHandler(this);
                zXingScannerView.startCamera();
            }
            else
            {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        zXingScannerView.stopCamera();
    }

    public  void displayAlertMessage(String message, DialogInterface.OnClickListener Listener)
    {
        new AlertDialog.Builder(Scan.this)
                .setMessage(message)
                .setPositiveButton("OK",Listener)
                .setNegativeButton("Cancel",null)
                .create()
                .show();

    }


    public void  scan(){
        View view;
        zXingScannerView=new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }



    @Override
    public void handleResult(final com.google.zxing.Result result) {
        final String scanResult=result.getText();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                zXingScannerView.resumeCameraPreview(Scan.this);
            }
        });
        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult) );
                startActivity(intent);
            }
        });

        builder.setMessage(scanResult);
        AlertDialog alert=builder.create();
        alert.show();
        Toast.makeText(getApplicationContext(),result.getText(),Toast.LENGTH_SHORT).show();
        zXingScannerView.resumeCameraPreview(this);

    }
}

