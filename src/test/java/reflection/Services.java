package reflection;

import attributes.Inject;
import attributes.Scope;
import attributes.Service;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import reflection.base.Initializer;
import utils.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Services {

    private static final Map<Class<?>, Supplier<?>> services = new HashMap<>();

    private static final Map<Class<?>, Object> singletonServices = new HashMap<>();

    private static final Map<Class<?>, List<Field>> injectFields = new HashMap<>();

    private static final Stack<Object> injectionStack = new Stack<>();
    private static final Stack<Object> initiationStack = new Stack<>();

    static CountDownLatch latch = new CountDownLatch(1);

    static {

        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages("reflection").addScanners(Scanners.TypesAnnotated));

        reflections.getTypesAnnotatedWith(Service.class).forEach(c -> {
            try {
                registerBaseOnScope(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
    }

    private static void registerBaseOnScope(Class<?> c) {
        Service annotation = c.getAnnotation(Service.class);
        Scope scope = c.getAnnotation(Scope.class);
        Class<?> registerType = annotation.type() == Object.class ? c : annotation.type();
        if (scope == null || scope.name().equalsIgnoreCase("singleton")) {

            register(registerType, () -> {
                Object service = singletonServices.get(registerType);
                if (service == null) {
                    service = createService(c, annotation);
                    if(service!=null)
                    singletonServices.put(registerType, service);
                }
                return service;
            });
        } else {
            register(registerType, () -> createService(c, annotation));
        }
    }

    public static <T> T get(Class<T> serviceClass) {

        try {
            T service = getLocal(serviceClass);
            latch.await();
           initPendingObjects(); 
            
            return service;
        } catch (InterruptedException ignored) {

        }

        return null;

    }

    private static void initPendingObjects() {
        List<Object> objects = initiationStack.stream().filter(o -> o instanceof Initializer).toList();
        for (int i = objects.size() - 1; i >= 0; i--) {
            try {
                ((Initializer) objects.get(i)).init();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        initiationStack.clear();
    }


    private static <T> T getLocal(Class<T> serviceClass) {

        //noinspection unchecked
        return (T) services.get(serviceClass).get();
    }

    public static <T> void register(Class<T> serviceClass, Object service) {
        services.put(serviceClass, () -> service);
    }

    public static <T> void register(Class<T> serviceClass, Supplier<?> service) {
        services.put(serviceClass, service);
    }

    private static Object createService(Class<?> c, Service annotation) {
        Constructor<?> constructor = c.getConstructors()[0];
        constructor.setAccessible(true);
        Object[] parameters = Arrays.stream(constructor.getParameterTypes()).map(p -> {
            if (!isRegistered(p)) {
                System.err.println("The Service " + c.getName() + " is not registered as a service. Please register it first.");
                throw new RuntimeException("Class " + p.getName() + " is not registered as a service");
            }
            return getLocal(p);
        }).toArray(Object[]::new);

        try {
            Class<?> type = annotation.type() == Object.class ? c : annotation.type();
            Object obj = constructor.newInstance(parameters);
            injectionStack.add(obj);
  
            startIfNotRunning();
            System.out.println("Service " + type.getName() + " created successfully. Waiting for injection to complete.");
            return type.cast(obj);
        } catch (Exception e) {
//            startIfNotRunning();
//            throw new RuntimeException(e);
            return null;
        }
    }

    public static boolean isRegistered(Class<?> serviceClass) {
        return services.containsKey(serviceClass);
    }

    private static List<Field> getInjectFields(Class<?> c) {
        if (injectFields.containsKey(c)) {
            return injectFields.get(c);
        }
        List<Field> fields = ClassUtils.getAllFields(c).stream()
        .filter(f -> f.isAnnotationPresent(Inject.class)).toList();
        injectFields.put(c, fields);
        return fields;
    }

    private static void injectFields(Object obj) {
        getInjectFields(obj.getClass()).forEach(f -> {
            try {
                f.setAccessible(true);
                if (isRegistered(f.getType())) {
                    Object local = getLocal(f.getType());
                    f.set(obj, local);
                }else {
                    System.err.println("==================================================================================================");
                    System.err.println("The Service " + f.getType().getName() + " is not registered as a service. Please register it first.");
                    System.err.println("==================================================================================================");
                }
                System.out.println("f" + f);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    private static final AtomicBoolean isRunning = new AtomicBoolean(false);

    public static synchronized void startIfNotRunning() {
        if (latch.getCount() == 0) {
            latch = new CountDownLatch(1);
        }
        if (isRunning.compareAndSet(false, true)) {


            Thread thread = new Thread(() -> {
                try {

                    while (isRunning.get()) {
                        while (!injectionStack.isEmpty()) {
                            Object o = injectionStack.pop();
                            injectFields(o);
                            initiationStack.add(o);
                        }
                        latch.countDown();

                        if (latch.getCount() == 0)
                            throw new Exception();
                    }
                } catch (Exception ignored) {

                } finally {
                    isRunning.set(false);
                }
            });
            thread.start();
        }
    }

    public void stop() {
        isRunning.set(false);
    }

}
