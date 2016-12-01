package com.quadcore.Utils;

/**
 * Created by bbong on 2016-10-26.
 */

public class Constants {

    ///////////////////////////////////////////////////
    // 비콘 상수들
    ///////////////////////////////////////////////////
    public static final String UUID_BEACON = "b9407f30-f5f8-466e-aff9-25556b57fe6d";

    //비콘 스티커
    public static final int MAJOR_STICKER_1 = 51348;
    public static final int MAJOR_STICKER_2 = 56968;

    //최신 비콘 4

    public static final int MAJOR_BEACON_1 = 864;
    public static final int MAJOR_BEACON_2 = 13669;
    public static final int MAJOR_BEACON_3 = 26687;
/*

    // 옛날 비콘 3
    public static final int MAJOR_BEACON_1 = 46032;
    public static final int MAJOR_BEACON_2 = 50438;
    public static final int MAJOR_BEACON_3 = 32253;
*/

    // 4변 보류
    public static final int MAJOR_BEACON_4 = 123;

    // immediate
    public static final int _BC3_IMMEDIATE = 67;
    public static final int _BC2_IMMEDIATE = 67;
    public static final int _BC1_IMMEDIATE = 67;

    // far
    public static final int _BC3_FAR = 77;
    public static final int _BC2_FAR = 77;
    public static final int _BC1_FAR = 77;


    public static final String QUADCORE_LOG = "QUADCORE_LOG";

    // 서버 타임아웃
    public static final int _SERVER_TIMEOUT = 15000;

    // 결제 존 체크 횟수
    public static final int _PAYMENT_ZONE_DURATION = 10;
    // 텍스트 사이즈
    public static final int _VIEW_CM_TEXT_SIZE = 40;
    // viewWidth, vieHeight Text 위치
    public static final int viewWidthX=250;
    public static final int viewWidthY=-50;
    public static final int viewHeightX=50;
    public static final int viewHeightY=350;

    //power
    public static final int POWER_BEACON=-62;

    ///////////////////////////////
    // 지오펜스 사각형 크기 지정
    ///////////////////////////////
    // 605
    public static final int viewWidth = 1000;

    /////////////////////////////
    // 비콘 모니터링 주기
    //////////////////////////////
    public static final int _MONITORING_PERIOD_MILLIS = 3000;
    //////////////////////////////////////////
    // 사용자 위치 업데이트 주기 설정 (ms)
    ///////////////////////////////////////////
    public static final int _INTERVAL_USER_POSITION_UPDATE = 300;
    // 비콘 그릴때 원 크기
    public static final float _BC_RADIUS = 20;
    // (0,0) 좌표 지정 상수
    public static final float _SCALE_FACTOR=9;
    // 비콘 초당 스캔 (ms)
    public static final int _SCAN_PERIOD_MILLIS = 1;
    // 최신 비콘 값 유지 개수(큐 크기)
    public static final int _RECENT_QUEUE_SIZE=10;







    // 룸 타입
    public static final int _ROOM_TYPE_CUSTOMIZE = 0;
    public static final int _ROOM_TYPE_RECTANGLE_12_7 = 1;
    public static final int _ROOM_TYPE_TRIANGLE_6_6 = 2;
    public static final int _ROOM_TYPE_TRIANGLE_3_3 = 3;
    public static final int _ROOM_TYPE_RECTANGLE_6_6 = 4;
    public static final int _ROOM_TYPE_Paldal1 = 5;


}
