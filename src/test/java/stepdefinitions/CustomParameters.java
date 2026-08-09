package stepdefinitions;

import attributes.Inject;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import models.HandlerInfo;
import models.ValidatorInfo;
import reflection.*;
import resolvers.DataResolver;
import resolvers.FunctionResolver;
import resolvers.LocatorResolver;
import utils.StringUtils;

import java.util.*;

public class CustomParameters extends BaseStep {

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
        if (value == null) {
            return 0;
        }

        return Integer.parseInt(StringUtils.getTextByRegex(value, "\\d+"));
    }

    @ParameterType("( with timeout \\d+ seconds)?")
    public int timeout(String value) {
        if (value == null) {
            return 30;
        }

        return Integer.parseInt(StringUtils.getTextByRegex(value, "\\d+"));
    }

    @ParameterType("( not throw| throw| if can| if there)?")
    public boolean optional(String value) {
        if (value == null) {
            return false;
        }

        return !value.trim().equalsIgnoreCase("throw");
    }

    @ParameterType("( in \\d+ frame)?")
    public int inFrame(String value) {
        if (value == null) {
            return -1;
        }

        return Integer.parseInt(StringUtils.getTextByRegex(value, "\\d+"));
    }

    @ParameterType("\"[^\"]*\"")
    public Locator locator(String locator) {
        return LocatorResolver.resolve(locator);
    }

    @ParameterType("\"[^\"]*\"")
    public String dataString(String value) {
        value = StringUtils.removeQuotes(value);
        return DataResolver.resolveString(value, false);
    }

    @ParameterType("\"[^\"]*\"")
    public String resultString(String key) {
        key = StringUtils.removeQuotes(key);
        if (key.startsWith("{{")) {
            return DataResolver.resolveString(key, true);
        }
        return result(key);
    }

    @ParameterType("\"[^\"]*\"")
    public String result(String value) {

        return FunctionResolver.resolve(value);
    }


    @ParameterType("( inside \"[^\"]*\")?")
    public Locator inside(String locator) {
        if (locator == null) {
            return null;
        }

        locator = locator.replaceFirst("inside ", "").trim();

        return LocatorResolver.resolve(locator);
    }


    @ParameterType("( type (normal|js|force))?")
    public String clickType(String type) {
        return type;
    }


    @ParameterType("(loaded|dom content loaded|network idle)")
    public LoadState loadState(String state) {
        return LoadState.valueOf(state.trim().replaceAll(" ", "").toUpperCase());
    }

    public static Locator getSingleLocator(String locator) {
        String[] items = Arrays.stream(locator.split("::")).toArray(String[]::new);
        items[0] = StringUtils.toUpperSnakeCase(items[0]);
        return Objects.requireNonNull(Services.get(HostLocatorService.class)).getLocator(items[0], Arrays.stream(items).skip(1).toArray());
    }


    @ParameterType(".*")
    public FrameLocator frameLocator(String locator) {
        locator = StringUtils.removeQuotes(locator);
        String[] items = Arrays.stream(locator.split("::")).toArray(String[]::new);
        items[0] = StringUtils.toUpperSnakeCase(items[0]);
        return Objects.requireNonNull(Services.get(HostLocatorService.class)).getFrameLocator(items[0], Arrays.stream(items).skip(1).toArray());
    }

    @ParameterType(".*")
    public boolean condition(String name) {
        name = StringUtils.removeQuotes(name);
        String[] items = Arrays.stream(name.split("::")).toArray(String[]::new);
        items[0] = StringUtils.toUpperSnakeCase(items[0]);

        Object result = Services.get(ExecuteService.class).get(items[0], Arrays.stream(items).skip(1).toArray());
        return (boolean) result;
    }

    @ParameterType("( if .*)?")
    public boolean ifCondition(String name) {
        if (name == null) {
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

    @DataTableType
    public Map<String, String> table(Map<String, String> table) {
        var keys = table.keySet().stream().toList();
        var result = new HashMap<String, String>();
        for (String key : keys) {
            var val = table.get(key);
            if (val == null) {
                result.put(key, "");
            } else
                result.put(key, resultString(val));
        }

        return result;
    }
}
