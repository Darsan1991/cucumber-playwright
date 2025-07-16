package pages;

import attributes.Host;
import attributes.Inject;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.cucumber.java.en.When;
import reflection.base.Initializer;
import reflection.base.LoggerService;

public abstract class BasePage implements Initializer {

	/**
	 * Page
	 */
	
	@Inject public  Page page;
	@Inject protected LoggerService logger;


	@Override
	public void init() {
		logger.logSuccess("Page initialized:"+this.getClass().getSimpleName() );
	}
}