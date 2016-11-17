package com.quadcore.Services;

import com.quadcore.MainActivity;
import com.quadcore.Utils.Constants;

/**
 * Created by bbong on 2016-10-22.
 */

////////////////////////////////////////////////////////////////////////////
// 평균 Rssi값 -> 거리로 바꿔주는 메소드
//////////////////////////////////////////////////////////////////////////
public class RssiToDist {

    public static double getDistance(double Rssi) {
        // RSSI는 -30~-9999, -가 붙어있어서 읽기쉽게 부호 바꿔줌
        double rssi = -Rssi;
        double distance = 0;

        switch (MainActivity.roomType) {
            case Constants._ROOM_TYPE_CUSTOMIZE:
                distance = getDistance_cust(rssi);
                break;
            case Constants._ROOM_TYPE_RECTANGLE_12_7:
                distance = getDistance_option1(rssi);
                break;
            case Constants._ROOM_TYPE_TRIANGLE_6_6:
                distance = getDistance_Triangle_6_6(rssi);
                break;
            case Constants._ROOM_TYPE_TRIANGLE_3_3:
                distance = getDistance_Triangle_3_3(rssi);
                break;
            case Constants._ROOM_TYPE_RECTANGLE_6_6:
                distance = getDistance_Rectangle_6_6(rssi);
                break;
            default:
                break;
        }

        return distance;
    }

    /////////////////////////////////////////////////
    // 사각형 6*6
    ////////////////////////////////////////////////
    public static double getDistance_Rectangle_6_6(double rssi)
    {
        double distance = 0;

        if( rssi <= 45 )
        {
            distance = 200;
        }
        else if( rssi <= 66 )
        {
            distance = 350;
        }
        else if( rssi <= 70)
        {
            distance = 700;
        }
        else
        {
            distance = 800;
        }
        return distance;
    }

    /////////////////////////////////////////////////
    // 옵션 2 - 삼각혁 일때 맵핑
    ////////////////////////////////////////////////
    public static double getDistance_Triangle_3_3(double rssi)
    {
        double distance = 0;

        if( rssi <= 45 )
        {
            distance = 200;
        }
        else if( rssi <= 66 )
        {
            distance = 350;
        }
        else if( rssi <= 70)
        {
            distance = 700;
        }
        else
        {
            distance = 800;
        }
        return distance;
    }


    public static double getDistance_Triangle_6_6(double rssi)
    {
        double distance = 0;

        if( rssi <= 45 )
        {
            distance = 200;
        }
        else if( rssi <= 66 )
        {
            distance = 350;
        }
        else if( rssi <= 70)
        {
            distance = 700;
        }
        else
        {
            distance = 800;
        }
        return distance;
    }

    /////////////////////////////////////////////////
    // 사용자 정의
    ////////////////////////////////////////////////
    public static double getDistance_cust(double rssi)
    {
        double distance = 0;

        if( rssi <= 45)
        {
            distance = 300;
        }
        else if( rssi <= 50 )
        {
            distance = 400;
        }
        else
        {
            distance = 500;
        }
        return distance;
    }

    /////////////////////////////////////////////////
    // 옵션 1 일때 맵핑
    ////////////////////////////////////////////////
    public static double getDistance_option1(double rssi)
    {
        double distance = 0;

        if( rssi <= 45 )
        {
            distance = 200;
        }
        else if( rssi <= 66 )
        {
            distance = 350;
        }
        else if( rssi <= 70)
        {
            distance = 700;
        }
        else
        {
            distance = 800;
        }
        return distance;
    }
}
