package com.billionsfinance.crs.disruptor.event;

import javax.management.MXBean;

/**
 * 事件总线的JMX管理接口
 */
@MXBean
public interface EventBusImplMBean {

	/**
	 * 当前事件队列大小
	 */
	int getEventQueueSize();

	/**
	 * 活跃线程数量
	 */
	int getPoolActiveCount();
}
