package com.quadcore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.quadcore.Room_Activity.GeofenceSettingsActivity_Customize;
import com.quadcore.Room_Activity.GeofenceSettingsActivity_Triangle_3_3;
import com.quadcore.Room_Activity.GeofenceSettingsActivity_Triangle_6_6;
import com.quadcore.Room_Activity.GeofenceSettingsActivity_Rectangle_12_7;
import com.quadcore.Room_Activity.GeofenceSettingsActivity_Rectangle_6_6;
import com.quadcore.Room_Activity.GeofenceSettingsActivity_paldal1;

public class GeofenceSettingsActivity_choice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence_settings_choice);
    }


    public void onViewCustomizeClicked(View view)
    {
        final Intent intent = new Intent(this, GeofenceSettingsActivity_Customize.class);
        startActivity(intent);
    }

    public void onSetting_paldal1Clicked(View view)
    {

        final Intent intent = new Intent(this, GeofenceSettingsActivity_paldal1.class);
        startActivity(intent);
    }

    public void onSetting_rectangle_6_6Clicked(View view)
    {

        final Intent intent = new Intent(this, GeofenceSettingsActivity_Rectangle_6_6.class);
        startActivity(intent);
    }

    public void onSetting_rectangle_12_7Clicked(View view)
    {

        final Intent intent = new Intent(this, GeofenceSettingsActivity_Rectangle_12_7.class);
        startActivity(intent);
    }

    public void onSetting_triangle_6_6Clicked(View view)
    {
        final Intent intent = new Intent(this, GeofenceSettingsActivity_Triangle_6_6.class);
        startActivity(intent);
    }

    public void onSetting_triangle_3_3Clicked(View view)
    {
        final Intent intent = new Intent(this, GeofenceSettingsActivity_Triangle_3_3.class);
        startActivity(intent);
    }

}
