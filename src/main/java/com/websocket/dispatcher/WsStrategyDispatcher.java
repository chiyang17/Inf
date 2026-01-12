package com.websocket.dispatcher;

import com.Inf;
import com.websocket.inter.WsAction;
import com.websocket.strategy.WsMessageStrategy;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WsStrategyDispatcher {

    private final Map<String, WsMessageStrategy> strategyMap = new HashMap<>();

    public WsStrategyDispatcher(Inf plugin) {
        scanAndRegister(plugin);
    }

    private void scanAndRegister(Inf plugin) {
        Reflections reflections = new Reflections("com.websocket.strategy.impl");

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(WsAction.class);

        for (Class<?> clazz : classes) {
            try {
                WsAction action = clazz.getAnnotation(WsAction.class);
                String type = action.value();

                WsMessageStrategy strategy;

                // 如果有插件构造
                try {
                    strategy = (WsMessageStrategy)
                            clazz.getConstructor(Inf.class).newInstance(plugin);
                } catch (NoSuchMethodException e) {
                    strategy = (WsMessageStrategy)
                            clazz.getConstructor().newInstance();
                }

                strategyMap.put(type, strategy);
                plugin.getLogger().info("已注册 WebSocket 策略: " + type);

            } catch (Exception e) {
                plugin.getLogger().severe("注册策略失败: " + clazz.getName());
                e.printStackTrace();
            }
        }
    }

    public WsMessageStrategy getStrategy(String type) {
        return strategyMap.get(type);
    }
}

