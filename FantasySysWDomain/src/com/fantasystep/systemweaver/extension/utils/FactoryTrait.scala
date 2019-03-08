package com.fantasystep.systemweaver.extension.utils

import scala.collection.JavaConversions.asScalaBuffer

import autosar40.genericstructure.generaltemplateclasses.arpackage.ARPackage
import autosar40.genericstructure.generaltemplateclasses.identifiable.Identifiable
import gautosar.ggenericstructure.ginfrastructure.GARPackage
import autosar40.genericstructure.generaltemplateclasses.admindata.AdminData
import autosar40.util.Autosar40Factory

trait FactoryTrait {
  def createARPackage(shortName: String, parent: GARPackage = null): ARPackage = {
    val arPackage: ARPackage = Autosar40Factory.eINSTANCE.createARPackage
    if(parent != null)
      parent.gGetSubPackages().add(arPackage)
    arPackage.setShortName(shortName)
    arPackage
  }
  
  def getHandleIdFromAdminData(element: Identifiable): Option[String] = {
    for (
      adminData <- Option(element.getAdminData);
      sdg <- adminData.getSdgs.find { e => e.getGid == "SystemWeaverData" } if sdg.getSdgContentsType != null;
      sd <- sdg.getSdgContentsType.getSds.collectFirst { case sd if sd.getGid == "Handle" => sd.getValue }
    ) yield sd
  }
  
  def createAdminData(target: Identifiable, data: (String, Seq[(String, String)])): AdminData = {
    val adminData = target.getAdminData match {
      case adminData: AdminData => adminData
      case null =>
        target.setAdminData(Autosar40Factory.eINSTANCE.createAdminData)
        target.getAdminData
    }
    data match {
      case (sdgGid, sds) =>
        val sdgContentsType = adminData.getSdgs.collectFirst { case sdg if sdg.getGid == sdgGid => sdg } match {
          case Some(sdg) => sdg.getSdgContentsType
          case None =>
            val sdg = Autosar40Factory.eINSTANCE.createSdg
            adminData.getSdgs.add(sdg)
            sdg.setGid(sdgGid)
            val sdgContentsType = Autosar40Factory.eINSTANCE.createSdgContents
            sdg.setSdgContentsType(sdgContentsType)
            sdgContentsType
        }
        sds.foreach {
          case (sdGid, value) =>
            val sd = Autosar40Factory.eINSTANCE.createSd
            sdgContentsType.getSds.add(sd)
            sd.setGid(sdGid)
            sd.setValue(value)
        }
    }
    adminData
  }
}