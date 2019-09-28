package com.beetech.module.code;

import com.beetech.module.code.request.ReadDataRequest;
import com.beetech.module.code.response.QueryConfigResponse;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.code.response.SetTimeResponse;
import com.beetech.module.utils.ByteUtilities;

public class TestCode {

    public static void main(String[] args){
        System.out.print("Hello world!");
        String hex = "ACAC3D0711185350005500581909280945370D27101055430F035D031E610013631A640CA2621909280945006719092809452568D2650B166614E50100000000BA1ECACA";
        byte[] buf = ByteUtilities.asByteArray(hex);
        BaseResponse response = ResponseFactory.unpack(buf);
        System.out.println(response);

        if(response instanceof ReadDataResponse){
            ReadDataResponse readDataResponse = (ReadDataResponse)response;
            System.out.println(readDataResponse.getSensorId());
            System.out.println(readDataResponse.getTemp());
            System.out.println(readDataResponse.getSensorDataTime());
//            System.out.println(readDataResponse.getCrc());

        } else if (response instanceof QueryConfigResponse){
            QueryConfigResponse queryConfigResponse = (QueryConfigResponse)response;
        } else if(response instanceof SetTimeResponse){
            SetTimeResponse setTimeResponse = (SetTimeResponse)response;
        }

//        System.out.println(Integer.valueOf("2B53", 16));
//
//        String hexCrc = "ACAC0E04111804030811061812151343022B53CACA";
//        buf = ByteUtilities.asByteArray(hexCrc);
//        byte[] bufCrc = Arrays.copyOfRange(buf, 3, 17);
//        System.out.println(ByteUtilities.asHex(bufCrc));
//        System.out.println(CRC16.getCrc(bufCrc)- '0');
//        System.out.println(Integer.valueOf("10", 16)*256+Integer.valueOf("00", 16));
//      2B53
        String gwId = "00000000";
        int serialNo = 0;
        ReadDataRequest readDataRequest = new ReadDataRequest(gwId, 0, serialNo);
        readDataRequest.pack();
        byte[] readDataRequestBuf = readDataRequest.getBuf();
        String readDataRequestBufHex = ByteUtilities.asHex(readDataRequestBuf);
        System.out.println(readDataRequestBufHex.toUpperCase());

    }
}
