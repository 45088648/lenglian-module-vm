package com.beetech.module.bean.vt;

import com.beetech.module.code.response.ReadDataResponse;
import java.util.ArrayList;
import java.util.List;

public class ShtrfRequestBean extends VtRequestBean{

	private ShtrfRequestBeanBody body ;

	public ShtrfRequestBean() {
		setCmd("SHTRF");
	}
	public ShtrfRequestBean(ReadDataResponse readDataResponse) {
		this();
		setId(readDataResponse.get_id());
		this.body = new ShtrfRequestBeanBody(readDataResponse);
	}

	public ShtrfRequestBean(List<ReadDataResponse> readDataResponseList) {
		this();
		this.body = new ShtrfRequestBeanBody();
		List<ShtrfData> data = new ArrayList<>();

		for (ReadDataResponse readDataResponse:readDataResponseList) {
			ShtrfData shtrfData = new ShtrfData(readDataResponse);
			data.add(shtrfData);
		}

		this.body.setData(data);
	}

	public ShtrfRequestBeanBody getBody() {
		return body;
	}

	public void setBody(ShtrfRequestBeanBody body) {
		this.body = body;
	}
}
