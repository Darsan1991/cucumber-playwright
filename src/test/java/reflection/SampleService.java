package reflection;

import attributes.Host;
import attributes.Scope;
import attributes.Service;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Scope
public class SampleService {

    private static SampleService instance;

    public SampleService() {
        init();
    }

    public static SampleService getInstance() {
        if (instance == null) {
            instance = new SampleService();
        }
        return instance;
    }
    
    private static final List<Group> groups = new ArrayList<>();

    public void init() {
        cacheIfNotAlready();
    }

    private static void cacheIfNotAlready() {

        if (!groups.isEmpty()) return;
        
        Reflections pages = new Reflections("pages", Scanners.FieldsAnnotated, Scanners.MethodsAnnotated);

        pages.getFieldsAnnotatedWith(Host.class).forEach(f -> {
            try {
                String name = f.getAnnotation(Host.class).name()
                        .isEmpty() ? f.getName() : f.getAnnotation(Host.class).name();
                groups.add(new Group(StringUtils.toUpperSnakeCase(name), f));
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        });

        pages.getMethodsAnnotatedWith(Host.class).forEach(m -> {
            try {
                String name = m.getAnnotation(Host.class).name()
                        .isEmpty() ? m.getName() : m.getAnnotation(Host.class).name();
                groups.add(new Group(StringUtils.toUpperSnakeCase(name), m));
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        SampleService.getInstance().init();
    }

    public Object getLocatorObject(Page page, String[] names, Object... args) {

        Object value = getValue(names, args);
        
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return page.locator((String) value);
        }
        if (value instanceof Locator) {
            return value;
        }
        
        return null;
    }

    public Object getLocatorObject(Page page, String name, Object... args) {
        return getLocatorObject(page, new String[]{name}, args);
    }

    public <T> T getLocatorByType(Page page, String name, Object... args) {
        Object locator = getLocatorObject(page, name, args);
        //noinspection unchecked
        return locator == null ? null : (T) locator;
    }

    public Locator getLocator(Page page, String name, Object... args) {
        Object locator = getLocatorObject(page, name, args);
        if (locator == null) return null;
        return locator instanceof Locator loc ? loc : null;
    }

    public FrameLocator getFrameLocator(Page page, String name, Object... args) {
        Object locator = getLocatorObject(page, new String[]{
                name,
                name.replace("_FRAME_", "").replace("_FRAME", ""),
               name+ "_FRAME",
        }, args);

        if (locator == null) return null;
        return locator instanceof Locator loc ? loc.contentFrame() : (FrameLocator) locator;
    }



    
    public Object getValue(String[] names,Object[] args,Object... classArgs) {
        List<Group> group = findGroups(names);
        if (groups.isEmpty())
            throw new RuntimeException("No group found for names: " + Arrays.toString(names)); 
        
        if (group.get(0).isField()) {
            return fieldValue(group.get(0).field,classArgs);
        }
        
        if (group.get(0).isMethod()) {
            return getMethodValue(group.get(0).method,args,classArgs);
        }
        return null;
    }
    
    public Object fieldValue(Field field,Object... classArgs) {
        try {
            return Modifier.isStatic(field.getModifiers()) ? field.get(null) : field.get(createObject(field.getDeclaringClass(),classArgs));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Object getMethodValue(Method method,Object[] args, Object... classArgs) {
        try {
            return Modifier.isStatic(method.getModifiers())? method.invoke(null,args):
                    method.invoke(createObject(method.getDeclaringClass(),classArgs), args);
        } catch (Exception ignored) {}
        
        return null;
    }


    public Object createObject(Class<?> page, Object... args) {
        try {
            return page.getDeclaredConstructor().newInstance(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Group> findGroups(String... names) {
        List<String> list = Arrays.stream(names).toList();
        return groups.stream().filter(g -> list.contains(g.name)).toList();
    }



    static class Group {

        public String name;
        public Field field;
        public Method method;

        public boolean isField() {
            return field != null;
        }

        public boolean isMethod() {
            return method != null;
        }

        public Class<?> getDeclaringClass() {
            return isField() ?  field.getDeclaringClass() :  method.getDeclaringClass();
        }

        public Class<?> getReturnType() {
            return isField() ? field.getType() : method.getReturnType();
        }

        public Group(String name, Field field) {
            this.name = name;
            this.field = field;
            this.field.setAccessible(true);
            this.method = null;
        }

        public Group(String name, Method method) {

            this.name = name;
            this.field = null;
            this.method = method;
            this.method.setAccessible(true);
        }
    }
}
