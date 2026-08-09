package reflection.base;

import attributes.Host;
import attributes.Inject;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import reflection.CreationService;
import utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class AnnotationService<T extends Annotation> implements Initializer {

    @Inject
    protected LoggerService logger;
    @Inject
    private CreationService creationService;


    protected final List<Group> groups = new ArrayList<>();

    private void cacheIfNotAlready() {

        if (!groups.isEmpty()) return;
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(packages()) // multiple packages
                .addScanners(Scanners.FieldsAnnotated, Scanners.MethodsAnnotated) // optional
        );

//        Reflections reflections = new Reflections("pages", Scanners.FieldsAnnotated, Scanners.MethodsAnnotated);

        Class<? extends Annotation> annotation = getAnnotationClass();
        reflections.getFieldsAnnotatedWith(annotation).forEach(f -> {
            try {
                @SuppressWarnings("unchecked") T an = (T) f.getAnnotation(annotation);
                String name = getName(an)
                        .isEmpty() ? f.getName() : getName(an);
                groups.add(new Group(StringUtils.compactCompare(name), f));
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        });

        reflections.getMethodsAnnotatedWith(annotation).forEach(m -> {
            try {
                @SuppressWarnings("unchecked") T an = (T) m.getAnnotation(annotation);
                String name = getName(an)
                        .isEmpty() ? m.getName() : getName(an);
                groups.add(new Group(StringUtils.compactCompare(name), m));
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        });

        onCached();
    }

    protected void onCached() {

    }

    protected abstract Class<T> getAnnotationClass();

    protected abstract String getName(T annotation);


    protected String[] packages() {
        return new String[]{"pages", "stepdefinitions","utilities"};
    }

    public void init() {
        cacheIfNotAlready();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String[] names, Object... args) {
        Object value = getValue(names, args);

        if (value == null) {
            return null;
        }
        return (T) value;
    }

    public <T> T get(String name, Object... args) {
        return get(new String[]{name}, args);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getByType(Class<T> type, Object... args) {
        List<Group> groups = findGroups(type);
        return groups.isEmpty() ? Optional.empty() : Optional.ofNullable((T) getValue(groups.getFirst(), args));
    }

    protected List<Group> findGroups(Class<?> type) {
        return groups.stream().filter(g -> g.getReturnType() == type).toList();
    }

    public boolean isPresent(String... names) {
        return !findGroups(names).isEmpty();
    }

    public Object getValue(String[] names, Object[] args) {
        names = Arrays.stream(names).map(StringUtils::compactCompare).toArray(String[]::new);
        List<Group> group = findGroups(names);
        if (group.isEmpty()) {
            
            logger.logError("No group found for names: " + Arrays.toString(names) + " ");
            return null;

        }

        if (group.getFirst().isField()) {
            return fieldValue(group.getFirst().field);
        }

        if (group.getFirst().isMethod()) {
            return getMethodValue(group.getFirst().method, args);
        }
        return null;
    }

    public Object get(List<? extends Class<? extends Annotation>> otherAnnotations, String[] names, Object... args) {
        names = Arrays.stream(names).map(StringUtils::compactCompare).toArray(String[]::new);
        List<Group> group = findGroups(otherAnnotations, names);
        if (groups.isEmpty()) {
            logger.logError("No group found for names: " + Arrays.toString(names) + " ");
            return null;
        }

        return getValue(group.getFirst(), args);
    }

    public Object fieldValue(Field field) {
        try {
            return Modifier.isStatic(field.getModifiers()) ? field.get(null) : field.get(createObject(field.getDeclaringClass()));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Object getMethodValue(Method method, Object... args) {
        try {
            return Modifier.isStatic(method.getModifiers()) ? method.invoke(null, args) :
                    method.invoke(createObject(method.getDeclaringClass()), args);
        } catch (Exception ignored) {
        }

        return null;
    }

    protected Object getValue(Group group, Object... args) {
        if (group.isField()) {
            return fieldValue(group.field);
        }

        if (group.isMethod()) {
            return getMethodValue(group.method, args);
        }

        return null;
    }


    public Object createObject(Class<?> type) {
        try {
            return creationService.create(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected List<Group> findGroups(String... names) {
        List<String> list = Arrays.stream(names).map(StringUtils::compactCompare).toList();
        return groups.stream().filter(g -> list.contains(g.name)).toList();
    }

    protected List<Group> findGroups(List<? extends Class<? extends Annotation>> otherAnnotations, String... names) {
//        List<Class<? extends Annotation>> annotaitonList = Arrays.stream(otherAnnotations).toList();
        List<String> list = Arrays.stream(names).toList();
        return groups.stream().filter(g -> g.otherAnnotations().containsAll(otherAnnotations)).filter(g ->
                list.contains(g.name)).toList();
    }

    public static class Group {

        public String name;
        public Field field;
        public Method method;

        public List<? extends Class<? extends Annotation>> otherAnnotations() {
            if (isField()) {
                return Arrays.stream(field.getAnnotations()).map(Annotation::annotationType).filter(aClass -> aClass != Host.class).toList();
            }
            return Arrays.stream(method.getAnnotations()).map(Annotation::annotationType)
                    .filter(aClass -> aClass != Host.class).toList();
        }

        public boolean isField() {
            return field != null;
        }

        public boolean isMethod() {
            return method != null;
        }

        public Class<?> getDeclaringClass() {
            return isField() ? field.getDeclaringClass() : method.getDeclaringClass();
        }

        public Type getGenericReturnType() {
            return isField() ? field.getGenericType() : method.getGenericReturnType();
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
