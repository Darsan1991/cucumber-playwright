package reflection;

import attributes.Host;
import attributes.Inject;
import attributes.Service;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import reflection.base.AnnotationService;

@Service
public class HostLocatorService extends AnnotationService<Host> {

   @Inject
   public HostService hostService;



    @Override
    protected Class<Host> getAnnotationClass() {
       return Host.class; 
    }

    @Override
    protected String getName(Host annotation) {
        return annotation.name(); 
    }


    public Object getLocatorObject( String[] names, Object... args) {
        
        Object value = get( names, args);

        switch (value) {
            case null -> {
                return null;
            }
            case String str -> {
                str = str.formatted(args);
                //noinspection resource
                return hostService.getByType(Page.class).orElseThrow().locator(str);
            }
            case Locator ignored -> {
                return value;
            }
            default -> {
            }
        }


        return null;
    }

    public Object getLocatorObject(String name, Object... args) {
        return getLocatorObject(new String[]{name}, args);
    }



    public Locator getLocator(String name, Object... args) {
        String[] names = new String[]{name, name.replace("locator", ""), name + "locator", "locator" + name};
        Object locator = getLocatorObject( names, args);
        if (locator == null) return null;
        return locator instanceof Locator loc ? loc : null;
    }

    public FrameLocator getFrameLocator( String name, Object... args) {
        Object locator = getLocatorObject(new String[]{name, name.replace("frame", ""), name + "frame", "frame" + name}, args);

        if (locator == null) return null;
        return locator instanceof Locator loc ? loc.contentFrame() : (FrameLocator) locator;
    }
 
    
}
