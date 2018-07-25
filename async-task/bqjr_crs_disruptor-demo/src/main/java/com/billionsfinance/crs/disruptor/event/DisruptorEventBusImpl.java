package com.billionsfinance.crs.disruptor.event;

import com.billionsfinance.crs.disruptor.utils.NamedThreadFactory;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DisruptorEventBusImpl extends AbstractEventBusImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorEventBusImpl.class);

	private Integer ringBufferSize = 1024 * 4;

	private RingBuffer<Event> ringBuffer;

	private Disruptor<Event> disruptor;

	private static final EventFactory<Event> FACTORY = new EventFactory<Event>() {

		@Override
		public Event newInstance() {
			return new Event();
		}
	};

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
			DisruptorEventBusImpl.this.onEvent(event);
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
				LOGGER.error("事件[" + event.getName() + "]对象类型不符合接收器声明", e);
			} catch (Throwable t) {
				LOGGER.error("事件[" + event.getName() + "]处理时发生异常", t);
			}
		}
		event.setBody(null);
		event.setName(null);
	}

	@Override
	void initialize() {
		ThreadGroup threadGroup = new ThreadGroup("事件模块");
		NamedThreadFactory threadFactory = new NamedThreadFactory(threadGroup, "事件处理线程");
		disruptor = new Disruptor<>(FACTORY, ringBufferSize, threadFactory, ProducerType.MULTI, new SleepingWaitStrategy());
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

}
