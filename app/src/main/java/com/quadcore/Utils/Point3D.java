package com.quadcore.Utils;

/**
 * Created by bbong on 2016-10-22.
 */

//////////////////////////////////////////////////////////
// 위치를 표현할 클래스
//////////////////////////////////////////////////////////
public class Point3D {
    private float x;
    private float y;
    private float z;
    private float r;
    private int zoneNumber;

    public void setZoneNumber(int n)
    {
        this.zoneNumber=n;
    }

    public int getZoneNumber()
    {
        return zoneNumber;
    }
    public Point3D(){ 	}
    public Point3D(float x, float y )
    {
        this.x = x;
        this.y = y;
        this.z= 0;
    }
    public Point3D(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Point3D(float x, float y,float z, float r)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r=r;
    }


    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }

    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }
    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }
    public float getZ() {
        return z;
    }
    public void setZ(float z) {
        this.z = z;
    }
}