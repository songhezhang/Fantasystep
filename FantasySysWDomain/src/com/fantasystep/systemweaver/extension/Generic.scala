package com.fantasystep.systemweaver.extension

import java.lang.reflect.Modifier

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.asScalaSet
import scala.reflect.ClassTag
import scala.reflect.classTag

import org.eclipse.emf.ecore.EObject
import org.reflections.Reflections

import com.fantasystep.systemweaver.SysWDomain
import com.fantasystep.systemweaver.annotation.SystemWeaver
import com.fantasystep.systemweaver.annotation.SystemWeaverDomain

import gautosar.ggenericstructure.ginfrastructure.GARPackage
import autosar40.util.Autosar40Factory
import autosar40.genericstructure.generaltemplateclasses.arpackage.ARPackage

trait ReporterTrait {
  val reportLevel: Int
  def reportFatal  (msg: String) : Unit = if(reportLevel <= 1) println(s"Fatal   : $msg")
  def reportError  (msg: String) : Unit = if(reportLevel <= 2) println(s"Error   : $msg")
  def reportWarning(msg: String) : Unit = if(reportLevel <= 3) println(s"Warning : $msg")
  def reportInfo   (msg: String) : Unit = if(reportLevel <= 4) println(s"Info    : $msg")
  def reportDebug  (msg: String) : Unit = if(reportLevel <= 5) println(s"Debug   : $msg")
}

class Singleton[A >: Null <: AnyRef] {
  private[this] var _a = null: A
  def :=(a: A) { if (_a eq null) _a = a else throw new IllegalStateException }
  def apply() = if (_a eq null) throw new IllegalStateException else _a
}

case class ContextObject(
  val rootSysWDomain : Singleton[SysWDomain]      = new Singleton[SysWDomain],
  val rootPackage: Singleton[ARPackage]           = new Singleton[ARPackage],
  val ethernetNetwork: Singleton[EObject]         = new Singleton[EObject],
  val transformationPackage: Singleton[ARPackage] = new Singleton[ARPackage],
  val baseTypePackage: Singleton[ARPackage]       = new Singleton[ARPackage],
  val swcTypePackage: Singleton[ARPackage]        = new Singleton[ARPackage]
)

object ContextObject {
  def apply(root: SysWDomain): ContextObject = { val context = new ContextObject; context.rootSysWDomain := root; context }
}

abstract class Interceptor[T <: SysWDomain : ClassTag](val reporter: ReporterTrait = Generic.reporter) {
  protected[extension] def applyCondition(obj: T): Boolean = true
  private[extension] def getDomainIdentifier: Class[_ <: SysWDomain] = classTag[T].runtimeClass.asInstanceOf[Class[_ <: SysWDomain]]
  def before (obj: T, context: ContextObject, passedContext: EObject*) : Unit = Unit
  def process(obj: T, context: ContextObject, passedContext: EObject*) : Seq[_ <: EObject]
  def after  (obj: T, context: ContextObject, passedContext: EObject*) : Unit = Unit
}

object Generic {
  private[extension] val reporter = new { val reportLevel = 3 } with ReporterTrait
  
  private val reflections = new Reflections("com.fantasystep.systemweaver.extension.interceptor")
  private val allAttributeClasses = reflections.getSubTypesOf(classOf[Interceptor[_]])
  private val concreteAttributeClasses = allAttributeClasses.filter { klass =>
    !Modifier.isAbstract(klass.getModifiers)
  }

  private val customProcesses: Map[Class[_ <: SysWDomain], Seq[Interceptor[_ <: SysWDomain]]] = {
    concreteAttributeClasses.map { c => val interceptor = c.newInstance.asInstanceOf[Interceptor[_ <: SysWDomain]]; 
                                        (interceptor.getDomainIdentifier, interceptor)}.groupBy(_._1).mapValues(x => x.map(_._2).toSeq)
  }
  
  private def getSysWAnnotation(clazz: Class[_]): SystemWeaverDomain = {
    Option(clazz.getAnnotation(classOf[SystemWeaverDomain]))
        .getOrElse(getSysWAnnotation(clazz.getSuperclass))
  }
                                        
  def start(root: SysWDomain): Unit = root.create(ContextObject(root))
                                        
  implicit class GenericDomain[+T <: SysWDomain : ClassTag](obj: T) {
    
    private[Generic] def create(context: ContextObject, passedContext: EObject*): Unit = {
      customProcesses.get(obj.getClass) match {
        case Some(interceptors) => interceptors.find { x => x.asInstanceOf[Interceptor[T]].applyCondition(obj) } match {
          case Some(interceptor: Interceptor[T]) =>
            interceptor.before(obj, context, passedContext: _*)
            val newPassedContext = interceptor.process(obj, context, passedContext: _*)
            obj.getChildren.map(_.asInstanceOf[SysWDomain])
                           .sortBy(d => getSysWAnnotation(d.getClass).handlingOrder)
                           .foreach(_.create(context, newPassedContext: _*))
            interceptor.after(obj, context, passedContext: _*)
          case _ => reporter.reportError(s"Missing Fatal business logic on ${obj.getClass.getName} for special Applied Condition.")
        }
        case _ => reporter.reportError(s"Missing Fatal business logic on ${obj.getClass.getName}.")
      }
    }
    
    def contains(clazz: Class[_ <: SysWDomain]): Boolean = {
      if (obj.getChildren(clazz).size() > 1) true
      else {
        reporter.reportError(s"${obj.getName} should contain ${clazz.getName}.")
        false
      }
    }
    
    def containsOnlyOne(clazz: Class[_ <: SysWDomain]): Boolean = {
      if (obj.getChildren(clazz).size() == 1) true
      else {
        reporter.reportError(s"${obj.getName} should only contain one ${clazz.getName}.")
        false
      }
    }
  }
//  val autosarFactory: Autosar40Factory = Autosar40Factory.eINSTANCE
//  val m = autosarFactory.createPduToFrameMapping()
//  import Test2._
//  m.withStartPosition(1).withStartPosition(1).withStartPosition(2)
}

//import autosar40.util.Autosar40Factory
//import autosar40.system.fibex.fibexcore.corecommunication.PduToFrameMapping
//import src.WithTrait
//import src.hello
//@hello 
//object Test2 extends App {
//  println(this.hellooo)
//  
//  @WithTrait
//  implicit class PduToFrameMappingExtension(pduToFrameMapping: PduToFrameMapping) {
//  }
//  val a = Autosar40Factory.eINSTANCE.createPduToFrameMapping
//  a.withStartPosition(1).withStartPosition(1).withStartPosition(2)
//  println(a.getStartPosition)      
//}