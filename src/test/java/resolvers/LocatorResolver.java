package resolvers;

import com.microsoft.playwright.Locator;
import stepdefinitions.CustomParameters;
import utils.StringUtils;

import java.util.Arrays;
import java.util.List;

public class LocatorResolver {

    public static Locator resolve(String value) {
        value = value.startsWith("$")?value.substring(1):value;
        value = DataResolver.resolveString(value, true);
     
        return getLocator(value);
        
    }


    public static com.microsoft.playwright.Locator getLocator(String locator) {
        locator = StringUtils.removeQuotes(locator);
        List<com.microsoft.playwright.Locator> items = Arrays.stream(locator.split("->")).map(String::trim)
                .map(CustomParameters::getSingleLocator).toList();

        com.microsoft.playwright.Locator resultLocator = items.getFirst();
        for (com.microsoft.playwright.Locator value : items.stream().skip(1).toList()) {
            resultLocator = resultLocator.locator(value);
        }
        return resultLocator;
    }
}
