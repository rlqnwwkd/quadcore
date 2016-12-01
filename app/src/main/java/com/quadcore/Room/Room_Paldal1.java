package com.quadcore.Room;

import com.quadcore.Utils.Constants;
import com.quadcore.Utils.Point3D;

/**
 * Created by bbong on 2016-11-23.
 */

public class Room_Paldal1 {
    // cm - 방 사이즈 조절, 이곳만 변경하면됨
    public static double xLength=1200;
    public static double yLength=600;

    public static final int xPadding=200;
    public static final int yPadding=300;


    // 비콘위치
    public static Point3D bc1Position, bc2Position, bc3Position,bc4Position;
    // 결제존 위치
    public static Point3D leftUp, rightDown;
    // screen ratio
    public static float screenRatio;

    static{
        screenRatio=((float)(Constants.viewWidth / xLength));
        bc1Position = new Point3D((float)xLength/2 ,0);
        bc2Position = new Point3D((float)xLength, (float)yLength);
        bc3Position = new Point3D( 0, (float)yLength);
        bc4Position = new Point3D(0,0);

        leftUp = new Point3D( (float)((xLength/2)-xPadding), (float)(yLength));
        rightDown = new Point3D( (float)((xLength/2)+xPadding), (float)(yLength+yPadding));
    }

    public static Point3D pos0 = new Point3D(620,1797);
    public static Point3D pos1 = new Point3D(620,700);
    public static Point3D pos3 = new Point3D(620,900);
    public static Point3D pos5 = new Point3D(200,1280);
    public static Point3D pos6 = new Point3D(620,1197);
    public static Point3D pos7 = new Point3D(1030,1280);
    public static Point3D pos8 = new Point3D(620,1500);
    public static Point3D pos6_up = new Point3D(620,950);


    public static Point3D getPosition(int n)
    {
        switch (n)
        {
            case 0:
                return pos0;
            case 1:
                return pos1;
            case 3:
                return pos3;
            case 5:
                return pos5;
            case 6:
                return pos6;
            case 7:
                return pos7;
            case 8:
                return pos8;
            default:
                return new Point3D(0,0);
        }
    }

}
