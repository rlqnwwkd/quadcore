package com.quadcore.Utils;

import com.quadcore.GeofenceView_main;
import com.quadcore.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bbong on 2016-11-08.
 */

public class QuadCoreMapper
{
    public static List<Point3D> getUserPosition(List<Point3D> user_tril, double avgRssi1, double avgRssi2, double avgRssi3, double avgRssi4)
    {
        double avgRssi_1 = -avgRssi1;
        double avgRssi_2 = -avgRssi2;
        double avgRssi_3 = -avgRssi3;
        double avgRssi_4 = -avgRssi4;

        Point3D userLocation = null;

        List<Point3D> result = new ArrayList<Point3D>();

        if(user_tril != null)
        {
            userLocation = user_tril.get(0);
        }
        else
        {
            return user_tril;
        }

        ////////////////////////////
        // bc1에 가까울 경우
        ////////////////////////////
        if( avgRssi_1 <= 50)
        {
            // bc1 바로 근처에 찍는다
            userLocation.setX(GeofenceView_main.bc1.getX()+30);
            userLocation.setY(GeofenceView_main.bc1.getY()+30);
        }
        ////////////////////////////
        // bc2에 가까울 경우
        ////////////////////////////
        else if( avgRssi_2 <= 50)
        {
            // bc2 바로 근처에 찍는다
            userLocation.setX(GeofenceView_main.bc2.getX()-30);
            userLocation.setY(GeofenceView_main.bc2.getY()+30);
        }
        ////////////////////////////
        // bc2에 가까울 경우
        ////////////////////////////
        else if(avgRssi_3 <= 50)
        {
            // bc3 바로 근처에 찍는다
            userLocation.setX(GeofenceView_main.bc3.getX()-30);
            userLocation.setY(GeofenceView_main.bc3.getY()-30);
        }
        else if(avgRssi4 <= 50)
        {
            // bc4 바로 근처에 찍는다
            userLocation.setX(GeofenceView_main.bc4.getX()+30);
            userLocation.setY(GeofenceView_main.bc4.getY()-30);
        }

        /*
        ////////////////////////////
        // 가운데
        ////////////////////////////
        else if( avgRssi_2 >= 60 && avgRssi_2<= 65)
        {
            userLocation.setX((float)(GeofenceView_main.avgTopDist/2.0));
            userLocation.setY((float)(GeofenceView_main.avgRightDist/2.0));
        }
        //////////////////////////////////
        // 4번째 비콘 위치(지금은 없음)
        //////////////////////////////////
        else if( avgRssi_2 > 65)
        {
            userLocation.setX(GeofenceView_main.bc4.getX()+30);
            userLocation.setY(GeofenceView_main.bc4.getY()-30);
        }

        ////////////////////////////
        // 아래쪽 결제존
        ////////////////////////////
        else if( avgRssi_1 >= 73 && avgRssi_2 >= 70 && avgRssi_3 >=59)
        {
            userLocation.setX((float)(GeofenceView_main.avgTopDist/2.0));
            userLocation.setY((float)(GeofenceView_main.avgRightDist+30));
        }
        */

        result.add(userLocation);
        return result;
    }
}
