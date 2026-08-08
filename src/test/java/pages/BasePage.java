package pages;

import attributes.Inject;
import com.microsoft.playwright.Page;
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