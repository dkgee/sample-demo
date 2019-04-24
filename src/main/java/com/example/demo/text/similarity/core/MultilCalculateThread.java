package com.example.demo.text.similarity.core;

import com.example.demo.text.similarity.entity.SimilarityParam;
import com.example.demo.text.similarity.entity.Text;

import java.util.Vector;


public class MultilCalculateThread implements Runnable {
	private SynCalculateStack synStack = new SynCalculateStack();

	public MultilCalculateThread(SynCalculateStack synCalculateStack) {
		this.synStack = synCalculateStack;
	}

	@Override
	public void run() {
		Vector<Text> synSourcesStack = synStack.getSynSourcesStack();
		double similarity = 0.0;
		while (true) {

			Text text = synStack.getText();
			if (text == null
					&& synStack.getCalculateThread() == SimilarityParam.getCalculateThreadNum()) {
				break;
			}
			if (text == null) {
				synStack.addCalculateThread();
				continue;
			}

			for (Text txt : synSourcesStack) {
				similarity = TextSimilarity.calculateSimilary(text, txt);
				if (similarity > SimilarityParam.getThreshold()) {
					text.getIds().add(txt.getId());
				}
			}
			synStack.addCalculateNum();
		}
	}
}
