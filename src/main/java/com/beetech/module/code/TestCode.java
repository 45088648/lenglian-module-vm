package com.beetech.module.code;

import com.beetech.module.code.request.ReadDataRequest;
import com.beetech.module.code.response.QueryConfigResponse;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.code.response.SetTimeResponse;
import com.beetech.module.utils.ByteUtilities;
import com.beetech.module.utils.CRC16;
import com.beetech.module.utils.CRC16_ITU;

import java.util.Arrays;

public class TestCode {

    public static void main(String[] args){
        System.out.print("Hello world!");
        String hex = "acac3d0711180403005500041812121555380cfc1010101800045d031e613520630e640b75621812111554006718121115490168b6650779661849020000000084b6caca";
        byte[] buf = ByteUtilities.asByteArray(hex);
        BaseResponse response = ResponseFactory.unpack(buf);
        System.out.println(response);

        if(response instanceof ReadDataResponse){
            ReadDataResponse readDataResponse = (ReadDataResponse)response;
            System.out.println(readDataResponse.getTemp());
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
