package com.billionsfinance.crs.disruptor.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @fileName: AbstractEventBusImpl.java
 * @author: WeiHui
 * @date: 2018/7/10 15:49
 * @version: v1.0.0
 * @since JDK 1.8
 */
public abstract class AbstractEventBusImpl implements EventBus {

    /**
     * 日志
     */
    protected Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 接受者map
     */
    protected Map<String, CopyOnWriteArraySet<Receiver<?>>> receivers = new ConcurrentHashMap<>();

    /**
     * 停止状态
     */
    private volatile boolean stop;

    /**
     * 应用启动时加载
     */
    @PostConstruct
    protected void init() {
        initialize();
    }

    /**
     * 初始化
     */
    abstract void initialize();

    /**
     * 关闭容器时调用
     */
    @PreDestroy
    protected void destroy() {
        if (isStop()) {
            return;
        }
        stop = true;
        shutdown();
    }

    /**
     * 关停
     */
    abstract void shutdown();

    /**
     * 注册时间处理者
     *
     * @param name     事件名
     * @param receiver 接收者
     */
    @Override
    public void register(String name, Receiver receiver) {
        this.receiverCheck(name, receiver);

        CopyOnWriteArraySet<Receiver<?>> set = receivers.get(name);

        if (set == null) {
            set = new CopyOnWriteArraySet<>();
            CopyOnWriteArraySet<Receiver<?>> prev = receivers.putIfAbsent(name, set);
            set = prev != null ? prev : set;
        }

        set.add(receiver);
    }

    /**
     * 检测事件是否为空
     *
     * @param name     事件名称
     * @param receiver 事件接收者
     */
    private void receiverCheck(String name, Receiver receiver) {
        if (name == null || receiver == null) {
            throw new IllegalArgumentException("事件名称和接收事件者均不能为空");
        }
    }

    /**
     * @param name     事件名
     * @param receiver 接收者
     */
    @Override
    public void unregister(String name, Receiver receiver) {
        this.receiverCheck(name, receiver);
        CopyOnWriteArraySet<Receiver<?>> set = receivers.get(name);
        if (set != null) {
            set.remove(receiver);
        }
    }

    @Override
    public void post(Event<?> event) {
        if (event == null) {
            throw new IllegalArgumentException("事件对象不能为空");
        }
        if (stop) {
            throw new IllegalStateException("事件总线已经停止，不能再接收事件");
        }
        publish(event);
    }

    /**
     * 发送事件
     *
     * @param event 事件
     */
    abstract void publish(Event<?> event);

    @Override
    public void syncPost(Event<?> event) {
        String name = event.getName();
        if (!receivers.containsKey(name)) {
            log.error("事件'{}'没有对应的接收器", name);
            return;
        }
        for (Receiver receiver : receivers.get(name)) {
            try {
                receiver.onEvent(event);
            } catch (Exception e) {
                log.error("事件[" + event.getName() + "]处理时发生异常", e);
            }
        }

    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}
