package models;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThreadLocal<T> {
    
//    private final ThreadLocal<T> threadLocal = new ThreadLocal<>();
    private final ConcurrentMap<Long,T> map = new ConcurrentHashMap<>();
    
    public T get() {
        return get(Thread.currentThread().threadId()); 
    }
    
    public void set(T value) {
       set(Thread.currentThread().threadId(), value); 
    }
    
    public void set(Long threadId,T value) {
        map.put(threadId, value);
    }
    
    public T get(Long threadId) {
        return map.get(threadId);
    }
   
    public void remove() {
        remove(Thread.currentThread().threadId());
    }
    
    public void remove(Long threadId) {
        map.remove(threadId);
    }
    
}
