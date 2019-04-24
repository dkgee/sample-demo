package com.example.demo.text.similarity.core;


import com.example.demo.text.similarity.entity.SimilarityParam;
import com.example.demo.text.similarity.entity.Text;

public class CalculateThread implements Runnable {
	private ShareObject syncStack = new ShareObject();

	public CalculateThread(ShareObject syncStack) {
		this.syncStack = syncStack;
	}

	@Override
	public void run() {
		while (true) {

			Text text = syncStack.getCalculateText();
			if (text == null
					&& syncStack.getCalculateIndex() >= syncStack.getTotal()) {
				syncStack.addCalculateThread();
				break;
			}
			if (text == null) {
				try {
					Thread.sleep(SimilarityParam.getSleepTime());
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			double similarity = TextSimilarity.calculateSimilary(
					syncStack.getText(), text);
			if (similarity > SimilarityParam.getThreshold()) {
				syncStack.getText().getIds().add(text.getId());
			}
			syncStack.addCalculateNum();
		}
	}

}
