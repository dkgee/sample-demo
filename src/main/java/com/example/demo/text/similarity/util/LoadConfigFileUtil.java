package com.example.demo.text.similarity.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;

public class LoadConfigFileUtil {
	private static final Logger logger = Logger.getLogger(LoadConfigFileUtil.class);

	public static InputStream loadConfigFile(Class<?> cls, String file) {
		try {
			// 得到类的类装载器
			ClassLoader loader = cls.getClassLoader();

			// 1.先从当前类所处路径的根目录中寻找属性文件
			File configFile = new File(file);
			if(configFile.exists()){
				logger.info("配置文件存在");
			}
			InputStream in = loader.getResourceAsStream(file);
			if (in != null) {
				return in;
			}

			// 2.如果没有找到，再从当前系统的用户目录中进行查找
			File f = null;
			String curDir = System.getProperty("user.dir");
			f = new File(curDir, file);
			if (f.exists()) {
				return new FileInputStream(f);
			}
			// 3.如果没有找到，再从当前系统的用户目录中的config下进行查找
			File fconfig = null;
			String curDirConfig = System.getProperty("user.dir")
					+ File.separator + "config" + File.separator;
			fconfig = new File(curDirConfig, file);
			if (fconfig.exists()) {
				return new FileInputStream(fconfig);
			}
			// 4.没有找到，就从该类所处的包目录中查找属性文件
			Package pack = cls.getPackage();
			if (pack != null) {
				String packName = pack.getName();
				String path = "";
				if (packName.indexOf(".") < 0)
					path = packName + File.separator;
				else {
					int start = 0, end = 0;
					end = packName.indexOf(".");
					while (end != -1) {
						path = path + packName.substring(start, end)
								+ File.separator;
						start = end + 1;
						end = packName.indexOf(".", start);
					}
					path = path + packName.substring(start) + File.separator;
				}
				in = loader.getResourceAsStream(path + file);
				if (in != null)
					return in;
			}

			// 5.如果还是没有找到，则从系统所有的类路径中查找
			String classpath = System.getProperty("java.class.path");
			String[] cps = classpath
					.split(System.getProperty("path.separator"));

			for (int i = 0; i < cps.length; i++) {
				f = new File(cps[i], file);
				if (f.exists())
					break;
				f = null;
			}
			if (f != null) {
				return new FileInputStream(f);
			}
			logger.error("配置文件未找到：" + file);
			return null;
		} catch (Exception e) {
			logger.error("加载配置文件出错：" + e.getMessage());
			throw new RuntimeException(e);
		}

	}

	public static File loadResourceFile(Class<?> cls, String file) {
		try {
			String path = "";
			File f = null;
			// 得到类的类装载器
			ClassLoader loader = cls.getClassLoader();

			// 1.先从当前类所处路径的根目录中寻找属性文件
			// InputStream in = loader.getResourceAsStream(file);
			URL url = loader.getResource(file);
			if (url != null) {
				path = loader.getResource(file).getPath();
				return new File(path);
			}

			// 2.如果没有找到，再从当前系统的用户目录中进行查找

			String curDir = System.getProperty("user.dir") + File.separator;
			f = new File(curDir, file);

			if (f.exists()) {
				return f;
			} else {
				curDir = System.getProperty("user.dir") + File.separator
						+ "res" + File.separator;
				f = new File(curDir, file);
				if (f.exists()) {
					return f;
				}
			}
		} catch (Exception e) {
			logger.error("加载配置文件出错：" + e.getMessage());
			throw new RuntimeException(e);
		}
		return null;
	}
}
