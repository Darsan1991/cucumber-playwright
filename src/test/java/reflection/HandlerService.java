package reflection;

import attributes.Handler;
import attributes.Inject;
import attributes.Service;
import com.microsoft.playwright.Locator;
import models.HandlerInfo;
import reflection.base.AnnotationService;
import reflection.base.LoggerService;

import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class HandlerService extends AnnotationService<Handler> {


    
//    @Inject
//    SystemLoggerService logger;

    @Override
    protected Class<Handler> getAnnotationClass() {
        return Handler.class;
    }

    @Override
    protected String getName(Handler annotation) {
        return annotation.name();
    }

    @Override
    protected void onCached() {
        super.onCached();
        groups.stream().filter(g-> !validHandler(g, true)).forEach(g-> logger.logError("The handler: " + g.name + " is not valid. It should have a String or Locator parameter and a boolean or void return type. " + g.method.toGenericString() ));
    }

    public Optional<HandlerInfo> get(String name) {
        return get(new String[]{name});
    }
    public Optional<HandlerInfo> get(String[] names) {

        names = Arrays.stream(names)
                .map(n->List.of(n,n+"handler",n.toLowerCase().replace("handler",""),"handler"+n))
                .flatMap(List::stream).toList().toArray(new String[0]);
        
        List<Group> groups = findGroups(names);
        if (groups.isEmpty()) {
            logger.logWarning("The handler: " + Arrays.toString(names) + " is not found.");
            return Optional.empty();
        }

        Optional<Group> result = groups.stream().filter(g -> validHandler(g, true)).findFirst();

        if (result.isEmpty()) {
            result = groups.stream().filter(g -> validHandler(g, false)).findFirst();
        }

        if (result.isEmpty()) {
            logger.logWarning("The handler: " + Arrays.toString(names) + " is not found.");
            return Optional.empty();
        }
       
        Group value = result.get();

        value.method.setAccessible(true);
        return Optional.of(new HandlerInfo(value.method, Modifier.isStatic(value.method.getModifiers()) ? null : createObject(value.getDeclaringClass())));
    }

    
    
    public boolean validHandler(Group group, boolean isStrict) {
        if (group.isMethod() && group.method.getParameters().length >= 1) {
            Parameter[] parameters = group.method.getParameters();
            return !isStrict 
                   || (parameters[0].getType() == String.class 
                       || parameters[0].getType() == Locator.class) 
                      && (group.method.getReturnType() == boolean.class || group.method.getReturnType() == void.class) ;
        }

        return false;
    }

}
