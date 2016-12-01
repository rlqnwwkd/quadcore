package com.quadcore.Utils;

/**
 * Created by bbong on 2016-11-25.
 */

public class UserLocation {
    public static Point3D getUserLocation(int num)
    {
        Point3D user = new Point3D();

        if(num == 1)
        {
            user.setX(600);
            user.setY(-200);
            user.setZoneNumber(1);
        }
        else if(num == 2)
        {
            user.setX(100);
            user.setY(200);
            user.setZoneNumber(2);
        }
        else if(num == 3)
        {
            user.setX(600);
            user.setY(30);
            user.setZoneNumber(3);
        }
        else if(num == 4)
        {
            user.setX(1100);
            user.setY(200);
            user.setZoneNumber(4);
        }
        else if(num == 5)
        {
            user.setX(100);
            user.setY(500);
            user.setZoneNumber(5);
        }
        else if(num == 6)
        {
            user.setX(600);
            user.setY(500);
            user.setZoneNumber(6);
        }
        else if(num == 7)
        {
            user.setX(1100);
            user.setY(500);
            user.setZoneNumber(7);
        }
        else if( num == 8)
        {
            user.setX(600);
            user.setY(750);
            user.setZoneNumber(8);
        }
        return user;
    }
}
