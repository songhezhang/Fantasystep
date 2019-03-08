package com.fantasystep.persistence.utils

import javax.tools.SimpleJavaFileObject
import javax.tools.JavaFileObject.Kind
import java.net.URI

class DynamicScalaSourceCodeObject(name: String, code: String)
    extends SimpleJavaFileObject(URI.create("string:///" + name.replaceAll("\\.", "/") + Kind.SOURCE.extension), Kind.SOURCE) {

  var qualifiedName: String = name

  var sourceCode: String = code

  override def getCharContent(ignoreEncodingErrors: Boolean): CharSequence = sourceCode
}