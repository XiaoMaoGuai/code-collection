package com.billionsfinance.crs.disruptor.event;

/**
 * 事件订阅者
 *
 * @ ClassName: Receiver.java
 * @ Author: WeiHui-Z
 * @ Date: 2017/2/20 19:18
 * @ Version: v1.0.0
 */
public interface Receiver<T> {

	/**
	 * 事件处理方法
	 *
	 * @param event 待处理事件对象
	 */
	void onEvent(Event<T> event);
}
