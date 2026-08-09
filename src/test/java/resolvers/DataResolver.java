package resolvers;

import org.junit.Test;
import reflection.DataService;
import reflection.Services;
import utils.StringUtils;

import java.util.List;

public class DataResolver {

    public static String resolve(String key, Object def) {
        key = key.startsWith("{{") && key.endsWith("}}") ? key.substring(2, key.length() - 2) : key;
        return Services.get(DataService.class).get(key, def).toString();
    }

    public static String resolveString(String value, boolean strict) {
        if (!strict) {
            return resolve(value, value);
        }
        var items = StringUtils.getAllTextByRegex(value, "\\{\\{.*\\}\\}");
        for (var item : items) {
            value = value.replace(item, resolve(item, item));
        }
        return value;
    }

    @Test
    public void testResolveString() {
        System.out.println(resolveString("=hello({{sampleSite}})",true));
    }
}
