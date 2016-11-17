package com.quadcore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.quadcore.Room.Room_Rectangle_12_7;
import com.quadcore.Room.Room_Rectangle_6_6;
import com.quadcore.Room.Room_Triangle_3_3;
import com.quadcore.Room.Room_Triangle_6_6;
import com.quadcore.Utils.Constants;
import com.quadcore.Utils.PaymentZone;
import com.quadcore.Utils.Point3D;


public class GeofenceView_main extends View {


    // 실제 위, 오른쪽 변 길이 평균 값
    public static double avgTopDist, avgRightDist= 0;
    public Paint paint;
    public static int locatedBeaconCnt = 0;
    // 비콘 4개의 위치
    public static Point3D bc1, bc2, bc3,bc4;
    // 사용자 위치
    public static Point3D userLocation;


    ///////////////////////////////////////////////////////////////////////
    // 실제거리 , view에서 표현된 거리 비율
    // 실제 크기는 bc1~bc4, userLocation에 저장되어 있고,
    // 화면에서는 실제 크기에 lengthRatio를 곱해서 확대해서 보여준다
    /////////////////////////////////////////////////////////////////
    public static float screenRatio;

    //////////////////////////////////////////////////////////////////////
    // (0,0) 좌표 지정 -> (0,0)으로 그대로 찍으면 왼쪽 모서리에 찍히기 때문
    ///////////////////////////////////////////////////////////////////////
    public static float startingPoint_x;
    public static float startingPoint_y;


    ////////////////////////////////////////////////////////
    // invalidate()할 때마다 계속 그려주는 메소드
    ///////////////////////////////////////////////////////
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        paint = new Paint();




        ///////////////////////////////////
        // canvas background
        /////////////////////////////////////
        if(getWidth()!=0 && getHeight()!=0)
        {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.tiles);
            Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, getWidth()-20, getHeight()-20, true);
            canvas.drawBitmap(scaledBmp,10,10,null);
        }

        if(bc1 == null || bc2 == null || bc3 == null)
        {
            return;
        }

        ///////////////////////////////////////
        //// (0,0)좌표 위치 지정
        ////////////////////////////////////////
        startingPoint_x = (getWidth()/Constants._SCALE_FACTOR);
        startingPoint_y = (getHeight()/Constants._SCALE_FACTOR);


        // 사각형일 경우
        if(MainActivity.roomType == Constants._ROOM_TYPE_CUSTOMIZE)
        {
            drawGeofnece_Rectangle(canvas);
        }
        else if( MainActivity.roomType ==  Constants._ROOM_TYPE_RECTANGLE_12_7 )
        {
            screenRatio = Room_Rectangle_12_7.screenRatio;
            drawGeofnece_Rectangle(canvas);
        }
        else if( MainActivity.roomType == Constants._ROOM_TYPE_RECTANGLE_6_6 )
        {
            screenRatio = Room_Rectangle_6_6.screenRatio;
            drawGeofnece_Rectangle(canvas);
        }
        // 삼각형
        else if(MainActivity.roomType == Constants._ROOM_TYPE_TRIANGLE_6_6)
        {
            screenRatio = Room_Triangle_6_6.screenRatio;
            drawGeofence_Triangle(canvas);
        }
        else if(MainActivity.roomType == Constants._ROOM_TYPE_TRIANGLE_3_3)
        {
            screenRatio = Room_Triangle_3_3.screenRatio;
            drawGeofence_Triangle(canvas);
        }

        //////////////////////////////////////////////////////
        // 사용자 위치 그리기
        /////////////////////////////////////////////////////
        if(userLocation != null)
        {
            paint.setColor(Color.BLACK);
            myDrawCircle(canvas, userLocation.getX(), userLocation.getY(), Constants._BC_RADIUS);
        }

        ///////////////////////////////////////////////////////////
        // 비콘 반경 그리기
        ////////////////////////////////////////////////////////////
        if(bc1 != null && bc2 != null && bc3 != null)
        {
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            myDrawCircle(canvas, bc1.getX(), bc1.getY(),bc1.getR()*screenRatio);
            myDrawCircle(canvas, bc2.getX(), bc2.getY(),bc2.getR()*screenRatio);
            myDrawCircle(canvas, bc3.getX(), bc3.getY(),bc3.getR()*screenRatio);
            myDrawCircle(canvas, bc4.getX(), bc4.getY(),bc4.getR()*screenRatio);

        }

        ///////////////////////////////////////////////////////
        // special zone 그리기
        //////////////////////////////////////////////////////////
        if(PaymentZone.actual_leftUp != null && PaymentZone.actual_rightDown != null)
        {
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            myDrawRectangle(canvas, PaymentZone.actual_leftUp.getX(),PaymentZone.actual_leftUp.getY()
                                  ,PaymentZone.actual_rightDown.getX(),PaymentZone.actual_rightDown.getY());
        }

    }


    //////////////////
    // 삼각형 그리기
    /////////////////////
    private void drawGeofence_Triangle(Canvas canvas) {
        ////////////////////////////////////////////////////////
        // 지오펜스 그리기
        // 설치된 비콘 갯수에 따라 비콘을 그려준다
        ////////////////////////////////////////////////////////
        //////////////////////
        // 3개 원 그리기
        //////////////////////
        paint.setColor(Color.BLUE);
        myDrawCircle(canvas, bc1.getX(), bc1.getY(), Constants._BC_RADIUS);

        paint.setColor(Color.RED);
        myDrawCircle(canvas, bc2.getX(), bc2.getY(), Constants._BC_RADIUS);

        paint.setColor(Color.MAGENTA);
        myDrawCircle(canvas, bc3.getX(), bc3.getY(), Constants._BC_RADIUS);


        ///////////////////////////////
        // 지오펜스 둘레 그리기
        ///////////////////////////////
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        myDrawLine(canvas, 0, 0
                , bc3.getX(), 0);
        myDrawLine(canvas, bc3.getX(), 0
                , bc3.getX(), bc3.getY());
        myDrawLine(canvas, bc3.getX(), bc3.getY()
                , bc2.getX(), bc2.getY());
        myDrawLine(canvas, bc2.getX(), bc2.getY()
                , 0, 0);

        ////////////////////////////////////////////////////////////////////////////////
        // view width, height 실제 값 텍스트 출력 -> 위치 보정하기                *************************************************************************
        ///////////////////////////////////////////////////////////////////////////////
        paint.setColor(Color.BLACK);
        paint.setTextSize(Constants._VIEW_CM_TEXT_SIZE);

        myDrawText(canvas, String.format("%.0fcm", avgTopDist), Constants.viewWidthX,Constants.viewWidthY);
        myDrawText(canvas, String.format("%.0fcm", avgRightDist), Constants.viewHeightX,Constants.viewHeightY);
    }

    /////////////////////////////////////
    // 사각형 그리기
    ///////////////////////////////////
    private void drawGeofnece_Rectangle(Canvas canvas) {
        ////////////////////////////////////////////////////////
        // 지오펜스 그리기
        // 설치된 비콘 갯수에 따라 비콘을 그려준다
        ////////////////////////////////////////////////////////
        if(locatedBeaconCnt == 0)
        {

        }
        else if(locatedBeaconCnt == 1)
        {
            paint.setColor(Color.BLUE);
            myDrawCircle(canvas, bc1.getX(), bc1.getY(), Constants._BC_RADIUS);
        }
        else if (locatedBeaconCnt == 2)
        {
            paint.setColor(Color.BLUE);
            myDrawCircle(canvas, bc1.getX(), bc1.getY(), Constants._BC_RADIUS);
            paint.setColor(Color.RED);
            myDrawCircle(canvas, bc2.getX(), bc2.getY(), Constants._BC_RADIUS);
            paint.setColor(Color.GREEN);
            myDrawLine(canvas, bc1.getX(), bc1.getY(), bc2.getX(), bc2.getY());
            paint.setColor(Color.BLACK);
            paint.setTextSize(Constants._VIEW_CM_TEXT_SIZE);

            // 텍스트 ---------------> 거리보정 ***********************************************************************************************************************
            myDrawText(canvas, String.format("%.2f", avgTopDist), Constants.viewWidthX,Constants.viewWidthY);

        }
        else if (locatedBeaconCnt == 3)
        {

            //////////////////////
            // 4개 원 그리기
            //////////////////////
            paint.setColor(Color.BLUE);
            myDrawCircle(canvas, bc1.getX(), bc1.getY(), Constants._BC_RADIUS);

            paint.setColor(Color.RED);
            myDrawCircle(canvas, bc2.getX(), bc2.getY(), Constants._BC_RADIUS);

            paint.setColor(Color.MAGENTA);
            myDrawCircle(canvas, bc3.getX(), bc3.getY(), Constants._BC_RADIUS);

            paint.setColor(Color.CYAN);
            myDrawCircle(canvas, bc4.getX(), bc4.getY(), Constants._BC_RADIUS);

            ///////////////////////////////
            // 지오펜스 둘레 그리기
            ///////////////////////////////
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(5);
            myDrawLine(canvas, bc1.getX(), bc1.getY()
                    , bc2.getX(), bc2.getY());
            myDrawLine(canvas, bc2.getX(), bc2.getY()
                    , bc3.getX(), bc3.getY());
            myDrawLine(canvas, bc3.getX(), bc3.getY()
                    , bc4.getX(), bc4.getY());
            myDrawLine(canvas, bc4.getX(), bc4.getY()
                    , bc1.getX(), bc1.getY());

            ////////////////////////////////////////////////////////////////////////////////
            // view width, height 실제 값 텍스트 출력 -> 위치 보정하기                *************************************************************************
            ///////////////////////////////////////////////////////////////////////////////
            paint.setColor(Color.BLACK);
            paint.setTextSize(Constants._VIEW_CM_TEXT_SIZE);
            myDrawText(canvas, String.format("%.0fcm", avgTopDist), Constants.viewWidthX,Constants.viewWidthY);
            myDrawText(canvas, String.format("%.0fcm", avgRightDist), Constants.viewHeightX,Constants.viewHeightY);
        }
    }

    ////////////////////////////////////////////////
    // 지오펜스 모두 지우기 -> 변수 초기화
    ////////////////////////////////////////////////
    public void resetGeofenceView()
    {
        this.locatedBeaconCnt=0;
        this.bc1=null;
        this.bc2=null;
        this.bc3=null;
        this.bc4=null;
        this.userLocation=null;
        this.startingPoint_x=0;
        this.startingPoint_y=0;
        GeofenceView_main.bc1=null;
        GeofenceView_main.bc2=null;
        GeofenceView_main.bc3=null;
        GeofenceView_main.bc4=null;
        PaymentZone.actual_leftUp = new Point3D();
        PaymentZone.actual_rightDown = new Point3D();
        PaymentZone.screen_leftUp = new Point3D();
        PaymentZone.screen_rightDown = new Point3D();
        this.invalidate();
    }

    /////////////////////////////////////////////////////////////
    // myDraw는 startingPoint, screenRatio를 고려해서 그려준다
    ////////////////////////////////////////////////////////////
    public void myDrawText(Canvas canvas, String text, float x1, float y1)
    {
        canvas.drawText(text,startingPoint_x+x1, startingPoint_y+y1,paint);
    }
    public void myDrawLine(Canvas canvas, float x1, float y1, float x2, float y2)
    {
        x1 = x1 * screenRatio;
        y1 = y1 * screenRatio;
        x2 = x2 * screenRatio;
        y2 = y2 * screenRatio;
        canvas.drawLine(startingPoint_x+x1,startingPoint_y+y1,startingPoint_x+x2,startingPoint_y+y2,paint);
    }
    public void myDrawCircle(Canvas canvas, float x, float y, float radius)
    {
        // 실제 크기에 비율을 곱해서 확대해준다
        x = x * screenRatio;
        y = y * screenRatio;
        canvas.drawCircle( startingPoint_x+x , startingPoint_y+y , radius,  paint);
    }
    public GeofenceView_main(Context context, AttributeSet attrs, int defStyle) {super(context, attrs, defStyle);}
    public GeofenceView_main(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    public GeofenceView_main(Context context) {
        super(context);
    }

    public void myDrawRectangle(Canvas canvas, float x1, float y1, float x2, float y2)
    {
        x1 = x1*screenRatio;
        y1 = y1*screenRatio;
        x2 = x2*screenRatio;
        y2 = y2*screenRatio;
        canvas.drawRect(startingPoint_x+x1,startingPoint_y+y1,startingPoint_x+x2,startingPoint_y+y2,paint);
    }
}