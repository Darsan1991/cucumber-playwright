package pages;

import attributes.Execute;
import attributes.Host;
import attributes.Inject;
import com.microsoft.playwright.Locator;
import io.cucumber.java.sl.In;
import org.junit.Assert;

import com.microsoft.playwright.Page;

public class CheckoutPage extends BasePage {


    @Host
    public String locatorFormat = "//iframe[text()='%s']";
    
    @Inject
    ItemsPage itemsPage;


    public CheckoutPage() {
    }

    @Host
    protected static String locatorCheckout() {
        return "//*[text()='Checkout']";
    }

    public CheckoutPage(Page page) {
        this.page = page;
    }

    @Execute
    public static boolean isTrue(String value) {
        return value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("1"));
    }

    public void fillCheckoutDetails(String firstname, String lastname, String postcode) {
        page.fill("[data-test=\"firstName\"]", firstname);
        page.fill("[data-test=\"lastName\"]", lastname);
        page.fill("[data-test=\"postalCode\"]", postcode);
    }

    public void completeCheckout() {
        page.click("[data-test=\"continue\"]");
        page.click("[data-test=\"finish\"]");
    }
	
    public void checkoutSuccessful() {
        Assert.assertTrue(page.isVisible("text=THANK YOU FOR YOUR ORDER"));

        page.frame("").locator("//*[text()='Sign Out']").click();
    }
}
