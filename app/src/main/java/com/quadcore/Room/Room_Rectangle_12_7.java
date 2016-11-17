package com.quadcore.Room;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.quadcore.Room_Activity.GeofenceSettingsActivity_Customize;
import com.quadcore.Room_Activity.GeofenceSettingsActivity_Rectangle_12_7;
import com.quadcore.Utils.Constants;
import com.quadcore.Utils.Point3D;
import com.quadcore.Utils.ServerInfo;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by bbong on 2016-11-08.
 */

////////////////////////////////////
// 1층 12m * 7.2m
////////////////////////////////////
public class Room_Rectangle_12_7 {
    // cm - 방 사이즈 조절, 이곳만 변경하면됨
    public static double xLength=1200;
    public static double yLength=720;
    // 결제존
    public static double height = 300;
    public static double padding = 50;


    // 비콘위치
    public static Point3D bc1Position, bc2Position, bc3Position,bc4Position;
    // 결제존 위치
    public static Point3D leftUp, rightDown;
    // screen ratio
    public static float screenRatio;

    static{
        screenRatio=((float)(Constants.viewWidth / xLength));
        bc1Position = new Point3D(0,0);
        bc2Position = new Point3D( (float)xLength, 0);
        bc3Position = new Point3D( (float)xLength, (float)yLength);
        bc4Position = new Point3D(0, (float)yLength);

        leftUp = new Point3D(0, (float)(yLength+padding));
        rightDown = new Point3D((float)xLength, (float)(yLength+height+padding));
    }
}
