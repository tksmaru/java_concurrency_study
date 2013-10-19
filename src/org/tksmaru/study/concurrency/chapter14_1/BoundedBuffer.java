package org.tksmaru.study.concurrency.chapter14_1;

public class BoundedBuffer<V> extends BaseBoundedBuffer<V> {

	public BoundedBuffer(int size) {
		super(size);
	}

	public synchronized void put(V v) throws InterruptedException {
		while (isFull()) {
			System.out.println(Thread.currentThread().getId() + ": put wait start.");
			wait();
			System.out.println(Thread.currentThread().getId() + ": put wait end.");
		}
		doPut(v);
		System.out.println(Thread.currentThread().getId() + ": notify after put start.");
		notifyAll();
		System.out.println(Thread.currentThread().getId() + ": notify after put end.");
	}

	public synchronized V take() throws InterruptedException {
		while (isEmpty()) {
			System.out.println("\t" + Thread.currentThread().getId() + ": take wait start.");
			wait();
			System.out.println("\t" + Thread.currentThread().getId() + ": take wait end.");
		}
		V v = doTake();
		System.out.println("\t" + Thread.currentThread().getId() + ": notify after take start.");
		notifyAll();
		System.out.println("\t" + Thread.currentThread().getId() + ": notify after take end.");
		return v;
	}
}
