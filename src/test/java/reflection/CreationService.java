package reflection;

import attributes.Inject;
import attributes.Service;
import com.sun.source.tree.ModifiersTree;
import reflection.base.Initializer;
import reflection.base.InjectionHandler;
import utils.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@Service
public class CreationService {

    private static final Map<Class<?>, List<Field>> injectFields = new HashMap<>();
    @Inject
    private HostService hostService;

    @Inject
    private PageService pageService;
    
    private  final ConcurrentLinkedDeque<ThreadObject> injectionStack =  new ConcurrentLinkedDeque<>();
    private  final ConcurrentLinkedDeque<ThreadObject> initiationStack =  new ConcurrentLinkedDeque<>();
    
     CountDownLatch latch = new CountDownLatch(1);
    
   private final List<InjectionHandler> injectionHandlers = new ArrayList<>(); 
   
   
   
    public void registerInjectionHandler(InjectionHandler injectionHandler) {
        injectionHandlers.add(injectionHandler);
    }
    
     public void unregisterInjectionHandler(InjectionHandler injectionHandler) {
       injectionHandlers.remove(injectionHandler);
    }


    public Object create(Class<?> c) {
        return create(-1, c);
    }

    public  Object create(long threadId,Class<?> aClass) {

        try {
            Object object = createInternal(threadId,aClass);
            latch.await();
            initPendingObjects();
            return object;
        } catch (InterruptedException ignored) {

        }
        return null;

    }

    private void initPendingObjects() {
        List<ThreadObject> objects = initiationStack.stream().filter(i -> i.obj instanceof Initializer).toList();
        for (int i = objects.size() - 1; i >= 0; i--) {
            try {

                ((Initializer) objects.get(i).obj).init();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } 
        initiationStack.clear();
    }

    public Object createInternal(long threadId, Class<?> c) {
        
        if (!validateObjectCanCreate(c)) {
            throw new RuntimeException("Class " + c.getName() + " is not a valid object to create");
        }
        
        Constructor<?> constructor = c.getConstructors()[0];
        constructor.setAccessible(true);
        Object[] parameters = Arrays.stream(constructor.getParameterTypes()).map(p -> {
            if (Services.isRegistered(p)) {
                return Services.get(p);
            }
            return hostService.getByType(threadId, p).
                    orElse(null);
        }).toArray(Object[]::new);

        try {
            Object obj = constructor.newInstance(parameters);
            injectionStack.add(new ThreadObject(threadId, obj));
            startIfNotRunning(); 
            return obj;
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
    
    public boolean validateObjectCanCreate(Class<?> c) {
        return !c.isInterface() && !Modifier.isAbstract(c.getModifiers());
    }

    public static List<Field> getInjectFields(Class<?> c) {
        if (injectFields.containsKey(c)) {
            return injectFields.get(c);
        }
        List<Field> fields = ClassUtils.getAllFields(c).stream().filter(f -> f.isAnnotationPresent(Inject.class)).toList();
        injectFields.put(c, fields);
        return fields;
    }



    public  void injectFields(long threadId, Object obj) {
        List<Field> iFields = getInjectFields(obj.getClass());
        iFields.forEach(f -> {
            try {
                f.setAccessible(true);
                if (Services.isRegistered(f.getType()))
                    f.set(obj, Services.get(f.getType()));
                else if (hostService.getByType(threadId, f.getType()).isPresent()) {
                    Object val = hostService.getByType(threadId, f.getType()).orElse(null);
                    f.set(obj, val);
                }

                handleInjectionUsingHandlers(threadId,obj, f);

//                else if (BasePage.class.isAssignableFrom(f.getType())) {
//                    Class<?> type = f.getType();
//                    //noinspection unchecked
//                    f.set(obj, pageService.getPage(threadId, (Class<? extends BasePage>) type));
//                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean handleInjectionUsingHandlers(long threadId,Object obj, Field f) {
        for (InjectionHandler injectionHandler : injectionHandlers.stream().filter(h -> h.canHandle(f)).toList()) {
            if (injectionHandler.handle(threadId,f, obj)) {
               return true; 
            } 
        }
        
        return false;
    }

    static class ThreadObject{
        long threadId;
        Object obj;
        public ThreadObject(long threadId, Object obj) {
            this.threadId = threadId;
            this.obj = obj;
        }
    }

    private    final AtomicBoolean isRunning = new AtomicBoolean(false);

    public    synchronized void startIfNotRunning() {
        if (latch.getCount() == 0) {
            latch = new CountDownLatch(1);
        }
        if (isRunning.compareAndSet(false, true)) {

            Thread thread = new Thread(() -> {
                try {

                    while (isRunning.get()) {
                        while (!injectionStack.isEmpty()) {
                            ThreadObject pop = injectionStack.pop();
                            injectFields(pop.threadId, pop.obj);
                            initiationStack.add(pop);
                        }
                        latch.countDown();

                        if (latch.getCount() == 0)
                           break; 
                    }
                } catch (Exception ignored) {
                    System.err.println("Exception in thread "+ ignored.getMessage()+"--" + Thread.currentThread().getName());

                } finally {
                    isRunning.set(false);
                    if(latch.getCount()>0)
                        latch.countDown();
                }
            });
            thread.start();
        }
    }

    public void stop() {
        isRunning.set(false);
    } 
}

 