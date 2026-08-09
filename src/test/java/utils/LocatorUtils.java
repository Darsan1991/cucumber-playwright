package utils;

import com.microsoft.playwright.Locator;

public class LocatorUtils {
    
    public static String tag(Locator locator) {
        return locator.evaluate("el=>el.tagName").toString();
    }
}
