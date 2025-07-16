package reflection;

import attributes.Service;
import reflection.base.TypeProviderService;
import stepdefinitions.BaseStep;

import java.lang.reflect.Field;

@Service

//@Scope
public class StepDefinitionService extends TypeProviderService<BaseStep> {


    public StepDefinitionService(CreationService creationService) {
        super(creationService);
    }

    @Override
    protected boolean canUseCached(long threadId, BaseStep page) {
        return page!=null ;
        //&& page.page.equals(hostService.getByType(threadId,Page.class).orElse(null));
    }

    @Override
    public boolean canHandle(Field field) {
        return BaseStep.class.isAssignableFrom(field.getType());
    }
}
