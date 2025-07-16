package reflection.base;

import java.lang.reflect.Field;

public interface InjectionHandler {
    boolean canHandle(Field field);

    @SuppressWarnings("UnusedReturnValue")
    boolean handle(long threadId, Field field, Object obj);

//    default boolean handle(Field field, Object obj) {
//        handle(-1,field,obj);
//    }
}
