package org.tksmaru.study.concurrency.chapter14_1;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * syncronized ブロックの代わりに {@link Lock} インタフェースを利用した実装例<br />
 * 14-3の先取り。
 */
public class ConditionBoundedBuffer<V> extends BaseBoundedBuffer<V> {

	private final Lock lock = new ReentrantLock();
	private final Condition notFull = lock.newCondition();
	private final Condition notEmpty = lock.newCondition();

	public ConditionBoundedBuffer(int size) {
		super(size);
	}

	public void put(V v) throws InterruptedException {
		lock.lock();
		try {
			while (isFull()) {
				System.out.println(Thread.currentThread().getId() + ": put wait start.");
				notFull.await();
				System.out.println(Thread.currentThread().getId() + ": put wait end.");
			}
			doPut(v);
			System.out.println(Thread.currentThread().getId() + ": notify after put start.");
			notEmpty.signal();
			System.out.println(Thread.currentThread().getId() + ": notify after put end.");
		} finally {
			lock.unlock();
		}
	}

	public V take() throws InterruptedException {
		lock.lock();
		try {
			while (isEmpty()) {
				System.out.println("\t" + Thread.currentThread().getId() + ": take wait start.");
				notEmpty.await();
				System.out.println("\t" + Thread.currentThread().getId() + ": take wait end.");
			}
			V v = doTake();
			System.out.println("\t" + Thread.currentThread().getId() + ": notify after take start.");
			notFull.signal();
			System.out.println("\t" + Thread.currentThread().getId() + ": notify after take end.");
			return v;
		} finally {
			lock.unlock();
		}
	}

}
