package com.quadcore.Utils;

/**
 * Created by bbong on 2016-10-26.
 */

public class Constants {

    public static final String QUADCORE_LOG = "QUADCORE_LOG";

    // 결제 존 체크 횟수
    public static final int _PAYMENT_ZONE_DURATION = 10;
    // 텍스트 사이즈
    public static final int _VIEW_CM_TEXT_SIZE = 40;
    // viewWidth, vieHeight Text 위치
    public static final int viewWidthX=250;
    public static final int viewWidthY=-50;
    public static final int viewHeightX=50;
    public static final int viewHeightY=350;

    ///////////////////////////////
    // 지오펜스 사각형 크기 지정
    ///////////////////////////////
    public static final int viewWidth = 605;

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
    public static final float _SCALE_FACTOR=5;
    // 비콘 초당 스캔 (ms)
    public static final int _SCAN_PERIOD_MILLIS = 10;
    // 최신 비콘 값 유지 개수(큐 크기)
    public static final int _RECENT_QUEUE_SIZE=10;
    ///////////////////////////////////////////////////
    // 비콘 상수들
    ///////////////////////////////////////////////////
    public static final String UUID_BEACON = "b9407f30-f5f8-466e-aff9-25556b57fe6d";
    public static final int POWER_BEACON = -78;

    public static final int MAJOR_BEACON_1 = 34245;
    public static final int MAJOR_BEACON_2 = 13669;
    public static final int MAJOR_BEACON_3 = 26687;
    public static final int MAJOR_BEACON_4 = 10008;


    public static final int MINOR_BEACON_1 = 46938;
    public static final int MINOR_BEACON_2 = 245;
    public static final int MINOR_BEACON_3 = 45732;

    // 룸 타입
    public static final int _ROOM_TYPE_CUSTOMIZE = 0;
    public static final int _ROOM_TYPE_RECTANGLE_12_7 = 1;
    public static final int _ROOM_TYPE_TRIANGLE_6_6 = 2;
    public static final int _ROOM_TYPE_TRIANGLE_3_3 = 3;
    public static final int _ROOM_TYPE_RECTANGLE_6_6 = 4;

}
