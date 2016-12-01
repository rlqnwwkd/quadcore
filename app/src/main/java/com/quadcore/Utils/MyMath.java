package com.quadcore.Utils;

import com.quadcore.MainActivity;
import com.quadcore.Services.RssiToDist;

import java.util.List;

public class MyMath {

    public static double getAvg(List<Integer> list)
    {
        double avg = 0.0;
        for(int value : list)
        {
            avg += value;
        }
        avg = avg / list.size();
        return avg;
    }

    public static double getCMeterFromRssi(double avgRssi)
    {
        return (Math.pow(10, (Constants.POWER_BEACON-avgRssi)/20.0 ))*100;
    }

    // 공식 / 매핑 사용해서 거리 얻음
    public static double getDistUsingFormulaOrMapping(double avgRssi, int type)
    {
        double dist = 0.0;
        // 공식
        if(MainActivity.isUsingFormula)
        {
            dist = MyMath.getCMeterFromRssi(avgRssi);
        }
        // 맵핑
        else
        {
            dist = RssiToDist.getDistance(avgRssi, type);
        }
        return dist;
    }
}
