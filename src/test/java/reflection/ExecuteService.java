package reflection;

import attributes.Execute;
import attributes.Service;
import reflection.base.AnnotationService;
import reflection.base.LoggerService;
import utils.StringUtils;

@Service
public class ExecuteService  extends AnnotationService<Execute> {


  

    public void execute(String[] names, Object... args) {
        get(names,args); 
    }

    @Override
    protected Class<Execute> getAnnotationClass() {
       return Execute.class; 
    }

    @Override
    protected String getName(Execute annotation) {
        return StringUtils.compactCompare(annotation.name()); 
    }
}
