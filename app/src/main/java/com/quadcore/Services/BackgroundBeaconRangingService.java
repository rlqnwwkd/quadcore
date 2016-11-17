package com.quadcore.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.quadcore.GeofenceView_main;
import com.quadcore.MainActivity;
import com.quadcore.Mock.MockServer;
import com.quadcore.RssiCheckActivity;
import com.quadcore.Utils.RecentDataQueue;
import com.quadcore.Utils.*;
import java.lang.Math;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;


///////////////////////////////////////////////////////////////////////
// 비콘으로부터 거리값을 읽어서 RecentDataQueue를 지속적으로 업데이트
// 각각의 비콘은 자신만의 RecentDataQueue를 갖고 있고, 최신값 n개를 유지한다
////////////////////////////////////////////////////////////////////////
public class BackgroundBeaconRangingService extends Service {
    public static boolean isRssiCheck = false;

    ///////////////////////////////////////////
    // 최신값 n개를 갖고있는 큐
    /////////////////////////////////////////////
    public static RecentDataQueue<Integer> bc1Queue,bc2Queue,bc3Queue,bc4Queue;


    ///////////////////////////////////////////
    // ESTIMOTE 라이브러리
    //////////////////////////////////////////
    private BeaconManager beaconManager;
    private Region region;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        // 큐 생성
        bc1Queue = new RecentDataQueue<Integer>();
        bc2Queue = new RecentDataQueue<Integer>();
        bc3Queue = new RecentDataQueue<Integer>();
        bc4Queue = new RecentDataQueue<Integer>();
        bc1Queue.makeQueue(Constants._RECENT_QUEUE_SIZE);
        bc2Queue.makeQueue(Constants._RECENT_QUEUE_SIZE);
        bc3Queue.makeQueue(Constants._RECENT_QUEUE_SIZE);
        bc4Queue.makeQueue(Constants._RECENT_QUEUE_SIZE);

        // 비콘매니저 - 비콘 인터페이스 역할 -> 전역으로 둬서, 모든 어플리케이션에서 사용하게끔
        beaconManager = new BeaconManager(getApplicationContext());

        // 초당 10개 스캔
        beaconManager.setForegroundScanPeriod(Constants._SCAN_PERIOD_MILLIS,0);
        //beaconManager.setBackgroundScanPeriod(100,0);
    }


    ///////////////////////////////////////////////////////////////////////
    // 비콘으로부터 거리값을 읽어서 RecentDataQueue를 지속적으로 업데이트
    ////////////////////////////////////////////////////////////////////////
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        ////////////////////////////////////////////////
        // 같은 UUID_BEACON를 갖는 비콘들을 모두 읽는다
        ////////////////////////////////////////////////
        startBeaconRanging(Constants.UUID_BEACON);
        return Service.START_STICKY;
    }


    ////////////////////////////////////////////////////////////////////
    // 같은 UUID_BEACON를 갖는 비콘들을 모두 읽는다 -> queue에 저장
    // queue에 insert하기만 하면, 큐는 알아서 최신값 n개를 유지함
    ////////////////////////////////////////////////////////////////////
    public void startBeaconRanging(String m_uuid)
    {
        Log.d(Constants.QUADCORE_LOG,"Ranging Service : start Beacon Ranging");
        ///////////////////////////////////////////////////////////////////////////////
        // ESTIMOTE API
        ////////////////////////////////////////////////////////////////////////////////
        // 스캔할 비콘 종류 설정, uuid, major, minor 넣고싶은 것만 넣으면 알아서 동작
        // uuid만 넣을 경우, major,minor는 모두 인식
        ///////////////////////////////////////////////////////////////////////////////
        region = new Region("ranged region", UUID.fromString(m_uuid),
                null, null);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback(){
            @Override
            public void onServiceReady(){
                beaconManager.startRanging(region);
            }
        });

        ///////////////////////////////////////////////////////////
        // 비콘 발견했을 경우
        ////////////////////////////////////////////////////////////
        beaconManager.setRangingListener(new BeaconManager.RangingListener(){
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list)
            {
                // 같은 uuid를 갖는 비콘들이 list로 저장되있음
                if(!list.isEmpty())
                {
                    for(Beacon beacon : list)
                    {
                        /////////////////////////////////////
                        // rssi값 저장
                        // Major값으로 큐를 구분한다
                        // MAJOR1 -> bc1Queue,  MAJOR2->bc2Queue,  MAJOR3->bc3Queue
                        //////////////////////////////////////
                        switch(beacon.getMajor())
                        {
                            ////////////////////////////////////////////////////////////////////////////////////
                            // geofence를 대표하는 bc1번 MAJOR 값을 센싱했을 경우 서버로부터 geofence를 얻어온다
                            /////////////////////////////////////////////////////////////////////////////////////
                            case Constants.MAJOR_BEACON_1:
                                ///////////////////////////////////////////////////////////
                                // 비콘신호 넣을때, 큐에있는 평균값보다 너무 크면 빼버리기
                                ////////////////////////////////////////////////////////////
                                //Log.d("beacon Scan test","getting data"+new Date());
                                bc1Queue.insertData(beacon.getRssi());
                                break;
                            case Constants.MAJOR_BEACON_2:
                                bc2Queue.insertData(beacon.getRssi());
                                break;
                            case Constants.MAJOR_BEACON_3:
                                bc3Queue.insertData(beacon.getRssi());
                                break;
                            case Constants.MAJOR_BEACON_4:
                                bc4Queue.insertData(beacon.getRssi());
                                break;
                            default:
                                break;
                        }

                        if(isRssiCheck)
                        {
                            switch(beacon.getMajor())
                            {
                                ////////////////////////////////////////////////////////////////////////////////////
                                // geofence를 대표하는 bc1번 MAJOR 값을 센싱했을 경우 서버로부터 geofence를 얻어온다
                                /////////////////////////////////////////////////////////////////////////////////////
                                case Constants.MAJOR_BEACON_1:
                                    RssiCheckActivity.rssi1TextView.setText(""+beacon.getRssi()+",Power:"+beacon.getMeasuredPower());
                                    break;
                                case Constants.MAJOR_BEACON_2:
                                    RssiCheckActivity.rssi2TextView.setText(""+beacon.getRssi()+",Power:"+beacon.getMeasuredPower());
                                    break;
                                case Constants.MAJOR_BEACON_3:
                                    RssiCheckActivity.rssi3TextView.setText(""+beacon.getRssi()+",Power:"+beacon.getMeasuredPower());
                                    break;
                                case Constants.MAJOR_BEACON_4:
                                    RssiCheckActivity.rssi4TextView.setText(""+beacon.getRssi()+",Power:"+beacon.getMeasuredPower());
                                    break;

                                default:
                                    break;
                            }
                        }
                    }

                }
            }
        });
    }












    //////////////////////////////////////////////////////
    // 한 비콘만 지정해서 스캔
    // 사용할 용도가 있을 것 같아, 혹시몰라서 놔둠...
    ////////////////////////////////////////////////////////
    public void startBeaconRanging_1(String l_UUID, int MAJOR, int MINOR)
    {
        // 스캔할 비콘 종류 설정
        region = new Region("ranged region", UUID.fromString(l_UUID),
                MAJOR, MINOR);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback(){
            @Override
            public void onServiceReady(){
                beaconManager.startRanging(region);
            }
        });

        // 비콘 발견했을 경우
        beaconManager.setRangingListener(new BeaconManager.RangingListener(){

            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list)
            {
                if(!list.isEmpty())
                {
                    // 한 비콘만 지정하니, list 스캔 필요 없음
                    Beacon beacon = list.get(0);
                    bc1Queue.insertData(beacon.getRssi());
                }
            }
        });
    }
}
