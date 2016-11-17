package com.quadcore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.quadcore.Services.BackgroundBeaconRangingService;
import com.quadcore.Utils.Constants;

import java.util.List;
import java.util.UUID;

public class RssiCheckActivity extends AppCompatActivity {
    public static TextView rssi1TextView;
    public static TextView rssi2TextView;
    public static TextView rssi3TextView;
    public static TextView rssi4TextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("RssiCheckActivity","onCreate()");
        setContentView(R.layout.activity_rssi_check);

        rssi1TextView = (TextView)findViewById(R.id.rssi1TextView);
        rssi2TextView = (TextView)findViewById(R.id.rssi2TextView);
        rssi3TextView = (TextView)findViewById(R.id.rssi3TextView);
        rssi4TextView = (TextView)findViewById(R.id.rssi4TextView);

        BackgroundBeaconRangingService.isRssiCheck=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("RssiCheckActivity","onDestroy()");
        BackgroundBeaconRangingService.isRssiCheck=false;
    }
}
