package com.billionsfinance.crs.disruptor.event;

/**
 * 基于事件的任务处理
 *
 * @ FileName: Event.java
 * @ Author: WeiHui-Z
 * @ Date: 2017-02-20  19:13
 * @ Version: v1.0.0
 */
public class Event<T> {

	/**
	 * 事件名称
	 */
	private String name;

	/**
	 * 事件体
	 */
	private T body;

	/**
	 * 无参构造方法
	 */
	public Event() {
	}

	public Event(String name, T body) {
		this.name = name;
		this.body = body;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}
}
