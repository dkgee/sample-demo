package com.example.demo.text.similarity.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Word implements Serializable {

	private static final long serialVersionUID = 1L;

	// 词的特征值
	private double weight;
	// 词频
	private int wordFrequent;
	// 词的内容
	private String content;

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public int getWordFrequent() {
		return wordFrequent;
	}

	public void setWordFrequent(int wordFrequent) {
		this.wordFrequent = wordFrequent;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public int hashCode() {
		return content.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this == null) {
			return false;
		}
		Word word = (Word) obj;
		if (word.content.equals(this.content)) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		Word word = new Word();
		Word word1 = null;// new Word();

		word.setWordFrequent(1);
		// word1.setContent("自动");
		// word1.setWordPos("a");
		List<Word> words = new ArrayList<Word>();
		// word1.setWordFrequent(1);
		words.add(word);
		// words.add(word1);
		System.out.println(words.indexOf(word1));
	}
}
