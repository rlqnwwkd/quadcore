package com.quadcore.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.quadcore.Room_Activity.GeofenceSettingsActivity_Customize;
import com.quadcore.GeofenceView_settings;
import com.quadcore.Utils.Constants;
import com.quadcore.Utils.MyMath;
import com.quadcore.Utils.PaymentZone;
import com.quadcore.Utils.Point3D;
import com.quadcore.Utils.QuadCoreMapper;
import com.quadcore.Utils.ServerInfo;
import com.quadcore.Utils.Trilateration;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.quadcore.*;

/////////////////////////////////////////////////////////////////////////
// 주기적으로 bc#Queue값들을 읽은 후 삼변측량 적용, 사용자의 위치 파악하는 서비스
// n초마다 사용자 위치를 주기적으로 업데이트한다 => Canvas View까지 적용
///////////////////////////////////////////////////////////////////////
public class UserPositionUpdateService extends Service {

    Handler handler;
    public static UserPositionThread userPositionThread;
    public static boolean Flag_UserPositionThreadIsRunning;
    /////////////////////////////////////////////////////////////
    // GeofenceView_main(사용자) / GeofenceView(관리자)를 구별하기 위한 플래그
    /////////////////////////////////////////////////////////////
    public static boolean isUser = false;


    @Override
    public void onCreate() {    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //////////////////////////////////////////////////
    // 서비스 시작 - 1초마다 사용자 위치 업데이트
    ////////////////////////////////////////////////////
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        handler = new Handler();
        userPositionThread = new UserPositionThread();
        userPositionThread.setDaemon(true);
        userPositionThread.start();
        return Service.START_STICKY;
    }

    //////////////////////////////////////////////
    // isUser 이면, 유저 뷰에 사용자 위치를 업데이트
    // 아니면, 관리자 뷰에 사용자 위치를 업데이트
    ///////////////////////////////////////////////////
    public void userLocationUpdate(boolean isUser)
    {
        ///////////////////////////////////////
        // 비콘 3개의 위치 가져오기
        ////////////////////////////////////////
        Point3D bc1Pos,bc2Pos,bc3Pos,bc4Pos;

        if(isUser)
        {
            bc1Pos = GeofenceView_main.bc1;
            bc2Pos = GeofenceView_main.bc2;
            bc3Pos = GeofenceView_main.bc3;
            bc4Pos = GeofenceView_main.bc4;
        }
        else
        {
            bc1Pos = GeofenceView_settings.bc1;
            bc2Pos = GeofenceView_settings.bc2;
            bc3Pos = GeofenceView_settings.bc3;
            bc4Pos = GeofenceView_settings.bc4;
        }

        List<Point3D> userLocations = new ArrayList<Point3D>();

        if(bc1Pos == null || bc2Pos ==null || bc3Pos == null || bc4Pos == null)
        {
            return;
        }
        Point3D bc1 = new Point3D(bc1Pos.getX(), bc1Pos.getY());
        Point3D bc2 = new Point3D(bc2Pos.getX(), bc2Pos.getY());
        Point3D bc3 = new Point3D(bc3Pos.getX(), bc3Pos.getY());
        Point3D bc4 = new Point3D(bc4Pos.getX(), bc4Pos.getY());


        ///////////////////////////////////////////////////////////////////
        // bcQueue(사용자의 rssi 최신값 n개)로 부터 사용자신호 평균rssi 구하기
        ///////////////////////////////////////////////////////////////////
        double avgRssi_1 = 0;
        for(int rssi : BackgroundBeaconRangingService.bc1Queue.getAllDatas())
        {
            avgRssi_1 += rssi;
        }
        avgRssi_1 = avgRssi_1 / ((double)BackgroundBeaconRangingService.bc1Queue.getSize());

        double avgRssi_2 = 0;
        for(int rssi : BackgroundBeaconRangingService.bc2Queue.getAllDatas())
        {
            avgRssi_2 += rssi;
        }
        avgRssi_2 = avgRssi_2 / ((double)BackgroundBeaconRangingService.bc2Queue.getSize());

        double avgRssi_3 = 0;
        for(int rssi : BackgroundBeaconRangingService.bc3Queue.getAllDatas())
        {
            avgRssi_3 += rssi;
        }
        avgRssi_3 = avgRssi_3 / ((double)BackgroundBeaconRangingService.bc3Queue.getSize());

        double avgRssi_4 = 0;
        for(int rssi : BackgroundBeaconRangingService.bc4Queue.getAllDatas())
        {
            avgRssi_4 += rssi;
        }
        avgRssi_4 = avgRssi_4 / ((double)BackgroundBeaconRangingService.bc4Queue.getSize());

        //////////////////////////////////////////////////////////////////
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!무조건 해야할 것
        // RSSI -> DIST 바꾸는 메소드 만들기 , 공식 말고 매칭으로
        ////////////////////////////////////////////////////////////////////
        // 평균 rssi -> 평균 거리로 변환                                            신호 -> 거리 변환...
        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        double dist_1,dist_2,dist_3,dist_4;

        if(MainActivity.isUsingFormula)
        {
            dist_1 = MyMath.getCMeterFromRssi(avgRssi_1);
            dist_2 = MyMath.getCMeterFromRssi(avgRssi_2);
            dist_3 = MyMath.getCMeterFromRssi(avgRssi_3);
            dist_4 = MyMath.getCMeterFromRssi(avgRssi_4);
        }
        else
        {
            dist_1 = RssiToDist.getDistance(avgRssi_1);
            dist_2 = RssiToDist.getDistance(avgRssi_2);
            dist_3 = RssiToDist.getDistance(avgRssi_3);
            dist_4 = RssiToDist.getDistance(avgRssi_4);
        }



        bc1.setR((float)dist_1);
        bc2.setR((float)dist_2);
        bc3.setR((float)dist_3);
        bc4.setR((float)dist_3);


        if(isUser)
        {
            GeofenceView_main.bc1.setR((float)dist_1);
            GeofenceView_main.bc2.setR((float)dist_2);
            GeofenceView_main.bc3.setR((float)dist_3);
            GeofenceView_main.bc4.setR((float)dist_4);

        }
        else
        {
            GeofenceView_settings.bc1.setR((float)dist_1);
            GeofenceView_settings.bc2.setR((float)dist_2);
            GeofenceView_settings.bc3.setR((float)dist_3);
            GeofenceView_settings.bc4.setR((float)dist_4);
        }


        // activity_main의 rssiAndMeter 업데이트

        MainActivity.rssiAndMeter1.setText("R:"+String.format("%.2f",avgRssi_1));
        MainActivity.rssiAndMeter2.setText("R:"+String.format("%.2f",avgRssi_2));
        MainActivity.rssiAndMeter3.setText("R:"+String.format("%.2f",avgRssi_3));
        MainActivity.rssiAndMeter4.setText("R:"+String.format("%.2f",avgRssi_4));


        //////////////////////////////////////////////////////////
        // 리스트로 얻어오는 이유는 z축을 감안하기 때문, 우린 필요없음
        ///////////////////////////////////////////////////////////
        // 삼변측량 적용 - imm
        ////////////////////////////////////////////////////////////
        userLocations = Trilateration.trilaterate(bc1,bc2,bc3);

        //////////////////
        // 맵핑 추가 적용
        ////////////////////
        userLocations = QuadCoreMapper.getUserPosition(userLocations, avgRssi_1,avgRssi_2,avgRssi_3, avgRssi_4);

        /////////////////////////////////////////
        // TEST - 결제 테스트
        ///////////////////////////////////////////
        // userLocations = new ArrayList<Point3D>();
        // userLocations.add(new Point3D(300,800));

        if(isUser)
        {
            //////////////////////////
            // 사용자 뷰 업데이트
            ///////////////////////////////////////////////////////////
            // 사용자의 위치 파악 못했을 경우 ( 겹치는 곳이 없을 경우 )
            /////////////////////////////////////////////////////////////
            if(userLocations==null)
            {
                Log.d(Constants.QUADCORE_LOG, "UserPositionService : No user location found");
                GeofenceView_main.userLocation=null;
                MainActivity.view.invalidate();
            }
            ///////////////////////////////////////////////////////////
            // 위치 파악 된 경우
            /////////////////////////////////////////////////////////////
            else
            {

                GeofenceView_main.userLocation = userLocations.get(0);
                MainActivity.view.invalidate();

                if(ServerInfo.isConnected == true)
                {
                    ////////////////////////////////
                    // 서버로 사용자 위치 전송
                    //////////////////////////////
                    UserPositionTransferThread userPositionTransferThread = new UserPositionTransferThread();
                    userPositionTransferThread.start();
                }
                // 테스트
                else
                {

                }

                //////////////////////////////////////////////////////////
                // 만약 사용자의 위치가 결제존에 5초이상 머무른다면 결제 시작
                ///////////////////////////////////////////////////////////
                // 유저 위치 : GeofenceView_main.userLocation
                boolean isOnPaymentZone = PaymentZone.checkOnPaymentZone(GeofenceView_main.userLocation);

                Log.d(Constants.QUADCORE_LOG, "UserPositionService : user location : "+GeofenceView_main.userLocation.getX()+","+GeofenceView_main.userLocation.getY()
                        +", PaymentZone : "+PaymentZone.actual_leftUp.getX()+","+PaymentZone.actual_leftUp.getY()+","+PaymentZone.actual_rightDown.getX()+","+PaymentZone.actual_rightDown.getY()
                        +", isOnPaymentZone : "+isOnPaymentZone);

                if(isOnPaymentZone == true)
                {
                    PaymentZone.zoneCount++;
                }
                // Payment 실행
                // ㅇ-----------------> PaymentZone.isPaid = false일 경우에만 실행,
                // isPaid = false 사용자가 처음 존에 입장했을 때
                if(PaymentZone.zoneCount > Constants._PAYMENT_ZONE_DURATION)
                {
                    if(PaymentZone.isPaid == false) {
                        PaymentZone.zoneCount = 0;
                        ////////////////////////////
                        // 결제 팝업 뛰우기
                        //////////////////////////
                        MainActivity.popupThePayment();
                        PaymentZone.isPaid=true;
                    }
                }

            }
        }
        else
        {
            //////////////////////////////
            // 관리자 뷰 업데이트
            ///////////////////////////////
            if(userLocations==null)
            {
                // 사용자의 위치 파악 못했을 경우 ( 겹치는 곳이 없을 경우 )
                Log.d("UserPositionService","No user location found");
                GeofenceView_settings.userLocation=null;
                GeofenceSettingsActivity_Customize.view.invalidate();
            }
            // 위치 파악 된 경우
            else
            {
                GeofenceView_settings.userLocation = userLocations.get(0);
                Log.d("UserPositionService", "user location : "+ GeofenceView_settings.userLocation.getX()+","+ GeofenceView_settings.userLocation.getY());
                GeofenceSettingsActivity_Customize.view.invalidate();
            }
        }
    }





    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Flag_UserPositionThreadIsRunning가 True이고 isUser가 True이면 n초마다 GeofenceView_main(사용자가 사용하는 뷰)에 사용자 위치 업데이트
    // Flag_UserPositionThreadIsRunning가 True이고 isUser가 False이면 n초마다 GeofenceView(비콘 셋팅할때 관리자가 사용하는 뷰)에 사용자 위치 업데이트
    //////////////////////////////////////////////////////////////////////////////////////////////////
    class UserPositionThread extends Thread
    {
        @Override
        public void run()
        {
            while(true)
            {
                if(Flag_UserPositionThreadIsRunning) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            userLocationUpdate(isUser);
                        }
                    });

                    // 업데이트 주기 설정
                    try {
                        Thread.sleep(Constants._INTERVAL_USER_POSITION_UPDATE);

                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /////////////////////////////////////////////
    // 웹서버에 사용자 위치 전송하는 스레드
    ////////////////////////////////////////////
    class UserPositionTransferThread extends Thread
    {
        @Override
        public void run()
        {
            ///////////////////////////////////////////////////
            // WEB 서버와 연결 - 서버에게 설치된 비콘 위치 전송
            /////////////////////////////////////////////////
            String server_response="";
            String param1 = "userPosition="+GeofenceView_main.userLocation.getX()+","+GeofenceView_main.userLocation.getY();
            String param2 = "id="+MainActivity.geofenceId;

            try{
                ServerInfo.url = new URL("http://"+ServerInfo.serverIP+":"+ServerInfo.serverPort
                        +"/SpringMVC/insertUserLocation.do?"
                        +param1+"&"+param2);

                ServerInfo.urlConnection = (HttpURLConnection)ServerInfo.url.openConnection();


                int responseCode = ServerInfo.urlConnection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    server_response = ServerInfo.readStream(ServerInfo.urlConnection.getInputStream());
                    Log.d("SERVER RESPONSE",server_response);
                }
                else
                {
                    Log.d("SERVER RESPONSE","ERROR CODE :"+responseCode);
                }

            }catch(Exception e)
            {
                Log.d("User Pos Trans","server response:"+server_response);
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

    
    ////////////////////////////////////////////////////
    // 사용자 위치 업데이트 스레드 동작 ON/OFF 플래그
    ////////////////////////////////////////////////////
    public static void startUserPositionThread() { Flag_UserPositionThreadIsRunning=true; }
    public static void stopUserPositionThread(){ Flag_UserPositionThreadIsRunning = false; }
}
