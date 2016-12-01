package com.quadcore.Room_Activity;

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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.quadcore.GeofenceView_main;
import com.quadcore.GeofenceView_settings;
import com.quadcore.MainActivity;
import com.quadcore.Mock.MockServer;
import com.quadcore.R;
import com.quadcore.Services.BackgroundBeaconMonitoringService;
import com.quadcore.Utils.*;
import com.quadcore.Services.BackgroundBeaconRangingService;
import com.quadcore.Services.RssiToDist;
import com.quadcore.Services.UserPositionUpdateService;
import com.quadcore.Utils.MyMath;
import com.quadcore.Utils.Point3D;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.TimeoutException;


///////////////////////////////////////////////
// Geofence settings 액티비티
/////////////////////////////////////////////////
public class GeofenceSettingsActivity_Customize extends AppCompatActivity {
    public static boolean isConnected = false;

    public static Thread beaconPositionTransferThread;
    public static View view;
    public static Point3D bc1Position, bc2Position, bc3Position,bc4Position;
    public Canvas canvas;
    // public static Point3D zoneStartPoint, zoneEndPoint;

    //////////////////////////////////////
    // Service Binder ( 메시지 교환)
    //////////////////////////////////////
    private Messenger bcService;
    private boolean bcBound = false;

    private ServiceConnection bcConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bcService = new Messenger(iBinder);
            bcBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bcService = null;
            bcBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.QUADCORE_LOG,"GeofenceSettingActivity : onCreate()");

        setContentView(R.layout.activity_geofence_settings);
        view = (View)findViewById(R.id.geofenceView);
        canvas = new Canvas();
        view.draw(canvas);

        // 서비스 바인드
        Intent serviceIntent = new Intent(this, BackgroundBeaconMonitoringService.class);
        bindService(serviceIntent, bcConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onResume() {
        /////////////////////////////////////////
        // 백그라운드 스레드 다시시작,
        /////////////////////////////////////////
        super.onResume();
        Log.d(Constants.QUADCORE_LOG,"GeofenceSetting : onResume()");

        ////////////////////////////////////
        // 셋팅 하던거 다 지움
        ////////////////////////////////////
        ((GeofenceView_settings)view).resetGeofenceView();
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(Constants.QUADCORE_LOG, "GeofenceSetting : onStop()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Constants.QUADCORE_LOG, "GeofenceSetting : onPause()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Constants.QUADCORE_LOG, "GeofenceSetting: onDestroy()");

        /////////////////////////////////////////
        // MainActivty 지오펜스 서버로부터 다시 얻어옴
        /////////////////////////////////////////
        MainActivity.isGeofenceInstalled=false;
    }


    ///////////////////////////////////////////////////
    // 첫번째 비콘 설치했을 때 -> (0,0)으로 위치 고정
    //////////////////////////////////////////////////
    public void onBeacon1SettingClicked(View mView)
    {
        bc1Position = new Point3D(0,0);
        ((GeofenceView_settings)view).bc1=bc1Position;
        ((GeofenceView_settings)view).locatedBeaconCnt = 1;
        ((GeofenceView_settings)view).invalidate();
    }

    ///////////////////////////////////////////////////////////
    // 두번째 비콘 설치 -> 첫번째 비콘까지의 거리를 받아서 셋팅
    // *********************************************************** -> 첫번째 비콘까지의 거리를 스마트폰 GPS 미터로 설정하면 더 정확하려나?? -> No.. 해결방법 필요
    /////////////////////////////////////////////////////////////
    public void onBeacon2SettingClicked(View mView) throws Exception
    {
        /////////////////////////////////////////////////////////
        // bc1의 신호 최근값 n개값 평균 구하기
        ////////////////////////////////////////////////////////
        double avgRssiOfBeacon1 = MyMath.getAvg(BackgroundBeaconRangingService.bc1Queue.getAllDatas());

        ///////////////////////////////////////////////////////////////////
        // 평균 RSSI값으로부터 거리 구하기  -> 노가다 매핑 적용하기
        ////////////////////////////////////////////////////////////////////
        // 거리를 변환하는 공식을 쓰면 안됨, 삼변측량은 거리가 모자랄 경우 적용안됨.
        // 거리를 조금 넓게 잡아야됨
        /////////////////////////////////////////////////////////////////////////
        // 공식 또는 매핑사용해서 거리를 얻는다
        double dist = MyMath.getDistUsingFormulaOrMapping(avgRssiOfBeacon1, 1);

        ////////////////////////////////////
        // dist = 테스트할 때 수동 삽입
        ///////////////////////////////////////
        Toast.makeText(getApplicationContext(),"CM:"+String.format("%.2f", dist)+", avgR"+String.format("%.2f", avgRssiOfBeacon1),Toast.LENGTH_SHORT).show();

        //////////////////////////////////////////
        // 실제 지오펜스 윗 변의 길이
        ////////////////////////////////////////////
        GeofenceView_settings.avgTopDist = dist;

        // 스크린 비율 설정
        GeofenceView_settings.screenRatio= (float)(Constants.viewWidth/GeofenceView_settings.avgTopDist);

        // 뷰 업데이트
        bc2Position = new Point3D( (float)GeofenceView_settings.avgTopDist, 0);
        ((GeofenceView_settings)view).bc2=bc2Position;
        ((GeofenceView_settings)view).locatedBeaconCnt = 2;
        ((GeofenceView_settings)view).invalidate();
    }


    ///////////////////////////////////////////////////////////////////////////////
    // 세번째 비콘 설치 -> 네번째 비콘은 사각형 모양을 유지하기 위한 곳에 임의로 설정
    ////////////////////////////////////////////////////////////////////////////////
    public void onBeacon3SettingClicked(View mView)
    {
        /////////////////////////////////////////////////////////
        // bc2의 신호 최근값 n개값 평균 구하기
        ////////////////////////////////////////////////////////
        double avgRssiOfBeacon2=MyMath.getAvg(BackgroundBeaconRangingService.bc2Queue.getAllDatas());
        ///////////////////////////////////////////////////////////////////
        // 평균 RSSI값으로부터 거리 구하기  -> 노가다 매핑 적용하기
        ////////////////////////////////////////////////////////////////////

        double dist = MyMath.getDistUsingFormulaOrMapping(avgRssiOfBeacon2,2);

        ////////////////////////////////////
        // dist = 테스트할 때 수동 삽입
        ///////////////////////////////////////
        Toast.makeText(getApplicationContext(),"CM:"+String.format("%.2f", dist)+", avgR"+String.format("%.2f", avgRssiOfBeacon2),Toast.LENGTH_SHORT).show();
        GeofenceView_settings.avgRightDist = dist;

        bc3Position = new Point3D(((float)GeofenceView_settings.avgTopDist), ((float)(GeofenceView_settings.avgRightDist)));
        bc4Position = new Point3D(0, ((float)(GeofenceView_settings.avgRightDist)));

        // 뷰 업데이트
        ((GeofenceView_settings)view).bc3=bc3Position;
        ((GeofenceView_settings)view).bc4=bc4Position;
        ((GeofenceView_settings)view).locatedBeaconCnt = 3;
        ((GeofenceView_settings)view).invalidate();
    }

    //////////////////////////////////////////////////////
    // 지오펜스 설치 완료 버튼 -> 서버에 저장
    ////////////////////////////////////////////////////////
    public void onConfirmBtnClicked(View mView)
    {
        Log.d(Constants.QUADCORE_LOG,"GeofenceSettingActivity : onConfirmBtnClicked");

        //////////////////////////////////////////////////////////////////////
        // 결제존, 비콘이 설치안된 경우 알림 메시지
        //////////////////////////////////////////////////////////////////////
        if(bc1Position == null || bc2Position == null || bc3Position == null || bc4Position == null)
        {
            Toast.makeText(getApplicationContext(),"Setup the beacons",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(PaymentZone.actual_leftUp == null || PaymentZone.actual_rightDown == null
              || PaymentZone.actual_leftUp.getX() == 0 || PaymentZone.actual_leftUp.getY() == 0
              || PaymentZone.actual_rightDown.getX() == 0 || PaymentZone.actual_rightDown.getY() == 0 )
        {
            Toast.makeText(getApplicationContext(),"Drag to Make the zone",Toast.LENGTH_SHORT).show();
            return;
        }


        ///////////////////////////////////////////////////////////////////////////
        // bc1,bc2,bc3,bc4의 위치 값을 서버로 전송한다
        // -> 서버는 DB에 설치된 geofence를 저장
        ///////////////////////////////////////////////////////////////////////////
        if(ServerInfo.isConnected == true)
        {
            beaconPositionTransferThread = new BeaconPositionTransferThread();
            beaconPositionTransferThread.start();
            Toast.makeText(getApplicationContext(),"SEND GEOFENCE TO SERVER",Toast.LENGTH_SHORT).show();

            // 기다림
            try
            {
                beaconPositionTransferThread.join();
            }
            catch(InterruptedException ie)
            {
                ie.printStackTrace();
            }

            if(isConnected)
            {
                if(bcBound)
                {
                    Message msg = Message.obtain(null, BackgroundBeaconMonitoringService.MSG_NUMBER,0,0);
                    try{
                        bcService.send(msg);
                    }catch(RemoteException re)
                    {
                        re.printStackTrace();
                    }
                }
            }
        }
        // test
        else
        {
            // 가짜 데이터 삽입
            MockServer.insertGeofence();
            Toast.makeText(getApplicationContext(),"SEND GEOFENCE TO MOCK",Toast.LENGTH_SHORT).show();
        }

        MainActivity.roomType=Constants._ROOM_TYPE_CUSTOMIZE;
    }

    /////////////////////////////////////////////////////////////////
    // 지오펜스 서버에서 제거
    //////////////////////////////////////////////////////////////////
    public void onResetBtnClicked(View mView)
    {
        Toast.makeText(getApplicationContext(),"GEOFENCE IS DELETED",Toast.LENGTH_SHORT).show();

        /////////////////////////////////
        // 사용자 위치 추적 스레드 중지
        /////////////////////////////////
        UserPositionUpdateService.stopUserPositionThread();
        if(MainActivity.userPositionUpdateServiceIntent!=null)
        {
            stopService(MainActivity.userPositionUpdateServiceIntent);
        }

        if(ServerInfo.isConnected==true)
        {
            // 서버에게 요청해서 GEOFENCE 제거
            GeofenceDeleteRequestThread geofenceDeleteRequestThread
                    = new GeofenceDeleteRequestThread();
            geofenceDeleteRequestThread.start();
        }
        // 테스트
        else
        {
            MockServer.deleteGeofence();
        }

        ((GeofenceView_settings)view).resetGeofenceView();
        ((GeofenceView_main)MainActivity.view).resetGeofenceView();
    }

    //////////////////////////////////////////////////
    // 위치 추적 시작 토글 버튼 ON/OFF
    /////////////////////////////////////////////////
    public void onLocationThreadToggleBtnClicked(View mView)
    {
        if ( ((ToggleButton)mView).isChecked() )
        {
            // 관리자
            UserPositionUpdateService.isUser=false;
            // 사용자 위치 추적 시작
            UserPositionUpdateService.startUserPositionThread();

        }
        else
        {
            // 사용자
            UserPositionUpdateService.isUser=true;
            // 사용자 위치 추적 중지
            UserPositionUpdateService.stopUserPositionThread();
            GeofenceView_settings.userLocation=null;
            GeofenceView_settings.bc1.setR(0);
            GeofenceView_settings.bc2.setR(0);
            GeofenceView_settings.bc3.setR(0);
            ((GeofenceView_settings)view).invalidate();
        }
    }




    ///////////////////////////////////////////////
    // 지오펜스 제거 요청을 서버로 보내는 스레드
    ///////////////////////////////////////////////
    class GeofenceDeleteRequestThread extends Thread
    {
        @Override
        public void run()
        {
            ///////////////////////////////////////////////////
            // WEB 서버와 연결 - 서버에게 설치된 비콘 위치 전송
            /////////////////////////////////////////////////
            String server_response="";
            String param_id = "id="+Constants.MAJOR_BEACON_1;

            try{
                ServerInfo.url = new URL("http://"+ServerInfo.serverIP+":"+ServerInfo.serverPort
                        +"/SpringMVC/deleteGeofence.do?"
                        +param_id);
                ServerInfo.urlConnection = (HttpURLConnection)ServerInfo.url.openConnection();


                int responseCode = ServerInfo.urlConnection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    server_response = ServerInfo.readStream(ServerInfo.urlConnection.getInputStream());
                    ///////////////////////
                    // Log & Toast
                    //////////////////////////
                    Log.d(Constants.QUADCORE_LOG, "SERVER RESPONSE : GEOFENCE DELETE: SUCCESS:"+server_response);
                }
                else
                {
                    Log.d(Constants.QUADCORE_LOG, "SERVER RESPONSE : geofence delete : ERROR CODE :"+responseCode);
                }

            }catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(ServerInfo.urlConnection!= null)
                {
                    ServerInfo.urlConnection.disconnect();
                }
            }
        }
    }


    /////////////////////////////////////////////
    // 웹서버로 설치된 비콘 위치 전송해주는 스레드
    ////////////////////////////////////////////
    class BeaconPositionTransferThread extends Thread
    {
        @Override
        public void run()
        {
            ///////////////////////////////////////////////////
            // WEB 서버와 연결 - 서버에게 설치된 비콘 위치 전송, 존위치 전송
            /////////////////////////////////////////////////
            String server_response="";
            String param_bc = "bcPositions="+bc1Position.getX()+","+bc1Position.getY()
                                        +","+bc2Position.getX()+","+bc2Position.getY()
                                        +","+bc3Position.getX()+","+bc3Position.getY()
                                        +","+bc4Position.getX()+","+bc4Position.getY();
            String param_id = "id="+Constants.MAJOR_BEACON_1;
            String param_name = "name=counter";
            String param_zone = "zonePosition="+String.format("%.2f",PaymentZone.actual_leftUp.getX())+","+String.format("%.2f",PaymentZone.actual_leftUp.getY())
                    +","+String.format("%.2f",PaymentZone.actual_rightDown.getX())+","+String.format("%.2f",PaymentZone.actual_rightDown.getY());

            String param_type = "type="+ Constants._ROOM_TYPE_CUSTOMIZE;

            try{
                ServerInfo.url = new URL("http://"+ServerInfo.serverIP+":"+ServerInfo.serverPort
                                        +"/SpringMVC/insertGeofence.do?"
                                        +param_bc+"&"+param_id+"&"+param_name+"&"+param_zone+"&"+param_type);

                ServerInfo.urlConnection = (HttpURLConnection)ServerInfo.url.openConnection();
                ServerInfo.urlConnection.setConnectTimeout(Constants._SERVER_TIMEOUT);

                int responseCode = ServerInfo.urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK)
                {
                    server_response = ServerInfo.readStream(ServerInfo.urlConnection.getInputStream());
                    ///////////////////////
                    // Log & Toast
                    //////////////////////////
                    Log.d(Constants.QUADCORE_LOG, "SERVER RESPONSE : INSERT GEOFENCE : SUCCESS:"+server_response);
                    popupToast("GEOFENCE INSERT SUCCESS");
                    isConnected = true;
                }
                else
                {
                    ///////////////////////
                    // Log & Toast
                    //////////////////////////
                    Log.d(Constants.QUADCORE_LOG, "SERVER RESPONSE : INSERT GEOFENCE : ERROR CODE :"+responseCode);
                    popupToast("GEOFENCE INSERT FAIL");
                    isConnected = false;
                }

            }
            // 서버 연결 안되는 경우
            catch(SocketTimeoutException ste)
            {
                Log.d(Constants.QUADCORE_LOG, "SERVER CONNECTION FAILED : SocketTimeoutException");
                popupToast("SERVER CONNECTION FAILED");
                isConnected = false;
            }
            catch(ConnectException e)
            {
                Log.d(Constants.QUADCORE_LOG,"SERVER CONNECTION FAILED : ConnectException");
                popupToast("SERVER CONNECTION FAILED");
                isConnected = false;
            }
            catch(Exception e)
            {
                Log.d(Constants.QUADCORE_LOG,"BeaconPositionTransferThread : EXCEPTION");
                popupToast("SERVER CONNECTION FAILED");
                isConnected =false;
            }
            finally
            {
                if(ServerInfo.urlConnection!= null)
                {
                    ServerInfo.urlConnection.disconnect();
                }
            }
        }
    }

    private void popupToast(final String msg) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 내용
                Toast.makeText(GeofenceSettingsActivity_Customize.this, msg,Toast.LENGTH_SHORT).show();
            }
        }, 0);
    }
}
