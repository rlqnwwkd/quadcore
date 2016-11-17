package com.quadcore.Mock;

import android.util.Log;

import com.quadcore.Room_Activity.GeofenceSettingsActivity_Customize;
import com.quadcore.Utils.Constants;
import com.quadcore.Utils.PaymentZone;
import com.quadcore.Utils.Point3D;


public class MockServer {

    // 서버의 geofence 테이블 역할
    public static GeofenceDTO geofence;

    ////////////////////////////////////////////
    // 목 geofence 테이블 생성 - customize
    ////////////////////////////////////////
    public static void insertGeofence(){
        geofence = new GeofenceDTO();

        geofence.setBC1X(GeofenceSettingsActivity_Customize.bc1Position.getX());
        geofence.setBC1Y(GeofenceSettingsActivity_Customize.bc1Position.getY());

        geofence.setBC2X(GeofenceSettingsActivity_Customize.bc2Position.getX());
        geofence.setBC2Y(GeofenceSettingsActivity_Customize.bc2Position.getY());

        geofence.setBC3X(GeofenceSettingsActivity_Customize.bc3Position.getX());
        geofence.setBC3Y(GeofenceSettingsActivity_Customize.bc3Position.getY());

        geofence.setBC4X(GeofenceSettingsActivity_Customize.bc4Position.getX());
        geofence.setBC4Y(GeofenceSettingsActivity_Customize.bc4Position.getY());

        geofence.setId(Constants.MAJOR_BEACON_1);
        geofence.setName("counter");

        geofence.setZONE_X1(PaymentZone.actual_leftUp.getX());
        geofence.setZONE_Y1(PaymentZone.actual_leftUp.getY());
        geofence.setZONE_X2(PaymentZone.actual_rightDown.getX());
        geofence.setZONE_Y2(PaymentZone.actual_rightDown.getY());



        geofence.setType(Constants._ROOM_TYPE_CUSTOMIZE);

        Log.d(Constants.QUADCORE_LOG,"MockServer : insertGeofence : ("+geofence.getBC1X()+","+geofence.getBC1Y()+"),("
                +geofence.getBC2X()+","+geofence.getBC2Y()+"),("
                +geofence.getBC3X()+","+geofence.getBC3Y()+"),("
                +geofence.getBC4X()+","+geofence.getBC4Y()+"),("
                +geofence.getZONE_X1()+","+geofence.getZONE_Y1()+"),("
                +geofence.getZONE_X2()+","+geofence.getZONE_Y2()+")");
    }

    ////////////////////////////////////////////
    // 목 geofence 테이블 생성 - type 별
    //////////////////////////////////////////////
    public static void insertGeofence_type(Point3D bc1,Point3D bc2,Point3D bc3,Point3D bc4, Point3D zoneLeftTop,Point3D zoneRightDown, int roomType){
        geofence = new GeofenceDTO();

        geofence.setBC1X(bc1.getX());
        geofence.setBC1Y(bc1.getY());

        geofence.setBC2X(bc2.getX());
        geofence.setBC2Y(bc2.getY());

        geofence.setBC3X(bc3.getX());
        geofence.setBC3Y(bc3.getY());

        geofence.setBC4X(bc4.getX());
        geofence.setBC4Y(bc4.getY());

        geofence.setId(Constants.MAJOR_BEACON_1);
        geofence.setName("counter");

        geofence.setZONE_X1(zoneLeftTop.getX());
        geofence.setZONE_Y1(zoneLeftTop.getY());
        geofence.setZONE_X2(zoneRightDown.getX());
        geofence.setZONE_Y2(zoneRightDown.getY());

        geofence.setType(roomType);

        Log.d(Constants.QUADCORE_LOG,"MockServer : insertGeofence_type : ("+geofence.getBC1X()+","+geofence.getBC1Y()+"),("
                +geofence.getBC2X()+","+geofence.getBC2Y()+"),("
                +geofence.getBC3X()+","+geofence.getBC3Y()+"),("
                +geofence.getBC4X()+","+geofence.getBC4Y()+"),("
                +geofence.getZONE_X1()+","+geofence.getZONE_Y1()+"),("
                +geofence.getZONE_X2()+","+geofence.getZONE_Y2()+")");
    }

    // 테이블 삭제
    public static void deleteGeofence(){

        geofence = null;
        Log.d(Constants.QUADCORE_LOG,"MockServer : deleteGeofence : SUCCESS");
    }

    // 지오펜스 가져오기
    public static String selectGeofence(long major){
        String response = "";
        long id = major;
        GeofenceDTO geofenceDTO = geofence;


        if(geofenceDTO == null)
        {
            response="null";
        }
        else
        {
            response=geofenceDTO.getBC1X()+","+geofenceDTO.getBC1Y()
                    +","+geofenceDTO.getBC2X()+","+geofenceDTO.getBC2Y()
                    +","+geofenceDTO.getBC3X()+","+geofenceDTO.getBC3Y()
                    +","+geofenceDTO.getBC4X()+","+geofenceDTO.getBC4Y()
                    +","+geofenceDTO.getName()
                    +","+geofenceDTO.getId()
                    +","+geofenceDTO.getZONE_X1()+","+geofenceDTO.getZONE_Y1()
                    +","+geofenceDTO.getZONE_X2()+","+geofenceDTO.getZONE_Y2()
                    +","+geofenceDTO.getType();

        }

        Log.d(Constants.QUADCORE_LOG,"MockServer : selectGeofence : "+response);
        return response;
    }



}
