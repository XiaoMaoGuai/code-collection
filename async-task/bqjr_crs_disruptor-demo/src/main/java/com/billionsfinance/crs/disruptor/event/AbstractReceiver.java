package com.billionsfinance.crs.disruptor.event;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @ ClassName: AbstractReceiver.java
 * @ Author: WeiHui-Z
 * @ Date: 2017/2/20 19:36
 * @ Version: v1.0.0
 */
public abstract class AbstractReceiver<T> implements Receiver<T> {

    @Autowired
    protected EventBus eventBus;

    @PostConstruct
    protected void init() {
        eventBus.register(getEventName(), this);
    }

    /**
     * 获取该订阅者负责处理的事件名数组
     */
    protected abstract String getEventName();

    @Override
    public final void onEvent(Event<T> event) {
        T content = event.getBody();
        doEvent(content);
    }

    /**
     * 事件处理方法
     *
     * @param event 事件消息体
     */
    protected abstract void doEvent(T event);
}
