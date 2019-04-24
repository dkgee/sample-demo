package com.example.demo.text.similarity.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Text implements Serializable {

	private static final long serialVersionUID = 1L;
	// 标题
	private String title;
	// 内容
	private String content;
	// id
	private String id;
	// 所有相似文本id
	private List<String> ids = new ArrayList<String>();
	// 所有主题词
	private List<Word> words;
	// 向量模
	private double module;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public List<Word> getWords() {
		return words;
	}

	public void setWords(List<Word> words) {
		this.words = words;
	}

	public double getModule() {
		return module;
	}

	public void setModule(double module) {
		this.module = module;
	}

}
