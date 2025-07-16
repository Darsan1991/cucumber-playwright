package models;

import java.lang.reflect.Method;

public class HandlerInfo {
    public Method method;
    public Object object;

    public HandlerInfo(Method method, Object object) {
        this.method = method;
        this.object = object;
    }
}
