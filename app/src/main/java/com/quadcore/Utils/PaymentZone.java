package com.quadcore.Utils;

import android.content.ComponentName;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.quadcore.GeofenceView_main;
import com.quadcore.GeofenceView_settings;
import com.quadcore.MainActivity;

import static com.quadcore.GeofenceView_settings.startingPoint_x;
import static com.quadcore.GeofenceView_settings.startingPoint_y;

/**
 * Created by bbong on 2016-10-30.
 */

public class PaymentZone {
    public static boolean isPaid = false;
    public static int zoneCount=0;
    public static Point3D screen_leftUp ;
    public static Point3D screen_rightDown;

    public static Point3D actual_leftUp;
    public static Point3D actual_rightDown ;

    static{
        screen_leftUp = new Point3D();
        screen_rightDown = new Point3D();
        actual_leftUp = new Point3D();
        actual_rightDown = new Point3D();
    }

    public static boolean checkOnPaymentZone(Point3D userLocation)
    {
        boolean isOnPaymentZone = false;

        // 사용자가 결제 존 안에 있으면
        if( userLocation.getX() >= actual_leftUp.getX()
         && userLocation.getX() <= actual_rightDown.getX()
         && userLocation.getY() >= actual_leftUp.getY()
         && userLocation.getY() <= actual_rightDown.getY() )
        {
            isOnPaymentZone = true;
            Log.d(Constants.QUADCORE_LOG,"PaymentZone : checkOnPaymentZone : USER IS IN THE ZONE!!");
        }

        return isOnPaymentZone;
    }

    // 실제 위치 계산 ( 확대 -> 실제 )
    public static void calculateActualPosition(){

        float x,y;

        x = screen_leftUp.getX() - GeofenceView_settings.startingPoint_x;
        x = x / GeofenceView_settings.screenRatio;
        actual_leftUp.setX(x);

        y = screen_leftUp.getY() - GeofenceView_settings.startingPoint_y;
        y = y / GeofenceView_settings.screenRatio;
        actual_leftUp.setY(y);

        x = screen_rightDown.getX() - GeofenceView_settings.startingPoint_x;
        x = x / GeofenceView_settings.screenRatio;
        actual_rightDown.setX(x);

        y = screen_rightDown.getY() - GeofenceView_settings.startingPoint_y;
        y = y / GeofenceView_settings.screenRatio;
        actual_rightDown.setY(y);
    }

    public static void calculateScreenPosition()
    {
        float x1 = actual_leftUp.getX();
        float y1 = actual_leftUp.getY();
        float x2 = actual_rightDown.getX();
        float y2 = actual_rightDown.getY();

        float startingPoint_x = (Constants.viewWidthX/Constants._SCALE_FACTOR);
        float startingPoint_y = (Constants.viewHeightY/Constants._SCALE_FACTOR);

        x1 = x1 * GeofenceView_main.screenRatio;
        y1 = y1 * GeofenceView_main.screenRatio;
        x2 = x2 * GeofenceView_main.screenRatio;
        y2 = y2 * GeofenceView_main.screenRatio;

        x1 =x1 + startingPoint_x;
        y1 =y1 + startingPoint_y;
        x2 =x2 + startingPoint_x;
        y2 =y2 + startingPoint_y;

        screen_leftUp.setX(x1);
        screen_leftUp.setY(y1);
        screen_rightDown.setX(x2);
        screen_rightDown.setY(y2);
    }

}
