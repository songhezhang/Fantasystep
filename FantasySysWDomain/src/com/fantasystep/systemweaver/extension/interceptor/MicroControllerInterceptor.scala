package com.fantasystep.systemweaver.extension.interceptor

import gautosar.ggenericstructure.ginfrastructure.GARPackage
import com.fantasystep.systemweaver.extension.Interceptor
import org.eclipse.emf.ecore.EObject
import com.fantasystep.systemweaver.item.MicroController
import com.fantasystep.systemweaver.extension.utils.FactoryTrait
import com.fantasystep.systemweaver.extension.ContextObject

class MicroControllerInterceptor extends Interceptor[MicroController] with FactoryTrait {
  override def before (obj: MicroController, context: ContextObject, passedContext: EObject*) : Unit = println("Before MicroController")
  def process(obj: MicroController, context: ContextObject, passedContext: EObject*) : Seq[_ <: EObject] = {
    Seq()
  }
  override def after (obj: MicroController, context: ContextObject, passedContext: EObject*) : Unit = println("After MicroController")
}