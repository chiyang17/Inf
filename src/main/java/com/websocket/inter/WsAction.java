package com.websocket.inter;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WsAction {
    String value(); // 对应 type
}
