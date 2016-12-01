package com.quadcore.Services;

import com.quadcore.MainActivity;
import com.quadcore.Utils.Constants;

import static com.quadcore.Utils.Constants._BC1_FAR;
import static com.quadcore.Utils.Constants._BC1_IMMEDIATE;
import static com.quadcore.Utils.Constants._BC2_FAR;
import static com.quadcore.Utils.Constants._BC2_IMMEDIATE;
import static com.quadcore.Utils.Constants._BC3_FAR;
import static com.quadcore.Utils.Constants._BC3_IMMEDIATE;

/**
 * Created by bbong on 2016-10-22.
 */

////////////////////////////////////////////////////////////////////////////
// 평균 Rssi값 -> 거리로 바꿔주는 메소드
//////////////////////////////////////////////////////////////////////////
public class RssiToDist {

    public static final int _BC1_DIST_IMME = 100;
    public static final int _BC1_DIST_NEAR = 500;
    public static final int _BC1_DIST_FAR = 1100;

    public static final int _BC2_DIST_IMME = 100;
    public static final int _BC2_DIST_NEAR = 800;
    public static final int _BC2_DIST_FAR = 1150;
    public static final int _BC3_DIST_IMME = 100;
    public static final int _BC3_DIST_NEAR = 800;
    public static final int _BC3_DIST_FAR = 1150;


    // 팔달 1
    public static double getDistance_Paldal1(double rssi, int type)
    {
        double distance = 0;

        // 비콘 1 일 경우
        if(type == 1)
        {
            if( rssi < 70 )
            {
                distance = _BC1_DIST_IMME;
            }
            else if( rssi < 90)
            {
                distance = _BC1_DIST_NEAR;
            }
            else
            {
                distance = _BC1_DIST_FAR;
            }
        }

        if(type==2)
        {
            if( rssi < 70 )
            {
                distance = _BC2_DIST_IMME;
            }
            else if( rssi < 88)
            {
                distance = _BC2_DIST_NEAR;
            }
            else
            {
                distance = _BC2_DIST_FAR;
            }
        }

        if(type==3)
        {
            if( rssi < 70 )
            {
                distance = _BC3_DIST_IMME;
            }
            else if( rssi < 88)
            {
                distance = _BC3_DIST_NEAR;
            }
            else
            {
                distance = _BC3_DIST_FAR;
            }
        }

        return distance;
    }


    public static double getDistance(double Rssi, int type) {
        // RSSI는 -30~-9999, -가 붙어있어서 읽기쉽게 부호 바꿔줌
        double rssi = -Rssi;
        double distance = 0;

        switch (MainActivity.roomType)
        {
            case Constants._ROOM_TYPE_CUSTOMIZE:
                distance = getDistance_cust(rssi,type);
                break;
            case Constants._ROOM_TYPE_RECTANGLE_12_7:
                distance = getDistance_option1(rssi, type);
                break;
            case Constants._ROOM_TYPE_TRIANGLE_6_6:
                distance = getDistance_Triangle_6_6(rssi,type);
                break;
            case Constants._ROOM_TYPE_TRIANGLE_3_3:
                distance = getDistance_Triangle_3_3(rssi,type);
                break;
            case Constants._ROOM_TYPE_RECTANGLE_6_6:
                distance = getDistance_Rectangle_6_6(rssi,type);
                break;
            case Constants._ROOM_TYPE_Paldal1:
                distance = getDistance_Paldal1(rssi,type);
                break;
            default:
                break;
        }

        return distance;
    }


    ///////////////////////////////
    // 강의실 1
    ///////////////////////////////
    public static double getDistance_Triangle_6_6(double rssi, int type)
    {
        double distance = 0;

        // 비콘 1 일 경우
        if(type == 1)
        {
            if( rssi <= _BC1_IMMEDIATE )
            {
                distance = 100;
            }
            else if( rssi <= 84)
            {
                distance = 500;
            }
            else
            {
                distance = 1200;
            }
        }

        if(type==2)
        {
            if( rssi <= _BC2_IMMEDIATE )
            {
                distance = 100;
            }
            else if( rssi <= 80)
            {
                distance = 500;
            }
            else
            {
                distance = 900;
            }
        }

        if(type==3)
        {
            if( rssi <= _BC3_IMMEDIATE )
            {
                distance = 100;
            }
            else if( rssi <= 80)
            {
                distance = 500;
            }
            else
            {
                distance = 900;
            }
        }

        return distance;
    }


    /////////////////////////////////////////////////
    // 사각형 6*6
    ////////////////////////////////////////////////
    public static double getDistance_Rectangle_6_6(double rssi, int type)
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
    public static double getDistance_Triangle_3_3(double rssi, int type)
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
    public static double getDistance_cust(double rssi, int type)
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
    public static double getDistance_option1(double rssi, int type)
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
