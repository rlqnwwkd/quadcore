package com.quadcore.Room;

import com.quadcore.Utils.Constants;
import com.quadcore.Utils.Point3D;

/**
 * Created by bbong on 2016-11-15.
 */

public class Room_Rectangle_6_6 {

    // cm - 방 사이즈 조절, 이곳만 변경하면됨
    public static double xLength=600;
    public static double yLength=600;
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
