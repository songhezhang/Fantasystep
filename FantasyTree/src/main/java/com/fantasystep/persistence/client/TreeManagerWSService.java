package com.fantasystep.persistence.client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import com.fantasystep.persistence.TreeManagerSubclassHolder;

@WebServiceClient(name = "FantasyStepTreeManagerService", targetNamespace = "http://persistence.fantasystep.com/", wsdlLocation = "http://192.168.59.103:8080/persistence-1.0.0/cxf/TreeManagerService?wsdl")
public class TreeManagerWSService extends Service {
	private final static URL TREE_MANAGER_WSDL_LOCATION;
	static {
		URL url = null;
		try {
			url = new URL("http://192.168.59.103:8080/persistence-1.0.0/cxf/TreeManagerService?wsdl");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		TREE_MANAGER_WSDL_LOCATION = url;
	}
	
	public TreeManagerWSService() {
		super(TREE_MANAGER_WSDL_LOCATION, new QName(
				"http://persistence.fantasystep.com/", "FantasyStepTreeManagerService"));
	}

	public TreeManagerWSService(URL url) {
		super(url, new QName(
				"http://persistence.fantasystep.com/", "FantasyStepTreeManagerService"));
	}

	@WebEndpoint(name = "TreeManagerPort")
	public TreeManagerSubclassHolder getTreeManagerPort() {
		return (TreeManagerSubclassHolder) super.getPort(new QName(
				"http://persistence.fantasystep.com/", "FantasyStepTreeManagerPort"),
				TreeManagerSubclassHolder.class);
	}

	@WebEndpoint(name = "TreeManagerPort")
	public TreeManagerSubclassHolder getTreeManagerPort(WebServiceFeature... features) {
		return (TreeManagerSubclassHolder) super.getPort(new QName(
				"http://persistence.fantasystep.com/", "FantasyStepTreeManagerPort"),
				TreeManagerSubclassHolder.class, features);
	}
	
	public static void main(String[] args) {
//		TreeManagerWSService service = new TreeManagerWSService();
//		try {
//			boolean a = service.getTreeManagerPort().activate(UUID.randomUUID());
//			System.out.println(a);
//		} catch (PersistenceException e) {
//			e.printStackTrace();
//		}
	}
}
