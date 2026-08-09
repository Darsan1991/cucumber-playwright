package pages;

import attributes.Host;
import com.microsoft.playwright.Locator;

import java.util.Locale;

public class ElementPage extends BasePage{
    
    @Host
    public Locator byPlaceholder(String placeholder) {
        return page.getByPlaceholder(placeholder);
    }

    @Host
    public Locator byId(String id) {
        return page.locator("//*[@id='%s']".formatted(id));
    }


    @Host
    public Locator textButton(String text) {
        return page.locator("//button[normalize-space()='%s']".formatted(text));
    }


    @Host
    public Locator byClass(String text) {
        return page.locator("//*[contains(@class,'%s')]".formatted(text));
    }

    @Host
    public Locator inputGroup(String text) {
        return page.locator("//*[contains(@class,'inputGroup') and .//*[normalize-space()='%s']]".formatted(text));
    }
}
