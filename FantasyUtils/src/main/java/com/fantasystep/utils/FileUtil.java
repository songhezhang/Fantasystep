package com.fantasystep.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.Files;

public class FileUtil {
	public static List<String> getLines(File file) {
		List<String> list = new ArrayList<String>();
		try {
			list = Files.readLines(file, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
}
