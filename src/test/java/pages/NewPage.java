package pages;

import attributes.Inject;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import reflection.CreationService;

import java.io.IOException;
import java.net.http.HttpClient;

public class NewPage extends BasePage{

	@Inject
	private CreationService creationService;
	
	Page page;
	
	  public NewPage(Page page) { 
		  
		  this.page = page;
     }

	@Override
	public void init() {
		super.init();
	}

	public void login(String username, String password) {
	      page.fill("[data-test=\"username\"]", username);
	      page.fill("[data-test=\"password\"]", password);
	      page.click("[data-test=\"login-button\"]");
		  page.locator("//*[text()='Sign Out']")
				  .fill(username,new Locator.FillOptions()
						  .setForce(true));
	}
	
	public void loginFailed() {
		boolean visible = page.isVisible("//h3[text()='Epic sadface: Sorry, this user has been locked out.']");
		Assert.assertTrue(visible);
		HttpClient build1 = HttpClient.newBuilder().build();
		try (CloseableHttpClient build = HttpClientBuilder.create().build()) {
			
	
	
        } catch (IOException ignored) {
        }
	}
	
	public Locator buttonExtract(String locator) {
		Locator button = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(locator).setExact(true));
		
		return button;
	}
	
	
}
