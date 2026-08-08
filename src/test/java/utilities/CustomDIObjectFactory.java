package utilities;

import io.cucumber.core.backend.ObjectFactory;
import reflection.Services;
import reflection.StepDefinitionService;
import stepdefinitions.BaseStep;

import java.util.*;

public class CustomDIObjectFactory implements ObjectFactory {

    
    private final Set<Class<?>> stepClasses = new HashSet<>();

    @Override
    public boolean addClass(Class<?> glueClass) {
        stepClasses.add(glueClass);
        return true;
    }

    @Override
    public <T> T getInstance(Class<T> glueClass) {
        StepDefinitionService stepDefinitionService = Services.get(StepDefinitionService.class);
        //noinspection unchecked
        return (T)stepDefinitionService.get((Class<? extends BaseStep>) glueClass);
    }

    @Override
    public void start() {
        System.out.println("hello world");
        // Optional setup
    }

    @Override
    public void stop() {
       
        Services.get(StepDefinitionService.class).clear();
        
        System.out.println("bye world");
        // Optional cleanup
    }
}