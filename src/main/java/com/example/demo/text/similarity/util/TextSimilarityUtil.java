package com.example.demo.text.similarity.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.example.demo.text.similarity.core.*;
import com.example.demo.text.similarity.entity.SimilarityParam;
import com.example.demo.text.similarity.entity.Text;
import org.apache.log4j.Logger;

/**
 * 文本相似度公共接口
 * 
 * @author JinHuatao
 * 
 */
public class TextSimilarityUtil {
	private static final Logger logger = Logger
			.getLogger(TextSimilarityUtil.class);

	/**
	 * 计算文本相似度
	 * 
	 * @param text
	 * @param texts
	 * @return
	 */
	public static Text similarity(Text text, List<Text> texts) {
		TextSimilarity.init(text);
		double similarity = 0;
		List<String> ids = new ArrayList<String>();
		for (Text temp : texts) {
			TextSimilarity.init(temp);
		}

		long begin = System.currentTimeMillis();

		for (Text txt : texts) {
			similarity = TextSimilarity.calculateSimilary(text, txt);
			if (similarity > SimilarityParam.getThreshold()) {
				ids.add(txt.getId());
			}
		}
		long end = System.currentTimeMillis();
		logger.info("统计耗时：" + (end - begin));
		text.setIds(ids);
		return text;
	}

	/**
	 * 计算文本相似度
	 * 
	 * @param text
	 * @param texts
	 * @return
	 */
	public static Text similarityMutil(Text text, List<Text> texts) {
		TextSimilarity.init(text);
		ShareObject syncStack = new ShareObject();
		syncStack.setText(text);
		syncStack.setSyncStack(new Vector<Text>(texts));
		syncStack.setTotal(texts.size());
		logger.info("启动" + SimilarityParam.getCountThreadNum() + "个统计分析线程...");
		for (int i = 0; i < SimilarityParam.getCountThreadNum(); i++) {
			CountThread countThread = new CountThread(syncStack);
			Thread thread = new Thread(countThread, countThread.getClass()
					.getSimpleName() + i);
			thread.start();
		}
		logger.info("启动" + SimilarityParam.getCalculateThreadNum()
				+ "个相似性计算线程...");
		for (int i = 0; i < SimilarityParam.getCalculateThreadNum(); i++) {
			CalculateThread calculateThread = new CalculateThread(syncStack);
			Thread thread = new Thread(calculateThread, calculateThread
					.getClass().getSimpleName() + i);
			thread.start();
		}
		while (true) {
			if (syncStack.getCountThread() == SimilarityParam
					.getCountThreadNum()
					&& syncStack.getCalculateThread() == SimilarityParam
							.getCalculateThreadNum()) {
				break;
			} else {
				try {
					Thread.sleep(SimilarityParam.getSleepTime());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return text;
	}

	/**
	 * 计算文本相似度
	 * 
	 * @param texts
	 * @param texts
	 * @return
	 */
	public static List<Text> mutilSimilarity(List<Text> texts, List<Text> sources) {
		long begin = System.currentTimeMillis();
		if (texts == null || texts.size() == 0) {
			logger.error("无待测文本内容,请检查！");
			return null;
		}
		if (sources == null || sources.size() == 0) {
			logger.error("无资源文本内容,请检查！");
			return null;
		}
		logger.info("启动" + SimilarityParam.getCountThreadNum() + "个待测统计分析线程...");
		SynCountStack synTextsStack = new SynCountStack();
		synTextsStack.setSynStack(new Vector<Text>(texts));
		synTextsStack.setTotal(texts.size());
		for (int i = 0; i < SimilarityParam.getCountThreadNum(); i++) {
			MultilCountThread countThread = new MultilCountThread(synTextsStack);
			Thread thread = new Thread(countThread, countThread.getClass()
					.getSimpleName() + i);
			thread.start();
		}

		logger.info("启动" + SimilarityParam.getCountThreadNum() + "个来源统计分析线程...");
		SynCountStack synSourcesStack = new SynCountStack();
		synSourcesStack.setSynStack(new Vector<Text>(sources));
		synSourcesStack.setTotal(sources.size());
		for (int i = 0; i < SimilarityParam.getCountThreadNum(); i++) {
			MultilCountThread countThread = new MultilCountThread(
					synSourcesStack);
			Thread thread = new Thread(countThread, countThread.getClass()
					.getSimpleName() + i);
			thread.start();
		}
		// 控制所有统计数据完成
		while (true) {
			if (synTextsStack.getCountThread() == SimilarityParam
					.getCountThreadNum()
					&& synSourcesStack.getCountThread() == SimilarityParam
							.getCountThreadNum()) {
				break;
			} else {
				try {
					Thread.sleep(SimilarityParam.getSleepTime());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		long end = System.currentTimeMillis();
		logger.info("统计耗时：" + (end - begin) + "ms");

		logger.info("启动" + SimilarityParam.getCalculateThreadNum()
				+ "个相似性计算线程...");
		long beginCalcu = System.currentTimeMillis();
		SynCalculateStack synCalculateStack = new SynCalculateStack();
		synCalculateStack.setSynTextsStack(new Vector<Text>(texts));
		synCalculateStack.setSynSourcesStack(new Vector<Text>(sources));
		synCalculateStack.setTextNum(texts.size());
		for (int i = 0; i < SimilarityParam.getCalculateThreadNum(); i++) {
			MultilCalculateThread calculateThread = new MultilCalculateThread(synCalculateStack);
			Thread thread = new Thread(calculateThread, calculateThread.getClass().getSimpleName() + i);
			thread.start();
		}
		// 控制所有相似性计算完成
		while (true) {
			if (synCalculateStack.getCalculateThread() == SimilarityParam.getCalculateThreadNum()
					&& synCalculateStack.getCalculateNum() == texts.size()) {
				break;
			} else {
				try {
					Thread.sleep(SimilarityParam.getSleepTime());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		long endCalcu = System.currentTimeMillis();
		logger.info("相似性计算耗时：" + (endCalcu - beginCalcu) + "ms");
		return texts;
	}
}
