package com.billionsfinance.crs.disruptor.event;

import com.billionsfinance.crs.disruptor.utils.NamedThreadFactory;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ FileName: AbstractRuleEngineEventBusImpl.java
 * @ Author: WeiHui-Z
 * @ Date: 2017-03-05  11:25
 * @ Version: v1.0.0
 */
public class AbstractRuleEngineEventBusImpl extends AbstractEventBusImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRuleEngineEventBusImpl.class);

	private RingBuffer<Event> ringBuffer;

	private Disruptor<Event> disruptor;

	//默认
	private Integer ringBufferSize = 1024;

	private static final CrsEventFactory FACTORY = new CrsEventFactory();

	private static final EventTranslatorOneArg<Event, Event> TRANSLATOR = new EventTranslatorOneArg<Event, Event>() {

		@Override
		public void translateTo(Event event, long sequence, Event arg0) {
			event.setName(arg0.getName());
			event.setBody(arg0.getBody());
		}
	};

	private class RingEventWork implements WorkHandler<Event> {

		@Override
		public void onEvent(Event event) throws Exception {
			AbstractRuleEngineEventBusImpl.this.onEvent(event);
		}
	}

	private void onEvent(Event event) {
		String name = event.getName();
		if (!receivers.containsKey(name)) {
			LOGGER.warn("事件[{}]没有对应的接收器", name);
			return;
		}

		for (Receiver receiver : receivers.get(name)) {
			try {
				receiver.onEvent(event);
			} catch (ClassCastException e) {
				LOGGER.error("事件[{}]对象类型不符合接收器声明", event.getName(), e);
			} catch (Throwable t) {
				LOGGER.error("事件[{}]处理时发生异常", event.getName(), t);
			}
		}
		event.setBody(null);
		event.setName(null);
	}

	@Override
	void initialize() {
		NamedThreadFactory threadFactory = new NamedThreadFactory("规则引擎线程");
		disruptor = new Disruptor<>(FACTORY, ringBufferSize, threadFactory, ProducerType.MULTI, new YieldingWaitStrategy());
		RingEventWork[] works = new RingEventWork[3];
		for (int i = 0; i < 3; i++) {
			works[i] = new RingEventWork();
		}
		disruptor.handleEventsWithWorkerPool(works);
		ringBuffer = disruptor.start();
	}

	@Override
	void shutdown() {
		for (; ; ) {
			if (ringBuffer.remainingCapacity() == ringBuffer.getBufferSize()) {
				break;
			}
			Thread.yield();
		}
		//等待线程关闭
		disruptor.shutdown();
	}

	@Override
	void publish(Event<?> event) {
		ringBuffer.publishEvent(TRANSLATOR, event);
	}

	public Integer getRingBufferSize() {
		return ringBufferSize;
	}

	public void setRingBufferSize(Integer ringBufferSize) {
		this.ringBufferSize = ringBufferSize;
	}
}
