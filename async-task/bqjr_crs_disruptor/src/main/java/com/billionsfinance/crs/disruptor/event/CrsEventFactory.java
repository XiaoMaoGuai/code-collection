package com.billionsfinance.crs.disruptor.event;

import com.lmax.disruptor.EventFactory;

/**
 * @ FileName: CrsEventFactory.java
 * @ Author: WeiHui-Z
 * @ Date: 2017-03-05  11:32
 * @ Version: v1.0.0
 */
public class CrsEventFactory implements EventFactory<Event> {

	@Override
	public Event newInstance() {
		return new Event();
	}
}
