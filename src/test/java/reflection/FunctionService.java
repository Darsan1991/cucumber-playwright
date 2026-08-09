package reflection;

import attributes.Function;
import attributes.Service;
import com.microsoft.playwright.Locator;
import models.FunctionInfo;
import models.HandlerInfo;
import reflection.base.AnnotationService;

import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class FunctionService extends AnnotationService<Function> {


//    @Inject
//    SystemLoggerService logger;

    @Override
    protected Class<Function> getAnnotationClass() {
        return Function.class;
    }

    @Override
    protected String getName(Function annotation) {
        return annotation.name();
    }

    @Override
    protected void onCached() {
        super.onCached();
        groups.stream().filter(g -> !valid(g, true)).forEach(g -> logger.logError("The function: " + g.name + " is not valid. It should have a String or Locator parameter and a boolean or void return type. " + g.method.toGenericString()));
    }

    public Optional<FunctionInfo> get(String name) {
        return get(new String[]{name});
    }

    public Optional<FunctionInfo> get(String[] names) {

        names = Arrays.stream(names)
                .map(n -> List.of(n, n + "function", n.toLowerCase().replace("function", ""), "function" + n))
                .flatMap(List::stream).toList().toArray(new String[0]);

        List<Group> groups = findGroups(names);
        if (groups.isEmpty()) {
            logger.logWarning("The function: " + Arrays.toString(names) + " is not found.");
            return Optional.empty();
        }

        Optional<Group> result = groups.stream().filter(g -> valid(g, true)).findFirst();

        if (result.isEmpty()) {
            result = groups.stream().filter(g -> valid(g, false)).findFirst();
        }

        if (result.isEmpty()) {
            logger.logWarning("The function: " + Arrays.toString(names) + " is not found.");
            return Optional.empty();
        }

        Group value = result.get();

        value.method.setAccessible(true);
        return Optional.of(new FunctionInfo(value.method, Modifier.isStatic(value.method.getModifiers()) ? null : createObject(value.getDeclaringClass())));
    }


    public boolean valid(Group group, boolean isStrict) {
        return group.isMethod();
    }

}
