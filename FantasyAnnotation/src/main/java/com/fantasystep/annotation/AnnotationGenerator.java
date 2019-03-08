package com.fantasystep.annotation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

public class AnnotationGenerator extends Task {

	@SuppressWarnings("unused")
	private Project project;

	public void setProject(Project proj) {
		project = proj;
	}

	@Override
	public void execute() throws BuildException {
		accessorGenerator("FieldAttributeAccessor", FantasyStep.class,
				FantasyView.class);
		accessorGenerator("DomainAttributeAccessor", DomainClass.class);
	}

	public void accessorGenerator(String name, Class<?>... classes) {
		StringBuffer sb = new StringBuffer();
		sb.append("package com.fantasystep.annotation;\n\n");
		// sb.append("import java.lang.reflect.Field;\n");
		// sb.append("import java.util.List;\n\n");
		// sb.append("import com.fantasystep.domain.Node;\n");
		// sb.append("import com.fantasystep.utils.Storage;\n\n");
		sb.append("public class " + name + " {\n\n");
		for (Class<?> clazz : classes)
			for (Method m : clazz.getDeclaredMethods()) {
				sb.append("    private ").append(getTypeString(m)).append(" ")
						.append(m.getName()).append(";\n");
				sb.append("\n");
			}
		for (Class<?> clazz : classes)
			for (Method m : clazz.getDeclaredMethods()) {
				sb.append("    public ")
						.append(getTypeString(m))
						.append(" ")
						.append("get")
						.append(Character.toString(m.getName().charAt(0))
								.toUpperCase())
						.append(m.getName().substring(1)).append("() {\n");
				sb.append("        return ").append(m.getName()).append(";\n");
				sb.append("    }\n\n");
			}
		for (Class<?> clazz : classes)
			for (Method m : clazz.getDeclaredMethods()) {
				sb.append("    public void set")
						.append(Character.toString(m.getName().charAt(0))
								.toUpperCase())
						.append(m.getName().substring(1)).append("(")
						.append(getTypeString(m)).append(" ")
						.append(m.getName()).append(") {\n")
						.append("        this.").append(m.getName())
						.append(" = ").append(m.getName()).append(";\n");
				sb.append("    }\n\n");
			}

		sb.append("}\n");

		String path = new File("").getAbsolutePath();
		if (System.getProperty("os.name").contains("Windows"))
			path += "\\src\\main\\java\\com\\fantasystep\\annotation\\";
		else
			path += "/src/main/java/com/fantasystep/annotation/";

		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(path + name + ".java"), "utf-8"));
			writer.write(sb.toString());
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		// project.log(sb.toString());
	}

	private String getTypeString(Method m) {
		if (m.getReturnType().getSimpleName().endsWith("Class")) {
			if (m.getDefaultValue() == null
					|| ((Class<?>) m.getDefaultValue()).getCanonicalName()
							.equals("void"))
				return "java.lang.Class<?>";
			else
				return "java.lang.Class<? extends "
						+ (((Class<?>) m.getDefaultValue()).getCanonicalName()
								.equals("java.lang.Enum") ? "java.lang.Enum<?>"
								: ((Class<?>) m.getDefaultValue())
										.getCanonicalName()) + ">";
		} else if (m.getReturnType().getSimpleName().endsWith("Class[]"))
			return "java.lang.Class<?>[]";
		else
			return m.getReturnType().getCanonicalName();
	}

//	public static void main(String[] args) {
//		new AnnotationGenerator().accessorGenerator("FieldAttributeAccessor",
//				FantasyStep.class, FantasyView.class, SystemWeaver.class);
//		new AnnotationGenerator().accessorGenerator("DomainAttributeAccessor",
//				DomainClass.class);
//	}
}
