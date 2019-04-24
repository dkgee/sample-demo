package com.example.demo.text.similarity.core;

import com.example.demo.text.similarity.entity.Text;

import java.util.Vector;


/**
 * 同步统计变量类
 * 
 * @author 许国耀
 * 
 */
public class SynCountStack {
	// 待计算共享变量
	private Vector<Text> synStack = new Vector<Text>();
	// 统计分析线程标识
	private int countThread = 0;
	// 统计分析数位置
	private int countIndex = 0;
	// 统计分析数量标识
	private int countNum = 0;
	// 待计算量
	private int total = 0;

	/**
	 * 获得统计分析文本
	 * 
	 * @return
	 */
	public synchronized Text getCountText() {
		if (countIndex < total) {
			return synStack.get(countIndex++);
		}
		return null;
	}

	/**
	 * 增加统计完成线程
	 */
	public synchronized void addCountThread() {
		countThread++;
	}

	/**
	 * 增加文本统计完成数量
	 */
	public synchronized void addCountNum() {
		countNum++;
	}

	public Vector<Text> getSynStack() {
		return synStack;
	}

	public void setSynStack(Vector<Text> synStack) {
		this.synStack = synStack;
	}

	public int getCountThread() {
		return countThread;
	}

	public void setCountThread(int countThread) {
		this.countThread = countThread;
	}

	public int getCountIndex() {
		return countIndex;
	}

	public void setCountIndex(int countIndex) {
		this.countIndex = countIndex;
	}

	public int getCountNum() {
		return countNum;
	}

	public void setCountNum(int countNum) {
		this.countNum = countNum;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
