package org.tksmaru.study.concurrency.chapter14_1;

import java.util.concurrent.*;

public class TestSleepyBoundedBuffer {

	private static final SleepyBoundedBuffer<String> buffer = new SleepyBoundedBuffer<String>(3);

	public static void main(String [] args) {

		Runtime runtime = Runtime.getRuntime();
		CountDownLatch latch = new CountDownLatch(3);

		System.out.println("\t\t" + Thread.currentThread().getId() + ": main start.");

		ScheduledExecutorService producerService = Executors.newSingleThreadScheduledExecutor(new SimpleThreadFactory("t-producer"));
		runtime.addShutdownHook(new Thread(new Finalizer(producerService), "t-producer_shutdown"));

		ScheduledExecutorService consumerService = Executors.newSingleThreadScheduledExecutor(new SimpleThreadFactory("t-consumer"));
		runtime.addShutdownHook(new Thread(new Finalizer(consumerService), "t-consumer_shutdown"));

		producerService.scheduleWithFixedDelay(new Producer(), 3L, 1L, TimeUnit.SECONDS);
		consumerService.scheduleWithFixedDelay(new Consumer(latch), 0L, 5L, TimeUnit.SECONDS);

		int status = 0;
		try {
			// バッファから3回取り出されるまで停止
			latch.await();
		} catch (InterruptedException e) {
			status = 1;
		} finally {
			System.out.println("\t\t" + Thread.currentThread().getId() + ": main shutdown.");
			System.exit(status);
		}
	}

	static class Producer implements Runnable {
		@Override
		public void run() {
			try {
				buffer.put("test");
			} catch (InterruptedException ignore) {
			}
		}
	}

	static class Consumer implements Runnable {

		private final CountDownLatch latch;
		private int counter = 0;

		Consumer(CountDownLatch latch) {
			this.latch = latch;
		}

		@Override
		public void run() {
			try {
				System.out.println(buffer.take() + " : " + (++counter) + "回目");
				latch.countDown();
			} catch (InterruptedException ignore) {
			}
		}
	}

	static class Finalizer implements Runnable {

		private final ExecutorService service;

		Finalizer(ExecutorService service) {
			this.service = service;
		}

		@Override
		public void run() {
			Thread current = Thread.currentThread();
			System.out.println("\t\t" + current.getId() + ": " + current.getName() + " shutdown.");
			service.shutdown();
			try {
				if (!service.awaitTermination(3, TimeUnit.SECONDS)) {
					service.shutdownNow();
				}
			} catch (InterruptedException e) {
				service.shutdownNow();
			}
			String result = service.isTerminated() ? " is shutdown." : "is not shutdown";
			System.out.println("\t\t" + current.getId() + ": " + current.getName() + result);
		}
	}
}
