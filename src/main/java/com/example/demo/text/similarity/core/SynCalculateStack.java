package com.example.demo.text.similarity.core;

import com.example.demo.text.similarity.entity.Text;

import java.util.Vector;


public class SynCalculateStack {
	// 待计算所有文本
	private Vector<Text> synTextsStack = new Vector<Text>();
	// 所有资源
	private Vector<Text> synSourcesStack = new Vector<Text>();
	// 相似性计算线程完成数
	private int calculateThread = 0;
	// 待计算文本位置
	private int calculateTextIndex = 0;
	// 相似性计算完成数量
	private int calculateNum = 0;
	// 待计算文本数
	private int textNum = 0;

	/**
	 * 增加相似性计算完成线程
	 */
	public synchronized void addCalculateThread() {
		calculateThread++;
	}

	/**
	 * 增加相似性计算完成数量
	 */
	public synchronized void addCalculateNum() {
		calculateNum++;
	}

	/**
	 * 获得待计算文本
	 * 
	 * @return
	 */
	public synchronized Text getText() {
		if (synTextsStack == null || synTextsStack.size() == 0) {
			return null;
		}
		if (calculateTextIndex > textNum - 1) {
			return null;
		}
		return synTextsStack.get(calculateTextIndex++);
	}

	public Vector<Text> getSynTextsStack() {
		return synTextsStack;
	}

	public void setSynTextsStack(Vector<Text> synTextsStack) {
		this.synTextsStack = synTextsStack;
	}

	public Vector<Text> getSynSourcesStack() {
		return synSourcesStack;
	}

	public void setSynSourcesStack(Vector<Text> synSourcesStack) {
		this.synSourcesStack = synSourcesStack;
	}

	public int getCalculateThread() {
		return calculateThread;
	}

	public void setCalculateThread(int calculateThread) {
		this.calculateThread = calculateThread;
	}

	public int getCalculateNum() {
		return calculateNum;
	}

	public void setCalculateNum(int calculateNum) {
		this.calculateNum = calculateNum;
	}

	public int getTextNum() {
		return textNum;
	}

	public void setTextNum(int textNum) {
		this.textNum = textNum;
	}

}
