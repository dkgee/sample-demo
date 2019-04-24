package com.example.demo.text.similarity.entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Properties;

import com.example.demo.text.similarity.util.LoadConfigFileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


/**
 * 摘要参数
 * 
 * @author Jinhuatao
 * 
 */
public class SimilarityParam implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SimilarityParam.class);

	// 分词模式
	private static boolean maxMatch;
	// 停用词路径
	private static String stopWordPath;
	// 过滤词
	private static List<String> stopWord;
	// 相似度阀值
	private static double threshold;
	// 小点保留位数
	private static int decimalPointPlaces;
	// 数据格式化器对象
	private static NumberFormat numberFormat;
	// 统计分析线程数
	private static int countThreadNum;
	// 相似性计算线程数
	private static int calculateThreadNum;
	// 相似性线程等待时间(毫秒)
	private static int sleepTime;
	static {
		init();
	}

	/**
	 * 初始化数据
	 * 
	 * @return
	 */
	private static void init() {
		Properties p = new Properties();
		InputStream is;
		try {
			String configPath = File.separator + "similarity" + File.separator + "similarity.properties";
			configPath = "\\src\\main\\resources" + configPath;
			is = LoadConfigFileUtil.loadConfigFile(SimilarityParam.class, configPath);
			p.load(is);
			maxMatch = Boolean.parseBoolean(p.getProperty("maxMatch", "false"));
			threshold = Double.parseDouble(p.getProperty("threshold", "0"));
			decimalPointPlaces = Integer.parseInt(p.getProperty("decimalPointPlaces", "5"));
			numberFormat = NumberFormat.getInstance();
			// 设置最小保留小数位
			numberFormat.setMinimumFractionDigits(decimalPointPlaces);
			// 设置最大保留小数位
			numberFormat.setMaximumFractionDigits(decimalPointPlaces);

			countThreadNum = Integer.parseInt(p.getProperty("countThreadNum",
					"10"));
			calculateThreadNum = Integer.parseInt(p.getProperty(
					"calculateThreadNum", "6"));
			sleepTime = Integer.parseInt(p.getProperty("sleepTime", "6"));

			stopWordPath = p.getProperty("stopWordPath", "");
			File file = LoadConfigFileUtil.loadResourceFile(
					SimilarityParam.class, stopWordPath);
			stopWord = FileUtils.readLines(file);
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("配置文件未找到：", e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("配置文件读取错误:", e);
		}
	}

	public static boolean isMaxMatch() {
		return maxMatch;
	}

	public static void setMaxMatch(boolean maxMatch) {
		SimilarityParam.maxMatch = maxMatch;
	}

	public static String getStopWordPath() {
		return stopWordPath;
	}

	public static void setStopWordPath(String stopWordPath) {
		SimilarityParam.stopWordPath = stopWordPath;
	}

	public static List<String> getStopWord() {
		return stopWord;
	}

	public static void setStopWord(List<String> stopWord) {
		SimilarityParam.stopWord = stopWord;
	}

	public static double getThreshold() {
		return threshold;
	}

	public static void setThreshold(double threshold) {
		SimilarityParam.threshold = threshold;
	}

	public static int getDecimalPointPlaces() {
		return decimalPointPlaces;
	}

	public static void setDecimalPointPlaces(int decimalPointPlaces) {
		SimilarityParam.decimalPointPlaces = decimalPointPlaces;
	}

	public static NumberFormat getNumberFormat() {
		return numberFormat;
	}

	public static void setNumberFormat(NumberFormat numberFormat) {
		SimilarityParam.numberFormat = numberFormat;
	}

	public static int getCountThreadNum() {
		return countThreadNum;
	}

	public static void setCountThreadNum(int countThreadNum) {
		SimilarityParam.countThreadNum = countThreadNum;
	}

	public static int getCalculateThreadNum() {
		return calculateThreadNum;
	}

	public static void setCalculateThreadNum(int calculateThreadNum) {
		SimilarityParam.calculateThreadNum = calculateThreadNum;
	}

	public static int getSleepTime() {
		return sleepTime;
	}

	public static void setSleepTime(int sleepTime) {
		SimilarityParam.sleepTime = sleepTime;
	}

}
