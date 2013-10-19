package org.tksmaru.study.concurrency.chapter14_1;

public class SleepyBoundedBuffer<V> extends BaseBoundedBuffer<V> {

	private static final long SLEEP_GRANULARITY = 300;

	public SleepyBoundedBuffer(int size) {
		super(size);
	}

	public void put(V v) throws InterruptedException {
		while (true) {
			synchronized (this) {
				if (!isFull()) {
					System.out.println(Thread.currentThread().getId() + ": put start.");
					doPut(v);
					System.out.println(Thread.currentThread().getId() + ": put end.");
					return;
				}
			}
			System.out.println(Thread.currentThread().getId() + ": sleep start.");
			Thread.sleep(SLEEP_GRANULARITY);
			System.out.println(Thread.currentThread().getId() + ": sleep end.");
		}
	}

	public V take() throws InterruptedException {
		while (true) {
			synchronized (this) {
				if (!isEmpty()) {
					System.out.println("\t" + Thread.currentThread().getId() + ": take.");
					return doTake();
				}
			}
			System.out.println("\t" + Thread.currentThread().getId() + ": sleep start.");
			Thread.sleep(SLEEP_GRANULARITY);
			System.out.println("\t" + Thread.currentThread().getId() + ": sleep end.");
		}
	}
}
