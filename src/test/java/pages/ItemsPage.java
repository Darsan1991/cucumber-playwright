package pages;

import attributes.Execute;
import attributes.Host;
import attributes.Inject;
import com.microsoft.playwright.Locator;
import org.junit.Assert;

import com.microsoft.playwright.Page;

public class ItemsPage extends BasePage {

    @Inject
    CheckoutPage checkoutPage;

    @Inject
    Page page;
 
    public ItemsPage() {
        System.out.println("ItemsPage");
    }

    @Override
    public void init() {
        super.init();
    }

    public void orderProduct(String ProductName) {
        page.click("//div[text()='" + ProductName + "']/following::button[1]");
        page.click("#shopping_cart_container > a");
        Assert.assertTrue(page.isVisible("text=" + ProductName));
        page.click("[data-test=\"checkout\"]");
    }

    public void loginSuccessful() {
        Assert.assertTrue(page.isVisible("text=Products"));
    }

    @Host(name = "locatorItem")
    public Locator locatorItem(String name) {

        return page.locator("//*[text()='%s']".formatted(name));
    }

    @Host
    public Locator locatorItem123(String name) {

        return page.locator("//button[text()='%s']".formatted(name));
    }

    @Execute
    public void sampleRun() {
        System.out.println("sample run");
    }


    @Execute
    public boolean notEmpty(String value) {
        return value != null && !value.isBlank();
    }


}
