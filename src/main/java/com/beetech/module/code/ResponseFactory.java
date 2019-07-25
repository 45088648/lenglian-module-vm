package com.beetech.module.code;

import com.beetech.module.code.response.DeleteHistoryDataResponse;
import com.beetech.module.code.response.QueryConfigResponse;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.code.response.SetDataBeginTimeResponse;
import com.beetech.module.code.response.SetTimeResponse;
import com.beetech.module.code.response.UpdateConfigResponse;
import com.beetech.module.code.response.UpdateSSParamResponse;
import com.beetech.module.utils.ByteUtilities;

public class ResponseFactory {

	public static BaseResponse getResponse(byte[] buf) {
		if(buf == null || buf.length < 4) {
			return null;
		}
		int cmd = ByteUtilities.toUnsignedInt(buf[3]);
		switch (cmd) {
			case 1:
				return new QueryConfigResponse(buf);

			case 2:
				return new UpdateConfigResponse(buf);

			case 3:
				return new DeleteHistoryDataResponse(buf);
				
			case 4:
				return new SetTimeResponse(buf);
				
			case 7:
				return new ReadDataResponse(buf);

			case 9:
				return new SetDataBeginTimeResponse(buf);

			case 0x9C:
				return new UpdateSSParamResponse(buf);
	
			default:
				return null;
		}
	}
	
	public static BaseResponse unpack(byte[] buf) {
		BaseResponse response = getResponse(buf);
		if(response != null) {
			response.unpack();
		}
		
		return response;
	}
}
