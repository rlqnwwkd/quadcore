package com.quadcore;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.repackaged.retrofit_v1_9_0.retrofit.http.PartMap;
import com.kakao.usermgmt.response.model.UserProfile;
import com.quadcore.Mock.MockServer;
import com.quadcore.Room.Room_Rectangle_12_7;
import com.quadcore.Room.Room_Rectangle_6_6;
import com.quadcore.Room.Room_Triangle_3_3;
import com.quadcore.Room.Room_Triangle_6_6;
import com.quadcore.Services.BackgroundBeaconMonitoringService;
import com.quadcore.Services.BackgroundBeaconRangingService;
import com.quadcore.Services.UserPositionUpdateService;
import com.quadcore.Utils.Constants;
import com.quadcore.Utils.PaymentZone;
import com.quadcore.Utils.Point3D;
import com.quadcore.Utils.ServerInfo;


public class MainActivity extends AppCompatActivity {

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
    public static TextView rssiAndMeter1,rssiAndMeter2,rssiAndMeter3, rssiAndMeter4;



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
        Log.d(Constants.QUADCORE_LOG,"MainActivity : onCreate()");
        startActivity(new Intent(this, LoadingActivity.class));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainContext = this.getApplicationContext();

        // 관리자 버튼
        geofenceSettingsBtn = (Button)findViewById(R.id.GeofenceSettingsBtn);
        rssiCheckBtn = (Button)findViewById(R.id.rssiCheckBtn);
        mapOrFormulaToggleButton = (ToggleButton)findViewById(R.id.mapOrFormulaToggleButton);
        // 평균 RSSI 값
        rssiAndMeter1 = (TextView)findViewById(R.id.rssiAndMeter1);
        rssiAndMeter2 = (TextView)findViewById(R.id.rssiAndMeter2);
        rssiAndMeter3 = (TextView)findViewById(R.id.rssiAndMeter3);
        rssiAndMeter4 = (TextView)findViewById(R.id.rssiAndMeter4);

        // on off btn
        locationThreadToggleBtn = (Button)findViewById(R.id.locationThreadToggleBtn);

        /////////////////////////////
        // 유저정보 가져오기
        //////////////////////////
        //Toast.makeText(getApplicationContext(),""+userProfile.getNickname(),Toast.LENGTH_SHORT).show();


        /////////////////////////////////////////////////////////////////
        // 블루투스 허용 메시지 팝업
        ///////////////////////////////////////////////////////////////
        SystemRequirementsChecker.checkWithDefaultDialogs(this);


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


        ////////////////////////////////////////////////////////////
        // BackgroundBeaconMonitoringService 시작
        // 서비스는 비콘을 모니터링한다, 발견하면 더욱 빠른 주기로 스캔하는 Ranging을 실행한다
        //////////////////////////////////////////////////////////////
       // if(backgroundBeaconMonitoringServiceIntent==null)
       // {
            Log.d(Constants.QUADCORE_LOG,"----------------  Beacon Monitoring Service create!");
            backgroundBeaconMonitoringServiceIntent = new Intent(this, BackgroundBeaconMonitoringService.class);
            startService(backgroundBeaconMonitoringServiceIntent);
     //   }


        ////////////////////////////////////////////////////////////
        // BackgroundBeaconRangingService 생성, 시작은 Monitoring되면 시작한다
        // 서비스는 게속 비콘들을 스캔하고, 최근 신호값 n개를 유지한다
        //////////////////////////////////////////////////////////////
      //  if(backgroundBeaconRangingServiceIntent==null)
       // {
            Log.d(Constants.QUADCORE_LOG,"------------------  Beacon Ranging Service create!");
            backgroundBeaconRangingServiceIntent = new Intent(this, BackgroundBeaconRangingService.class);

        //}


        ///////////////////////////////////////////////////
        // 사용자일 경우 관리하는 것들은 없앤다
        //////////////////////////////////////////////////
        // 사용자 로직 적용하기
        /*
        if(userProfile.getNickname().equals("admin") == false)
        {
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

        ((GeofenceView_main)view).bc1=bc1;
        ((GeofenceView_main)view).bc2=bc2;
        ((GeofenceView_main)view).bc3=bc3;
        ((GeofenceView_main)view).bc4=bc4;
        ((GeofenceView_main)view).locatedBeaconCnt=3;
        ((GeofenceView_main)view).invalidate();
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
    public static void popupThePayment()
    {
        final Intent intent = new Intent(mainContext, PaymentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainContext.startActivity(intent);
    }
}
