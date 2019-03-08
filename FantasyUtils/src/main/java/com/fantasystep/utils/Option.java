package com.fantasystep.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Option {

	private static String fileName;
	private static List<AbstractOption<?>> options = new ArrayList<AbstractOption<?>>();

	public static void setConfigFileName(String fileName) {
		Option.fileName = fileName;
		options.clear();
	}

	public static void load() {
		File file = new File(fileName);
		if (file.exists())
			return;

		int endindex = 0;
		if (System.getProperty("os.name").contains("Windows")) 
			endindex = fileName.lastIndexOf("\\");
		else endindex = fileName.lastIndexOf("/");
		try {
			new File(fileName.substring(0, endindex)).mkdirs();
		} catch(Exception e) {
		}
		
		StringBuffer sb = new StringBuffer();
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "utf-8"));
			for(AbstractOption<?> option : options) {
				if(option.getDescription() != null && !option.getDescription().isEmpty())
					sb.append("# ").append(option.getDescription()).append("\n");
				if(option.isIfCommentOut())
					sb.append("# ").append(option.key()).append("=").append(option.value()).append("\n");
				sb.append(option.key()).append("=").append(option.value()).append("\n\n");
			}
			
			writer.write(sb.toString());
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
			}
		}
		options.clear();
	}

	public static abstract class AbstractOption<T> {
		private String key;
		private T value;
		private boolean ifCommentOut;
		private String description;

		public AbstractOption(String key, T value, boolean ifCommentOut,
				String description) throws InvalidOptionFormatException {
			if(key == null || value == null || key.isEmpty() || value == null)
				throw new InvalidOptionFormatException();
			this.setKey(key);
			this.setValue(value);
			this.setIfCommentOut(ifCommentOut);
			this.setDescription(description);
			options.add(this);
		}

		public String key() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public T value() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}

		public boolean isIfCommentOut() {
			return ifCommentOut;
		}

		public void setIfCommentOut(boolean ifCommentOut) {
			this.ifCommentOut = ifCommentOut;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}

	public static class StringOption extends AbstractOption<String> {
		public StringOption(String key, String value, boolean ifCommentOut,
				String description) throws InvalidOptionFormatException {
			super(key, value, ifCommentOut, description);
		}
	}

	public static class BooleanOption extends AbstractOption<Boolean> {
		public BooleanOption(String key, boolean value, boolean ifCommentOut,
				String description) throws InvalidOptionFormatException {
			super(key, value, ifCommentOut, description);
		}
	}

	public static class FileOption extends AbstractOption<File> {
		public FileOption(String key, File value, boolean ifCommentOut,
				String description) throws InvalidOptionFormatException {
			super(key, value, ifCommentOut, description);
		}
	}

	public static class IntegerOption extends AbstractOption<Integer> {
		public IntegerOption(String key, Integer value, boolean ifCommentOut,
				String description) throws InvalidOptionFormatException {
			super(key, value, ifCommentOut, description);
		}
	}

	public static class FloatOption extends AbstractOption<Float> {
		public FloatOption(String key, float value, boolean ifCommentOut,
				String description) throws InvalidOptionFormatException {
			super(key, value, ifCommentOut, description);
		}
	}
	
	public static class URLOption extends AbstractOption<URL> {
		public URLOption(String key, String value, boolean ifCommentOut,
				String description) throws InvalidOptionFormatException, MalformedURLException {
			super(key, new URL(value), ifCommentOut, description);
		}
	}
	
	public static class InvalidOptionFormatException extends Exception {
		private static final long serialVersionUID = -4858726633778939863L;
	}
}
