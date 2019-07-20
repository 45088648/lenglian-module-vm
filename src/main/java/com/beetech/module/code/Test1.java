package com.beetech.module.code;

import com.beetech.module.code.response.DeleteHistoryDataResponse;
import com.beetech.module.code.response.QueryConfigResponse;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.code.response.SetDataBeginTimeResponse;
import com.beetech.module.code.response.SetTimeResponse;
import com.beetech.module.code.response.UpdateConfigResponse;
import com.beetech.module.code.response.UpdateSSParamResponse;

public class Test1 {
    public static void main(String[] args){
        int cmd = 0x9C;
        switch (cmd) {
            case 1:

            case 2:

            case 3:

            case 4:

            case 7:

            case 9:

            case 0x9C:
                System.out.println(0x9c);

            default:
        }
    }
}
