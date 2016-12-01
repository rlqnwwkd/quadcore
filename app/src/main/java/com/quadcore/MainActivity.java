package com.quadcore;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.repackaged.retrofit_v1_9_0.retrofit.http.PartMap;
import com.kakao.usermgmt.response.model.UserProfile;
import com.quadcore.Mock.MockServer;
import com.quadcore.Room.Room_Paldal1;
import com.quadcore.Room.Room_Rectangle_12_7;
import com.quadcore.Room.Room_Rectangle_6_6;
import com.quadcore.Room.Room_Triangle_3_3;
import com.quadcore.Room.Room_Triangle_6_6;
import com.quadcore.Room_Activity.GeofenceSettingsActivity_Rectangle_6_6;
import com.quadcore.Services.BackgroundBeaconMonitoringService;
import com.quadcore.Services.BackgroundBeaconRangingService;
import com.quadcore.Services.UserPositionUpdateService;
import com.quadcore.Utils.Constants;
import com.quadcore.Utils.PaymentZone;
import com.quadcore.Utils.Point3D;
import com.quadcore.Utils.QuadCoreMapper;
import com.quadcore.Utils.ServerInfo;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static Animation animation1, animation2;

    // animation
    public static ImageView userImg;

    private static Context mainContext;
    // 룸 타입
    public static int roomType;
    // 로그인 유저 세션
    public static UserProfile userProfile;
    // 신호 -> 거리 맵핑 또는 공식 사용여부 플래그
    public static boolean isUsingFormula = false;
    // isGeofenceInstalled = false -> 검색된 지오펜스 없음, 계속 검색
    // isGeofenceInstalled = true -> 검색 중지
    public static boolean isGeofenceInstalled = false;
    // 지오펜스 이름 ( counter )
    public static String geofenceName;
    // 지오펜스 아이디
    public static long geofenceId;
    // 중간 유저위치 온오프 버튼
    public static Button locationThreadToggleBtn;
    // 지오펜스 그려주는 캔버스 뷰
    public static View view;
    public Canvas canvas;
    // 비콘 백그라운드 모니터링 서비스
    public static Intent backgroundBeaconMonitoringServiceIntent;
    // 비콘 백그라운드 레인징 센싱 서비스
    public static Intent backgroundBeaconRangingServiceIntent;
    // 사용자 위치 업데이트 서비스
    public static Intent userPositionUpdateServiceIntent;

    ///////////////////////
    // widgets for admin
    ////////////////////////
    public static Button geofenceSettingsBtn, rssiCheckBtn, mapOrFormulaToggleButton;
    public static TextView rssiAndMeter1,rssiAndMeter2,rssiAndMeter3;
            //rssiAndMeter4;



    /////////////////////////////////////////////
    // 리줌
    /////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Constants.QUADCORE_LOG, "MainActivity : OnResume()");

        //////////////////////////////////////
        // 테스트 일 경우만 , 지오펜스 뷰 가져오기 위함
        //////////////////////////////////////
        if(ServerInfo.isConnected == false
        && MockServer.geofence != null)
        {
            String server_response = MockServer.selectGeofence(Constants.MAJOR_BEACON_1);
            MainActivity.updateGeofenceView(server_response);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(Constants.QUADCORE_LOG, "MainActivity : onCreate()");
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, LoadingActivity.class));


        /////////////////////////////////////////////////////////////////
        // 블루투스 허용 메시지 팝업 - 카톡 로그인으로 넣기
        ///////////////////////////////////////////////////////////////
        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        setContentView(R.layout.activity_main);
        mainContext = this.getApplicationContext();

        // animation
        userImg = (ImageView)findViewById(R.id.userImage);
        userImg.bringToFront();
        userImg.setVisibility(View.GONE);

        // 관리자 버튼
        geofenceSettingsBtn = (Button) findViewById(R.id.GeofenceSettingsBtn);
        rssiCheckBtn = (Button) findViewById(R.id.rssiCheckBtn);
        //mapOrFormulaToggleButton = (ToggleButton) findViewById(R.id.mapOrFormulaToggleButton);
        // 평균 RSSI 값
        rssiAndMeter1 = (TextView) findViewById(R.id.rssiAndMeter1);
        rssiAndMeter2 = (TextView) findViewById(R.id.rssiAndMeter2);
        rssiAndMeter3 = (TextView) findViewById(R.id.rssiAndMeter3);
        //rssiAndMeter4 = (TextView) findViewById(R.id.rssiAndMeter4);

        // on off btn
        locationThreadToggleBtn = (Button) findViewById(R.id.locationThreadToggleBtn);

        /////////////////////////////
        // 유저정보 가져오기
        //////////////////////////
        //Toast.makeText(getApplicationContext(),""+userProfile.getNickname(),Toast.LENGTH_SHORT).show();




        ///////////////////////////////////////////////////
        // 사용자 위치 보여줄 canvas view 추가
        ///////////////////////////////////////////////////
        view = (View)findViewById(R.id.geofenceView_main);
        canvas = new Canvas();
        view.draw(canvas);





        ///////////////////////////////////////////////////////
        // 사용자 위치 추적 서비스 생성 -> 스레드는 아직 실행 안함
        // startUserPositionThread()로 스레드를 동작시켜야 업데이트됨
        ////////////////////////////////////////////////////////
        userPositionUpdateServiceIntent = new Intent(this, UserPositionUpdateService.class);
        startService(userPositionUpdateServiceIntent);


        Log.d(Constants.QUADCORE_LOG,"------------------  Beacon Ranging Service create!");
        backgroundBeaconRangingServiceIntent = new Intent(this, BackgroundBeaconRangingService.class);


        Log.d(Constants.QUADCORE_LOG,"----------------  Beacon Monitoring Service create!");
        backgroundBeaconMonitoringServiceIntent = new Intent(this, BackgroundBeaconMonitoringService.class);
        startService(backgroundBeaconMonitoringServiceIntent);


        ///////////////////////////////////////////////////
        // 사용자일 경우 관리하는 것들은 없앤다
        //////////////////////////////////////////////////
        // 사용자 로직 적용하기
        /*
        if(userProfile.getNickname().equals("admin") == false) {
            geofenceSettingsBtn.setVisibility(View.GONE);
            rssiCheckBtn.setVisibility(View.GONE);
            mapOrFormulaToggleButton.setVisibility(View.GONE);
            rssiAndMeter1.setVisibility(View.GONE);
            rssiAndMeter2.setVisibility(View.GONE);
            rssiAndMeter3.setVisibility(View.GONE);
            rssiAndMeter4.setVisibility(View.GONE);
        }
        */
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(Constants.QUADCORE_LOG, "MainActivity : onDestroy() : service stop");
        stopService(backgroundBeaconMonitoringServiceIntent);
        stopService(backgroundBeaconRangingServiceIntent);
    }

    ///////////////////////////////////////////////
    // Geofence Settings 버튼 클릭시 화면 이동
    ///////////////////////////////////////////////
    public void onGeofenceSettingsBtnClicked(View view)
    {
        final Intent intent = new Intent(this, GeofenceSettingsActivity_choice.class);
        startActivity(intent);
    }

    // 장바구니
    public void onCartBtnClicked(View mView)
    {
        final Intent intent = new Intent(this, CartCheckActivity.class);
        startActivity(intent);
    }

    ////////////////////////////////////////////////////
    // RSSI -> Cmter 변환 방식 선택(매핑 / 공식)토글버튼
    ////////////////////////////////////////////////////
    public void onMapOrFormulaToggleButtonClicked(View mView)
    {

        // 공식 사용
        if ( ((ToggleButton)mView).isChecked() )
        {
            isUsingFormula = true;
        }
        // 매핑 사용
        else
        {
            isUsingFormula = false;
        }
    }


    ////////////////////////////////////////////////////
    // 사용자 위치 추적 토글버튼 ON/OFF
    ////////////////////////////////////////////////////
    public void onLocationThreadToggleBtnClicked(View mView)
    {
        if ( ((ToggleButton)mView).isChecked() )
        {
            // 사용자 위치 추적 시작
            UserPositionUpdateService.isUser=true;
            UserPositionUpdateService.startUserPositionThread();
        }
        else
        {
            // 사용자 위치 추적 중지
            UserPositionUpdateService.isUser=false;
            UserPositionUpdateService.stopUserPositionThread();
            GeofenceView_main.userLocation=null;
            if(GeofenceView_main.bc1 != null)
            {
                GeofenceView_main.bc1.setR(0);
                GeofenceView_main.bc2.setR(0);
                GeofenceView_main.bc3.setR(0);

                // 사각형이면
                if( MainActivity.roomType == Constants._ROOM_TYPE_RECTANGLE_6_6
                        || MainActivity.roomType == Constants._ROOM_TYPE_RECTANGLE_12_7
                        || MainActivity.roomType == Constants._ROOM_TYPE_CUSTOMIZE )
                {
                    GeofenceView_main.bc4.setR(0);
                }
            }
            ((GeofenceView_main)view).invalidate();
        }
    }


    //////////////////////////////////////////////////////////////////////
    // 서버에서 받아온 Geofence 정보를 이용해서(비콘 4개의 위치)
    // Geofence view 업데이트
    //////////////////////////////////////////////////////////////////////
    public static void updateGeofenceView(String server_response)
    {
        /////////////////////////////////
        // Geofence 뷰 수정하기
        /////////////////////////////////
        Point3D bc1 = new Point3D();
        Point3D bc2 = new Point3D();
        Point3D bc3 = new Point3D();
        Point3D bc4 = new Point3D();
        String[] server_response_split = server_response.split(",");
        bc1.setX(Float.parseFloat(server_response_split[0]));
        bc1.setY(Float.parseFloat(server_response_split[1]));
        bc2.setX(Float.parseFloat(server_response_split[2]));
        bc2.setY(Float.parseFloat(server_response_split[3]));
        bc3.setX(Float.parseFloat(server_response_split[4]));
        bc3.setY(Float.parseFloat(server_response_split[5]));
        bc4.setX(Float.parseFloat(server_response_split[6]));
        bc4.setY(Float.parseFloat(server_response_split[7]));
        geofenceName = server_response_split[8];
        geofenceId = Long.parseLong(server_response_split[9]);

        // special zone
        PaymentZone.actual_leftUp = new Point3D();
        PaymentZone.actual_rightDown = new Point3D();
        PaymentZone.actual_leftUp.setX(Float.parseFloat(server_response_split[10]));
        PaymentZone.actual_leftUp.setY(Float.parseFloat(server_response_split[11]));
        PaymentZone.actual_rightDown.setX(Float.parseFloat(server_response_split[12]));
        PaymentZone.actual_rightDown.setY(Float.parseFloat(server_response_split[13]));

        ////////////////////////////////////////////////////
        // 결제존에 진입했을 경우, 결제존 체크가 잘 안됨, 실제위치, 스크린위치 개념 햇갈림
        //////////////////////////////////////////////////////
        PaymentZone.calculateScreenPosition();
        roomType = Integer.parseInt(server_response_split[14]);


        // 룸 타입에 따라 화면 크기 조정
        if  ( roomType == Constants._ROOM_TYPE_CUSTOMIZE)
        {
            GeofenceView_main.avgTopDist = bc2.getX()-bc1.getX();
            GeofenceView_main.avgRightDist = bc3.getY() - bc2.getY();
            GeofenceView_main.screenRatio=(float)(Constants.viewWidth/GeofenceView_main.avgTopDist);
        }
        else if( roomType == Constants._ROOM_TYPE_RECTANGLE_6_6 )
        {
            GeofenceView_main.avgTopDist = Room_Rectangle_6_6.xLength;
            GeofenceView_main.avgRightDist = Room_Rectangle_6_6.yLength;
            GeofenceView_main.screenRatio=Room_Rectangle_6_6.screenRatio;
        }
        else if( roomType == Constants._ROOM_TYPE_RECTANGLE_12_7)
        {
            GeofenceView_main.avgTopDist = Room_Rectangle_12_7.xLength;
            GeofenceView_main.avgRightDist = Room_Rectangle_12_7.yLength;
            GeofenceView_main.screenRatio=Room_Rectangle_12_7.screenRatio;
        }
        else if ( roomType == Constants._ROOM_TYPE_TRIANGLE_6_6)
        {
            GeofenceView_main.avgTopDist = Room_Triangle_6_6.xLength;
            GeofenceView_main.avgRightDist = Room_Triangle_6_6.yLength;
            GeofenceView_main.screenRatio = Room_Triangle_6_6.screenRatio;
        }
        else if(roomType== Constants._ROOM_TYPE_TRIANGLE_3_3)
        {
            GeofenceView_main.avgTopDist = Room_Triangle_3_3.xLength;
            GeofenceView_main.avgRightDist = Room_Triangle_3_3.yLength;
            GeofenceView_main.screenRatio=Room_Triangle_3_3.screenRatio;
        }
        else if(roomType== Constants._ROOM_TYPE_Paldal1)
        {
            GeofenceView_main.avgTopDist = Room_Paldal1.xLength;
            GeofenceView_main.avgRightDist = Room_Paldal1.yLength;
            GeofenceView_main.screenRatio=Room_Paldal1.screenRatio;
        }

        if((GeofenceView_main)view != null)
        {
            ((GeofenceView_main)view).bc1=bc1;
            ((GeofenceView_main)view).bc2=bc2;
            ((GeofenceView_main)view).bc3=bc3;
            ((GeofenceView_main)view).bc4=bc4;
            ((GeofenceView_main)view).locatedBeaconCnt=3;
            ((GeofenceView_main)view).invalidate();
            //UserPositionUpdateService.lastUserPosition=null;
        }

        MainActivity.isGeofenceInstalled = true;

    }

    ////////////////////////////////////////////////////
    // RSSI 체크 버튼 클릭
    ////////////////////////////////////////////////////
    public void onRssiCheckBtnClicked(View mView)
    {
        final Intent intent = new Intent(this, RssiCheckActivity.class);
        startActivity(intent);
    }


    ////////////////////////////////////////
    // 페이먼트 실행
    //////////////////////////////////////////
    public static void popupThePayment(List<Integer> nearStickers)
    {
        if(nearStickers.size()==0)
        {
            Log.d(Constants.QUADCORE_LOG,"PAY START : NO NEAR PRODUCT : PAY SKIP");
        }
        else
        {
            final Intent intent = new Intent(mainContext, PaymentActivity.class);
            int[] idArr = new int[nearStickers.size()];
            for(int i=0;i<nearStickers.size();i++)
            {
                idArr[i] = nearStickers.get(i);
            }

            intent.putExtra("stickerId",idArr);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainContext.startActivity(intent);
        }
    }

    public static void changeUserPosition(int last, int current)
    {
        Point3D lastP = Room_Paldal1.getPosition(last);
        Point3D currentP = Room_Paldal1.getPosition(current);

        Log.d(Constants.QUADCORE_LOG,"Animation : "+last+"->"+current);

        if( last == 0)
        {

            if( current == 5 || current == 7)
            {
                Point3D middleP = Room_Paldal1.getPosition(6);
                startAnimationThread_2(lastP, middleP, currentP);
            }
            else
            {
                startAnimationThread(lastP, currentP);
            }
        }

        else if( last == 1)
        {
            if( current == 3 || current == 6)
            {
                startAnimationThread(lastP, currentP);
            }
            else if( current == 5 || current == 7)
            {
                Point3D middleP = Room_Paldal1.pos6_up;
                startAnimationThread_2(lastP, middleP, currentP);
            }
        }

        else if( last == 3 )
        {
            startAnimationThread(lastP, currentP);
        }

        else if( last == 5)
        {
            if(current == 8 ||  current == 7)
            {
                Point3D middleP = Room_Paldal1.getPosition(6);
                startAnimationThread_2(lastP, middleP, currentP);
            }
            else if(current == 1)
            {
                Point3D middleP = Room_Paldal1.pos6_up;
                startAnimationThread_2(lastP, middleP, currentP);
            }
            else
            {
                startAnimationThread(lastP, currentP);
            }
        }

        else if( last == 7)
        {
            if(current == 8 ||  current == 5)
            {
                Point3D middleP = Room_Paldal1.getPosition(6);
                startAnimationThread_2(lastP, middleP, currentP);
            }
            else if(current == 1)
            {
                Point3D middleP = Room_Paldal1.pos6_up;
                startAnimationThread_2(lastP, middleP, currentP);
            }
            else
            {
                startAnimationThread(lastP, currentP);
            }
        }

        else if( last == 6 )
        {
            startAnimationThread(lastP, currentP);
        }

        else if( last == 8)
        {
            if(current == 5 ||  current == 7)
            {
                Point3D middleP = Room_Paldal1.getPosition(6);
                startAnimationThread_2(lastP, middleP, currentP);
            }
            else
            {
                startAnimationThread(lastP, currentP);
            }
        }
        else
        {
            startAnimationThread(lastP, currentP);
        }
    }


    private static void startAnimationThread_2(final Point3D p1, final Point3D p2, final Point3D p3 ) {
        Handler mHandler = new Handler(Looper.getMainLooper());

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                AnimationSet set = new AnimationSet(true); // 여러 애니메이션을 관리한다
                set.setFillEnabled(true);
                set.setFillAfter(true); // 이미지가 애니메이션이 종료된 위치에서 머무른다.


                // lastP->currentP로 움직이는 애니메이션 Animation.ABSOLUTE,
                //Animation animation1 = new TranslateAnimation(lastP.getX(),currentP.getX(),lastP.getY(),currentP.getY());
                animation1 = new TranslateAnimation(Animation.ABSOLUTE, p1.getX()   , Animation.ABSOLUTE, p2.getX()
                                                             ,Animation.ABSOLUTE, p1.getY()    , Animation.ABSOLUTE,p2.getY());


                animation1.setDuration(1000); // 2초 동안 움직인다
                animation1.setStartOffset(0);
                animation1.setFillEnabled(true);
                animation1.setFillAfter(true);
                animation1.setInterpolator(AnimationUtils.loadInterpolator(MainActivity.mainContext, android.R.anim.decelerate_interpolator));

                animation1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        userImg.startAnimation(animation2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                // 애니메이션 추가
                set.addAnimation(animation1);


               // Animation animation2 = new TranslateAnimation(currentP.getX(),lastP.getX(),currentP.getY(),lastP.getY());
                animation2 = new TranslateAnimation(Animation.ABSOLUTE, p2.getX()   , Animation.ABSOLUTE, p3.getX()
                                                             ,Animation.ABSOLUTE, p2.getY()    , Animation.ABSOLUTE,p3.getY());
                animation2.setDuration(1000); // 2초 동안 움직인다
                animation2.setStartOffset(0); // 2초 뒤에 실행
                animation2.setFillEnabled(true);
                animation2.setFillAfter(true);
                animation2.setInterpolator(AnimationUtils.loadInterpolator(MainActivity.mainContext, android.R.anim.decelerate_interpolator));

                // 애니메이션 추가
                set.addAnimation(animation2);


                // set에 들어있는 애니매이션 시작
                userImg.startAnimation(animation1);
                userImg.setVisibility(View.VISIBLE);
            }
        }, 0);
    }

    private static void startAnimationThread(final Point3D lastP, final Point3D currentP) {
        Handler mHandler = new Handler(Looper.getMainLooper());

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                animation1 = new TranslateAnimation(Animation.ABSOLUTE, lastP.getX()   , Animation.ABSOLUTE, currentP.getX()
                        ,Animation.ABSOLUTE, lastP.getY()    , Animation.ABSOLUTE,currentP.getY());

                animation1.setDuration(2000); // 2초 동안 움직인다
                animation1.setStartOffset(0);
                animation1.setFillEnabled(true);
                animation1.setFillAfter(true);
                animation1.setInterpolator(AnimationUtils.loadInterpolator(MainActivity.mainContext, android.R.anim.decelerate_interpolator));

                // set에 들어있는 애니매이션 시작
                userImg.startAnimation(animation1);
                userImg.setVisibility(View.VISIBLE);
            }
        }, 0);
    }

}
