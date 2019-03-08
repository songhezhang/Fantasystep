package com.fantasystep.systemweaver;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import com.fantasystep.annotation.ControlType;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FantasyStep;
import com.fantasystep.annotation.FantasyView;
import com.fantasystep.annotation.Storage;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.PropertyGroups;

import autosar40.genericstructure.generaltemplateclasses.arpackage.ARPackage;
import autosar40.system.fibex.fibex4ethernet.ethernettopology.EthernetPhysicalChannel;
import autosar40.util.Autosar40Factory;
import autosar40.util.Autosar40ResourceFactoryImpl;
import gautosar.ggenericstructure.ginfrastructure.GAUTOSAR;

@DomainClass(label = "LABEL_SYSW_MODEL", icon = "dropline.png")
public class SysWModel extends Node {

	private static final long serialVersionUID = -7531081644215153874L;
	
	@FantasyStep(storage = Storage.MONGO)
	@FantasyView(controlType = ControlType.TEXTBOX, order = 1, group = PropertyGroups.BASE_PROPERTY, label = "LABEL_NAME")
	private String name = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getLabel() {
		return this.name;
	}
	
	public static void main(String[] args) {
		GAUTOSAR gautosar = Autosar40Factory.eINSTANCE.createAUTOSAR();
		EthernetPhysicalChannel obj = Autosar40Factory.eINSTANCE.createEthernetPhysicalChannel();
		obj.setShortName("aaaaa");
		ARPackage p = Autosar40Factory.eINSTANCE.createARPackage();
		p.setShortName("p");
		ARPackage p2 = Autosar40Factory.eINSTANCE.createARPackage();
		p2.setShortName("p2");
		p.getArPackages().add(p2);
//		EObject e = p2.eContainer();
		gautosar.gGetArPackages().add(p);
		
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put("*", new Autosar40ResourceFactoryImpl());

        // Obtain a new resource set
        ResourceSet resSet = new ResourceSetImpl(); //new AutosarResourceSetImpl();//

        // create a resource
        Resource resource = resSet.createResource(URI
                .createURI("website/My2.website"));
        resource.getContents().add(gautosar);
//        Resource rrrr = resSet.getResource(URI
//                .createURI("website/My2.website"), true);
//        rrrr.getContents();
//        
//        Resource resource2 = resourceSet.createResource
//    		  (URI.createURI("website/data.json"));
//
//		resource2.getContents().add(p2);
////		ObjectMapper mapper = new ObjectMapper();
//		try {
//			resource2.save(Collections.EMPTY_MAP);
//		} catch (JsonProcessingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
        try {
            resource.save(System.out, Collections.EMPTY_MAP);
            
            
//            Resource.Factory.Registry reg2 = Resource.Factory.Registry.INSTANCE;
//    		Map<String, Object> m2 = reg2.getExtensionToFactoryMap();
//            m2.put("arxml", new Autosar40ResourceFactoryImpl());
//            ResourceSet resSet2 = new ResourceSetImpl();
//            Resource resource2 = resSet2.createResource(URI
//                    .createURI("website/ModifiedMergedSystem.arxml"));
            
//			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl() {
//				public Resource createResource(URI uri) {
//					XMIResource xmiResource = new XMIResourceImpl(uri);
//					return xmiResource;
//				}
//			});
//			Resource resource3 = EcorePlatformUtil.getResource(URI
//                    .createURI("website/SPA2510_ASDMARGUS218_170707_AR-4.2.2_Unflattened_WithSparePNC.arxml"));
//            Resource resource2 = resSet.getResource(URI
//                    .createURI("website/SPA2510_ASDMARGUS218_170707_AR-4.2.2_Unflattened_WithSparePNC.arxml"), true);
//            resource2.load(Collections.EMPTY_MAP);
            
//            ModelDescriptorRegistry.INSTANCE.getModel(null);
            System.out.println();
        } catch (IOException ee) {
            // TODO Auto-generated catch block
            ee.printStackTrace();
        }
        
//		URI targetURI = EcorePlatformUtil.createURI(null);
//		
//		EcoreResourceUtil.saveNewModelResource(new ScopingResourceSetImpl(), targetURI, "", obj, null);
		System.out.println(obj);
	}
}
