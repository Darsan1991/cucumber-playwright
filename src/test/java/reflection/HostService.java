package reflection;

import attributes.Host;

import attributes.Service;
import models.ThreadLocal;
import reflection.base.AnnotationService;
import reflection.base.LoggerService;

import java.lang.reflect.*;
import java.util.List;
import java.util.Optional;

@Service
public class HostService extends AnnotationService<Host> {


       @Override
    protected Class<Host> getAnnotationClass() {
        return Host.class;
    }

    @Override
    protected String getName(Host annotation) {
        return annotation.name();
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getByType(long threadId,Class<T> type, Object... args)
    {

        if(threadId<=0)
            return getByType(type, args);

        List<Group> groups = findGroups(models.ThreadLocal.class).stream()
                .filter(g -> g.getGenericReturnType() instanceof ParameterizedType).filter(g-> ((ParameterizedType)g.getGenericReturnType()).getActualTypeArguments()[0] == type ).toList();

        Optional<?> val = groups.isEmpty() ? Optional.empty() : Optional.ofNullable((T) getValue(groups.getFirst(), args));

        return Optional.ofNullable(val.map(v -> (T) ((ThreadLocal<?>) v).get(threadId)).orElseGet(() -> getByType(type, args).orElse(null)));

    } 
}
