package com.fantasystep.persistence.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Modifier;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.fantasystep.domain.Node;
import com.fantasystep.persistence.TreeManager;
import com.fantasystep.utils.NodeClassUtil;

public class TreeManagerHolderGenerator extends Task{
	@SuppressWarnings("unused")
	private Project project;

    public void setProject(Project proj) {
        project = proj;
    }

	@Override
	public void execute() throws BuildException {
		generateHolder("TreeManagerSubclassHolder", TreeManager.class);
	}
	
	public void generateHolder(String name, Class<?> clazz) {
		StringBuffer sb = new StringBuffer();
		sb.append("package com.fantasystep.persistence;\n\n");
		for(Class<? extends Node> nodeClass : NodeClassUtil.getSubClassesInJVM(Node.class))
			if(!Modifier.isAbstract(nodeClass.getModifiers()))
				sb.append("import ").append(nodeClass.getCanonicalName()).append(";\n");
//		Reflections reflections = new Reflections("autosar40");
//		Set<Class<? extends GARObject>> set = reflections.getSubTypesOf(GARObject.class);
//		for(Class<? extends GARObject> eObject : set)
//			if(!Modifier.isAbstract(eObject.getModifiers()))
//				sb.append("import ").append(eObject.getCanonicalName()).append(";\n");
		sb.append("\n");
		sb.append("import javax.jws.WebService;\n");
		sb.append("import javax.xml.bind.annotation.XmlSeeAlso;\n\n");
		sb.append("import ").append(clazz.getCanonicalName()).append(";\n\n");
		sb.append("@WebService\n");
		sb.append("@XmlSeeAlso({");
		boolean first = true;
		for(Class<? extends Node> nodeClass : NodeClassUtil.getSubClassesInJVM(Node.class))
			if(Modifier.isAbstract(nodeClass.getModifiers()))
				continue;
			else if(first) {
				sb.append(nodeClass.getSimpleName()).append(".class");
				first = false;
			} else
				sb.append(", ").append(nodeClass.getSimpleName()).append(".class");
//		for(Class<? extends GARObject> eObject : set)
//			if(Modifier.isAbstract(eObject.getModifiers()) || Modifier.isInterface(eObject.getModifiers()))
//				continue;
//			else if(first) {
//				sb.append(eObject.getCanonicalName()).append(".class");
//				first = false;
//			} else
//				sb.append(", ").append(eObject.getCanonicalName()).append(".class");
		sb.append("})\n");
		sb.append("public interface " + name + " extends " + clazz.getSimpleName() + " {\n\n}\n");

		String path = new File("").getAbsolutePath();
		if (System.getProperty("os.name").contains("Windows"))
			path += "\\src\\main\\java\\com\\fantasystep\\persistence\\";
		else
			path += "/src/main/java/com/fantasystep/persistence/";

		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(path + name + ".java"),
					"utf-8"));
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
//		project.log(sb.toString());
	}

	public static void main(String[] args) {
		new TreeManagerHolderGenerator().generateHolder("TreeManagerSubclassHolder", TreeManager.class);
	}
}
