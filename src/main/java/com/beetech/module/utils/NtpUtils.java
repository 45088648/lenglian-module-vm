package com.beetech.module.utils;

import android.util.Log;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class NtpUtils {
    private final static String TAG = NtpUtils.class.getSimpleName();

    public static final String host = "ntp2.aliyun.com";//NTP时间服务器
    public static final int port = 123;
    public static final int timeout = 3000;

    public static long getTimeInMills(){
        long ret = 0;
        DatagramSocket socket = null;
        int serviceStatus = -1;
        try {


            // get the address and NTP address request
            InetAddress ipv4Addr = InetAddress.getByName(host);


            long responseTime = -1;
            socket = new DatagramSocket();
            socket.setSoTimeout(timeout); // will force the
            // InterruptedIOException

            // Send NTP request
            //
            byte[] data = new NtpMessage().toByteArray();
            DatagramPacket outgoing = new DatagramPacket(data, data.length, ipv4Addr, port);
            long sentTime = System.currentTimeMillis();
            socket.send(outgoing);

            // Get NTP Response
            // byte[] buffer = new byte[512];
            DatagramPacket incoming = new DatagramPacket(data, data.length);
            socket.receive(incoming);
            responseTime = System.currentTimeMillis() - sentTime;
            double destinationTimestamp = (System.currentTimeMillis() / 1000.0) + 2208988800.0;
            //这里要加2208988800，是因为获得到的时间是格林尼治时间，所以要变成东八区的时间，否则会与与北京时间有8小时的时差

            // Validate NTP Response
            // IOException thrown if packet does not decode as expected.
            NtpMessage msg = new NtpMessage(incoming.getData());
            double localClockOffset = ((msg.receiveTimestamp - msg.originateTimestamp) + (msg.transmitTimestamp - destinationTimestamp)) / 2;
            double utc = msg.transmitTimestamp-2208988800.0;
            ret = (long)(utc*1000L);
            Log.d(TAG, DateUtils.getCurrentDateStr(DateUtils.C_YYYY_MM_DD_HH_MM_SS_SSS)+"："+ret);
            Log.d(TAG, DateUtils.getCurrentDateStr(DateUtils.C_YYYY_MM_DD_HH_MM_SS_SSS)+" poll: valid NTP request received the local clock offset is " + localClockOffset + ", responseTime= " + responseTime + "ms");
            Log.d(TAG, DateUtils.getCurrentDateStr(DateUtils.C_YYYY_MM_DD_HH_MM_SS_SSS)+" poll: NTP message : " + msg.toString());
            serviceStatus = 1;

            // Store response time if available
            //
            if (serviceStatus == 1) {
//            System.out.println("responsetime=="+responseTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null){
                socket.close();
            }
        }

        return ret;
    }

    public static void main(String[] args){
        System.out.println("getTimeInMills=="+getTimeInMills());
        System.out.println("getTimeInMills=="+DateUtils.parseDateToString(new Date(getTimeInMills()), DateUtils.C_YYYY_MM_DD_HH_MM_SS_SSS));
    }
}
