package utils;

import java.util.HashMap;
import java.util.Map;

public class MethodUtils {
    
    private static final Map<String,Integer> tryRunTimes = new HashMap<>(); 

    public static void tryRun(Runnable runnable, boolean throwException,Runnable onException,Runnable onSuccess) {
        try {
            runnable.run();
            if (onSuccess != null) {
                onSuccess.run();
            }
        } catch (Exception e) {
            if (onException != null) {
                onException.run();
            }
            if (throwException) {
               throw e; 
            }
        }
    }
    
    public static void tryRun(Runnable runnable, boolean throwException) {
        tryRun(runnable, throwException,null,null);
    }
    public static void tryRun(Runnable runnable) {
        tryRun(runnable,false);
    }
    
    public static void tryRunTimesLazy(Runnable runnable,int maxTry,String uniqueName,Runnable onException,boolean throwExceptionAtEnd) {
       
        if (!tryRunTimes.containsKey(uniqueName)) {
            tryRunTimes.put(uniqueName, maxTry);
        }
        
        if (tryRunTimes.get(uniqueName) <= 0) {
            tryRunTimes.remove(uniqueName);
            if (onException != null) { onException.run();}
            if (throwExceptionAtEnd) { throw new RuntimeException("Max try times reached");}
            return;
        }
        
        tryRunTimes.put(uniqueName, tryRunTimes.get(uniqueName)-1);
        tryRun(runnable, throwExceptionAtEnd && tryRunTimes.get(uniqueName) <= 0,onException,()->tryRunTimes.remove(uniqueName));
    }
    
    public static void tryRunTimesLazy(Runnable runnable,int maxTry,String uniqueName) {
        tryRunTimesLazy(runnable,maxTry,uniqueName,null,false);
    }
}
