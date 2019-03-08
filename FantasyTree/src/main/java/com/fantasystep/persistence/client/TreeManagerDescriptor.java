package com.fantasystep.persistence.client;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import com.fantasystep.persistence.TreeManagerSubclassHolder;
import com.fantasystep.utils.ConstantUtil;

public class TreeManagerDescriptor {
	private URL serviceAddress;
	private TreeManagerWSService service;
	private String sessionKey;

	public URL getServiceAddress() {
		return serviceAddress;
	}

	public void setServiceAddress(URL serviceAddress) {
		this.serviceAddress = serviceAddress;
		this.service = new TreeManagerWSService(this.serviceAddress);
	}
	
	public TreeManagerSubclassHolder getProxy() {
		if(this.sessionKey != null) {
			Map<String, List<String>> requestHeaders = new HashMap<String, List<String>>();
			requestHeaders.put(ConstantUtil.SESSION_KEY, Arrays.asList(this.sessionKey));
			((BindingProvider)this.service.getTreeManagerPort()).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, requestHeaders);
		}
		((BindingProvider)this.service.getTreeManagerPort()).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", 60000);
		return this.service.getTreeManagerPort();
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
}
