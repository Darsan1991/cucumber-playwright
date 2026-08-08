package stepdefinitions;

import attributes.Inject;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.ParameterType;
import models.HandlerInfo;
import models.ValidatorInfo;
import reflection.*;
import resolvers.DataResolver;
import utils.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CustomParameters extends BaseStep{

    @Inject
    public ValidatorService validatorService;

    @Inject
    public HandlerService handlerService;
    
    @ParameterType(".*")
    public String username(String username) {
        return username;
    }

    @ParameterType(".*")
    public String password(String password) {
        return password;
    }
    @ParameterType("( and wait \\d+ seconds)?")
    public int wait(String value) {
        if (value==null) {
            return 0;
        }
        
        return Integer.parseInt(StringUtils.getTextByRegex(value,"\\d+"));
    }

    @ParameterType("( with timeout \\d+ seconds)?")
    public int timeout(String value) {
        if (value==null) {
            return 30;
        }

        return Integer.parseInt(StringUtils.getTextByRegex(value,"\\d+"));
    }

    @ParameterType("( not throw| throw| if can| if there)?")
    public boolean optional(String value) {
        if (value==null) {
            return false;
        }
        
        return !value.trim().equalsIgnoreCase("throw");
    }
    @ParameterType("( in \\d+ frame)?")
    public int inFrame(String value) {
        if (value==null) {
            return -1;
        }

        return Integer.parseInt(StringUtils.getTextByRegex(value,"\\d+"));
    }
    
    @ParameterType("\"[^\"]*\"")
    public Locator locator(String locator) {
        Locator resultLocator = getLocator(locator);

        return resultLocator;
    }

    @ParameterType("\"[^\"]*\"")
    public String dataString(String value) {
        value = StringUtils.removeQuotes(value);
        return resultString(value);
    }

    @ParameterType("\"[^\"]*\"")
    public String resultString(String key) {
        return DataResolver.resolve(key,"");
    }
    @ParameterType("\"[^\"]*\"")
    public String result(String locator) {
       return "";
    }

    public static Locator getLocator(String locator) {
        locator = StringUtils.removeQuotes(locator);
        List<Locator> items = Arrays.stream(locator.split("->")).map(String::trim)
                .map(CustomParameters::getSingleLocator).toList();

        Locator resultLocator = items.getFirst();
        for (Locator value : items.stream().skip(1).toList()) {
            resultLocator = resultLocator.locator(value);
        }
        return resultLocator;
    }

    @ParameterType("( inside \"[^\"]*\")?")
    public Locator inside(String locator) {
        if (locator==null) {
            return null;
        }
        
        locator = locator.replaceFirst("inside ", "").trim();

        Locator resultLocator = getLocator(locator);

        return resultLocator;
    }


    @ParameterType("( type (normal|js|force))?")
    public String clickType(String type) {
     return  type;
    }



    @ParameterType("(loaded|dom content loaded|network idle)")
    public LoadState loadState(String state) {
        return LoadState.valueOf(state.trim().replaceAll(" ","").toUpperCase());
    }

    public static Locator getSingleLocator(String locator) {
        String[] items = Arrays.stream(locator.split("::")).toArray(String[]::new);
        items[0] = StringUtils.toUpperSnakeCase(items[0]);
        return Objects.requireNonNull(Services.get(HostLocatorService.class)).getLocator( items[0], Arrays.stream(items).skip(1).toArray());
    }


    @ParameterType(".*")
    public FrameLocator frameLocator(String locator) {
        locator = StringUtils.removeQuotes(locator);
        String[] items = Arrays.stream(locator.split("::")).toArray(String[]::new);
        items[0] = StringUtils.toUpperSnakeCase(items[0]);
        return Objects.requireNonNull(Services.get(HostLocatorService.class)).getFrameLocator( items[0], Arrays.stream(items).skip(1).toArray());
    }

    @ParameterType(".*")
    public boolean condition(String name) {
       name = StringUtils.removeQuotes(name);
        String[] items = Arrays.stream(name.split("::")).toArray(String[]::new);
        items[0] = StringUtils.toUpperSnakeCase(items[0]);

        Object result = Services.get(ExecuteService.class).get(items[0], Arrays.stream(items).skip(1).toArray());
        return (boolean)result;
    }

    @ParameterType("( if .*)?") 
    public boolean ifCondition(String name)
    {
        if (name==null) {
            return true;
        }
        
        String trim = name.substring(4).trim();
        return condition(trim);
    }

    @ParameterType(".*")
    public boolean execute(String name) {
        name = name.trim();
        name = StringUtils.removeQuotes(name);
        String[] items = Arrays.stream(name.split("::")).toArray(String[]::new);
        items[0] = StringUtils.toUpperSnakeCase(items[0]);
   
        try {
            Services.get(ExecuteService.class).get(items[0], Arrays.stream(items).skip(1).toArray());
            return true;
        } catch (Exception e) {
           return false; 
        }
    }

    @ParameterType("(visible|not visible|hyper link|clickable|editable|\"[^\"]*\")")
    public ValidatorInfo validator(String name) {
        name = name.trim();
        name = StringUtils.removeQuotes(name);
        String[] items = Arrays.stream(name.split("::")).toArray(String[]::new);
        items[0] = StringUtils.toUpperSnakeCase(items[0]);

        return validatorService.get(items[0]).orElse(null);

    }

    @ParameterType(".*")
    public HandlerInfo handler(String name) {
        name = name.trim();
        name = StringUtils.removeQuotes(name);
        String[] items = Arrays.stream(name.split("::")).toArray(String[]::new);
        items[0] = StringUtils.toUpperSnakeCase(items[0]);

        return handlerService.get(items[0]).orElse(null);

    }
}
