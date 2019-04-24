package com.example.demo.text.similarity.core;

import com.example.demo.text.similarity.entity.Text;

import java.util.Vector;


/**
 * 同步变量类
 * 
 * @author JinHuatao
 * 
 */
public class ShareObject {
	// 待找出文本主体
	private Text text;
	// 待计算共享变量
	private Vector<Text> syncStack = new Vector<Text>();
	// 统计分析线程标识
	private int countThread = 0;
	// 相似性计算线程标识
	private int calculateThread = 0;
	// 统计分析数位置
	private int countIndex = 0;
	// 相似性计算数位置
	private int calculateIndex = 0;
	// 统计分析数量标识
	private int countNum = 0;
	// 相似性计算数量标识
	private int calculateNum = 0;
	// 待计算量
	private int total = 0;

	/**
	 * 获得统计分析文本
	 * 
	 * @return
	 */
	public synchronized Text getCountText() {
		if (countIndex < total) {
			return syncStack.get(countIndex++);
		}
		return null;
	}

	/**
	 * 获得相似性计算文本
	 * 
	 * @return
	 */
	public synchronized Text getCalculateText() {
		if (calculateIndex < total && countNum >= calculateIndex) {
			return syncStack.get(calculateIndex++);
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
	 * 增加相似性计算完成线程
	 */
	public synchronized void addCalculateThread() {
		calculateThread++;
	}

	/**
	 * 增加文本统计完成数量
	 */
	public synchronized void addCountNum() {
		countNum++;
	}

	/**
	 * 增加相似性计算完成数量
	 */
	public synchronized void addCalculateNum() {
		calculateNum++;
	}

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}

	public Vector<Text> getSyncStack() {
		return syncStack;
	}

	public void setSyncStack(Vector<Text> syncStack) {
		this.syncStack = syncStack;
	}

	public int getCountThread() {
		return countThread;
	}

	public void setCountThread(int countThread) {
		this.countThread = countThread;
	}

	public int getCalculateThread() {
		return calculateThread;
	}

	public void setCalculateThread(int calculateThread) {
		this.calculateThread = calculateThread;
	}

	public int getCountIndex() {
		return countIndex;
	}

	public void setCountIndex(int countIndex) {
		this.countIndex = countIndex;
	}

	public int getCalculateIndex() {
		return calculateIndex;
	}

	public void setCalculateIndex(int calculateIndex) {
		this.calculateIndex = calculateIndex;
	}

	public int getCountNum() {
		return countNum;
	}

	public void setCountNum(int countNum) {
		this.countNum = countNum;
	}

	public int getCalculateNum() {
		return calculateNum;
	}

	public void setCalculateNum(int calculateNum) {
		this.calculateNum = calculateNum;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
