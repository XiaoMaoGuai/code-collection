package com.billionsfinance.crs.disruptor.event;

/**
 * 事件总线
 *
 * @ FileName: EventBus.java
 * @ Author: WeiHui-Z
 * @ Date: 2017-02-20  19:14
 * @ Version: v1.0.0
 */
public interface EventBus {

	/**
	 * 发送事件
	 * @param event 事件对象，不允许为 null
	 * @throws IllegalArgumentException 事件对象为 null 时引发
	 */
	void post(Event<?> event);

	/**
	 * 注册事件接收者
	 * @param name 事件名
	 * @param receiver 接收者
	 * @throws IllegalArgumentException 事件名或接收者为 null 时引发
	 */
	void register(String name, Receiver<?> receiver);

	/**
	 * 撤销事件接收者
	 * @param name 事件名
	 * @param receiver 接收者
	 * @throws IllegalArgumentException 事件名或接收者为 null 时引发
	 */
	void unregister(String name, Receiver<?> receiver);

	/**
	 * 同步发送事件
	 * @param event 事件对象，不允许为 null
	 * @throws IllegalArgumentException 事件对象为 null 时引发
	 */
	void syncPost(Event<?> event);
}
