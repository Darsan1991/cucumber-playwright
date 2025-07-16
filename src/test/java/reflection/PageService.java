package reflection;

import attributes.Inject;
import attributes.Service;
import com.microsoft.playwright.Page;
import models.ThreadLocal;
import pages.BasePage;
import reflection.base.InjectionHandler;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Service

//@Scope
public class PageService implements InjectionHandler {
    
    private static final Map<Class<?>, ThreadLocal<BasePage>> pages = new HashMap<>();

    private final CreationService creationService;
    
    @Inject
    private HostService hostService;
    
    public PageService(CreationService creationService) {
       this.creationService = creationService;
       creationService.registerInjectionHandler(this);
    }

    
    public <T extends BasePage> T getPage(Class<T> pageClass) {

        return getPage(Thread.currentThread().threadId(), pageClass);
    }

    public <T extends BasePage> T getPage(long threadId,Class<T> pageClass) {

        if (pages.containsKey(pageClass)) {
            ThreadLocal<BasePage> threadLocal = pages.get(pageClass);
            BasePage page = threadLocal.get(threadId);
            //noinspection OptionalGetWithoutIsPresent
            if (page != null && page.page == hostService.getByType(threadId,Page.class).get()) {
                //noinspection unchecked
                return (T) page;
            }
        }

        return createNewPage(threadId,pageClass);
    }
    public <T extends BasePage> T getPageInternal(long threadId,Class<T> pageClass) {

        if (pages.containsKey(pageClass)) {
            ThreadLocal<BasePage> threadLocal = pages.get(pageClass);
            BasePage page = threadLocal.get(threadId);
            //noinspection OptionalGetWithoutIsPresent
            if (page != null && page.page == hostService.getByType(threadId,Page.class).get()) {
                //noinspection unchecked
                return (T) page;
            }
        }

        return createNewPageInternal(threadId,pageClass);
    }


    @SuppressWarnings("unchecked")
    public  <T extends BasePage> T createNewPage(long threadId,Class<T> pageClass) {
        
        try {
            T page = (T) creationService.create(threadId,pageClass);
            ThreadLocal<BasePage> threadLocal = pages.getOrDefault(pageClass, new ThreadLocal<>());
            threadLocal.set(threadId,page);
            pages.put(pageClass, threadLocal);
            return page;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    public  <T extends BasePage> T createNewPageInternal(long threadId,Class<T> pageClass) {

        try {
            T page = (T) creationService.createInternal(threadId,pageClass);
            ThreadLocal<BasePage> threadLocal = pages.getOrDefault(pageClass, new ThreadLocal<>());
            threadLocal.set(threadId,page);
            pages.put(pageClass, threadLocal);
            return page;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean canHandle(Field field) {

        return BasePage.class.isAssignableFrom(field.getType());
    }

    @Override
    public boolean handle(long threadId,Field field, Object obj) {

        field.setAccessible(true);
        try {
            field.set(obj, this.getPageInternal(threadId, (Class<? extends BasePage>) field.getType()));
        } catch (IllegalAccessException e) {
           return false; 
        }

        return true;
    
    }
}
