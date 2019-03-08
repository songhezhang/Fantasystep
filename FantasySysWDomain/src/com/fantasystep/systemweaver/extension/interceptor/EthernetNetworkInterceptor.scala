package com.fantasystep.systemweaver.extension.interceptor

import com.fantasystep.systemweaver.extension.utils.FactoryTrait
import com.fantasystep.systemweaver.extension.Interceptor
import com.fantasystep.systemweaver.item.EthernetNetwork
import gautosar.ggenericstructure.ginfrastructure.GARPackage
import com.fantasystep.systemweaver.item.EPlatform
import org.eclipse.emf.ecore.EObject
import com.fantasystep.systemweaver.extension.ContextObject

class EthernetNetworkInterceptor extends Interceptor[EthernetNetwork] with FactoryTrait {
  def process(obj: EthernetNetwork, context: ContextObject, passedContext: EObject*) : Seq[_ <: EObject] = {
    val ethernetPackage      = createARPackage(obj.getName, context.rootPackage())
    
    val communicationPackage = createARPackage("communications", context.rootPackage())
    Seq(ethernetPackage)
  }
}