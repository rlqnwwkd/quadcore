package com.quadcore;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quadcore.Services.BackgroundBeaconRangingService;
import com.quadcore.Utils.Constants;
import com.quadcore.Utils.PaymentZone;
import com.quadcore.Utils.ServerInfo;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CartCheckActivity extends AppCompatActivity {

    List<Integer> nearStickers;
    String server_response="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_check);

        // 초기화
        nearStickers = new ArrayList<Integer>();

        nearStickers = PaymentZone.getNearStickers();
        /*
        if(BackgroundBeaconRangingService.stickerQueue == null || BackgroundBeaconRangingService.stickerQueue2 == null)
        {
            // 상품 없음

        }
        else
        {
            // 신호가 잡히는 비콘 스티커들의 MAJOR를 모두 읽어서 nearStickers에 저장
            long timeDiff = System.currentTimeMillis() - BackgroundBeaconRangingService.stickerQueue.getRecentTime();
            if( timeDiff <= 5000 )
            {
                nearStickers.add(Constants.MAJOR_STICKER_1);
            }
            timeDiff = System.currentTimeMillis() - BackgroundBeaconRangingService.stickerQueue2.getRecentTime();
            if( timeDiff <= 5000 )
            {
                nearStickers.add(Constants.MAJOR_STICKER_2);
            }
        }
        */



        // 서버로부터 nearStickers를 전달, 상품들의 정보를 얻어온다
        if(nearStickers.size() == 0 )
        {
            Log.d(Constants.QUADCORE_LOG,"CartCheckActivity : No stickers found");
            // 아무런 상품이 없는 경우
        }
        else
        {
            Log.d(Constants.QUADCORE_LOG,"CartCheckActivity :"+nearStickers.size()+" stickers found");
            // 상품이 있는 경우 서버로 nearStickers 전달
            NearStickersTransferThread nearStickersTransferThread = new NearStickersTransferThread();
            nearStickersTransferThread.start();

            // 기다림
            try
            {
                nearStickersTransferThread.join();
            }
            catch(InterruptedException ie)
            {
                ie.printStackTrace();
            }
        }


        // 뷰 만들 차례 뷰만들면 끝
        Log.d(Constants.QUADCORE_LOG,"CartCheckActivity : Server response : "+ server_response);
        if( nearStickers.size() == 0 )
        {
            ImageView noProductView = new ImageView(this);
            noProductView.setImageResource(R.drawable.no_product);
            noProductView.setMinimumHeight(1000);
            LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout_product);
            ll.addView(noProductView);

            // 아무것도 없다는 뷰
        }
        else
        {
            // 상품 뷰
            String[] productInfo = server_response.split(",");
            int productCnt = productInfo.length/2;
            for(int i=0;i<productCnt;i++)
            {
                int idx = 2*i;
                TextView name = new TextView(this);
                name.setTypeface(Typeface.SERIF);
                name.setTextSize(15);
                name.setText("Product : "+productInfo[idx]);
                TextView price = new TextView(this);
                price.setText("Price : "+productInfo[idx+1]);

                // linear layout 생성
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                ll.addView(name);
                ll.addView(price);
                ImageView productImg = new ImageView(this);
                if(i%2 == 0)
                {
                    productImg.setImageResource(R.drawable.phone1);
                }
                else
                {
                    productImg.setImageResource(R.drawable.phone2);
                }
                // ll 마진 설정
                LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams)ll.getLayoutParams();
                lParams.topMargin=50;
                ll.setLayoutParams(lParams);

                // 이미지 설정
                productImg.setMinimumHeight(500);
                ll.addView(productImg);
                ll.setPadding(10,10,10,10);

                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setStroke(3, Color.rgb(204,230,255));
                drawable.setCornerRadius(50);
                drawable.setColor(Color.WHITE);
                ll.setBackgroundDrawable(drawable);


                LinearLayout ll_base = (LinearLayout)findViewById(R.id.linearLayout_product);
                ll_base.addView(ll);
            }
        }
        // VIEW를 만들어서 보여준다.
    }


    /////////////////////////////////////////////
    // 웹서버로 근처 스티커 MAJOR 값 전송해주는 스레드
    ////////////////////////////////////////////
    class NearStickersTransferThread extends Thread
    {
        @Override
        public void run()
        {
            Log.d(Constants.QUADCORE_LOG,"CartCheckActivity : NearStickersTransferThread : start");
            ///////////////////////////////////////////////////
            // WEB 서버와 연결 - 서버에게 설치된 비콘 위치 전송, 존위치 전송
            /////////////////////////////////////////////////


            String majors="majors=";
            for(int i=0;i<nearStickers.size();i++) {
                if (i == 0) {
                    majors += "" + nearStickers.get(i);
                } else {
                    majors += "," + nearStickers.get(i);
                }
            }
            Log.d(Constants.QUADCORE_LOG,"CartCheckActivity : majors : "+ majors);

            ///////////////////////////////////////
            // 서버 전송
            //////////////////////////////////////
            try
            {
                ServerInfo.url = new URL("http://" + ServerInfo.serverIP + ":" + ServerInfo.serverPort
                        + "/SpringMVC/selectProductInfo.do?"
                        + majors);

                // 전송
                ServerInfo.urlConnection = (HttpURLConnection) ServerInfo.url.openConnection();
                // 타임아웃
                ServerInfo.urlConnection.setConnectTimeout(Constants._SERVER_TIMEOUT);
                // 결과
                int responseCode = ServerInfo.urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK)
                {
                    server_response = ServerInfo.readStream(ServerInfo.urlConnection.getInputStream());
                    ///////////////////////
                    // Log & Toast
                    //////////////////////////
                    Log.d(Constants.QUADCORE_LOG, "SERVER RESPONSE : SELECT PRODUCT INFO : SUCCESS:"+server_response+","+server_response.length());


                }
                else
                {
                    ///////////////////////
                    // Log & Toast
                    //////////////////////////
                    Log.d(Constants.QUADCORE_LOG, "SERVER RESPONSE : SELECT PRODUCT INFO  : ERROR CODE :"+responseCode);
                }
            }
            catch(SocketTimeoutException ste)
            {
                Log.d(Constants.QUADCORE_LOG, "SERVER CONNECTION FAILED : SocketTimeoutException");
                popupToast("SERVER CONNECTION FAILED");
            }
            catch(ConnectException e)
            {
                Log.d(Constants.QUADCORE_LOG,"SERVER CONNECTION FAILED : ConnectException");
            }
            catch(Exception e)
            {
                Log.d(Constants.QUADCORE_LOG,"BeaconPositionTransferThread : EXCEPTION");
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
                Toast.makeText(CartCheckActivity.this, msg,Toast.LENGTH_SHORT).show();
            }
        }, 0);
    }
}
