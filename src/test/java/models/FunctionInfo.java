package models;

import java.lang.reflect.Method;

public class FunctionInfo {
    public Method method;
    public Object object;

    public FunctionInfo(Method method, Object object) {
        this.method = method;
        this.object = object;
    }
}
