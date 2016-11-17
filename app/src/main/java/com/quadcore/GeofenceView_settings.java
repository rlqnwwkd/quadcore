package com.quadcore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.quadcore.Room_Activity.GeofenceSettingsActivity_Customize;
import com.quadcore.Utils.Constants;
import com.quadcore.Utils.PaymentZone;
import com.quadcore.Utils.Point3D;

// main 뷰와는 다르게 표현할 수 있도록 따로 둠
public class GeofenceView_settings extends View {

    // 실제 위, 오른쪽 변 길이 평균 값
    public static double avgTopDist, avgRightDist= 0;

    public Paint paint;
    public static int locatedBeaconCnt = 0;

    // 비콘 4개의 위치
    public static Point3D bc1, bc2, bc3,bc4;
    public static Point3D userLocation;

    // (0,0) 좌표 지정
    public static float startingPoint_x;
    public static float startingPoint_y;


    ///////////////////////////////////////////////////////////////////////
    // 실제거리 , view에서 표현된 거리 비율
    // 실제 크기는 bc1~bc4, userLocation에 저장되어 있고,
    // 화면에서는 실제 크기에 lengthRatio를 곱해서 확대해서 보여준다
    /////////////////////////////////////////////////////////////////
    public static float screenRatio;

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

        ///////////////////////////////////////
        //// (0,0)좌표 위치 지정
        ////////////////////////////////////////
        startingPoint_x = (getWidth()/ Constants._SCALE_FACTOR);
        startingPoint_y = (getHeight()/Constants._SCALE_FACTOR);


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

            // 텍스트 출력 *************************************************************************************************************** 위치 보정
            paint.setColor(Color.BLACK);
            paint.setTextSize(Constants._VIEW_CM_TEXT_SIZE);
            myDrawText(canvas, String.format("%.2f", GeofenceView_settings.avgTopDist), Constants.viewWidthX,Constants.viewWidthY);
        }
        else if (locatedBeaconCnt == 3)
        {

            paint.setColor(Color.BLUE);
            myDrawCircle(canvas, bc1.getX(), bc1.getY(), Constants._BC_RADIUS);

            paint.setColor(Color.RED);
            myDrawCircle(canvas, bc2.getX(), bc2.getY(), Constants._BC_RADIUS);

            paint.setColor(Color.MAGENTA);
            myDrawCircle(canvas, bc3.getX(), bc3.getY(), Constants._BC_RADIUS);

            paint.setColor(Color.CYAN);
            myDrawCircle(canvas, bc4.getX(), bc4.getY(), Constants._BC_RADIUS);

            paint.setColor(Color.GREEN);
            myDrawLine(canvas, bc1.getX(), bc1.getY()
                             , bc2.getX(), bc2.getY());
            myDrawLine(canvas, bc2.getX(), bc2.getY()
                             , bc3.getX(), bc3.getY());
            myDrawLine(canvas, bc3.getX(), bc3.getY()
                             , bc4.getX(), bc4.getY());
            myDrawLine(canvas, bc4.getX(), bc4.getY()
                             , bc1.getX(), bc1.getY());


            // 텍스트 출력 *************************************************************************************************************** 위치 보정
            paint.setColor(Color.BLACK);
            paint.setTextSize(Constants._VIEW_CM_TEXT_SIZE);
            myDrawText(canvas, String.format("%.2f", GeofenceView_settings.avgTopDist), Constants.viewWidthX,Constants.viewWidthY);
            myDrawText(canvas, String.format("%.2f", GeofenceView_settings.avgRightDist), Constants.viewHeightX,Constants.viewHeightY);

        }

        //////////////////////////////////////////////////////
        // 사용자 위치 그리기
        /////////////////////////////////////////////////////
        if(userLocation != null)
        {
            paint.setColor(Color.RED);
            myDrawCircle(canvas, userLocation.getX(), userLocation.getY(), Constants._BC_RADIUS);
        }

        ///////////////////////////////////////////////////////////
        // 비콘 반경 그리기
        ////////////////////////////////////////////////////////////
        if(bc1 != null && bc2 != null & bc3 != null)
        {
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            myDrawCircle(canvas, bc1.getX(), bc1.getY(),bc1.getR()*screenRatio);
            myDrawCircle(canvas, bc2.getX(), bc2.getY(),bc2.getR()*screenRatio);
            myDrawCircle(canvas, bc3.getX(), bc3.getY(),bc3.getR()*screenRatio);
        }

        ///////////////////////////////////////////////
        // Speical Zone 그리기 ( 결제존 )
        ///////////////////////////////////////////////

        if(PaymentZone.actual_leftUp!=null && PaymentZone.actual_rightDown != null)
        {
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            myDrawRectangle(canvas, PaymentZone.actual_leftUp.getX(), PaymentZone.actual_leftUp.getY(),
                    PaymentZone.actual_rightDown.getX(), PaymentZone.actual_rightDown.getY());
        }


    }

    ////////////////////////////////////////////
    // 드래그 앤 드랍 -> 결제존 형성
    /////////////////////////////////////////
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int actionevent = event.getAction();
        int X = (int)event.getX();
        int Y= (int)event.getY();

        switch(actionevent)
        {
            case MotionEvent.ACTION_DOWN:
                PaymentZone.screen_leftUp.setX(X);
                PaymentZone.screen_leftUp.setY(Y);
                break;
            case MotionEvent.ACTION_MOVE:
                PaymentZone.screen_rightDown.setX(X);
                PaymentZone.screen_rightDown.setY(Y);
                break;
            case MotionEvent.ACTION_UP:
                PaymentZone.screen_rightDown.setX(X);
                PaymentZone.screen_rightDown.setY(Y);
                break;
        }

        PaymentZone.calculateActualPosition();
        invalidate();
        return true;
    }

    ////////////////////////////////////////////////
    // 지오펜스 모두 지우기 -> 변수 초기화
    ////////////////////////////////////////////////
    public void resetGeofenceView()
    {
        GeofenceView_settings.locatedBeaconCnt=0;
        GeofenceView_settings.bc1=null;
        GeofenceView_settings.bc2=null;
        GeofenceView_settings.bc3=null;
        GeofenceView_settings.bc4=null;
        GeofenceView_settings.userLocation=null;
        GeofenceView_settings.startingPoint_x=0;
        GeofenceView_settings.startingPoint_y=0;
        GeofenceSettingsActivity_Customize.bc1Position=null;
        GeofenceSettingsActivity_Customize.bc2Position=null;
        GeofenceSettingsActivity_Customize.bc3Position=null;
        GeofenceSettingsActivity_Customize.bc4Position=null;
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
        x1 = x1*screenRatio;
        y1 = y1*screenRatio;
        x2 = x2*screenRatio;
        y2 = y2*screenRatio;
        canvas.drawLine(startingPoint_x+x1,startingPoint_y+y1,startingPoint_x+x2,startingPoint_y+y2,paint);
    }
    public void myDrawCircle(Canvas canvas, float x, float y, float radius)
    {
        x=x*screenRatio;
        y=y*screenRatio;
        canvas.drawCircle( startingPoint_x+x , startingPoint_y+y , radius,  paint);
    }
    public void myDrawRectangle(Canvas canvas, float x1, float y1, float x2, float y2)
    {
        x1 = x1*screenRatio;
        y1 = y1*screenRatio;
        x2 = x2*screenRatio;
        y2 = y2*screenRatio;
        canvas.drawRect(startingPoint_x+x1,startingPoint_y+y1,startingPoint_x+x2,startingPoint_y+y2,paint);
    }

    public GeofenceView_settings(Context context, AttributeSet attrs, int defStyle) {super(context, attrs, defStyle);}
    public GeofenceView_settings(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    public GeofenceView_settings(Context context) {
        super(context);
    }

}