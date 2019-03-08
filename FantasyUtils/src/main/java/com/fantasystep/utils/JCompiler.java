package com.fantasystep.utils;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCompiler {
	

	private static Logger logger = LoggerFactory.getLogger(JCompiler.class);
	static String sourceCode = "package com.fantasystep.domain;"
			+ "public class Userr extends Node {" + "private String firstName;"
			+ "}";
	private static final String TEMP = ".";
	private static JCompiler jCompiler = null;
	
	public static JCompiler getInstance() {
		if(jCompiler == null)
			jCompiler = new JCompiler();
		return jCompiler;
	}
	private JCompiler() {
		getClassPath();
	}
	
	public static void test() {
		logger.info(JCompiler.class.getClassLoader().toString());
		JCompiler compiler = JCompiler.getInstance();
		Class<?> user1 = compiler.registerClass("com.fantasystep.domain.Userr", sourceCode);
		// printAllProperties(Class.forName("domain.User"));
		sourceCode = "package com.fantasystep.domain;"
				+ "public class Userr extends Node {"
				+ "private String firstName;" + "private String afterName;"
				+ "}";
		Class<?> user2 = compiler.registerClass("com.fantasystep.domain.Userr", sourceCode);
		Object obj1;
		try {
			obj1 = user1.newInstance();
		
			Field field1 = user1.getDeclaredField("firstName");
			field1.setAccessible(true);
			Object obj2 = user2.newInstance();
			Field field2 = user2.getDeclaredField("firstName");
			field2.setAccessible(true);
			field1.set(obj1, "songhe");
			field2.set(obj2, "ruixi");

			logger.info(obj1 + "songhe--------------------------");
			logger.info(obj2 + "ruxi");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException {
		
		test();
		// printAllProperties(Class.forName("domain.User"));
//		compiler.registerClass("com.fantasystep.domain.User",
//				new File("domain"));
	}

	public static void printAllProperties(Class<?> clazz) {
		if (clazz.getSuperclass() != null)
			printAllProperties(clazz.getSuperclass());
	}

	public static List<Field> getAllProperties(Class<?> clazz) {
		List<Field> list = new ArrayList<Field>();
		list.addAll(Arrays.asList(clazz.getDeclaredFields()));
		if (clazz.getSuperclass() != null)
			list.addAll(getAllProperties(clazz.getSuperclass()));
		return list;
	}

	public Class<?> registerClass(String key, String code) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(key, code);
		return doCompilation(key, map, null);
	}

	public Class<?> registerClass(String key, Map<String, String> codes) {
		return doCompilation(key, codes, null);
	}

	public Class<?> registerClass(String key, File file) {
		return doCompilation(key, null, file);
	}

	public Class<?> registerClass(String key, Map<String, String> codes, File file) {
		return doCompilation(key, codes, file);
	}

	public Class<?> registerClass(String key, String code, File file) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(key, code);
		return doCompilation(key, map, file);
	}
	
	private Class<?> doCompilation(String key, Map<String, String> codes, File file) {
		List<JavaFileObject> compilationUnits = new ArrayList<JavaFileObject>();
		if (codes != null)
			for (Entry<String, String> entry : codes.entrySet())
				compilationUnits
						.add(new DynamicJavaSourceCodeObject(entry.getKey(), entry.getValue()));
		File[] javaFiles = null;
		if (file != null) {
			if (file.isDirectory())
				javaFiles = file.listFiles(new FilenameFilter() {
					public boolean accept(File file, String name) {
						return name.endsWith(Kind.SOURCE.extension);
					}
				});
			else
				javaFiles = file.getName().endsWith(Kind.SOURCE.extension) ? new File[] { file }
						: null;
		}

		StandardJavaFileManager fileManager = null;
		JavaCompiler javaCompiler = null;

		try {
			javaCompiler = ToolProvider.getSystemJavaCompiler();
			String[] compileOptions = new String[] { "-d", TEMP };
			Iterable<String> compilationOptions = Arrays.asList(compileOptions);

			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			fileManager = javaCompiler.getStandardFileManager(diagnostics,
					Locale.getDefault(), Charset.forName("UTF-8"));

			if (javaFiles != null) {
				Iterator<? extends JavaFileObject> iterator = fileManager
						.getJavaFileObjectsFromFiles(Arrays.asList(javaFiles))
						.iterator();
				while (iterator.hasNext())
					compilationUnits.add(iterator.next());
			}
			javaCompiler.getTask(null, fileManager, diagnostics,
					compilationOptions, null, compilationUnits).call();

			boolean error = false;
			for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
				error = true;
				logger.info("Error on line " + diagnostic.getLineNumber() + " in " + diagnostic.getSource() + " " + diagnostic.getMessage(null));
			}
			if(error)
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileManager.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			File tmpFile = new File(TEMP);
			URL url = tmpFile.toURI().toURL();
			@SuppressWarnings("resource")
			ClassLoader cl = new CustomClassLoader(url);
			Class<?> clazz = cl.loadClass(key);
			printAllProperties(clazz);
			return clazz;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String classPath;

	private String getClassPath() {
		if (classPath == null) {
			String d = null;
			if (System.getProperty("os.name").contains("Windows"))
				d = ";";
			else
				d = ":";
			if (!System.getProperty("java.class.path").contains(
					this.getClass().getClassLoader().getResource("").getPath())) {
				
				StringBuilder currentPath = new StringBuilder(this.getClass()
						.getClassLoader().getResource("").getPath());
				logger.info(currentPath + "../lib");
				File dir = new File(currentPath + "../../applications/persistence-1.0.0/WEB-INF/lib");
				if (dir.listFiles() != null)
					for (File f : dir.listFiles())
						currentPath.append(d).append(f.getAbsolutePath());
				System.setProperty("java.class.path",
						System.getProperty("java.class.path") + d + currentPath);
			} else { 
				//Production environment
				StringBuilder sb = new StringBuilder("/opt/glassfish5/glassfish/domains/domain1/applications/persistence-1.0.0/WEB-INF/lib");
				File dir = new File(sb.toString());
				if (dir.listFiles() != null)
					for (File f : dir.listFiles())
						sb.append(d).append(f.getAbsolutePath());
				System.setProperty("java.class.path",
						System.getProperty("java.class.path") + d + sb);
			}
			classPath = System.getProperty("java.class.path");
			logger.info("Java Class Path : " + classPath);
		}
		return classPath;
	}
}

class CustomClassLoader extends URLClassLoader {
	
	private static Logger logger = LoggerFactory.getLogger(CustomClassLoader.class);
	
	public CustomClassLoader(URL url) {
		super(new URL[] { url }, null);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		try {
			return super.loadClass(name, resolve);
		} catch (ClassNotFoundException e) {
			return Class.forName(name, resolve,
					JCompiler.class.getClassLoader());
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		logger.info(this.toString() + " - CL Finalized.");
	}
}

class DynamicJavaSourceCodeObject extends SimpleJavaFileObject {
	private String qualifiedName;
	private String sourceCode;

	protected DynamicJavaSourceCodeObject(String name, String code) {
		super(URI.create("string:///" + name.replaceAll("\\.", "/")
				+ Kind.SOURCE.extension), Kind.SOURCE);
		this.qualifiedName = name;
		this.sourceCode = code;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors)
			throws IOException {
		return sourceCode;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}
}