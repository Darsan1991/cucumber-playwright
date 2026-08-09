package stepdefinitions;

import attributes.Inject;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class BaseStep {

    @Inject
    public Page page;
    
    protected static Browser browser;
    public static Page createPlaywrightPageInstance(String browserTypeAsString) {

        BrowserType browserType = switch (browserTypeAsString) {
            case "Firefox" -> Playwright.create().firefox();
            case "Chromium" -> Playwright.create().chromium();
            case "Webkit" -> Playwright.create().webkit();
            case String s when s.startsWith("Webkit") -> Playwright.create().webkit();
            default -> null;
        };

        if (browserType == null) {
            throw new IllegalArgumentException("Could not launch a browser for type " + browserTypeAsString);
        }

        
        browser = browserType.launch(new BrowserType.LaunchOptions().setHeadless(false));

        var newPage = browser.newPage();
        newPage.setViewportSize(1920,1080);
        return newPage;
    }
}
