package com.quadcore.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.quadcore.GeofenceView_main;
import com.quadcore.MainActivity;
import com.quadcore.Mock.MockServer;
import com.quadcore.Utils.Constants;
import com.quadcore.Utils.PaymentZone;
import com.quadcore.Utils.RecentDataQueue;
import com.quadcore.Utils.ServerInfo;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

/**
 * Created by bbong on 2016-11-09.
 */

public class BackgroundBeaconMonitoringService extends Service{

    //////////////////////////////////////////////////////////////////////
    // MAJOR_BEACON_1 값을 이용해서 서버에서 geofence 정보 얻어오는 스레드
    ///////////////////////////////////////////////////////////////////////
    public static GeofenceGetThread geofenceGetThread;
    public String server_response="";

    ///////////////////////////////////////////
    // ESTIMOTE 라이브러리
    //////////////////////////////////////////
    public static BeaconManager beaconManager;
    private Region region;

    ///////////////////
    // binder
    /////////////////////
    public static final int MSG_NUMBER=1;
    final Messenger messenger = new Messenger(new IncomingHandler());

    public class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            Log.d(Constants.QUADCORE_LOG, "Monitoring Service got messsage!");
            switch(msg.what)
            {
                case MSG_NUMBER:
                    // 서버로부터 비콘위치 다시 검색
                    getGeofenceFromServer(Constants.MAJOR_BEACON_1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    // 다른 곳에서 서비스의 메소드를 호출
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void onDestroy() {
        Log.d(Constants.QUADCORE_LOG,"Monitoring Service : onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onCreate()
    {
        Log.d(Constants.QUADCORE_LOG,"Monitoring Service : onCreate()");
        beaconManager = new BeaconManager(getApplicationContext());

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {

            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                // 비콘 메이저 1(존)의 범위에 들어왔을때
                Log.d(Constants.QUADCORE_LOG,"Monitoring Service : onEnteredRegion ");

                // 서버로부터 geofence 정보 얻어오기
                getGeofenceFromServer(Constants.MAJOR_BEACON_1);

                // RangingService 시작
                startService(MainActivity.backgroundBeaconRangingServiceIntent);


                // 결제 팝업 셋팅 -> 존에 처음 들어올 경우 리셋
                PaymentZone.isPaid = false;
            }
            @Override
            public void onExitedRegion(Region region) {
                // 비콘 메이저 1(존)의 범위에서 나갔을때 -> 30초정도 시간걸림 이유는 모르겠음
                // RangingService 종료
                Log.d(Constants.QUADCORE_LOG,"Monitoring Service : onExitedRegion");
                stopService(MainActivity.backgroundBeaconRangingServiceIntent);

                // 펜스 지우기
                ((GeofenceView_main)MainActivity.view).resetGeofenceView();

                // 결제 가능하게
                PaymentZone.isPaid = false;
                PaymentZone.zoneCount = 0;
            }
        });

        // 3 sec
        beaconManager.setForegroundScanPeriod(Constants._MONITORING_PERIOD_MILLIS,0);
        beaconManager.setBackgroundScanPeriod(Constants._MONITORING_PERIOD_MILLIS,0);
    }




    public void startBeaconMonitoring(String m_uuid, int major)
    {
        Log.d(Constants.QUADCORE_LOG,"Monitoring Service : start Beacon Monitoring");
        //////////////////////////////////
        // monitoring start
        //////////////////////////////////
        region = new Region("ranged region", UUID.fromString(m_uuid),
                major, null);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback(){
            @Override
            public void onServiceReady(){
                beaconManager.startMonitoring(region);
            }
        });

        Log.d(Constants.QUADCORE_LOG,"Monitoring Service : start Beacon Monitoring END");
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        ////////////////////////////////////////////////
        // MAJOR1 값 비콘(존을 대표하는 값)을 모니터링한다
        ////////////////////////////////////////////////
        Log.d(Constants.QUADCORE_LOG, "Monitoring onStartCommand start");
        startBeaconMonitoring(Constants.UUID_BEACON, Constants.MAJOR_BEACON_1);
        return Service.START_STICKY;
    }




    /////////////////////////////////////////////////////////////
    // 서버로부터 major값을 갖는 Geofence 얻어오는 함수
    //////////////////////////////////////////////////////////////
    public boolean getGeofenceFromServer(long major)
    {
        boolean isInstalled = false;

        if(ServerInfo.isConnected==true)
        {
            /////////////////////////////////////////
            // 서버로부터 geofence를 얻어오기
            ///////////////////////////////////////////
            geofenceGetThread = new BackgroundBeaconMonitoringService.GeofenceGetThread(major);
            geofenceGetThread.start();

            try
            {
                geofenceGetThread.join();
            }
            catch(InterruptedException ie)
            {
                ie.printStackTrace();
            }
        }
        // 테스트
        else
        {
            server_response = MockServer.selectGeofence(major);
        }

        ////////////////////////////////////////////////////
        // 뷰 업데이트
        ////////////////////////////////////////////////////
        Log.d(Constants.QUADCORE_LOG,"getGeofenceFromServer : server_response : "+server_response);
        // 서버에 등록된 지오펜스가 없으면
        if(server_response.equals("") || server_response.equals("null"))
        {
            isInstalled = false;
            // 화면 리셋
            ((GeofenceView_main)MainActivity.view).resetGeofenceView();
            ((GeofenceView_main)MainActivity.view).invalidate();
            //  계속 스캔
        }
        // 서버에서 지오펜스를 가져온 경우 ( 이미 설치된 경우)
        else
        {
            isInstalled = true;
            MainActivity.updateGeofenceView(server_response);
        }
        return isInstalled;
    }

    /////////////////////////////////////////////////////////
    // BC1 MAJOR값을 이용해서 서버에서 geofence 정보 얻어오기
    // 이미 설치된 geofence일 경우 bc1,2,3,4의 위치를 얻어온다
    // 설치가 안 된 경우 무시한다
    /////////////////////////////////////////////////////////
    class GeofenceGetThread extends Thread
    {
        // Geofence를 대표하는 BC1 Major 값
        private long major;

        public GeofenceGetThread(long major)
        {
            this.major=major;
        }

        @Override
        public void run()
        {
            /////////////////////////////////////////////////////////////////////////
            // WEB 서버와 연결 - 서버에게 BC1 MAJOR 값을 보내면서 Geofence 검색 요청
            /////////////////////////////////////////////////////////////////////////
            String params = "id="+major;

            try{
                ServerInfo.url = new URL("http://"+ServerInfo.serverIP+":"+ServerInfo.serverPort+"/SpringMVC/selectGeofence.do?"+params);
                ServerInfo.urlConnection = (HttpURLConnection)ServerInfo.url.openConnection();
                // 3초간 서버 검색
                ServerInfo.urlConnection.setConnectTimeout(Constants._SERVER_TIMEOUT);

                int responseCode = ServerInfo.urlConnection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    //////////////////////////////////////////////
                    // 서버로부터 받은 Geofence 정보 저장
                    //////////////////////////////////////////////
                    server_response = ServerInfo.readStream(ServerInfo.urlConnection.getInputStream());
                    Log.d(Constants.QUADCORE_LOG,"SERVER RESPONSE : GEOFENCE GET : "+server_response);
                }
                else
                {
                    Log.d(Constants.QUADCORE_LOG, "SERVER RESPONSE : GEOFENCE GET :  ERROR CODE :"+responseCode);
                }

            }
            // 서버 연결 안되는 경우
            catch(SocketTimeoutException ste)
            {
                ste.printStackTrace();
            }
            catch(Exception e)
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
}
