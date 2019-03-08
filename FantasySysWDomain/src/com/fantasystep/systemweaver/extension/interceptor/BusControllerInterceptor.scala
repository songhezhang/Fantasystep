package com.fantasystep.systemweaver.extension.interceptor

import gautosar.ggenericstructure.ginfrastructure.GARPackage
import com.fantasystep.systemweaver.item.BusController
import com.fantasystep.systemweaver.extension.utils.FactoryTrait
import com.fantasystep.systemweaver.extension.Interceptor
import org.eclipse.emf.ecore.EObject
import com.fantasystep.systemweaver.itemenum.BusType
import com.fantasystep.systemweaver.extension.ContextObject

class CanBusControllerInterceptor extends Interceptor[BusController] with FactoryTrait {
  override protected[extension] def applyCondition(obj: BusController): Boolean = obj.getBusType == BusType.CAN
  def process(obj: BusController, context: ContextObject, passedContext: EObject*) : Seq[_ <: EObject] = {
    val communicationPackage = createARPackage("communications", context.rootPackage())
    Seq(communicationPackage)
  }
}

class EthernetBusControllerInterceptor extends Interceptor[BusController] with FactoryTrait {
  override protected[extension] def applyCondition(obj: BusController): Boolean = obj.getBusType == BusType.ETHERNET
  def process(obj: BusController, context: ContextObject, passedContext: EObject*) : Seq[_ <: EObject] = {
    val communicationPackage = createARPackage("communications", context.rootPackage())
    Seq(communicationPackage)
  }
}