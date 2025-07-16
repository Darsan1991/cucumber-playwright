package models;

import java.lang.reflect.Method;

public class ValidatorInfo {
    public Method method;
    public Object object;

    public ValidatorInfo(Method method, Object object) {
        this.method = method;
        this.object = object;
    }
}
