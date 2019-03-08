package com.fantasystep.systemweaver.extension.interceptor

import org.eclipse.emf.ecore.EObject

import com.fantasystep.systemweaver.extension.Generic._
import com.fantasystep.systemweaver.extension.Interceptor
import com.fantasystep.systemweaver.extension.utils.FactoryTrait
import com.fantasystep.systemweaver.item.EPlatform
import com.fantasystep.systemweaver.item.EthernetNetwork

import autosar40.genericstructure.generaltemplateclasses.arpackage.ARPackage
import gautosar.ggenericstructure.ginfrastructure.GARPackage
import com.fantasystep.systemweaver.extension.ContextObject
import com.fantasystep.systemweaver.extension.Generic

class EPlatformInterceptor extends Interceptor[EPlatform] with FactoryTrait {
  
  override def before (obj: EPlatform, context: ContextObject, passedContext: EObject*) : Unit = println("Before EPlatform")
  
  def process(obj: EPlatform, context: ContextObject, passedContext: EObject*) : Seq[_ <: EObject] = {
    context.rootPackage           := createARPackage(obj.getName)
    context.transformationPackage := createARPackage("Transformers", context.rootPackage())
    context.baseTypePackage       := createARPackage("BaseTypes", context.rootPackage())
    context.swcTypePackage        := createARPackage("SwComponentTypes", context.rootPackage())
    obj containsOnlyOne classOf[EthernetNetwork]
    Seq(context.rootPackage())
  }
  
  override def after (obj: EPlatform, context: ContextObject, passedContext: EObject*) : Unit = println("After EPlatform")
}