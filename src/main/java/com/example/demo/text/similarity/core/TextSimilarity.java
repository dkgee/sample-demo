package com.example.demo.text.similarity.core;

import java.io.IOException;
import java.io.StringReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.text.similarity.entity.SimilarityParam;
import com.example.demo.text.similarity.entity.Text;
import com.example.demo.text.similarity.entity.Word;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;


/**
 * 余弦定理计算文本相似度
 * 
 * @author JinHuatao
 * 
 */
public class TextSimilarity {
	private static final Logger logger = Logger.getLogger(TextSimilarity.class);
	// 数据格式化器对象
	private static NumberFormat numberFormat = SimilarityParam.getNumberFormat();

	/**
	 * 初化文本
	 * 
	 * @param text
	 * @return
	 */
	public static void init(Text text) {
		countAnalysis(text);
	}

	/**
	 * 计算两个文本相似度
	 * 
	 * 
	 * @return
	 */
	public static double calculateSimilary(Text one, Text two) {
		List<Word> wordsOne = one.getWords();
		List<Word> wordsTwo = two.getWords();
		double ijSum = 0.0;
		double iw = 0;
		double jw = 0;
		double similarity = 0;
		for (Word word : wordsOne) {
			if (word == null) {
				continue;
			}
			if (wordsTwo != null && wordsTwo.contains(word)) {
				int index = wordsTwo.indexOf(word);
				iw = word.getWeight();
				jw = wordsTwo.get(index).getWeight();
				ijSum += (iw * jw);
			}
		}
		double iModule = one.getModule();
		double jModule = two.getModule();

		if ((iModule * jModule) != 0) {
			similarity = Double.valueOf(numberFormat.format(ijSum
					/ (iModule * jModule)));
		}
		//logger.info(one.getId() + "---" + two.getId() + " 得分：" + similarity);
		return similarity;
	}

	/**
	 * 统计分析
	 * 
	 * @return
	 */
	public static void countAnalysis(Text text) {
		List<Word> words = new ArrayList<Word>();
		String content = text.getContent();
		if (StringUtils.isBlank(content) || StringUtils.isEmpty(content)) {
			return;
		}
		IKSegmentation ikSeg = new IKSegmentation(new StringReader(content),
				SimilarityParam.isMaxMatch());
		Lexeme lexeme = null;
		String con = "";
		try {
			while ((lexeme = ikSeg.next()) != null) {
				con = lexeme.getLexemeText();
				// 停用词过滤
				if (filterWord(con)) {
					continue;
				}
				if (words.contains(con)) {
					int index = words.indexOf(con);
					int freq = words.get(index).getWordFrequent();
					words.get(index).setWordFrequent(freq + 1);
				} else {
					Word word = new Word();
					word.setContent(con);
					word.setWordFrequent(1);
					words.add(word);
					continue;
				}

			}
		} catch (IOException e) {
			logger.error("分词出错：", e);
			return;
		}
		text.setWords(words);
		paraCalculate(text);
	}

	/**
	 * 计算词特征值和文本向量模
	 * 
	 * @param text
	 * @return
	 */
	public static void paraCalculate(Text text) {
		int freq;
		double weight;
		double module;
		int sum = 0;
		List<Word> words = text.getWords();
		for (Word word : words) {
			freq = word.getWordFrequent();
			// 计算词特征值
			weight = Math.sqrt(freq);
			word.setWeight(weight);
			sum = sum + freq;
		}
		// 计算文本向量模
		module = Math.sqrt(sum);
		text.setModule(module);
	}

	/**
	 * 主题词过滤
	 * 
	 * @param word
	 * @return
	 */
	private static boolean filterWord(String word) {
		if (StringUtils.isBlank(word) || StringUtils.isEmpty(word)) {
			return true;
		}
		if (word.trim().length() < 2) {
			return true;
		}
		List<String> stopWord = SimilarityParam.getStopWord();
		if (stopWord != null && stopWord.contains(word)) {
			return true;
		}

		return false;
	}
}
