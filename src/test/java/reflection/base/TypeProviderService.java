package reflection.base;

import attributes.Inject;
import models.ThreadLocal;
import reflection.CreationService;
import reflection.HostService;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

//@Scope
public abstract class TypeProviderService<T> implements InjectionHandler {
    
    protected final Map<Class<?>, ThreadLocal<T>> objects = new HashMap<>();

    protected final CreationService creationService;
    
    @Inject
    protected HostService hostService;
    
    public TypeProviderService(CreationService creationService) {
       this.creationService = creationService;
       creationService.registerInjectionHandler(this);
    }

    
    public <TJ extends T> TJ get(Class<TJ> pageClass) {

        return get(Thread.currentThread().threadId(), pageClass);
    }

    public <TJ extends T> TJ get(long threadId,Class<TJ> pageClass) {

        if (objects.containsKey(pageClass)) {
            @SuppressWarnings("unchecked") ThreadLocal<T> threadLocal = (ThreadLocal<T>) objects.get(pageClass);
            T page = threadLocal.get(threadId);
            //noinspection OptionalGetWithoutIsPresent
            if (canUseCached(threadId, page)) {
                //noinspection unchecked
                return (TJ) page;
            }
        }

        return create(threadId,pageClass);
    }

    public <TJ extends T> TJ getInternal(long threadId,Class<TJ> pageClass) {

        if (objects.containsKey(pageClass)) {
            @SuppressWarnings("unchecked") ThreadLocal<T> threadLocal = (ThreadLocal<T>) objects.get(pageClass);
            T page = threadLocal.get(threadId);
            //noinspection OptionalGetWithoutIsPresent
            if (canUseCached(threadId, page)) {
                //noinspection unchecked
                return (TJ) page;
            }
        }

        return createInternal(threadId,pageClass);
    }

    protected abstract boolean canUseCached(long threadId, T page) ;   @SuppressWarnings("unchecked")
    public  <TJ extends T> TJ create(long threadId,Class<TJ> itemClass) {
        
        try {
            T item = (T) creationService.create(threadId,itemClass);
            ThreadLocal<T> threadLocal = objects.getOrDefault(itemClass, new ThreadLocal<>());
            threadLocal.set(threadId,item);
            objects.put(itemClass, threadLocal);
            return (TJ) item;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public  <TJ extends T> TJ createInternal(long threadId,Class<TJ> itemClass) {

        try {
            T item = (T) creationService.createInternal(threadId,itemClass);
            ThreadLocal<T> threadLocal = objects.getOrDefault(itemClass, new ThreadLocal<>());
            threadLocal.set(threadId,item);
            objects.put(itemClass, threadLocal);
            return (TJ) item;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void clear(long threadId) {
        objects.forEach( (k,v) -> {
           v.remove(threadId); 
        });
    }
    public void clear()
    {
        clear(Thread.currentThread().threadId());
    }

    @Override
    public abstract boolean canHandle(Field field)  ;  @Override
    public boolean handle(long threadId,Field field, Object obj) {

        field.setAccessible(true);
        try {
            field.set(obj, this.getInternal(threadId, (Class<? extends T>) field.getType()));
        } catch (IllegalAccessException e) {
           return false; 
        }

        return true;
    
    }
}
