package com.fantasystep.systemweaver.extension.interceptor

import gautosar.ggenericstructure.ginfrastructure.GARPackage
import com.fantasystep.systemweaver.extension.Interceptor
import org.eclipse.emf.ecore.EObject
import com.fantasystep.systemweaver.item.EcuSw
import com.fantasystep.systemweaver.extension.utils.FactoryTrait
import com.fantasystep.systemweaver.extension.ContextObject

class EcuSwInterceptor extends Interceptor[EcuSw] with FactoryTrait {
  override def before (obj: EcuSw, context: ContextObject, passedContext: EObject*) : Unit = println("Before EcuSw")
  def process(obj: EcuSw, context: ContextObject, passedContext: EObject*) : Seq[_ <: EObject] = {
    Seq()
  }
  override def after (obj: EcuSw, context: ContextObject, passedContext: EObject*) : Unit = println("After EcuSw")
}