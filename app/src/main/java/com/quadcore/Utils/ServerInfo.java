package com.quadcore.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by bbong on 2016-10-25.
 */

/////////////////////////////////////////////////////////
// 서버정보
/////////////////////////////////////////////////////////
public class ServerInfo {
    //////////////////////////////////////
    // 테스트모드 / 서버모드
    //////////////////////////////////////
    public static boolean isConnected = true;

    public static URL url;
    public static HttpURLConnection urlConnection = null;
    public static String serverIP="192.168.43.97";
    public static String serverPort="8080";
    public static final String paymentUrl = "http://"+serverIP+":"+serverPort+"/SpringMVC/payment.do";


    public static String readStream(InputStream in)
    {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}
