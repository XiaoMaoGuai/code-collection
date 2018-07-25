package com.billionsfinance.crs.disruptor.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 带线程前缀名称的线程工厂
 *
 * @ FileName: NamedThreadFactory.java
 * @ Author: WeiHui-Z
 * @ Date: 2017-02-17  17:45
 * @ Version: v1.0.0
 */
public class NamedThreadFactory implements ThreadFactory {

	private static AtomicInteger threadNumber = new AtomicInteger(1);

	private final String namePrefix;

	private final ThreadGroup group;

	/**
	 * 构造函数
	 *
	 * @param namePrefix 线程名称前缀
	 */
	public NamedThreadFactory(String namePrefix) {
		SecurityManager s = System.getSecurityManager();
		this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		this.namePrefix = "pool-" + namePrefix + "-thread-" + threadNumber.getAndIncrement();
	}

	/**
	 * 构造函数
	 *
	 * @param group      线程组
	 * @param namePrefix 线程名称前缀
	 */
	public NamedThreadFactory(ThreadGroup group, String namePrefix) {
		this.group = group;
		this.namePrefix = group.getName() + "-" + namePrefix + "-" + threadNumber.getAndIncrement();
	}

	@Override
	public Thread newThread(Runnable runnable) {
		Thread t = new Thread(group, runnable, namePrefix, 0);
		if (t.isDaemon()) {
			t.setDaemon(false);
		}
		if (t.getPriority() != Thread.NORM_PRIORITY) {
			t.setPriority(Thread.NORM_PRIORITY);
		}
		return t;
	}
}
