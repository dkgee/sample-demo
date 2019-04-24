package com.example.demo.text.similarity.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.text.similarity.entity.Text;
import com.example.demo.text.similarity.util.TextSimilarityUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


public class TextSimilarityEx {
	private static final Logger logger = Logger.getLogger(TextSimilarityEx.class);

	public static void main(String[] args) {
		//similarity();
		mutilSimilarity();
	}

	public static void mutilSimilarity() {
		List<Text> texts = new ArrayList<Text>();
		List<Text> sources = new ArrayList<Text>();
		sources = create();
		texts = sources.subList(0, 5);
		long begin = System.currentTimeMillis();
		TextSimilarityUtil.mutilSimilarity(texts, sources);
		long end = System.currentTimeMillis();
		for (Text txt : texts) {
			logger.info("文本：" + txt.getId() + " 相似文本 ：" + txt.getIds());
		}
		logger.info("总共耗时：" + (end - begin) + "ms");
	}

	public static void similarity() {

		Text txt = new Text();
		List<Text> texts = new ArrayList<Text>();
		String content = "与民间投资领域太窄、民间投资缺乏融资渠道有关。更有引发通货膨胀的危险。为使扩张性的财政政策对拉动经";
		/*
		 * for (int j = 0; j < 14; j++) { sb.append(content); } content =
		 * sb.toString();
		 */
		// content = readFile();
		txt.setContent(content);
		txt.setId("0");
		txt.setTitle("");
		/*
		 * for (File file : files.listFiles()) { try { content =
		 * FileUtils.readFileToString(file, "gbk"); Text text = new Text();
		 * text.setContent(content); text.setId(file.getName());
		 * texts.add(text); } catch (IOException e) { e.printStackTrace(); } }
		 */

		texts = create();
		for (int i = 0; i < 1; i++) {
			long begin = System.currentTimeMillis();
			TextSimilarityUtil.similarityMutil(txt, texts);
			// TextSimilarityUtil.similarity(txt, texts);
			logger.info("文本：" + txt.getId() + " 相似文本 ：" + txt.getIds());
			long end = System.currentTimeMillis();
			logger.info("耗时：" + (end - begin) + "ms");
		}
	}

	public static List<Text> create() {
		List<Text> texts = new ArrayList<Text>();
		String content = "";
		for (int i = 1; i < 100000; i++) {
			content = "与民间投资领域太窄、民间投资缺乏融资渠道有关。在以往的文章中，我曾提出，有些投资项目，特别是效益好的项目，不必都由政府财政投";
			Text text = new Text();
			text.setContent(content);
			text.setId(String.valueOf(i));
			texts.add(text);

		}
		return texts;
	}

	public static String readFile() {
		String content = "";
		String path = "H:/摘要和相似度/similarity/0.txt";
		File file = new File(path);
		try {
			content = FileUtils.readFileToString(file, "gbk");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
}
