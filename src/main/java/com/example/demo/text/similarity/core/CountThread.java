package com.example.demo.text.similarity.core;


import com.example.demo.text.similarity.entity.Text;

public class CountThread implements Runnable {
	private ShareObject syncStack = new ShareObject();

	public CountThread(ShareObject syncStack) {
		this.syncStack = syncStack;
	}

	@Override
	public void run() {
		while (true) {
			Text text = syncStack.getCountText();
			if (text == null) {
				syncStack.addCountThread();
				break;
			} else {
				TextSimilarity.init(text);
				syncStack.addCountNum();
			}
		}
	}

}
