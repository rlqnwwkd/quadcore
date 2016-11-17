package com.quadcore.Room_Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.Toast;
import com.quadcore.GeofenceView_main;
import com.quadcore.MainActivity;
import com.quadcore.Mock.MockServer;
import com.quadcore.R;
import com.quadcore.Room.Room_Rectangle_12_7;
import com.quadcore.Services.BackgroundBeaconMonitoringService;
import com.quadcore.Utils.Constants;
import com.quadcore.Utils.Point3D;
import com.quadcore.Utils.ServerInfo;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class GeofenceSettingsActivity_Rectangle_12_7 extends AppCompatActivity {

    public static Thread beaconPositionTransferThread;
    public static boolean isConnected = false;

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
        setContentView(R.layout.activity_geofence_settings_rectangle_12_7);

        if(bcBound == false)
        {
            // 서비스 바인드
            Intent serviceIntent = new Intent(this, BackgroundBeaconMonitoringService.class);
            bindService(serviceIntent, bcConnection, Context.BIND_AUTO_CREATE);
        }
    }


    public void onConfirmBtnClicked(View view)
    {
        // 실제
        if(ServerInfo.isConnected==true)
        {
            beaconPositionTransferThread = new BeaconPositionTransferThread();
            beaconPositionTransferThread.start();
            // 기다림
            try
            {
                beaconPositionTransferThread.join();
            }
            catch(InterruptedException ie)
            {
                ie.printStackTrace();
            }

            if(isConnected )
            {
                Log.d(Constants.QUADCORE_LOG,"bcBound : "+bcBound);

                ////////////////////////////////////////
                // 서비스에게 비콘위치 다시받아달라고 메시지보냄
                //////////////////////////////////////////////
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
        // 테스트
        else
        {
            // 가짜 데이터 삽입
            Point3D bc1 = com.quadcore.Room.Room_Rectangle_12_7.bc1Position;
            Point3D bc2 = com.quadcore.Room.Room_Rectangle_12_7.bc2Position;
            Point3D bc3 = Room_Rectangle_12_7.bc3Position;
            Point3D bc4 = com.quadcore.Room.Room_Rectangle_12_7.bc4Position;
            Point3D zoneLeftTop = com.quadcore.Room.Room_Rectangle_12_7.leftUp;
            Point3D zoneRightDown = com.quadcore.Room.Room_Rectangle_12_7.rightDown;
            MockServer.insertGeofence_type(bc1,bc2,bc3,bc4,zoneLeftTop,zoneRightDown,Constants._ROOM_TYPE_RECTANGLE_12_7);
            Toast.makeText(getApplicationContext(),"SEND GEOFENCE TO MOCK",Toast.LENGTH_SHORT).show();
        }

        // 스크린 비율 설정
        GeofenceView_main.avgTopDist = Room_Rectangle_12_7.xLength;
        GeofenceView_main.avgRightDist = Room_Rectangle_12_7.yLength;
        GeofenceView_main.screenRatio = com.quadcore.Room.Room_Rectangle_12_7.screenRatio;
        ((GeofenceView_main) MainActivity.view).locatedBeaconCnt=3;
        MainActivity.roomType = Constants._ROOM_TYPE_RECTANGLE_12_7;
    }


    /////////////////////////////////////////////
    // 웹서버로 설치된 비콘 위치 전송해주는 스레드
    ////////////////////////////////////////////
    class BeaconPositionTransferThread extends Thread
    {
        @Override
        public void run()
        {
            Log.d(Constants.QUADCORE_LOG,"Room_12_7 : BeaconPositionTransferThread : start");
            ///////////////////////////////////////////////////
            // WEB 서버와 연결 - 서버에게 설치된 비콘 위치 전송, 존위치 전송
            /////////////////////////////////////////////////
            // 파라미터 셋팅
            String server_response="";
            String param_bc = "bcPositions="+Room_Rectangle_12_7.bc1Position.getX()+","+Room_Rectangle_12_7.bc1Position.getY()
                    +","+Room_Rectangle_12_7.bc2Position.getX()+","+Room_Rectangle_12_7.bc2Position.getY()
                    +","+Room_Rectangle_12_7.bc3Position.getX()+","+Room_Rectangle_12_7.bc3Position.getY()
                    +","+Room_Rectangle_12_7.bc4Position.getX()+","+Room_Rectangle_12_7.bc4Position.getY();
            String param_id = "id="+ Constants.MAJOR_BEACON_1;
            String param_name = "name=counter";
            String param_type = "type="+ Constants._ROOM_TYPE_RECTANGLE_12_7;
            String param_zone = "zonePosition="+String.format("%.2f", Room_Rectangle_12_7.leftUp.getX())+","+String.format("%.2f",Room_Rectangle_12_7.leftUp.getY())
                    +","+String.format("%.2f",Room_Rectangle_12_7.rightDown.getX())+","+String.format("%.2f",Room_Rectangle_12_7.rightDown.getY());



            ///////////////////////////////////////
            // 서버 전송
            //////////////////////////////////////
            try
            {
                ServerInfo.url = new URL("http://" + ServerInfo.serverIP + ":" + ServerInfo.serverPort
                        + "/SpringMVC/insertGeofence.do?"
                        + param_bc + "&" + param_id + "&" + param_name + "&" + param_zone + "&" + param_type);

                // 전송
                ServerInfo.urlConnection = (HttpURLConnection) ServerInfo.url.openConnection();
                // 타임아웃
                ServerInfo.urlConnection.setConnectTimeout(3000);
                // 결과
                int responseCode = ServerInfo.urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK)
                {
                    server_response = ServerInfo.readStream(ServerInfo.urlConnection.getInputStream());
                    ///////////////////////
                    // Log & Toast
                    //////////////////////////
                    Log.d(Constants.QUADCORE_LOG, "SERVER RESPONSE : INSERT GEOFENCE : SUCCESS:"+server_response+","+server_response.length());
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
                Toast.makeText(GeofenceSettingsActivity_Rectangle_12_7.this, msg,Toast.LENGTH_SHORT).show();
            }
        }, 0);
    }
}
