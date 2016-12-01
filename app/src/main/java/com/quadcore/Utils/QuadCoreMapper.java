package com.quadcore.Utils;

        import android.util.Log;

        import com.quadcore.GeofenceView_main;
        import com.quadcore.MainActivity;
        import com.quadcore.Room.Room_Paldal1;
        import com.quadcore.Services.RssiToDist;

        import java.util.ArrayList;
        import java.util.List;

        import static com.quadcore.Utils.Constants._BC1_IMMEDIATE;
        import static com.quadcore.Utils.Constants._BC2_IMMEDIATE;
        import static com.quadcore.Utils.Constants._BC3_IMMEDIATE;
        import static com.quadcore.Utils.Constants._SERVER_TIMEOUT;

/**
 * Created by bbong on 2016-11-08.
 */

public class QuadCoreMapper
{
    public static boolean checkOutOfBound(Point3D userPosition)
    {
        boolean isOutOfBound = false;
        if(MainActivity.roomType == Constants._ROOM_TYPE_Paldal1 )
        {
            double x = userPosition.getX();
            double y = userPosition.getY();

            // 방 안에 있다면
            if(
            ( x >= ( GeofenceView_main.bc1.getX() - Room_Paldal1.xPadding )
            &&  y >= ( GeofenceView_main.bc1.getY() - Room_Paldal1.yPadding )
            &&  x <= ( GeofenceView_main.bc1.getX() + Room_Paldal1.xPadding )
            &&  y <= ( GeofenceView_main.bc2.getY() + Room_Paldal1.yPadding ) )
            ||
            (   x >= ( GeofenceView_main.bc3.getX() )
            &&  y >= ( GeofenceView_main.bc1.getY() )
            &&  x <= ( GeofenceView_main.bc2.getX() )
            &&  y <= ( GeofenceView_main.bc2.getY() ) )
            )
            {
               // Log.d(Constants.QUADCORE_LOG,"checkOutOfBound : User is in the Room");
                isOutOfBound = false;
            }
            // 방 안에 없을 경우
            else
            {
                isOutOfBound = true;
               // Log.d(Constants.QUADCORE_LOG,"checkOutOfBound : User is out of the Room");
            }
        }
        return isOutOfBound;
    }

    public static Point3D getUserPosition(List<Point3D> user_tril, double avgRssi1, double avgRssi2, double avgRssi3, double avgRssi4)
    {

        double avgRssi_1 = avgRssi1;
        double avgRssi_2 = avgRssi2;
        double avgRssi_3 = avgRssi3;
        double avgRssi_4 = avgRssi4;

        Point3D userLocation = null;

        /////////////////
        // 1 ~ 8 적용
        ///////////////
        if( MainActivity.roomType == Constants._ROOM_TYPE_Paldal1 )
        {
            double dist_1,dist_2,dist_3;
            dist_1 = RssiToDist.getDistance(avgRssi_1, 1);
            dist_2 = RssiToDist.getDistance(avgRssi_2, 2);
            dist_3 = RssiToDist.getDistance(avgRssi_3, 3);

            if(dist_1 == RssiToDist._BC1_DIST_NEAR
            && dist_2 == RssiToDist._BC2_DIST_FAR
            && dist_3 == RssiToDist._BC3_DIST_FAR )
            {
                userLocation = UserLocation.getUserLocation(1);
            }
            else
            if(dist_1 == RssiToDist._BC1_DIST_IMME )
            {
                userLocation =UserLocation.getUserLocation(3);

                // 시연을 위한 결제존 강제 초기화
                // 결제 가능하게
                PaymentZone.isPaid = false;
                PaymentZone.zoneCount = 0;
            }
            else
            if( dist_3 == RssiToDist._BC3_DIST_IMME )
            {
                userLocation =UserLocation.getUserLocation(5);
            }
            else
            if(dist_1 == RssiToDist._BC1_DIST_NEAR
            && dist_2 == RssiToDist._BC2_DIST_NEAR
            && dist_3 == RssiToDist._BC3_DIST_NEAR )
            {
                userLocation =UserLocation.getUserLocation(6);
            }
            else
            if( dist_2 == RssiToDist._BC2_DIST_IMME )
            {
                userLocation =UserLocation.getUserLocation(7);
            }
            else
            if( dist_1 == RssiToDist._BC1_DIST_FAR
            && ( dist_2 == RssiToDist._BC2_DIST_NEAR || dist_2 == RssiToDist._BC2_DIST_FAR )
            && ( dist_3 == RssiToDist._BC3_DIST_NEAR || dist_3 == RssiToDist._BC3_DIST_FAR)
            )
            {
                userLocation = UserLocation.getUserLocation(8);
            }

            return userLocation;
        }
        // 사각형이면
        else if( MainActivity.roomType == Constants._ROOM_TYPE_RECTANGLE_6_6
                || MainActivity.roomType == Constants._ROOM_TYPE_RECTANGLE_12_7
                || MainActivity.roomType == Constants._ROOM_TYPE_CUSTOMIZE )
        {
            ////////////////////////////
            // bc1에 가까울 경우
            ////////////////////////////
            if( avgRssi_1 <= _BC1_IMMEDIATE )
            {
                // bc1 바로 근처에 찍는다
                userLocation.setX(GeofenceView_main.bc1.getX()+30);
                userLocation.setY(GeofenceView_main.bc1.getY()+30);
            }
            ////////////////////////////
            // bc2에 가까울 경우
            ////////////////////////////
            else if( avgRssi_2 <= _BC2_IMMEDIATE)
            {
                // bc2 바로 근처에 찍는다
                userLocation.setX(GeofenceView_main.bc2.getX()-30);
                userLocation.setY(GeofenceView_main.bc2.getY()+30);
            }
            ////////////////////////////
            // bc3에 가까울 경우
            ////////////////////////////
            else if(avgRssi_3 <= _BC3_IMMEDIATE)
            {
                // bc3 바로 근처에 찍는다
                userLocation.setX(GeofenceView_main.bc3.getX()-30);
                userLocation.setY(GeofenceView_main.bc3.getY()-30);
            }
            else if(avgRssi_4 <= 73)
            {
                // bc4 바로 근처에 찍는다
                userLocation.setX(GeofenceView_main.bc3.getX()+30);
                userLocation.setY(GeofenceView_main.bc3.getY()-30);
            }
            else
            {
                return null;
            }
        }
        // 삼각형
        else
        {
            ////////////////////////////
            // bc1에 가까울 경우
            ////////////////////////////
            if( avgRssi_1 <= _BC1_IMMEDIATE )
            {
                // bc1 바로 근처에 찍는다
                userLocation.setX(GeofenceView_main.bc1.getX());
                userLocation.setY(GeofenceView_main.bc1.getY()+30);
            }
            ////////////////////////////
            // bc2에 가까울 경우
            ////////////////////////////
            else if( avgRssi_2 <= _BC2_IMMEDIATE)
            {
                // bc2 바로 근처에 찍는다
                userLocation.setX(GeofenceView_main.bc2.getX()-30);
                userLocation.setY(GeofenceView_main.bc2.getY()-30);
            }
            ////////////////////////////
            // bc2에 가까울 경우
            ////////////////////////////
            else if(avgRssi_3 <= _BC3_IMMEDIATE)
            {
                // bc3 바로 근처에 찍는다
                userLocation.setX(GeofenceView_main.bc3.getX()+30);
                userLocation.setY(GeofenceView_main.bc3.getY()-30);
            }
            else
            {
                return null;
            }
        }


        return userLocation;
    }
}
