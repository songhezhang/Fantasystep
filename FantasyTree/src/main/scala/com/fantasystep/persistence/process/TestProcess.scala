package com.fantasystep.persistence.process

import scala.reflect.runtime._
import scala.tools.reflect.ToolBox
import java.io.File
import com.googlecode.scalascriptengine.{Config, _}
import com.fantasystep.persistence.utils.DynamicScalaSourceCodeObject
import java.nio.file.Files
import java.nio.file.Paths

import scala.collection.JavaConversions._

object TestProcess extends App {
//	val cm = universe.runtimeMirror(getClass.getClassLoader)
//	val tb = cm.mkToolBox()
	val code = "package com.fantasystep.persistence.process;import org.json.JSONObject;class AAAProcess extends ProcessTrait {def process(name: String, input: JSONObject) = {println('a');new JSONObject();}}"
//	val a = tb.eval(tb.parse("package com.fantasystep.persistence.process;import org.json.JSONObject;class AAAProcess extends ProcessTrait {def process(name: String, input: JSONObject) = new JSONObject()}"))
//	val b = 1
	// the source directory
	Files.write(Paths.get("./scalacode/AAAProcess.scala"), code.getBytes());
	val sourceDir: File = new File("./scalacode/AAAProcess.scala")
	// compilation classpath
	val compilationClassPath = ScalaScriptEngine.currentClassPath
	// runtime classpath (empty). All other classes are loaded by the parent classloader
	val runtimeClasspath = Set[File]()
	// the output dir for compiled classes
	val outputDir = new File(System.getProperty("java.io.tmpdir"), "scala-script-engine-classes")
	outputDir.mkdir

	val sse = new ScalaScriptEngine(Config(
		List(SourcePath(Set(sourceDir), outputDir)),
		compilationClassPath,
		runtimeClasspath
	)) with RefreshAsynchronously with FromClasspathFirst
	{
		val recheckEveryMillis: Long = 1000 // each file will only be checked maximum once per second
	}

	// delete all compiled classes (i.e. from previous runs)
	sse.deleteAllClassesInOutputDirectory
	// since the refresh occurs async, we need to do the 1st refresh otherwise initially my.TryMe
	// class will not be found
	sse.refresh

	while (true) {
		val t = sse.newInstance[ProcessTrait]("com.fantasystep.persistence.process.AAAProcess")
		println(t)
		println("code version %d, result : %s".format(sse.versionNumber, t.process(null, null)))
		Thread.sleep(500)
	}
}