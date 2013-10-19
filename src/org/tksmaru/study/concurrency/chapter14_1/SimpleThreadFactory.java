package org.tksmaru.study.concurrency.chapter14_1;

import java.util.concurrent.ThreadFactory;

class SimpleThreadFactory implements ThreadFactory {

	private String name;

	SimpleThreadFactory(String name) {
		this.name = name;
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, name);
	}
}