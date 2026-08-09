package resolvers;

import models.FunctionInfo;
import org.junit.Test;
import reflection.FunctionService;
import reflection.Services;
import utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class FunctionResolver {

    public static String resolve(String value) {

        value = StringUtils.removeQuotes(value);
        value = DataResolver.resolveString(value,true);

        if (!value.startsWith("=")) {
            return value;
        }
        
        var name = value.substring(1,value.indexOf("("));
        var paramString = value.substring(value.indexOf("(")+1,value.indexOf(")"));
        var params = paramString.split(",");

        var service = Services.get(FunctionService.class);
        var info = service.get(name).get();

        try {
            return info.method.invoke(info.object,params).toString();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    @Test
    public void testResolve() {
        System.out.println(resolve("=encrypt({{sampleSite}})"));
    }
}
