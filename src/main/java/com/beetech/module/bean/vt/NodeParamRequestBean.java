package com.beetech.module.bean.vt;

import com.beetech.module.bean.ReadDataRealtime;
import java.util.List;

public class NodeParamRequestBean extends VtRequestBean {

	private static final long serialVersionUID = 5888934478699815699L;

	private NodeParamRequestBody body;

	public NodeParamRequestBean() {
		setCmd("NODEPARAM");
	}
	public NodeParamRequestBean(List<ReadDataRealtime> readDataRealtimeList) {
		this();
		body = new NodeParamRequestBody(readDataRealtimeList);
	}

	public NodeParamRequestBody getBody() {
		return body;
	}

	public void setBody(NodeParamRequestBody body) {
		this.body = body;
	}
}
